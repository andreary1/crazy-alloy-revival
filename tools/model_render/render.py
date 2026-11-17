import json, math, sys
import numpy as np
from PIL import Image

def render(mesh_path, tex_path, out_path, yaw=-35, pitch=28, size=1024, ss=3, margin=0.04):
    q = np.array(json.load(open(mesh_path)), dtype=np.float64).reshape(-1, 4, 8)
    tex = np.asarray(Image.open(tex_path).convert('RGBA')).astype(np.float64) / 255
    th, tw = tex.shape[:2]
    P = q[..., :3].copy(); P[..., 0] *= -1; P[..., 1] *= -1      # model space -> world (entity renderer flips X and Y)
    N = q[..., 5:8].copy(); N[..., 0] *= -1; N[..., 1] *= -1
    UV = q[..., 3:5]
    a, b = math.radians(yaw), math.radians(pitch)
    Ry = np.array([[math.cos(a), 0, math.sin(a)], [0, 1, 0], [-math.sin(a), 0, math.cos(a)]])
    Rx = np.array([[1, 0, 0], [0, math.cos(b), -math.sin(b)], [0, math.sin(b), math.cos(b)]])
    R = Rx @ Ry
    V = P @ R.T            # camera: x right, y up, z towards viewer = -z
    NW = N                 # lighting in world space
    xs, ys = V[..., 0], V[..., 1]
    span = max(xs.max() - xs.min(), ys.max() - ys.min())
    S = size * ss
    scale = S * (1 - 2 * margin) / span
    cx, cy = (xs.max() + xs.min()) / 2, (ys.max() + ys.min()) / 2
    sx = (xs - cx) * scale + S / 2
    sy = S / 2 - (ys - cy) * scale
    depth = V[..., 2]
    W = int(round((xs.max() - xs.min()) * scale + 2 * margin * S)); H = int(round((ys.max() - ys.min()) * scale + 2 * margin * S))
    ox, oy = (S - W) / 2, (S - H) / 2
    sx -= ox; sy -= oy
    col = np.zeros((H, W, 4)); zb = np.full((H, W), -1e9)
    # Light like Minecraft entities: soft top light, darker sides, darkest bottom.
    L1 = np.array([0.2, 1.0, -0.7]); L1 /= np.linalg.norm(L1)
    L2 = np.array([-0.2, 1.0, 0.7]); L2 /= np.linalg.norm(L2)
    for i in range(q.shape[0]):
        n = NW[i, 0]
        if not np.isfinite(n).all(): continue
        shade = min(1.0, 0.4 + 0.6 * (max(0, n @ L1) + max(0, n @ L2)) * 0.6 + 0.25)
        for tri in ((0, 1, 2), (0, 2, 3)):
            X = sx[i, list(tri)]; Y = sy[i, list(tri)]; Z = depth[i, list(tri)]; T = UV[i, list(tri)]
            x0, x1 = int(max(0, np.floor(X.min()))), int(min(W - 1, np.ceil(X.max())))
            y0, y1 = int(max(0, np.floor(Y.min()))), int(min(H - 1, np.ceil(Y.max())))
            if x1 < x0 or y1 < y0: continue
            den = (Y[1] - Y[2]) * (X[0] - X[2]) + (X[2] - X[1]) * (Y[0] - Y[2])
            if abs(den) < 1e-9: continue
            gx, gy = np.meshgrid(np.arange(x0, x1 + 1) + 0.5, np.arange(y0, y1 + 1) + 0.5)
            w0 = ((Y[1] - Y[2]) * (gx - X[2]) + (X[2] - X[1]) * (gy - Y[2])) / den
            w1 = ((Y[2] - Y[0]) * (gx - X[2]) + (X[0] - X[2]) * (gy - Y[2])) / den
            w2 = 1 - w0 - w1
            m = (w0 >= -1e-6) & (w1 >= -1e-6) & (w2 >= -1e-6)
            if not m.any(): continue
            z = w0 * Z[0] + w1 * Z[1] + w2 * Z[2]
            u = w0 * T[0, 0] + w1 * T[1, 0] + w2 * T[2, 0]
            v = w0 * T[0, 1] + w1 * T[1, 1] + w2 * T[2, 1]
            tx = np.clip((u * tw).astype(int), 0, tw - 1); ty = np.clip((v * th).astype(int), 0, th - 1)
            c = tex[ty, tx]
            m &= c[..., 3] > 0.1
            sub = zb[y0:y1 + 1, x0:x1 + 1]
            m &= z > sub
            sub[m] = z[m]
            cs = col[y0:y1 + 1, x0:x1 + 1]
            cs[m, :3] = c[m, :3] * shade
            cs[m, 3] = 1
    # Downsample with premultiplied alpha.
    h2, w2_ = H // ss, W // ss
    col = col[:h2 * ss, :w2_ * ss].reshape(h2, ss, w2_, ss, 4)
    a_ = col[..., 3].mean(axis=(1, 3))
    rgb = (col[..., :3] * col[..., 3:4]).sum(axis=(1, 3)) / np.maximum(col[..., 3:4].sum(axis=(1, 3)), 1e-9)
    img = np.dstack([rgb, a_])
    Image.fromarray((img * 255).round().astype(np.uint8), 'RGBA').save(out_path)

if __name__ == '__main__':
    mesh, tex, out = sys.argv[1:4]
    kw = {}
    for s in sys.argv[4:]:
        k, v = s.split('='); kw[k] = float(v) if k != 'size' else int(v)
    render(mesh, tex, out, **kw)
