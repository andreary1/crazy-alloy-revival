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
    # 0.8.0: matched to the owner's description of the reference: light yellow liquid, a light outline, a small cap and
    # sparkles around the bottle.
    elixir = art([
        "..*.............",
        "......kk.....*..",
        "......cc........",
        ".....eccE.......",
        "......ee........",
        ".*...e..e.......",
        "....e....e......",
        "...eYYYYYYe.....",
        "...eYyYYsYe..*..",
        "...eYYYYYYe.....",
        "...eYYsYyYe.....",
        "....eYYYYe......",
        ".....eeee.......",
        "..........*.....",
        "................",
        "................",
    ], {"k": LID_D, "c": hexc("d8c49a"), "e": hexc("e8f4f4"), "E": hexc("ffffff"), "Y": hexc("f2e48a"), "y": hexc("fff6c0"),
        "s": hexc("ffffff"), "*": hexc("fffbd8")})
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
    spawn_egg("shadow_eye", hexc("4a2a6a"), hexc("f08a1a"))
    spawn_egg("shadow_wanderer", hexc("2a1638"), hexc("6a3a9a"))

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

# ================================================================== shadow creatures
VIOLET = hexc("6a3a9a")
VIOLET_L = hexc("8a52c0")
VIOLET_D = hexc("4a2672")
SHADOW = hexc("2a1638")
SHADOW_D = hexc("150b1e")
EYE_O = hexc("f08a1a")
EYE_L = hexc("ffc04a")
EYE_D = hexc("b8560e")

def violet(x, y, w, h):
    """Purple surface with small differences in tone."""
    r = rng.random()
    return VIOLET_L if r < 0.12 else VIOLET_D if r < 0.3 else jitter(VIOLET, 0.08)

def violet_side(x, y, w, h):
    # Lighter top rows, darker bottom rows, to read the volume.
    if y <= 1:
        return jitter(VIOLET_L, 0.06)
    if y >= h - 2:
        return jitter(VIOLET_D, 0.06)
    return violet(x, y, w, h)

def shadow_purple(x, y, w, h):
    r = rng.random()
    return VIOLET_D if r < 0.25 else SHADOW_D if r < 0.4 else jitter(SHADOW, 0.1)

def iris(x, y, w, h):
    """Angular orange eye: a bright ring with darker corners cut off, lighter toward the top."""
    corner = (x in (0, w - 1)) and (y in (0, w - 1))
    if corner:
        return VIOLET_D
    if x in (0, w - 1) or y in (0, h - 1):
        return EYE_D
    return EYE_L if y <= 1 else jitter(EYE_O, 0.06)

def shadow_eye():
    painters = {
        "core": {"side": violet_side, "front": violet_side, "back": violet_side, "top": violet, "bottom": fill(VIOLET_D)},
        "step": {"side": fill(VIOLET_L), "top": violet, "bottom": fill(VIOLET_D)},
        "crest": {"side": fill(VIOLET_L), "top": lambda x, y, w, h: jitter(VIOLET_L, 0.08), "bottom": fill(VIOLET_D)},
        "iris": {"side": fill(EYE_D), "front": iris, "back": fill(EYE_D), "top": fill(EYE_D), "bottom": fill(EYE_D)},
        "pupil": fill(hexc("12080e")),
        "ring": lambda x, y, w, h: jitter(hexc("3a1a52"), 0.12),
    }
    paint("shadow_eye", "ShadowEyeModel.java", 64, 64, painters)

def wanderer_torso_front(x, y, w, h):
    # Nearly black down the front, with a faint ribbed edge.
    if x in (0, w - 1):
        return shadow_purple(x, y, w, h)
    if y % 3 == 1 and 1 < x < w - 2:
        return hexc("1e1028")
    return jitter(SHADOW_D, 0.1)

def snout_front(x, y, w, h):
    return SHADOW_D if y == h - 1 else shadow_purple(x, y, w, h)

def jaw_top(x, y, w, h):
    # The inside of the open mouth: near black, with a dim violet tongue line.
    return hexc("3a1a52") if x in (1, 2) and y > 1 else hexc("0c0610")

def dark_tip(base):
    def f(x, y, w, h):
        return SHADOW_D if y >= h - 2 else base(x, y, w, h)
    return f

def shadow_wanderer():
    painters = {
        "torso": {"side": shadow_purple, "front": wanderer_torso_front, "back": shadow_purple, "top": shadow_purple, "bottom": shadow_purple},
        "spine": {"side": fill(VIOLET_D), "top": fill(VIOLET), "bottom": fill(SHADOW)},
        "head": {"side": shadow_purple, "front": lambda x, y, w, h: SHADOW_D if y >= h - 2 and 1 < x < w - 2 else shadow_purple(x, y, w, h),
                 "top": shadow_purple, "bottom": fill(SHADOW_D)},
        "snout": {"side": shadow_purple, "front": snout_front, "top": shadow_purple, "bottom": fill(hexc("0c0610"))},
        "jaw": {"side": shadow_purple, "front": shadow_purple, "top": jaw_top, "bottom": shadow_purple},
        "spike": {"side": lambda x, y, w, h: VIOLET if y <= 1 else shadow_purple(x, y, w, h), "top": fill(VIOLET)},
        "thigh": shadow_purple, "shin": shadow_purple,
        "foot": {"side": dark_tip(shadow_purple), "front": fill(SHADOW_D), "top": shadow_purple, "bottom": fill(SHADOW_D)},
        "upper_arm": shadow_purple, "forearm": shadow_purple,
        "palm": shadow_purple,
        "finger": {"side": dark_tip(shadow_purple), "bottom": fill(hexc("0c0610"))},
    }
    paint("shadow_wanderer", "ShadowWandererModel.java", 64, 64, painters)

# ================================================================== small mushrooms (block textures)
def mystic_mushrooms():
    """Purple and Blue Mushroom: a cap top with light square spots, a cap side band and a beige stem."""
    def cap(base, light, dark, spot, edge_line):
        top = blank(); px = top.load()
        for y in range(16):
            for x in range(16):
                c = jitter(base, 0.08)
                # stepped rings: the outer ring (brim) darker, the middle step lighter
                ring = min(x, y, 15 - x, 15 - y)
                if ring == 3:
                    c = dark
                elif ring == 4 and edge_line:
                    c = edge_line
                elif ring >= 6:
                    c = jitter(light, 0.06)
                px[x, y] = c
        for (x, y) in [(5, 5), (10, 4), (7, 9), (11, 10), (4, 11), (8, 6), (12, 7)]:
            px[x, y] = spot
        side = blank(); px = side.load()
        for y in range(16):
            for x in range(16):
                c = jitter(base, 0.08)
                if y == 8 or y == 11:
                    c = dark                     # underside edge and the top of the brim
                if edge_line and y == 7:
                    c = edge_line                # a lighter line along the step
                px[x, y] = c
        for (x, y) in [(4, 9), (9, 10), (13, 9), (6, 7)]:
            px[x, y] = spot
        return top, side
    for name, base, light, dark, spot, edge in [
            ("purple", hexc("7a1a9a"), hexc("9a2ab8"), hexc("4a0e5e"), hexc("f0e6f2"), None),
            ("blue", hexc("1a2a8a"), hexc("2a3ca8"), hexc("10185a"), hexc("dfe4ee"), hexc("3e5ac8"))]:
        top, side = cap(base, light, dark, spot, edge)
        save(top, f"block/{name}_mushroom_top.png")
        save(side, f"block/{name}_mushroom_side.png")
    stem = blank(); px = stem.load()
    for y in range(16):
        for x in range(16):
            px[x, y] = jitter(hexc("d8c8b0") if y < 12 else hexc("c4b49a"), 0.05)
    save(stem, "block/mystic_mushroom_stem.png")

# ================================================================== Wizard and Dwarf
ROBE = hexc("2a4aa8")
ROBE_L = hexc("3a5cc0")
ROBE_D = hexc("1e3680")
STAR = hexc("f2f4ff")
PALE = hexc("f0d8c0")
BEARD_W = hexc("eeeeea")
BEARD_G = hexc("d4d4cc")

def robe(stars=0.06):
    def f(x, y, w, h):
        if (x * 5 + y * 3) % 11 == 0 and rng.random() < 0.7 or rng.random() < stars * 0.3:
            return STAR
        return jitter(ROBE if rng.random() < 0.8 else ROBE_L, 0.06)
    return f

def robe_band(x, y, w, h):
    # horizontal bands separate the top of the robe from the skirt
    if y in (h - 2,):
        return ROBE_D
    return robe()(x, y, w, h)

def wizard_face(x, y, w, h):
    # white hair framing the face, blue eyes, pale skin, white moustache joining the beard
    if x in (0, w - 1) or y == 0:
        return jitter(BEARD_W, 0.05)
    if y == 3 and x in (2, 5):
        return hexc("2a4ad8")
    if y == 2 and x in (2, 5):
        return BEARD_W  # bushy brows
    if y >= 5 and 1 <= x <= 6:
        return jitter(BEARD_W, 0.05) if not (y == 5 and x in (3, 4)) else hexc("c89a88")
    return jitter(PALE, 0.04)

def white_hair(x, y, w, h):
    return jitter(BEARD_W if rng.random() < 0.7 else BEARD_G, 0.04)

def wizard():
    beard = lambda x, y, w, h: jitter(BEARD_W if (x + y) % 3 else BEARD_G, 0.04)
    painters = {
        "head": {"side": lambda x, y, w, h: white_hair(x, y, w, h) if y >= 2 or x < 2 else jitter(PALE, 0.04),
                 "front": wizard_face, "back": white_hair, "top": white_hair, "bottom": fill(BEARD_G)},
        "beard": beard, "beard2": beard, "beard3": beard,
        "robe_top": {"side": robe(), "front": robe(), "back": robe(), "top": robe(), "bottom": fill(ROBE_D)},
        "robe_skirt": {"side": robe_band, "front": robe_band, "back": robe_band, "top": robe(), "bottom": fill(ROBE_D)},
        "sleeve": {"side": robe(), "top": robe(), "bottom": fill(ROBE_D)},
        "cuff": {"side": lambda x, y, w, h: ROBE_L if y == 0 else robe()(x, y, w, h), "top": robe(), "bottom": fill(ROBE_D)},
        "hand": fill(PALE),
        "brim": {"side": fill(ROBE_D), "top": robe(), "bottom": fill(ROBE_D)},
        "hat1": {"side": robe(), "top": robe(), "bottom": fill(ROBE_D)},
        "hat2": {"side": robe(), "top": robe(), "bottom": fill(ROBE_D)},
        "hat3": {"side": robe(), "top": robe(), "bottom": fill(ROBE_D)},
        "hat4": {"side": robe(), "top": fill(ROBE_L), "bottom": fill(ROBE_D)},
    }
    paint("wizard", "WizardModel.java", 128, 64, painters)

GINGER = hexc("d0661a")
GINGER_D = hexc("a8480e")
GINGER_L = hexc("e8842a")
SLEEVE_R = hexc("a8202a")
VEST = hexc("6a4428")
TROUSER = hexc("3a3a40")
BOOT = hexc("5a3418")
CAP_D = hexc("2a2224")

def ginger(x, y, w, h):
    # vertical strands
    return [GINGER, GINGER_D, GINGER_L][(x + (y // 3)) % 3] if rng.random() > 0.15 else GINGER_D

def dwarf_face(x, y, w, h):
    if y <= 1:
        return CAP_D if y == 0 else jitter(GINGER_D, 0.05)
    if x in (0, w - 1) and y >= 2:
        return ginger(x, y, w, h)  # hair at the sides
    if y == 4 and x in (2, 6):
        return hexc("2a4ad8")
    if y == 4 and x in (1, 3, 5, 7):
        return hexc("f4f4f4") if x in (3, 5) else jitter(PALE, 0.04)
    if y >= 6:
        if y == 6 and x in (4,):
            return hexc("e8c8b0")  # the light patch at the mouth
        return ginger(x, y, w, h)
    if y == 5 and x == 4:
        return shade(PALE, 0.85)  # nose
    return jitter(PALE, 0.04)

def dwarf():
    painters = {
        "head": {"side": lambda x, y, w, h: ginger(x, y, w, h) if y >= 2 else jitter(GINGER_D, 0.05),
                 "front": dwarf_face, "back": ginger, "top": fill(GINGER_D), "bottom": ginger},
        "beard": ginger, "beard_low": ginger,
        "braid": lambda x, y, w, h: jitter(hexc("6a3a1a"), 0.08),
        "body": {"side": lambda x, y, w, h: SLEEVE_R if y < 5 else TROUSER,
                 "front": lambda x, y, w, h: jitter(VEST, 0.06) if y < 6 else (hexc("2a1a10") if y == 6 else TROUSER),
                 "back": lambda x, y, w, h: jitter(SLEEVE_R, 0.06) if y < 6 else TROUSER, "top": fill(SLEEVE_R), "bottom": fill(TROUSER)},
        "arm": {"side": lambda x, y, w, h: jitter(PALE, 0.04) if y >= h - 2 else jitter(SLEEVE_R, 0.06),
                "top": fill(SLEEVE_R), "bottom": fill(PALE)},
        "leg": {"side": lambda x, y, w, h: jitter(BOOT, 0.06) if y >= h - 2 else jitter(TROUSER, 0.06), "bottom": fill(BOOT)},
        "cap": {"side": lambda x, y, w, h: jitter(CAP_D, 0.08), "top": lambda x, y, w, h: jitter(CAP_D, 0.08), "bottom": fill(CAP_D)},
        "visor": fill(hexc("1e181a")),
        "lamp": {"side": fill(hexc("b89a10")), "front": fill(hexc("f2e030"))},
    }
    paint("dwarf", "DwarfModel.java", 64, 64, painters)

def main():
    blocks(); items()
    troll(); fairy_textures(); ent(); shadow_eye(); shadow_wanderer()
    mystic_mushrooms(); wizard(); dwarf()
    spawn_egg("wizard", ROBE, STAR)
    spawn_egg("dwarf", SLEEVE_R, GINGER)
    print("stage 8 textures generated")

if __name__ == "__main__":
    main()
