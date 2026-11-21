#!/usr/bin/env python3
"""Stage 9 textures for Crazy Alloy: Revival (0.8.0): the Mushroom Depths.

Creatures: the Mushroom Man (thin, dark body, big red cap with light spots, two light yellow rectangular eyes), the
Mushroom Cyclops (bulky, very dark blue cap, one big square yellow eye, a mouth line, segmented arms, dark fists) and
the Mushroomcopter (purple cubic cap with big spots, dark face, slanted light eyes, a propeller). Each one also gets a
NAME_eyes.png holding only its eyes, drawn by the glowing-eyes layer.
Items: Mushroom Spore, Mushroom Sword, Mushroom Shield (32x32 sheet), the six elemental books and three spawn eggs.
Original art drawn in code from the project owner's written descriptions; no pixels copied from anywhere.
Run by tools/gen_textures.py after the stage 8 textures.
"""
import random
from gen_textures_v2 import hexc, shade, mix, art, save, blank
from gen_mobs_v3 import paint, fill
from gen_textures_v6 import spawn_egg

rng = random.Random(90)
CLEAR = (0, 0, 0, 0)


def jitter(c, amount=0.08):
    return shade(c, 1.0 - amount / 2 + amount * rng.random())


RED = hexc("c0262a"); RED_D = hexc("8e1a1e"); RED_L = hexc("dc3a36")
SPOT = hexc("f2e8d8")
BODY = hexc("2b2622"); BODY_D = hexc("1a1714"); BODY_L = hexc("3b342e")
GILL = hexc("d8c8a8"); GILL_D = hexc("b8a888")
EYE_Y = hexc("f6f0a0"); EYE_YL = hexc("fffbd0")
NAVY = hexc("1c2448"); NAVY_D = hexc("10162e"); NAVY_L = hexc("2c3868")
PURPLE = hexc("7a3aa8"); PURPLE_D = hexc("552478"); PURPLE_L = hexc("9a58c8")
STEM = hexc("e2dccb"); STEM_D = hexc("c4bca8")
BROWN = hexc("8a6a4a"); BROWN_D = hexc("6a4e34")


def spotted(base, light, dark, spot, density=0.12, size=2):
    """A cap surface with square light spots laid on a coarse grid."""
    cells = {}
    def f(x, y, w, h):
        key = (w, h, x // (size + 1), y // (size + 1))
        if key not in cells:
            cells[key] = rng.random() < density * 2.2
        if cells[key] and x % (size + 1) < size and y % (size + 1) < size:
            return jitter(spot, 0.04)
        r = rng.random()
        return light if r < 0.1 else dark if r < 0.25 else jitter(base, 0.08)
    return f


def dark_body(x, y, w, h):
    r = rng.random()
    return BODY_L if r < 0.12 else BODY_D if r < 0.3 else jitter(BODY, 0.1)


def gills(x, y, w, h):
    """Cap underside: pale radiating gill lines."""
    return GILL_D if (x + y) % 3 == 0 else jitter(GILL, 0.05)


def cap_side(base, dark, spot):
    sp = spotted(base, shade(base, 1.15), dark, spot, 0.1, 1)
    def f(x, y, w, h):
        return jitter(dark, 0.06) if y == h - 1 else sp(x, y, w, h)
    return f


# ================================================================== Mushroom Man (64x64)
def mushroom_man(eyes_only=False):
    eye = lambda x, y, w, h: EYE_YL if y == 0 and x == 0 else EYE_Y
    face = lambda x, y, w, h: BODY_D if y == 0 else dark_body(x, y, w, h)
    red_top = spotted(RED, RED_L, RED_D, SPOT, 0.14, 2)
    painters = {
        "head": {"side": dark_body, "front": face, "back": dark_body, "top": fill(BODY_D), "bottom": fill(BODY_D)},
        "eye": {"side": fill(shade(EYE_Y, 0.8)), "front": eye, "back": eye, "top": eye, "bottom": eye},
        "cap_brim": {"side": cap_side(RED, RED_D, SPOT), "top": red_top, "bottom": gills},
        "cap_top": {"side": cap_side(RED, RED_D, SPOT), "top": red_top, "bottom": fill(RED_D)},
        "cap_dome": {"side": cap_side(RED, RED_D, SPOT), "top": red_top, "bottom": fill(RED_D)},
        "torso": {"side": dark_body, "front": lambda x, y, w, h: BODY_L if x == w // 2 and y > 1 else dark_body(x, y, w, h),
                  "top": dark_body, "bottom": fill(BODY_D)},
        "arm": {"side": lambda x, y, w, h: BODY_D if y >= h - 2 else dark_body(x, y, w, h), "bottom": fill(BODY_D)},
        "leg": {"side": lambda x, y, w, h: BODY_D if y >= h - 2 else dark_body(x, y, w, h), "bottom": fill(BODY_D)},
    }
    if eyes_only:
        paint("mushroom_man_eyes", "MushroomManModel.java", 64, 64, {"eye": painters["eye"]}, only=lambda k: k == "eye")
    else:
        paint("mushroom_man", "MushroomManModel.java", 64, 64, painters)


# ================================================================== Mushroom Cyclops (128x64)
def mushroom_cyclops(eyes_only=False):
    def big_eye(x, y, w, h):
        if x in (1, 2) and y in (1, 2):
            return hexc("2a2410") if (x, y) == (2, 2) else hexc("4a3e18")   # a small dark pupil
        return EYE_YL if y == 0 else jitter(hexc("f2d23a"), 0.05)
    navy_top = spotted(NAVY, NAVY_L, NAVY_D, hexc("6878a8"), 0.06, 1)
    seg = lambda base: (lambda x, y, w, h: BODY_D if y % 4 == 3 else base(x, y, w, h))
    painters = {
        "head": {"side": dark_body, "front": lambda x, y, w, h: BODY_D if y == 0 else dark_body(x, y, w, h),
                 "back": dark_body, "top": fill(BODY_D), "bottom": fill(BODY_D)},
        "eye": {"side": fill(hexc("c8a828")), "front": big_eye, "back": big_eye, "top": fill(hexc("c8a828")), "bottom": fill(hexc("c8a828"))},
        "mouth": fill(hexc("0e0c0a")),
        "cap": {"side": cap_side(NAVY, NAVY_D, hexc("6878a8")), "top": navy_top, "bottom": lambda x, y, w, h: jitter(hexc("3a4268"), 0.06) if (x + y) % 3 else hexc("2a3050")},
        "cap_top": {"side": cap_side(NAVY, NAVY_D, hexc("6878a8")), "top": navy_top, "bottom": fill(NAVY_D)},
        "chest": {"side": dark_body, "front": lambda x, y, w, h: BODY_L if y in (2, 3) and 1 < x < w - 2 and x % 3 else dark_body(x, y, w, h),
                  "top": dark_body, "bottom": fill(BODY_D)},
        "waist": {"side": seg(dark_body), "top": dark_body, "bottom": fill(BODY_D)},
        "upper_arm": {"side": seg(dark_body), "top": dark_body, "bottom": fill(BODY_D)},
        "forearm": {"side": seg(dark_body), "top": fill(BODY_D), "bottom": fill(BODY_D)},
        "fist": {"side": lambda x, y, w, h: jitter(hexc("121010"), 0.1), "bottom": fill(hexc("0a0808"))},
        "leg": {"side": lambda x, y, w, h: BODY_D if y >= h - 2 or y % 5 == 4 else dark_body(x, y, w, h), "bottom": fill(BODY_D)},
    }
    if eyes_only:
        paint("mushroom_cyclops_eyes", "MushroomCyclopsModel.java", 128, 64, {"eye": painters["eye"]}, only=lambda k: k == "eye")
    else:
        paint("mushroom_cyclops", "MushroomCyclopsModel.java", 128, 64, painters)


# ================================================================== Mushroomcopter (64x64)
def mushroomcopter(eyes_only=False):
    def slanted(x, y, w, h):
        return hexc("f0f8ff") if x == 0 else hexc("b8e8ff")   # brighter at the inner end: a slanted look
    purple_cap = spotted(PURPLE, PURPLE_L, PURPLE_D, hexc("f0e2f8"), 0.16, 3)
    painters = {
        "cap": {"side": lambda x, y, w, h: jitter(PURPLE_D, 0.06) if y == h - 1 else purple_cap(x, y, w, h),
                "top": purple_cap, "bottom": gills},
        "face": {"side": dark_body, "front": lambda x, y, w, h: BODY_D if y == h - 1 else dark_body(x, y, w, h),
                 "top": fill(BODY_D), "bottom": fill(BODY_D)},
        "eye": {"side": fill(hexc("90c8e0")), "front": slanted, "back": slanted, "top": slanted, "bottom": slanted},
        "rod": fill(hexc("4a4a52")),
        "hub": fill(hexc("2e2e34")),
        "blade": {"side": lambda x, y, w, h: hexc("5a5a64"), "top": lambda x, y, w, h: hexc("8a8a96") if x in (0, w - 1) else jitter(hexc("70707c"), 0.05),
                  "bottom": fill(hexc("4a4a52"))},
    }
    if eyes_only:
        paint("mushroomcopter_eyes", "MushroomcopterModel.java", 64, 64, {"eye": painters["eye"]}, only=lambda k: k == "eye")
    else:
        paint("mushroomcopter", "MushroomcopterModel.java", 64, 64, painters)


# ================================================================== items (16x16, shield 32x32)
def items():
    k = hexc("1e1418")
    save(art([
        "................",
        "................",
        ".......pp.......",
        ".....pPPPp......",
        "....pPLPPPp.....",
        "...pPPPPPLPp....",
        "...pPLPPPPPp....",
        "..pPPPPPLPPPp...",
        "..pPPPLPPPPPp...",
        "...pPPPPPPPp....",
        "....pPPPLPp.....",
        ".....pppPp......",
        ".......pp.......",
        "................",
        "................",
        "................",
    ], {"p": PURPLE_D, "P": PURPLE, "L": hexc("e6d2f2")}), "item/mushroom_spore.png")
    save(art([
        "............kkk.",
        "...........kRSRk",
        "..........kRSRRk",
        ".........kRSRRk.",
        "........kRSRRk..",
        ".......kRSRRk...",
        "......kRSRRk....",
        "..k..kRSRRk.....",
        "..kkkRSRRk......",
        "...kGkRRk.......",
        "...kkGkk........",
        "...kHkGk........",
        "..kHkkkGk.......",
        ".kHk....k.......",
        "kHk.............",
        "kk..............",
    ], {"k": k, "R": RED, "S": SPOT, "G": STEM_D, "H": BROWN}), "item/mushroom_sword.png")
    shield()
    books()
    spawn_egg("mushroom_man", BODY, RED)
    spawn_egg("mushroom_cyclops", NAVY, hexc("f2d23a"))
    spawn_egg("mushroomcopter", PURPLE, hexc("f0e2f8"))


def shield():
    im = blank(32, 32); px = im.load()
    face = spotted(RED, RED_L, RED_D, SPOT, 0.16, 2)
    for y in range(22):          # front: a red spotted cap with a pale stem rim
        for x in range(12):
            edge = x in (0, 11) or y in (0, 21)
            px[x, y] = jitter(STEM_D, 0.05) if edge else face(x, y, 12, 22)
    for y in range(22):          # back: stem-coloured boards
        for x in range(12, 24):
            px[x, y] = jitter(STEM_D if (x - 12) % 4 == 0 else STEM, 0.05)
    for y in range(22):          # edges
        for x in range(24, 26):
            px[x, y] = jitter(STEM_D, 0.05)
    for x in range(12):          # top and bottom
        px[x, 22] = jitter(STEM_D, 0.05)
        px[x, 23] = jitter(STEM_D, 0.05)
    for y in range(12):          # handle
        for x in range(26, 32):
            px[x, y] = jitter(BROWN if y % 3 else BROWN_D, 0.06)
    save(im, "item/mushroom_shield.png")


def books():
    rows = [
        "................",
        "...kkkkkkkkkk...",
        "..kCCCCCCCCCPk..",
        "..kCCCCCCCCCPk..",
        "..kCCCSSSCCCPk..",
        "..kCCSEESSCCPk..",
        "..kCCSEEESCCPk..",
        "..kCCSSEESCCPk..",
        "..kCCCSSSCCCPk..",
        "..kCCCCCCCCCPk..",
        "..kCCCCCCCCCPk..",
        "..kDDDDDDDDDPk..",
        "..kWWWWWWWWWWk..",
        "...kkkkkkkkkk...",
        "................",
        "................",
    ]
    themes = {
        "fire_book": (hexc("a02818"), hexc("f08a1a"), hexc("ffd040")),
        "water_book": (hexc("1e4aa0"), hexc("4aa0e8"), hexc("c0ecff")),
        "poison_book": (hexc("3a6a1e"), hexc("8ac83a"), hexc("d8f8a0")),
        "air_book": (hexc("8aa0b0"), hexc("d8e8f0"), hexc("ffffff")),
        "lightning_book": (hexc("3a2a6a"), hexc("e8d830"), hexc("fffbc0")),
        "earth_book": (hexc("5a3a1e"), hexc("8a6a3a"), hexc("5aa03a")),
    }
    for name, (cover, sym, glow) in themes.items():
        pal = {"k": hexc("1a1410"), "C": cover, "D": shade(cover, 0.75), "P": hexc("e8dcc0"), "W": hexc("d8ccb0"),
               "S": sym, "E": glow}
        save(art(rows, pal), f"item/{name}.png")


def main():
    mushroom_man(); mushroom_man(True)
    mushroom_cyclops(); mushroom_cyclops(True)
    mushroomcopter(); mushroomcopter(True)
    items()
    print("stage 9 textures generated")


if __name__ == "__main__":
    main()
