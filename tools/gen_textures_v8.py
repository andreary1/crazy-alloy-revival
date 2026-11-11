#!/usr/bin/env python3
"""Stage 8 textures for Crazy Alloy: Revival: the Enchanted tab (Mystic Grove).

Blocks: Mystic Grass (light turquoise top, darker sides), Mystic Dirt, Mystic Log (very dark bark with blue-green
vertical streaks), Mystic Planks, Mystic Leaves, Mystic Sapling and the Mystic Door.
Items: Jar, Jar With A Fairy, Elixir of Life, the Mystic Door and four spawn eggs.
Creatures: the Troll, the Fairy, the Evil Fairy and the Ent, painted region by region from the "// tex:" tags in their
Java models (same method as gen_mobs_v3.py).
Original art drawn in code from the project owner's written descriptions; no pixels copied from anywhere.
Run by tools/gen_textures.py after the stage 7 textures.
"""
import random
from gen_textures_v2 import hexc, shade, mix, art, save, blank
from gen_mobs_v3 import paint, fill
from gen_textures_v6 import spawn_egg

rng = random.Random(80)
CLEAR = (0, 0, 0, 0)

# Mystic palette
BARK = hexc("1d2b2e")
BARK_D = hexc("121c1e")
STREAK = hexc("2f6b6a")
STREAK_L = hexc("3e8f83")
PLANK = hexc("3a6a66")
PLANK_D = hexc("2a4f4c")
PLANK_L = hexc("4f8580")
TURQ = hexc("4fdcc2")
TURQ_L = hexc("7cf2dc")
TURQ_D = hexc("33b59e")
SOIL = hexc("3a4744")
SOIL_D = hexc("2a3533")
SOIL_L = hexc("4d5c57")
LEAF = hexc("21705f")
LEAF_D = hexc("154d44")
LEAF_L = hexc("2f9178")

def jitter(c, amount=0.08):
    return shade(c, 1.0 - amount / 2 + amount * rng.random())

# ================================================================== blocks (16x16)
def bark_pixel(x, seed_cols):
    """Vertical bark lines: most columns dark, some carry a blue-green streak."""
    kind = seed_cols[x]
    if kind == 2:
        return jitter(STREAK_L, 0.1)
    if kind == 1:
        return jitter(STREAK, 0.12)
    return jitter(BARK if rng.random() < 0.8 else BARK_D, 0.1)

def blocks():
    # Log side: dark bark with vertical streaks that break now and then.
    cols = [rng.choice([0, 0, 1, 0, 2, 1, 0]) for _ in range(16)]
    im = blank(); px = im.load()
    for x in range(16):
        run = cols[x]
        for y in range(16):
            if rng.random() < 0.12:
                run = rng.choice([0, cols[x]])
            px[x, y] = bark_pixel(x, {x: run})
    save(im, "block/mystic_log.png")
    # Log top: bark ring and teal-grey wood rings.
    im = blank(); px = im.load()
    for y in range(16):
        for x in range(16):
            d = max(abs(x - 7.5), abs(y - 7.5))
            if d > 6.5:
                c = bark_pixel(0, {0: rng.choice([0, 0, 1])})
            else:
                ring = int(d) % 2
                c = jitter(PLANK if ring else PLANK_L, 0.08)
                if d < 1:
                    c = PLANK_D
            px[x, y] = c
    save(im, "block/mystic_log_top.png")
    # Planks: four boards with grain and dark seams.
    im = blank(); px = im.load()
    for y in range(16):
        for x in range(16):
            c = jitter(PLANK, 0.1)
            if y % 4 == 3:
                c = PLANK_D
            elif (x + (y // 4) * 5) % 8 == 0 and y % 4 != 0:
                c = PLANK_D  # board ends
            elif rng.random() < 0.12:
                c = PLANK_L
            px[x, y] = c
    save(im, "block/mystic_planks.png")
    # Dirt: dark blue-grey soil with specks.
    def dirt():
        im = blank(); px = im.load()
        for y in range(16):
            for x in range(16):
                r = rng.random()
                px[x, y] = SOIL_D if r < 0.2 else SOIL_L if r < 0.32 else jitter(SOIL, 0.08)
        return im
    save(dirt(), "block/mystic_dirt.png")
    # Grass top: light, vibrant turquoise.
    im = blank(); px = im.load()
    for y in range(16):
        for x in range(16):
            r = rng.random()
            px[x, y] = TURQ_L if r < 0.15 else TURQ_D if r < 0.3 else jitter(TURQ, 0.08)
    save(im, "block/mystic_grass_block_top.png")
    # Grass side: dirt with an uneven turquoise fringe hanging from the top.
    im = dirt(); px = im.load()
    for x in range(16):
        depth = 2 + rng.choice([0, 1, 1, 2, 3])
        for y in range(depth):
            px[x, y] = TURQ_L if y == 0 and rng.random() < 0.4 else jitter(TURQ_D if y == depth - 1 else TURQ, 0.08)
    save(im, "block/mystic_grass_block_side.png")
    # Leaves: dark teal clumps with gaps.
    im = blank(); px = im.load()
    for y in range(16):
        for x in range(16):
            r = rng.random()
            if r < 0.1:
                continue
            px[x, y] = LEAF_L if r < 0.25 else LEAF_D if r < 0.45 else jitter(LEAF, 0.1)
    save(im, "block/mystic_leaves.png")
    sap = art([
        "................",
        "......lL........",
        ".....LlLl..L....",
        "....lLLlL.lLl...",
        "...LlL.LlLLl....",
        "....l..lLlL.....",
        "...lLl..bL......",
        "..LlLLl.b.lL....",
        "...lL.lbblLl....",
        "......b.bLl.....",
        ".......bb.......",
        ".......b........",
        ".......b........",
        "......bb........",
        ".......b........",
        ".......b........",
    ], {"l": LEAF, "L": LEAF_L, "b": STREAK})
    save(sap, "block/mystic_sapling.png")
    # Door: dark mystic boards with a leaf-shaped window on top, two panels below.
    def door_half(top):
        im = blank(); d = im.load()
        for y in range(16):
            for x in range(16):
                if x in (0, 15) or (top and y == 0) or (not top and y == 15):
                    d[x, y] = BARK
                else:
                    d[x, y] = jitter(PLANK_D if x % 5 == 0 else PLANK, 0.08)
        if top:
            for y in range(3, 12):
                for x in range(4, 12):
                    dx, dy = x - 7.5, y - 7.5
                    if abs(dx) * 1.6 + abs(dy) * 0.9 < 5.2:
                        d[x, y] = hexc("8fe8d0") if (x + y) % 4 else hexc("c6fff0")
                    elif abs(dx) * 1.6 + abs(dy) * 0.9 < 6.2:
                        d[x, y] = STREAK_L
            for y in range(4, 11):
                d[7, y] = d[8, y] = STREAK if y > 4 else d[7, y]  # leaf vein
        else:
            for (y0, y1) in [(1, 6), (8, 13)]:
                for y in range(y0, y1 + 1):
                    for x in range(3, 13):
                        if y == y0 or x == 3:
                            d[x, y] = PLANK_L
                        elif y == y1 or x == 12:
                            d[x, y] = PLANK_D
            d[11, 2] = hexc("d8b45a"); d[11, 3] = hexc("9a7a2a")  # handle (top of the lower half)
        return im
    save(door_half(True), "block/mystic_door_top.png")
    save(door_half(False), "block/mystic_door_bottom.png")

# ================================================================== items (16x16)
GLASS = hexc("cfe9ef", 170)
GLASS_L = hexc("ffffff", 220)
GLASS_E = hexc("8fb7c0", 230)
LID = hexc("7a5230")
LID_D = hexc("54371f")

JAR = [
    "................",
    "....dLLLLLLd....",
    "....dLLLLLLd....",
    ".....eeeeee.....",
    "....e......e....",
    "...e.w......e...",
    "...e.w......e...",
    "...e.w......e...",
    "...e........e...",
    "...e........e...",
    "...e........e...",
    "...e........e...",
    "...e........e...",
    "....e......e....",
    ".....eeeeee.....",
    "................",
]

def jar_image(inside=None):
    pal = {"d": LID_D, "L": LID, "e": GLASS_E, "w": GLASS_L}
    im = art(JAR, pal); px = im.load()
    for y in range(4, 14):
        for x in range(4, 12):
            if px[x, y] == CLEAR and 3 < x < 12:
                px[x, y] = GLASS
    if inside:
        inside(px)
    return im

def items():
    save(jar_image(), "item/jar.png")
    def fairy(px):
        glow = hexc("fff6a8")
        for (x, y) in [(7, 8), (8, 8), (7, 9), (8, 9), (6, 7), (9, 7), (7, 10), (8, 10)]:
            px[x, y] = mix(glow, hexc("ffffff"), 0.3)
        px[7, 7] = hexc("9a4a2a"); px[8, 7] = hexc("9a4a2a")      # bun
        px[7, 8] = hexc("f2d2b8"); px[8, 8] = hexc("f2d2b8")      # face
        px[7, 9] = hexc("3f9a4a"); px[8, 9] = hexc("3f9a4a")      # dress
        for (x, y) in [(5, 8), (10, 8), (5, 9), (10, 9), (6, 8), (9, 8)]:
            px[x, y] = hexc("e8f4ff")                               # wings
        for (x, y) in [(5, 11), (10, 6), (9, 12)]:
            px[x, y] = glow                                         # sparkles
    save(jar_image(fairy), "item/jar_with_a_fairy.png")
    elixir = art([
        "................",
        "......kk........",
        "......cc........",
        ".....eccE.......",
        "......ee........",
        ".....e..e.......",
        "....e....e......",
        "...eRRRRRRe.....",
        "...eRrRRsRe.....",
        "...eRRRRRRe.....",
        "...eRRsRrRe.....",
        "....eRRRRe......",
        ".....eeee.......",
        "................",
        "................",
        "................",
    ], {"k": LID_D, "c": hexc("d8c49a"), "e": GLASS_E, "E": GLASS_L, "R": hexc("e04a7a"), "r": hexc("ff9ac0"), "s": hexc("fff6a8")})
    save(elixir, "item/elixir_of_life.png")
    door = art([
        "....bbbbbbbb....",
        "....bppppppb....",
        "....bpgGGgpb....",
        "....bpGggGpb....",
        "....bpgGGgpb....",
        "....bppvvppb....",
        "....bppppppb....",
        "....bbbbbbbb....",
        "....bpPPPPpb....",
        "....bpPppPpb....",
        "....bpPPPPyb....",
        "....bpppppYb....",
        "....bpPPPPpb....",
        "....bpPppPpb....",
        "....bpppppppb...",
        "....bbbbbbbb....",
    ], {"b": BARK, "p": PLANK, "P": PLANK_L, "g": hexc("8fe8d0"), "G": hexc("c6fff0"), "v": STREAK, "y": hexc("d8b45a"), "Y": hexc("9a7a2a")})
    save(door, "item/mystic_door.png")
    spawn_egg("troll", hexc("d8c49a"), hexc("4f8a3a"))
    spawn_egg("fairy", hexc("3f9a4a"), hexc("b05a32"))
    spawn_egg("evil_fairy", hexc("3e2350"), hexc("d02a2a"))
    spawn_egg("ent", BARK, STREAK_L)

# ================================================================== creatures
SKIN = hexc("d8c49a")
SKIN_D = hexc("b8a27a")
SKIN_L = hexc("ead8b0")
MOSS = hexc("4f8a3a")
MOSS_D = hexc("3a6a2a")
MOSS_L = hexc("6aa84e")

def skin(x, y, w, h):
    c = jitter(SKIN, 0.06)
    if h > 2 and y == h - 1:
        c = SKIN_D
    return c

def moss(x, y, w, h):
    r = rng.random()
    return MOSS_L if r < 0.2 else MOSS_D if r < 0.4 else jitter(MOSS, 0.08)

def moss_strands(x, y, w, h):
    """Hanging mossy strands: solid at the top, ragged gaps toward the bottom."""
    if y >= h - 2 and (x * 7 + y * 3) % 5 in (0, 2) or y == h - 1 and x % 2:
        return CLEAR
    return moss(x, y, w, h)

def troll_face(x, y, w, h):
    # Small eyes on row 4, a wide dark mouth on row 8 with light tusks at the ends; cheeks a bit darker.
    if y == 4 and x in (3, 8):
        return hexc("1a1a1a")
    if y == 3 and x in (2, 3, 8, 9):
        return SKIN_D  # heavy brow
    if y == 8:
        if x in (2, 9):
            return hexc("f2efe0")
        if 3 <= x <= 8:
            return hexc("3a1f1a")
    if x in (0, w - 1) and y > 5:
        return MOSS  # green sideburns joining the beard
    return skin(x, y, w, h)

def troll_head_side(x, y, w, h):
    if y >= h - 4 and x < 3:
        return MOSS  # sideburns
    return skin(x, y, w, h)

def troll_torso_front(x, y, w, h):
    if y == 10 and x in (6, 7):
        return hexc("5a4a32")  # navel
    if y == 4 and 2 <= x <= 11 and x not in (6, 7):
        return SKIN_D  # chest line
    return skin(x, y, w, h)

def leaf_strips(x, y, w, h):
    """Skirt of hanging leaves: vertical strips in shades of green with gaps between them at the bottom."""
    strip = (x // 2) % 3
    if y >= 2 and x % 2 == 1 and (x // 2) % 2 == 0:
        return CLEAR
    if y == h - 1 and (x + strip) % 3 == 0:
        return CLEAR
    return [MOSS, MOSS_D, MOSS_L][strip] if rng.random() > 0.15 else MOSS_D

def ear(x, y, w, h):
    return hexc("d89a8a") if 0 < y < h - 1 and x == 1 else skin(x, y, w, h)

def fist(x, y, w, h):
    return SKIN_D if y >= h - 3 and (x + y) % 3 == 0 else skin(x, y, w, h)

def troll():
    painters = {
        "head": {"side": troll_head_side, "front": troll_face, "top": moss, "back": skin, "bottom": skin},
        "nose": {"side": skin, "front": lambda x, y, w, h: SKIN_D if y == h - 1 and x in (0, 2) else SKIN_L, "top": skin, "bottom": skin},
        "ear": ear,
        "hair": {"side": moss_strands, "top": moss, "bottom": fill(CLEAR), "front": moss_strands, "back": moss_strands},
        "bangs": {"side": moss_strands, "front": moss_strands, "back": moss_strands, "top": moss, "bottom": fill(CLEAR)},
        "beard": {"side": moss_strands, "front": moss_strands, "top": moss, "bottom": moss},
        "torso": {"side": skin, "front": troll_torso_front, "top": skin, "back": skin, "bottom": skin},
        "skirt": {"side": leaf_strips, "front": leaf_strips, "back": leaf_strips, "top": fill(CLEAR), "bottom": fill(CLEAR)},
        "arm": {"side": fist, "top": skin, "bottom": fill(SKIN_D)},
        "leg": {"side": lambda x, y, w, h: hexc("8a6a42") if y >= h - 2 else skin(x, y, w, h), "bottom": fill(hexc("6a4f30"))},
    }
    paint("troll", "TrollModel.java", 128, 64, painters)

def fairy_textures():
    def kind(hair, skin_c, eye, dress, dress_l, wing, wing_e, shoes):
        def face(x, y, w, h):
            if y == 0:
                return hair  # fringe
            if y == 2 and x in (1, 3):
                return eye
            if y == 4 and x == 2:
                return shade(skin_c, 0.8)
            return jitter(skin_c, 0.04)
        def hair_f(x, y, w, h):
            return jitter(hair, 0.1)
        def hair_side(x, y, w, h):
            return jitter(hair, 0.1) if y < 3 or x >= 3 else jitter(skin_c, 0.04)
        def dress_f(x, y, w, h):
            return dress_l if y == h - 1 else jitter(dress, 0.08)
        def leg(x, y, w, h):
            return shoes if y == h - 1 else jitter(skin_c, 0.04)
        def wing_f(x, y, w, h):
            # A rounded, pixelated wing shape; transparent outside it.
            cx, cy = x / (w - 1), y / (h - 1)
            inside = (cx - 0.45) ** 2 / 0.3 + (cy - 0.45) ** 2 / 0.32 < 1.0 and not (cy > 0.85 and cx > 0.5)
            if not inside:
                return CLEAR
            edge = (cx - 0.45) ** 2 / 0.3 + (cy - 0.45) ** 2 / 0.32 > 0.6
            return wing_e if edge else wing
        return {
            "head": {"side": hair_side, "front": face, "top": hair_f, "back": hair_f, "bottom": fill(skin_c)},
            "bun": hair_f,
            "torso": dress_f,
            "skirt": {"side": dress_f, "top": fill(dress), "bottom": fill(shade(dress, 0.7))},
            "arm": lambda x, y, w, h: jitter(skin_c, 0.04),
            "leg": leg,
            "wing": wing_f,
        }
    paint("fairy", "FairyModel.java", 64, 32, kind(hexc("9a4a2a"), hexc("f2d2b8"), hexc("2fa04a"), hexc("3f9a4a"), hexc("6fc46a"),
                                                   hexc("eef8ff", 210), hexc("b8d8f0", 230), hexc("2f6a2a")))
    paint("evil_fairy", "FairyModel.java", 64, 32, kind(hexc("241a2a"), hexc("cfc0d2"), hexc("e01a1a"), hexc("3e2350"), hexc("6a2a6a"),
                                                        hexc("6a6a78", 200), hexc("3a3a44", 230), hexc("1a1a1a")))

def bark(x, y, w, h):
    """Dark wood with blue-green vertical streaks, like the mystic log."""
    k = (x * 37 + 11) % 7
    if k in (1, 4):
        return jitter(STREAK, 0.12)
    if k == 2 and rng.random() < 0.7:
        return jitter(STREAK_L, 0.1)
    return jitter(BARK if rng.random() < 0.85 else BARK_D, 0.1)

def mossy_top(x, y, w, h):
    return moss(x, y, w, h) if rng.random() < 0.6 else bark(x, y, w, h)

def leaves(x, y, w, h):
    r = rng.random()
    return LEAF_L if r < 0.2 else LEAF_D if r < 0.4 else jitter(LEAF, 0.1)

def leaves_ragged(x, y, w, h):
    """Leaf sides: irregular lower edge."""
    if y == h - 1 and (x * 5) % 3 == 0:
        return CLEAR
    return leaves(x, y, w, h)

def ent_face(x, y, w, h):
    # Narrow amber eyes under the crown, a dark crack for a mouth.
    if y == 3 and x in (1, 2, 5, 6):
        return hexc("ffb02e") if x in (2, 5) else hexc("d0701a")
    if y == 2 and x in (1, 2, 5, 6):
        return BARK_D  # brow ridge shadow
    if y == 6 and 2 <= x <= 5:
        return BARK_D
    return bark(x, y, w, h)

def ent_torso_front(x, y, w, h):
    # A knot in the chest.
    if (x - 9) ** 2 + (y - 6) ** 2 <= 2:
        return BARK_D if (x - 9) ** 2 + (y - 6) ** 2 == 0 else STREAK
    return bark(x, y, w, h)

def ent():
    painters = {
        "torso": {"side": bark, "front": ent_torso_front, "top": mossy_top, "bottom": bark},
        "head": {"side": bark, "front": ent_face, "top": leaves, "bottom": bark},
        "crown1": {"side": leaves_ragged, "top": leaves, "bottom": leaves},
        "crown2": {"side": leaves_ragged, "top": leaves, "bottom": leaves},
        "crown3": {"side": leaves, "top": leaves, "bottom": leaves},
        "shoulder": {"side": bark, "top": mossy_top, "bottom": bark},
        "upper_arm": bark, "forearm": {"side": bark, "bottom": fill(BARK_D)},
        "twig": bark, "twig_leaves": leaves,
        "leg": bark,
        "roots": {"side": bark, "top": mossy_top, "bottom": fill(BARK_D)},
    }
    paint("ent", "EntModel.java", 128, 128, painters)

def main():
    blocks(); items()
    troll(); fairy_textures(); ent()
    print("stage 8 textures generated")

if __name__ == "__main__":
    main()
