#!/usr/bin/env python3
"""Offline recipe pictures for Crazy Alloy: Revival, drawn in the style of the game's own screens.

Reads every recipe JSON in data/crazyalloy_revival/recipe (crafting, cooking, stonecutting and the mod's
Chocolate Factory and Ice Cream Machine types) and draws each one on the real GUI texture of its station,
with the items rendered from their real item/block models and textures (3D blocks are rasterised with the
model's GUI display transform). A tooltip box under the window names the result and gives the details.

Vanilla textures, models, fonts and pt_br names come from the Minecraft client jar, which is not in the repo:

    python3 tools/recipe_render/render_recipes.py --client-jar client-26.1.2.jar --out out/receitas

No game client is launched. Needs Pillow and numpy.
"""
import argparse
import io
import json
import math
import re
import unicodedata
import zipfile
from pathlib import Path

import numpy as np
from PIL import Image

MOD = "crazyalloy_revival"
REPO = Path(__file__).resolve().parents[2]
RES = REPO / "src/main/resources"
S = 4  # GUI scale: one GUI pixel = S image pixels, items are 16*S pixels


# ---------------------------------------------------------------- resources

class Assets:
    def __init__(self, client_jar):
        self.jar = zipfile.ZipFile(client_jar)
        self.names = set(self.jar.namelist())
        self.tex_cache = {}
        self.model_cache = {}

    def _path(self, kind, rid, ext):
        ns, _, path = rid.partition(":") if ":" in rid else ("minecraft", "", rid)
        return f"assets/{ns}/{kind}/{path}{ext}"

    def read(self, rel):
        local = RES / rel
        if local.exists():
            return local.read_bytes()
        if rel in self.names:
            return self.jar.read(rel)
        return None

    def json(self, kind, rid):
        data = self.read(self._path(kind, rid, ".json"))
        return None if data is None else json.loads(data)

    def texture(self, rid):
        if rid not in self.tex_cache:
            data = self.read(self._path("textures", rid, ".png"))
            if data is None:
                raise FileNotFoundError(rid)
            img = Image.open(io.BytesIO(data)).convert("RGBA")
            if img.height > img.width and self.read(self._path("textures", rid, ".png.mcmeta")) is not None:
                img = img.crop((0, 0, img.width, img.width))  # first animation frame
            self.tex_cache[rid] = img
        return self.tex_cache[rid]

    def model(self, rid):
        """Model with parents merged: textures, elements, display and whether it is a flat generated item."""
        if rid in self.model_cache:
            return self.model_cache[rid]
        rid_full = rid if ":" in rid else "minecraft:" + rid
        if rid_full == "minecraft:builtin/generated":
            m = {"textures": {}, "elements": None, "display": {}, "generated": True}
        else:
            raw = self.json("models", rid_full)
            if raw is None:
                raise FileNotFoundError("model " + rid_full)
            parent = self.model(raw["parent"]) if "parent" in raw else \
                {"textures": {}, "elements": None, "display": {}, "generated": False}
            m = {
                "textures": {**parent["textures"], **raw.get("textures", {})},
                "elements": raw.get("elements", parent["elements"]),
                "display": {**parent["display"], **raw.get("display", {})},
                "generated": parent["generated"] and "elements" not in raw,
            }
        self.model_cache[rid] = m
        return m


def resolve_tex(textures, ref):
    seen = 0
    while ref.startswith("#") and seen < 20:
        ref = textures.get(ref[1:], "")
        seen += 1
    return ref or None


# ---------------------------------------------------------------- item rendering

class Tint:
    def __init__(self, assets, spec):
        t = spec.get("type", "")
        if t.endswith("constant"):
            v = spec["value"] & 0xFFFFFF
            self.rgb = ((v >> 16) & 255, (v >> 8) & 255, v & 255)
        elif t.endswith("grass") or t.endswith("foliage"):
            cmap = assets.texture("minecraft:colormap/" + ("grass" if t.endswith("grass") else "foliage"))
            temp = max(0.0, min(1.0, spec.get("temperature", 0.5)))
            down = max(0.0, min(1.0, spec.get("downfall", 1.0))) * temp
            x, y = int((1 - temp) * 255), int((1 - down) * 255)
            self.rgb = cmap.getpixel((x, y))[:3]
        else:
            self.rgb = (255, 255, 255)


def item_model_def(assets, item_id):
    d = assets.json("items", item_id)
    if d is None:
        raise FileNotFoundError("item " + item_id)
    node = d["model"]
    while True:  # take the default branch of select/condition/range models
        t = node.get("type", "minecraft:model").split(":")[-1]
        if t == "model":
            return node["model"], node.get("tints", [])
        if t == "composite":
            node = node["models"][0]
        elif t == "condition":
            node = node["on_false"]
        elif t in ("select", "range_dispatch"):
            node = node.get("fallback") or node.get("cases", node.get("entries"))[0]["model"]
        elif t == "special":
            return node["base"], []
        else:
            raise ValueError("item model type " + t)


def tint_image(img, rgb):
    a = np.asarray(img).astype(np.float32)
    a[..., :3] *= np.array(rgb, np.float32) / 255.0
    return Image.fromarray(a.clip(0, 255).astype(np.uint8), "RGBA")


def render_flat(assets, model, tints):
    out = Image.new("RGBA", (16 * S, 16 * S))
    i = 0
    while f"layer{i}" in model["textures"]:
        tex = assets.texture(resolve_tex(model["textures"], model["textures"][f"layer{i}"]))
        if i < len(tints):
            tex = tint_image(tex, Tint(assets, tints[i]).rgb)
        out.alpha_composite(tex.resize((16 * S, 16 * S), Image.NEAREST))
        i += 1
    return out


def rot_matrix(axis, deg):
    r = math.radians(deg)
    c, s = math.cos(r), math.sin(r)
    if axis == "x":
        return np.array([[1, 0, 0], [0, c, -s], [0, s, c]])
    if axis == "y":
        return np.array([[c, 0, s], [0, 1, 0], [-s, 0, c]])
    return np.array([[c, -s, 0], [s, c, 0], [0, 0, 1]])


FACE_NORMALS = {"up": (0, 1, 0), "down": (0, -1, 0), "north": (0, 0, -1), "south": (0, 0, 1),
                "west": (-1, 0, 0), "east": (1, 0, 0)}


def face_corners(d, f, t):
    """Corners (top-left, top-right, bottom-right, bottom-left of the texture) of a face in model space."""
    x1, y1, z1 = f
    x2, y2, z2 = t
    return {
        "up": [(x1, y2, z1), (x2, y2, z1), (x2, y2, z2), (x1, y2, z2)],
        "down": [(x1, y1, z2), (x2, y1, z2), (x2, y1, z1), (x1, y1, z1)],
        "north": [(x2, y2, z1), (x1, y2, z1), (x1, y1, z1), (x2, y1, z1)],
        "south": [(x1, y2, z2), (x2, y2, z2), (x2, y1, z2), (x1, y1, z2)],
        "west": [(x1, y2, z1), (x1, y2, z2), (x1, y1, z2), (x1, y1, z1)],
        "east": [(x2, y2, z2), (x2, y2, z1), (x2, y1, z1), (x2, y1, z2)],
    }[d]


def default_uv(d, f, t):
    x1, y1, z1 = f
    x2, y2, z2 = t
    return {
        "up": [x1, z1, x2, z2], "down": [x1, 16 - z2, x2, 16 - z1],
        "north": [16 - x2, 16 - y2, 16 - x1, 16 - y1], "south": [x1, 16 - y2, x2, 16 - y1],
        "west": [z1, 16 - y2, z2, 16 - y1], "east": [16 - z2, 16 - y2, 16 - z1, 16 - y1],
    }[d]


def shade_for(n):
    """World-style face shading (up 1.0, down 0.5, north/south 0.8, east/west 0.6), blended by normal."""
    nx, ny, nz = n
    return (ny * ny) * (1.0 if ny > 0 else 0.5) + nz * nz * 0.8 + nx * nx * 0.6


def render_3d(assets, model, tints):
    N = 16 * S
    color = np.zeros((N, N, 4), np.float32)
    depth = np.full((N, N), -1e9, np.float32)
    gui = model["display"].get("gui", {})
    rx, ry, rz = gui.get("rotation", [0, 0, 0])
    R = rot_matrix("x", rx) @ rot_matrix("y", ry) @ rot_matrix("z", rz)
    T = np.array(gui.get("translation", [0, 0, 0]), float) / 16.0
    Sc = np.array(gui.get("scale", [1, 1, 1]), float)
    ys, xs = np.mgrid[0:N, 0:N]
    px = (xs + 0.5) / S  # GUI pixel coordinates of pixel centres
    py = (ys + 0.5) / S

    for el in model["elements"] or []:
        f, t = el["from"], el["to"]
        erot = el.get("rotation")
        for d, face in el.get("faces", {}).items():
            tex_id = resolve_tex(model["textures"], face["texture"])
            if not tex_id:
                continue
            tex = np.asarray(assets.texture(tex_id)).astype(np.float32)
            th, tw = tex.shape[:2]
            if "tintindex" in face and face["tintindex"] < len(tints):
                tex = tex.copy()
                tex[..., :3] *= np.array(Tint(assets, tints[face["tintindex"]]).rgb) / 255.0
            corners = np.array(face_corners(d, f, t), float)
            normal = np.array(FACE_NORMALS[d], float)
            if erot:
                axis, angle = erot.get("axis"), erot.get("angle", 0)
                if axis is None:
                    for ax in "xyz":
                        if erot.get(ax):
                            axis, angle = ax, erot[ax]
                if axis:
                    M = rot_matrix(axis, angle)
                    o = np.array(erot.get("origin", [8, 8, 8]), float)
                    corners = (corners - o) @ M.T
                    if erot.get("rescale"):
                        k = 1.0 / math.cos(math.radians(abs(angle)))
                        scale = np.array([1.0 if axis == a else k for a in "xyz"])
                        corners = corners * scale
                    corners = corners + o
                    normal = M @ normal
            light = 1.0 if not el.get("shade", True) else shade_for(normal)
            v = (corners / 16.0 - 0.5) * Sc
            v = v @ R.T + T
            n_view = R @ (normal * np.sign(Sc))
            if n_view[2] <= 1e-6:
                continue  # back face
            sx = 8 + 16 * v[:, 0]
            sy = 8 - 16 * v[:, 1]
            sz = v[:, 2]
            e1 = np.array([sx[1] - sx[0], sy[1] - sy[0]])
            e2 = np.array([sx[3] - sx[0], sy[3] - sy[0]])
            det = e1[0] * e2[1] - e1[1] * e2[0]
            if abs(det) < 1e-9:
                continue
            qx, qy = px - sx[0], py - sy[0]
            s = (qx * e2[1] - qy * e2[0]) / det
            tt = (e1[0] * qy - e1[1] * qx) / det
            inside = (s >= 0) & (s < 1) & (tt >= 0) & (tt < 1)
            if not inside.any():
                continue
            u1, v1, u2, v2 = face.get("uv", default_uv(d, f, t))
            uvs = [(u1, v1), (u2, v1), (u2, v2), (u1, v2)]
            k = (face.get("rotation", 0) // 90) % 4
            uvs = [uvs[(i - k) % 4] for i in range(4)]
            uvs = np.array(uvs, float)
            si, ti = s[inside], tt[inside]
            w0, w1, w2, w3 = (1 - si) * (1 - ti), si * (1 - ti), si * ti, (1 - si) * ti
            uu = w0 * uvs[0, 0] + w1 * uvs[1, 0] + w2 * uvs[2, 0] + w3 * uvs[3, 0]
            vv = w0 * uvs[0, 1] + w1 * uvs[1, 1] + w2 * uvs[2, 1] + w3 * uvs[3, 1]
            tx = np.clip((uu / 16.0 * tw).astype(int), 0, tw - 1)
            ty = np.clip((vv / 16.0 * th).astype(int), 0, th - 1)
            texel = tex[ty, tx]
            z = sz[0] + si * (sz[1] - sz[0]) + ti * (sz[3] - sz[0])
            iy, ix = ys[inside], xs[inside]
            front = (z > depth[iy, ix]) & (texel[:, 3] > 25)
            iy, ix, z, texel = iy[front], ix[front], z[front], texel[front]
            rgb = texel[:, :3] * light
            a = texel[:, 3:4] / 255.0
            color[iy, ix, :3] = rgb * a + color[iy, ix, :3] * (1 - a)
            color[iy, ix, 3] = np.maximum(color[iy, ix, 3], texel[:, 3])
            depth[iy, ix] = z
    return Image.fromarray(color.clip(0, 255).astype(np.uint8), "RGBA")


_item_cache = {}


def render_item(assets, item_id):
    if item_id not in _item_cache:
        model_id, tints = item_model_def(assets, item_id)
        model = assets.model(model_id)
        _item_cache[item_id] = render_flat(assets, model, tints) if model["generated"] or not model["elements"] \
            else render_3d(assets, model, tints)
    return _item_cache[item_id]


# ---------------------------------------------------------------- font

class Font:
    def __init__(self, assets):
        self.glyphs = {}
        self.space = {}
        for ref in ("minecraft:include/space", "minecraft:include/default"):
            for p in assets.json("font", ref)["providers"]:
                if p["type"] == "space":
                    self.space.update(p["advances"])
                elif p["type"] == "bitmap":
                    self._bitmap(assets, p)

    def _bitmap(self, assets, p):
        img = np.asarray(assets.texture(p["file"].replace(".png", "")))
        rows = p["chars"]
        ch = img.shape[0] // len(rows)
        cw = img.shape[1] // len(rows[0])
        height = p.get("height", 8)
        scale = height / ch
        for r, line in enumerate(rows):
            for c, char in enumerate(line):
                if char in self.glyphs or char == "\0":
                    continue
                cell = img[r * ch:(r + 1) * ch, c * cw:(c + 1) * cw, 3] > 0
                cols = np.where(cell.any(axis=0))[0]
                width = int(cols.max()) + 1 if len(cols) else 0
                self.glyphs[char] = (cell[:, :max(width, 1)], scale, p["ascent"], width * scale + 1)

    def width(self, text):
        return int(math.ceil(sum(self.space.get(ch, self.glyphs.get(ch, (None, 0, 0, 6))[3]) for ch in text)))

    def draw(self, canvas, text, x, y, color, shadow=False):
        """Draws at GUI coordinates (x, y) on an S-scaled canvas (numpy RGBA array)."""
        if shadow:
            r, g, b = color
            self.draw(canvas, text, x + 1, y + 1, (r // 4, g // 4, b // 4))
        cx = x
        for ch in text:
            if ch in self.space:
                cx += self.space[ch]
                continue
            g = self.glyphs.get(ch)
            if g is None:
                cx += 6
                continue
            cell, scale, ascent, adv = g
            top = y + 7 - ascent
            px = S * scale
            for gy, gx in zip(*np.nonzero(cell)):
                x0 = int(round((cx + gx * scale) * S))
                y0 = int(round((top + gy * scale) * S))
                canvas[y0:y0 + int(round(px)), x0:x0 + int(round(px)), :3] = color
                canvas[y0:y0 + int(round(px)), x0:x0 + int(round(px)), 3] = 255
            cx += adv


# ---------------------------------------------------------------- composition helpers

def up(img):
    return img.resize((img.width * S, img.height * S), Image.NEAREST)


def gui_panel(assets, tex_id, top_h=79, src_h=166, width=176):
    """The top part of a container window (no player inventory), closed with the window's bottom border."""
    tex = assets.texture(tex_id)
    panel = Image.new("RGBA", (width, top_h + 4))
    panel.paste(tex.crop((0, 0, width, top_h)), (0, 0))
    panel.paste(tex.crop((0, src_h - 4, width, src_h)), (0, top_h))
    return panel


def nine_slice(img, w, h, border, stretch_inner=False):
    out = Image.new("RGBA", (w, h))
    b = border
    iw, ih = img.size
    parts = [
        ((0, 0, b, b), (0, 0, b, b)), ((iw - b, 0, iw, b), (w - b, 0, w, b)),
        ((0, ih - b, b, ih), (0, h - b, b, h)), ((iw - b, ih - b, iw, ih), (w - b, h - b, w, h)),
        ((b, 0, iw - b, b), (b, 0, w - b, b)), ((b, ih - b, iw - b, ih), (b, h - b, w - b, h)),
        ((0, b, b, ih - b), (0, b, b, h - b)), ((iw - b, b, iw, ih - b), (w - b, b, w, h - b)),
        ((b, b, iw - b, ih - b), (b, b, w - b, h - b)),
    ]
    for src, dst in parts:
        dw, dh = dst[2] - dst[0], dst[3] - dst[1]
        if dw <= 0 or dh <= 0:
            continue
        piece = img.crop(src)
        if piece.width == 1 or piece.height == 1 or stretch_inner or True:
            piece = piece.resize((dw, dh), Image.NEAREST)
        out.paste(piece, dst[:2])
    return out


class Lang:
    def __init__(self, assets, client_jar):
        self.mod = json.loads((RES / f"assets/{MOD}/lang/pt_br.json").read_text(encoding="utf-8"))
        idx_vanilla = None
        p = Path(client_jar).with_name("pt_br.json")
        self.vanilla = json.loads(p.read_text(encoding="utf-8")) if p.exists() else {}
        self.en = json.loads(assets.jar.read("assets/minecraft/lang/en_us.json"))

    def name(self, item_id):
        ns, path = item_id.split(":")
        for key in (f"item.{ns}.{path}", f"block.{ns}.{path}"):
            for table in (self.mod, self.vanilla, self.en):
                if key in table:
                    return table[key]
        return item_id

    def get(self, key):
        for table in (self.mod, self.vanilla, self.en):
            if key in table:
                return table[key]
        return key


def load_tag(tag):
    ns, path = tag.split(":")
    data = json.loads((RES / f"data/{ns}/tags/item/{path}.json").read_text())
    items = []
    for v in data["values"]:
        v = v["id"] if isinstance(v, dict) else v
        items.extend(load_tag(v[1:]) if v.startswith("#") else [v])
    return items


def ingredient_items(ing):
    if isinstance(ing, list):
        return [x for i in ing for x in ingredient_items(i)]
    if isinstance(ing, dict):
        ing = ing.get("item") or "#" + ing["tag"]
    return load_tag(ing[1:]) if ing.startswith("#") else [ing]


def fmt_num(x):
    s = f"{x:g}".replace(".", ",")
    return s


class Picture:
    """One recipe picture: the window, then a tooltip with the result and details, on the menu backdrop."""

    def __init__(self, ctx, panel, title, title_x, title_y=6):
        self.ctx = ctx
        self.panel = panel
        self.canvas = np.asarray(up(panel)).copy()
        ctx.font.draw(self.canvas, title, title_x, title_y, (0x40, 0x40, 0x40))

    def item(self, item_id, x, y, count=1, ghost=False):
        img = render_item(self.ctx.assets, item_id)
        layer = Image.fromarray(self.canvas, "RGBA")
        layer.alpha_composite(img, (x * S, y * S))
        if ghost:  # the faded look the recipe book uses for missing ingredients
            over = Image.new("RGBA", img.size, (0x8B, 0x8B, 0x8B, 0x99))
            layer.alpha_composite(over, (x * S, y * S))
        self.canvas = np.asarray(layer).copy()
        if count > 1:
            text = str(count)
            self.ctx.font.draw(self.canvas, text, x + 17 - self.ctx.font.width(text), y + 9, (255, 255, 255),
                               shadow=True)

    def sprite(self, rid, x, y, crop=None):
        img = self.ctx.assets.texture(rid)
        if crop:
            img = img.crop(crop)
        layer = Image.fromarray(self.canvas, "RGBA")
        layer.alpha_composite(up(img), (x * S, y * S))
        self.canvas = np.asarray(layer).copy()

    def fill(self, x0, y0, x1, y1, argb):
        a = ((argb >> 24) & 255) / 255.0
        rgb = np.array([(argb >> 16) & 255, (argb >> 8) & 255, argb & 255], np.float32)
        region = self.canvas[y0 * S:y1 * S, x0 * S:x1 * S]
        region[..., :3] = (region[..., :3] * (1 - a) + rgb * a).astype(np.uint8)
        region[..., 3] = np.maximum(region[..., 3], int(a * 255))

    def text(self, s, x, y, color, shadow=False):
        self.ctx.font.draw(self.canvas, s, x, y, color, shadow)

    def finish(self, lines):
        """lines: list of (text, rgb). The first is the title line of the tooltip."""
        font = self.ctx.font
        pw, ph = self.panel.size
        tw = max(font.width(t) for t, _ in lines)
        th = 8 + 2 + 10 * (len(lines) - 1) if len(lines) > 1 else 8
        margin = 10
        tip_w = tw + 6  # visible tooltip box (3 px padding each side)
        width = max(pw, tip_w) + 2 * margin
        height = margin + ph + 8 + th + 6 + margin
        bg = self.ctx.backdrop(width, height)
        bg.alpha_composite(Image.fromarray(self.canvas, "RGBA"), ((width - pw) // 2 * S, margin * S))
        out = np.asarray(bg).copy()
        tx = (width - tw) // 2
        ty = margin + ph + 8 + 3
        tip = self.ctx.tooltip(tw, th)
        img = Image.fromarray(out, "RGBA")
        img.alpha_composite(tip, ((tx - 12) * S, (ty - 12) * S))
        out = np.asarray(img).copy()
        y = ty
        for i, (t, rgb) in enumerate(lines):
            font.draw(out, t, tx, y, rgb, shadow=True)
            y += 12 if i == 0 else 10
        return Image.fromarray(out, "RGBA")


class Context:
    def __init__(self, client_jar):
        self.assets = Assets(client_jar)
        self.font = Font(self.assets)
        self.lang = Lang(self.assets, client_jar)

    def backdrop(self, w, h):
        return Image.new("RGBA", (w * S, h * S), (0x2B, 0x2B, 0x2E, 255))

    def tooltip(self, w, h):
        bgs = self.assets.texture("minecraft:gui/sprites/tooltip/background")
        frame = self.assets.texture("minecraft:gui/sprites/tooltip/frame")
        size = (w + 6 + 18, h + 6 + 18)
        img = nine_slice(bgs, *size, 9)
        img.alpha_composite(nine_slice(frame, *size, 10))
        return up(img)


# ---------------------------------------------------------------- recipe pictures

WHITE = (255, 255, 255)
GRAY = (0xAA, 0xAA, 0xAA)
GOLD = (0xFF, 0xAA, 0x00)
GREEN = (0x55, 0xFF, 0x55)
AQUA = (0x55, 0xFF, 0xFF)


def result_of(r):
    res = r["result"]
    return (res, 1) if isinstance(res, str) else (res["id"], res.get("count", 1))


def tag_note(ctx, ing):
    if isinstance(ing, str) and ing.startswith("#"):
        names = [ctx.lang.name(i) for i in ingredient_items(ing)]
        return f"Qualquer um: {', '.join(names)}"
    return None


def title_line(ctx, r):
    rid, count = result_of(r)
    name = ctx.lang.name(rid)
    return (f"{name} x{count}" if count > 1 else name, WHITE)


def wrap(ctx, text, width=190):
    words, lines, cur = text.split(" "), [], ""
    for w in words:
        trial = (cur + " " + w).strip()
        if cur and ctx.font.width(trial) > width:
            lines.append(cur)
            cur = w
        else:
            cur = trial
    return lines + [cur] if cur else lines


def crafting(ctx, r):
    p = Picture(ctx, gui_panel(ctx.assets, "minecraft:gui/container/crafting_table"),
                ctx.lang.get("container.crafting"), 29)
    notes = []
    if r["type"].endswith("crafting_shaped"):
        for row, line in enumerate(r["pattern"]):
            for col, ch in enumerate(line):
                if ch == " ":
                    continue
                ing = r["key"][ch]
                p.item(ingredient_items(ing)[0], 30 + col * 18, 17 + row * 18)
                n = tag_note(ctx, ing)
                if n and n not in notes:
                    notes.append(n)
        kind = "Bancada de Trabalho · receita com formato"
    else:
        for i, ing in enumerate(r["ingredients"]):
            p.item(ingredient_items(ing)[0], 30 + (i % 3) * 18, 17 + (i // 3) * 18)
            n = tag_note(ctx, ing)
            if n and n not in notes:
                notes.append(n)
        kind = "Bancada de Trabalho · sem formato"
        notes.insert(0, "Ingredientes em qualquer posição da grade")
    rid, count = result_of(r)
    p.item(rid, 124, 35, count)
    return p, kind, notes


COOKING = {
    "smelting": ("minecraft:gui/container/furnace", "minecraft:gui/sprites/container/furnace", "container.furnace"),
    "blasting": ("minecraft:gui/container/blast_furnace", "minecraft:gui/sprites/container/blast_furnace",
                 "container.blast_furnace"),
    "smoking": ("minecraft:gui/container/smoker", "minecraft:gui/sprites/container/smoker", "container.smoker"),
}


def cooking(ctx, r):
    kind = r["type"].split(":")[1]
    rid, count = result_of(r)
    seconds = r.get("cookingtime", 200) / 20
    notes = [f"Tempo: {fmt_num(seconds)} s · XP: {fmt_num(r.get('experience', 0))}"]
    n = tag_note(ctx, r["ingredient"])
    if n:
        notes.append(n)
    if kind == "campfire_cooking":
        # Campfires have no window in the game: the item is placed on a lit campfire. Drawn on a furnace-like
        # panel with the campfire where the fuel would be.
        panel = gui_panel(ctx.assets, "minecraft:gui/container/furnace")
        title = ctx.lang.get("block.minecraft.campfire")
        p = Picture(ctx, panel, title, (176 - ctx.font.width(title)) // 2)
        p.fill(55, 35, 75, 71, 0xFFC6C6C6)
        p.item(ingredient_items(r["ingredient"])[0], 56, 17)
        p.item("minecraft:campfire", 56, 45)
        p.sprite("minecraft:gui/sprites/container/furnace/burn_progress", 79, 34)
        p.item(rid, 116, 35, count)
        notes.insert(0, "Coloque o item numa fogueira acesa (sem janela no jogo)")
        return p, "Fogueira", notes
    tex, sprites, key = COOKING[kind]
    title = ctx.lang.get(key)
    p = Picture(ctx, gui_panel(ctx.assets, tex), title, (176 - ctx.font.width(title)) // 2)
    p.item(ingredient_items(r["ingredient"])[0], 56, 17)
    p.item("minecraft:coal", 56, 53)
    p.sprite(sprites + "/lit_progress", 56, 36)
    p.sprite(sprites + "/burn_progress", 79, 34)
    p.item(rid, 116, 35, count)
    notes.append("Combustível: qualquer combustível de fornalha")
    return p, title, notes


def stonecutting(ctx, r):
    title = ctx.lang.get("container.stonecutter")
    p = Picture(ctx, gui_panel(ctx.assets, "minecraft:gui/container/stonecutter"), title, 8, 4)
    p.item(ingredient_items(r["ingredient"])[0], 20, 33)
    p.sprite("minecraft:gui/sprites/container/stonecutter/scroller_disabled", 119, 15)
    p.sprite("minecraft:gui/sprites/container/stonecutter/recipe_selected", 52, 14)
    rid, count = result_of(r)
    p.item(rid, 52, 15, count)
    p.item(rid, 143, 33, count)
    notes = []
    n = tag_note(ctx, r["ingredient"])
    if n:
        notes.append(n)
    notes.append("Um bloco de entrada por resultado")
    return p, title, notes


def chocolate_factory(ctx, r):
    title = ctx.lang.get(f"container.{MOD}.chocolate_factory")
    p = Picture(ctx, gui_panel(ctx.assets, f"{MOD}:gui/container/chocolate_factory", src_h=166), title, 8)
    for i, ing in enumerate(r["ingredients"]):
        p.item(ingredient_items(ing)[0], (38, 60)[i], 17)
    p.item("minecraft:coal", 49, 53)
    p.fill(50, 37, 64, 51, 0xFFE8862A)  # full flame, as the screen draws it while burning
    p.fill(85, 35 + 4, 85 + 24, 35 + 16 - 4, 0xFFC9782F)  # full progress arrow
    rid, count = result_of(r)
    p.item(rid, 120, 35, count)
    seconds = r.get("processing_time", 200) / 20
    notes = [f"Tempo: {fmt_num(seconds)} s · XP: {fmt_num(r.get('experience', 0))}",
             "Ingredientes em qualquer ordem nos espaços de cima",
             "Combustível: qualquer combustível de fornalha"]
    return p, title, notes


def ice_cream_machine(ctx, r):
    title = ctx.lang.get(f"container.{MOD}.ice_cream_machine")
    p = Picture(ctx, gui_panel(ctx.assets, f"{MOD}:gui/container/ice_cream_machine", src_h=166), title, 8)
    p.item(f"{MOD}:wafer_cone", 26, 20)
    p.item(ingredient_items(r["flavor"])[0], 26, 46)
    p.item("minecraft:milk_bucket", 61, 54)
    servings, cap = 4, 8  # one bucket of the default config (4 servings per bucket, tank holds 8)
    tx, ty, tw, th = 62, 18, 14, 31
    h = servings * th // cap
    p.fill(tx, ty + th - h, tx + tw, ty + th, 0xFFFBF6EE)
    for i in range(1, servings):
        ly = ty + th - i * th // cap
        p.fill(tx, ly, tx + tw, ly + 1, 0xFFD9CFC0)
    p.fill(88, 37 + 4, 88 + 24, 37 + 16 - 4, 0xFFF06A9A)
    rid, count = result_of(r)
    p.item(rid, 124, 33, count)
    label = ctx.lang.get(f"gui.{MOD}.ice_cream_machine.flavor").replace("%s", ctx.lang.name(rid))
    y = 54
    for line in wrap(ctx, label, 86):
        p.text(line, 128 - ctx.font.width(line) // 2, y, (0x8A, 0x3A, 0x5E))
        y += 9
    seconds = r.get("processing_time", 80) / 20
    notes = [f"Tempo: {fmt_num(seconds)} s · XP: {fmt_num(r.get('experience', 0))}",
             "Cada sorvete usa 1 Casquinha, 1 sabor e 1 porção de leite",
             "Um Balde de Leite rende 4 porções (configurável)"]
    return p, title, notes


HANDLERS = {
    "minecraft:crafting_shaped": ("01_bancada_de_trabalho", crafting),
    "minecraft:crafting_shapeless": ("01_bancada_de_trabalho", crafting),
    "minecraft:smelting": ("02_fornalha", cooking),
    "minecraft:blasting": ("03_alto_forno", cooking),
    "minecraft:smoking": ("04_defumador", cooking),
    "minecraft:campfire_cooking": ("05_fogueira", cooking),
    "minecraft:stonecutting": ("06_cortador_de_pedras", stonecutting),
    f"{MOD}:chocolate_factory": ("07_fabrica_de_chocolate", chocolate_factory),
    f"{MOD}:ice_cream_machine": ("08_maquina_de_sorvete", ice_cream_machine),
}


def slug(text):
    text = unicodedata.normalize("NFKD", text).encode("ascii", "ignore").decode().lower()
    return re.sub(r"[^a-z0-9]+", "_", text).strip("_")


def first_ingredient(r):
    for k in ("ingredient", "flavor"):
        if k in r:
            return ingredient_items(r[k])[0]
    if "ingredients" in r:
        return ingredient_items(r["ingredients"][0])[0]
    return ingredient_items(r["key"][r["pattern"][0].strip()[0]])[0]


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--client-jar", required=True, help="Minecraft 26.1.2 client jar (pt_br.json beside it)")
    ap.add_argument("--out", required=True)
    args = ap.parse_args()
    ctx = Context(args.client_jar)
    out = Path(args.out)
    recipe_dir = RES / f"data/{MOD}/recipe"
    used = set()
    made = []
    recipes = [(p, json.loads(p.read_text())) for p in sorted(recipe_dir.rglob("*.json"))]
    recipes.sort(key=lambda pr: HANDLERS[pr[1]["type"]][0])
    for path, r in recipes:
        folder, fn = HANDLERS[r["type"]]
        p, kind, notes = fn(ctx, r)
        lines = [title_line(ctx, r), (kind, GRAY)]
        for n in notes:
            lines += [(w, GRAY) for w in wrap(ctx, n)]
        img = p.finish(lines)
        rid, _ = result_of(r)
        name = slug(ctx.lang.name(rid))
        if (folder, name) in used or r["type"].endswith("stonecutting"):
            name = f"{name}__de_{slug(ctx.lang.name(first_ingredient(r)))}"
        used.add((folder, name))
        dest = out / folder / f"{name}.png"
        dest.parent.mkdir(parents=True, exist_ok=True)
        img.save(dest, optimize=True)
        made.append(dest)
        print(dest.relative_to(out))
    overview(made, out / "00_todas_as_receitas.png")
    print(f"{len(made)} receitas")


def overview(paths, dest, cols=6):
    """All pictures at half size on one sheet, in folder order."""
    ims = [Image.open(p) for p in paths]
    ims = [im.resize((im.width // 2, im.height // 2), Image.LANCZOS) for im in ims]
    cw = max(im.width for im in ims)
    rows = [ims[i:i + cols] for i in range(0, len(ims), cols)]
    heights = [max(im.height for im in row) for row in rows]
    sheet = Image.new("RGBA", (cw * cols, sum(heights)), (0x2B, 0x2B, 0x2E, 255))
    y = 0
    for row, h in zip(rows, heights):
        for i, im in enumerate(row):
            sheet.paste(im, (i * cw + (cw - im.width) // 2, y))
        y += h
    sheet.save(dest, optimize=True)


if __name__ == "__main__":
    main()
