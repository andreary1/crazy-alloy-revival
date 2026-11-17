#!/usr/bin/env python3
"""Stage 7 textures for Crazy Alloy: Revival: the Bank (Overworld tab).

Block: the Mineral Converter (front with coin slot, "$" display and cash drawer; riveted sides; a grille on top).
Items: Money, Coin, AK47, AK47 Ammo and the Guard Dinosaur and Segway spawn eggs. GUI: the Mineral Converter screen.
Creatures: the Guard Dinosaur and the Segway, painted region by region from the "// tex:" tags in their Java models
(same method as gen_mobs_v3.py).
Original art drawn in code from the project owner's written descriptions; no pixels copied from anywhere.
Run by tools/gen_textures.py after the stage 6 textures.
"""
import random
from PIL import Image, ImageDraw
from gen_textures_v2 import hexc, shade, mix, art, save, blank
from gen_mobs_v3 import paint, fill
from gen_textures_v6 import spawn_egg

rng = random.Random(70)

STEEL = hexc("6f7a80")
STEEL_D = hexc("4a5358")
STEEL_L = hexc("9aa6ac")
GOLD = hexc("e8c25a")
GOLD_D = hexc("b8902a")
GOLD_L = hexc("fff0a8")
CASH = hexc("6fbf73")
CASH_D = hexc("3f8a4a")
CASH_L = hexc("bfe8b0")

# ================================================================== Mineral Converter block (16x16)
def steel(x, y, seed=0):
    return shade(STEEL, 0.93 + 0.1 * rng.random())

def converter():
    # Side: two riveted steel panels with a gold band in the middle.
    im = blank(); px = im.load()
    for y in range(16):
        for x in range(16):
            c = steel(x, y)
            if x in (0, 15) or y in (0, 15):
                c = STEEL_D
            elif y in (7, 8):
                c = GOLD if y == 7 else GOLD_D
            elif x in (1,) or y in (1, 9):
                c = shade(STEEL_L, 0.95)
            px[x, y] = c
    for (x, y) in [(2, 2), (13, 2), (2, 13), (13, 13), (2, 5), (13, 5), (2, 11), (13, 11)]:
        px[x, y] = STEEL_L
    save(im, "block/mineral_converter_side.png")

    # Top: a steel lid with a grille where the materials drop in.
    im = blank(); px = im.load()
    for y in range(16):
        for x in range(16):
            c = steel(x, y)
            if x in (0, 15) or y in (0, 15):
                c = STEEL_D
            elif 3 <= x <= 12 and 3 <= y <= 12:
                c = hexc("2b3033") if (x + y) % 2 == 0 or y % 3 == 0 else STEEL_D
            elif x in (2, 13) and 2 <= y <= 13 or y in (2, 13) and 2 <= x <= 13:
                c = GOLD_D
            px[x, y] = c
    save(im, "block/mineral_converter_top.png")

    # Front: coin slot at the top, a green "$" display, and the cash drawer with a handle at the bottom.
    rows = [
        "kkkkkkkkkkkkkkkk",
        "kSSSSSSSSSSSSSSk",
        "kSSSSgGGGGgSSSSk",
        "kSSSSgbbbbgSSSSk",
        "kSSSSSSSSSSSSSSk",
        "kSdddddddddddSSk",
        "kSdeeeeEeeeedSSk",
        "kSdeeeEEEeeedSrk",
        "kSdeeeEeeeeedSSk",
        "kSdeeeEEEeeedSSk",
        "kSdeeeeeEeeedSSk",
        "kSdeeeEEEeeedSSk",
        "kSdddddEddddddSk",
        "kgggggggggggggSk",
        "kGGGGGhhhhGGGGGk",
        "kkkkkkkkkkkkkkkk",
    ]
    pal = {"k": STEEL_D, "S": STEEL, "g": GOLD_D, "G": GOLD, "b": hexc("15181a"), "d": hexc("2b3033"),
           "e": hexc("1f4a2a"), "E": hexc("7cff8a"), "r": hexc("ff4a4a"), "h": hexc("3a2a14")}
    im = art(rows, pal); px = im.load()
    for y in range(16):
        for x in range(16):
            if px[x, y] == STEEL:
                px[x, y] = steel(x, y)
    save(im, "block/mineral_converter_front.png")

# ================================================================== items (16x16)
def items():
    money = [
        "................",
        "................",
        "...kkkkkkkkkk...",
        "..kLLLLLLLLLLk..",
        "..kLCCCCCCCCLk..",
        ".kkLCCDDDCCCLk..",
        "kLLLLLLLLLLLLLk.",
        "kLCCCCCCCCCCCLk.",
        "kLCCDDCCCCdCCLk.",
        "kLCDCCDCCCCCCLk.",
        "kLCCDDCCC$$CCLk.",
        "kLCCCCCCCCCCCLk.",
        "kLLLLLLLLLLLLLk.",
        ".kkkkkkkkkkkkk..",
        "................",
        "................",
    ]
    save(art(money, {"k": hexc("2b5a2f"), "L": CASH_L, "C": CASH, "D": CASH_D, "d": CASH_D, "$": hexc("1f4a2a")}), "item/money.png")
    coin = [
        "................",
        "................",
        ".....kkkkkk.....",
        "....kGGGGGGk....",
        "...kGLLGGGGDk...",
        "..kGLGGdGGGGDk..",
        "..kGGGddddGGDk..",
        "..kGGGdGGGGGDk..",
        "..kGGGddddGGDk..",
        "..kGGGGGGdGGDk..",
        "..kGGGddddGGDk..",
        "...kGGGdGGGDk...",
        "....kDDDDDDk....",
        ".....kkkkkk.....",
        "................",
        "................",
    ]
    save(art(coin, {"k": hexc("7a5a14"), "G": GOLD, "L": GOLD_L, "D": GOLD_D, "d": hexc("9a7418")}), "item/coin.png")
    # AK47, pointing up-right: wooden stock and grip, black receiver and barrel, curved magazine, wooden handguard.
    ak = [
        "...............b",
        "..............bk",
        ".............bk.",
        "............wk..",
        "...........ww...",
        "..........ww....",
        ".........kkk....",
        "........kkkk....",
        ".......kkkm.....",
        "......kkkmm.....",
        "....wwkk..mm....",
        "...wwwk...m.....",
        "..wwwgw.........",
        ".wwwwg..........",
        "wwww............",
        "ww..............",
    ]
    save(art(ak, {"b": hexc("1b1b1b"), "k": hexc("2e2e2e"), "w": hexc("8a4f22"), "g": hexc("5e3414"), "m": hexc("3c3c3c")}), "item/ak47.png")
    ammo = [
        "................",
        "................",
        "....c...........",
        "...cBc..c.......",
        "...BBB.cBc......",
        "...BYB.BBB..c...",
        "...BYB.BYB.cBc..",
        "...BYB.BYB.BBB..",
        "...BYB.BYB.BYB..",
        "...BYB.BYB.BYB..",
        "...BYB.BYB.BYB..",
        "...DDD.BYB.BYB..",
        ".......DDD.BYB..",
        "...........DDD..",
        "................",
        "................",
    ]
    save(art(ammo, {"c": hexc("b87333"), "B": hexc("d9a441"), "Y": hexc("f3d27a"), "D": hexc("8a6420")}), "item/ak47_ammo.png")
    spawn_egg("guard_dinosaur", hexc("4f9a3c"), hexc("c62828"))
    spawn_egg("segway", hexc("3a3f44"), hexc("d6dadc"))

# ================================================================== GUI (176x166 in a 256x256 sheet)
def gui():
    im = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    W, H = 176, 166
    bg, light, darkc, black = hexc("c6c6c6"), hexc("ffffff"), hexc("555555"), hexc("000000")
    d.rectangle([0, 0, W - 1, H - 1], fill=bg)
    d.line([(0, 0), (W - 2, 0)], fill=black); d.line([(0, 0), (0, H - 2)], fill=black)
    d.line([(W - 1, 1), (W - 1, H - 1)], fill=black); d.line([(1, H - 1), (W - 1, H - 1)], fill=black)
    d.line([(1, 1), (W - 3, 1)], fill=light); d.line([(1, 1), (1, H - 3)], fill=light)
    d.line([(W - 2, 2), (W - 2, H - 2)], fill=darkc); d.line([(2, H - 2), (W - 2, H - 2)], fill=darkc)

    def slot(x, y, size=18):
        d.rectangle([x, y, x + size - 1, y + size - 1], fill=hexc("8b8b8b"))
        d.line([(x, y), (x + size - 2, y)], fill=hexc("373737")); d.line([(x, y), (x, y + size - 2)], fill=hexc("373737"))
        d.line([(x + 1, y + size - 1), (x + size - 1, y + size - 1)], fill=light); d.line([(x + size - 1, y + 1), (x + size - 1, y + size - 1)], fill=light)
    slot(34, 34)            # material (item at 35,35)
    slot(106, 25)           # money (item at 107,26)
    slot(132, 25)           # coins (item at 133,26)
    for r in range(3):
        for c in range(9):
            slot(7 + c * 18, 83 + r * 18)
    for c in range(9):
        slot(7 + c * 18, 141)
    # Arrow from the material to the outputs.
    d.polygon([(58, 39), (76, 39), (76, 35), (84, 43), (76, 51), (76, 47), (58, 47)], fill=hexc("8b8b8b"))
    # Gold band with "$" marks along the top edge, under the title area.
    for x in range(3, W - 3):
        im.putpixel((x, 16), GOLD_D if x % 2 else GOLD)
    # Help "?" box at 158,5.
    d.rectangle([158, 5, 169, 16], fill=hexc("8b8b8b"), outline=hexc("373737"))
    for (x, y) in [(162, 7), (163, 7), (164, 7), (165, 8), (165, 9), (164, 10), (163, 11), (163, 12), (163, 14)]:
        im.putpixel((x, y), light)
    save(im, "gui/container/mineral_converter.png")

# ================================================================== creatures
GREEN = hexc("4f9a3c")
GREEN_L = hexc("6fbf55")
GREEN_D = hexc("3a7a2c")
SUIT = hexc("1c1c22")
SUIT_L = hexc("2c2c36")
SHIRT = hexc("f4f4f0")
TIE = hexc("c62828")
TIE_D = hexc("8e1b1b")
CLAW = hexc("f2efe6")

def scales(x, y, w, h):
    """Green dinosaur skin: lighter top row, darker bottom, scattered scales."""
    c = shade(GREEN, 0.94 + 0.1 * rng.random())
    if h > 2 and y == 0:
        c = GREEN_L
    elif h > 2 and y == h - 1:
        c = GREEN_D
    elif rng.random() < 0.08:
        c = GREEN_D
    return c

def belly(x, y, w, h):
    return shade(hexc("a8d48a"), 0.95 + 0.08 * rng.random())

def suit(x, y, w, h):
    c = SUIT if rng.random() < 0.8 else SUIT_L
    if h > 2 and y == h - 1:
        c = shade(SUIT, 0.8)
    return c

def jacket_front(x, y, w, h):
    """Black jacket open in a V over a white shirt (the tie is its own cube), with two buttons."""
    mid = (w - 1) / 2
    v = max(0, 5 - y)  # the V narrows down to the first button
    if abs(x - mid) <= v * 0.6 and y < 6:
        return SHIRT
    if y in (7, 10) and abs(x - mid) < 1:
        return hexc("8a8a8a")  # buttons, under the tie edge
    if abs(x - mid) in (2.5, 3.0) and y < 7:
        return SUIT_L  # lapel edge
    return suit(x, y, w, h)

def collar(x, y, w, h):
    return SHIRT if y == 0 or rng.random() < 0.9 else hexc("dcdcd6")

def tie(x, y, w, h):
    if y == 0:
        return TIE_D  # the knot
    if y == h - 1:
        return TIE_D if x == 0 else TIE  # pointed tip
    return TIE if (y + x) % 4 else TIE_D  # thin diagonal stripes

def snout_front(x, y, w, h):
    if y == 1 and x in (1, w - 2):
        return hexc("1a2a14")  # nostrils
    return scales(x, y, w, h)

def snout_side(x, y, w, h):
    if y == h - 2 and x > 1:
        return GREEN_D  # mouth line
    return scales(x, y, w, h)

def glasses(x, y, w, h):
    if y == 0 and x in (2, w - 3):
        return hexc("4a4a58")  # a glint on each lens
    return hexc("0e0e12")

def guard_dinosaur():
    painters = {
        "torso": {"side": suit, "front": jacket_front, "top": fill(SUIT), "bottom": fill(shade(SUIT, 0.8))},
        "tie": {"side": tie, "front": tie, "top": fill(TIE_D), "bottom": fill(TIE_D)},
        "neck": scales, "collar": collar,
        "head": {"side": scales, "top": scales, "front": scales, "bottom": belly},
        "snout": {"side": snout_side, "front": snout_front, "top": scales, "bottom": belly},
        "glasses": glasses, "glasses_arm": fill(hexc("0e0e12")),
        "upper_arm": scales, "forearm": scales, "hand_claw": fill(CLAW),
        "thigh": scales, "shin": scales,
        "foot": {"side": scales, "top": scales, "bottom": fill(GREEN_D)},
        "claw": fill(CLAW),
        "tail1": {"side": scales, "bottom": belly}, "tail2": {"side": scales, "bottom": belly}, "tail3": scales,
    }
    paint("guard_dinosaur", "GuardDinosaurModel.java", 128, 64, painters)

def tyre(x, y, w, h):
    c = hexc("1e1e22") if rng.random() < 0.85 else hexc("2c2c32")
    if (x + y) % 3 == 0 and rng.random() < 0.3:
        c = hexc("38383e")  # tread
    return c

def hub(x, y, w, h):
    edge = x in (0, w - 1) or y in (0, h - 1)
    return hexc("b8bec2") if edge else hexc("e4e8ea")

def platform_top(x, y, w, h):
    # Dark deck with light grip strips across it.
    return hexc("d6dadc") if y % 2 == 1 and 1 <= x < w - 1 else hexc("2f3438")

def pole_front(x, y, w, h):
    return hexc("d6dadc")  # the light stripe up the column

def dark_metal(x, y, w, h):
    return shade(hexc("33383d"), 0.92 + 0.12 * rng.random())

def handlebar(x, y, w, h):
    return hexc("16181a") if x < 3 or x >= w - 3 else dark_metal(x, y, w, h)

def segway():
    painters = {
        "tire_a": tyre, "tire_b": tyre, "hub": hub,
        "platform": {"side": dark_metal, "top": platform_top, "bottom": fill(hexc("1e2125"))},
        "pole": {"side": dark_metal, "front": pole_front, "top": fill(hexc("1e2125"))},
        "handlebar": handlebar,
    }
    paint("segway", "SegwayModel.java", 64, 64, painters)

def main():
    converter(); items(); gui()
    guard_dinosaur(); segway()
    print("stage 7 textures generated")

if __name__ == "__main__":
    main()
