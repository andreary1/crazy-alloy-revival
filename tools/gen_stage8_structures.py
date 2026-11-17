#!/usr/bin/env python3
"""Builds the Mystic Grove structure templates: the Dwarf Hut and the Wizard Tower
(data/crazyalloy_revival/structure/dwarf_hut.nbt and wizard_tower.nbt).

Dwarf Hut (from the owner's reference picture): a small hut of dark wood planks with a stepped roof that narrows to
the top and overhangs the walls, a plain rectangular doorway with no door, a wooden floor and a chest at the back.
Beside it, a small fenced yard on stone with an ore block in it. The Dwarf stands inside.
Revival additions: a lantern under the roof, tools and a few stores (a barrel, a crafting table, a grindstone, a
pickaxe-shaped stack of ore in the yard), so the hut reads as a working miner's home.

Wizard Tower (from the owner's reference pictures): a tall central block with two side rooms joined to it by narrow
stone-brick bridges with low edges and torches. Dark wood frames, log pillars at the corners, light walls (calcite)
and stone bricks. The entrance is under a stepped stone arch with small steps out in front. The middle floor has a
pink and lilac diamond window; the top floor is a glass room (pink, purple and light blue panes) between light bands,
under a wide, layered wooden roof; the side rooms have their own layered roofs and small coloured windows.
Inside: a study (light walls, dark wood floor, narrow lilac windows, two lecterns side by side, a shelf of books
high on the wall); the glass laboratory on top (a long dark wood bench with candles and a brewing stand, a water
cauldron at one end, a barrel, a double chest, a big brown pot with a lid, a ladder hole down to the study). The
Wizard stands in the laboratory.
Revival additions: the side rooms get their own jobs (a store room with chests on one side, a bedroom with a bed and
a lectern on the other), hanging lanterns keep the glass room lit at night, and the bridges have torches on posts.
Front = north (z 0) in template space; the game rotates the whole template when it places it.
Run: python3 tools/gen_stage8_structures.py   (requires nbtlib)
"""
from nbtlib import Byte
from gen_stage4_structures import Template, container, STAIR, NOT_WET

DARK = "minecraft:dark_oak_planks"
DLOG = "minecraft:dark_oak_log"
DSTAIR = "minecraft:dark_oak_stairs"
DSLAB = "minecraft:dark_oak_slab"
LIGHT = "minecraft:calcite"
SB = "minecraft:stone_bricks"
SBS = "minecraft:stone_brick_stairs"
SBSL = "minecraft:stone_brick_slab"
PANES = ["minecraft:pink_stained_glass", "minecraft:purple_stained_glass", "minecraft:light_blue_stained_glass",
         "minecraft:magenta_stained_glass"]
FENCE = "minecraft:dark_oak_fence"


def fence(t, x, y, z, n=False, s=False, e=False, w=False):
    t.put(x, y, z, FENCE, north=str(n).lower(), south=str(s).lower(), east=str(e).lower(), west=str(w).lower(), **NOT_WET)


def stepped_roof(t, x0, x1, z0, z1, y, steps):
    """Layered wooden roof: each layer one block in from the last, overhanging edges as stairs, a slab cap."""
    for k in range(steps):
        lo_x, hi_x, lo_z, hi_z = x0 + k, x1 - k, z0 + k, z1 - k
        if lo_x > hi_x or lo_z > hi_z:
            break
        yy = y + k
        for x in range(lo_x, hi_x + 1):
            for z in range(lo_z, hi_z + 1):
                edge = x in (lo_x, hi_x) or z in (lo_z, hi_z)
                if not edge:
                    t.put(x, yy, z, DARK)
                elif z == lo_z and lo_x < x < hi_x:
                    t.put(x, yy, z, DSTAIR, facing="south", **STAIR)
                elif z == hi_z and lo_x < x < hi_x:
                    t.put(x, yy, z, DSTAIR, facing="north", **STAIR)
                elif x == lo_x and lo_z < z < hi_z:
                    t.put(x, yy, z, DSTAIR, facing="east", **STAIR)
                elif x == hi_x and lo_z < z < hi_z:
                    t.put(x, yy, z, DSTAIR, facing="west", **STAIR)
                else:
                    t.put(x, yy, z, DSLAB, type="bottom", **NOT_WET)
    return y + steps


# =============================================================================================== Dwarf Hut
def dwarf_hut():
    W, H, D = 11, 9, 9
    t = Template(W, H, D)
    t.fill(0, W - 1, 0, H - 1, 0, D - 1, "minecraft:air")
    # Hut: 5 x 5 outside at x 5..9, z 1..5; floor of dark planks.
    t.fill(5, 9, 0, 0, 1, 6, DARK)
    t.fill(5, 9, 1, 3, 1, 5, DARK)
    t.fill(6, 8, 1, 3, 2, 4, "minecraft:air")
    t.fill(7, 7, 1, 2, 1, 1, "minecraft:air")                 # doorway, no door
    t.put(7, 1, 4, "minecraft:chest", nbt=container("chest", "dwarf_hut"), facing="north", type="single", **NOT_WET)
    t.put(6, 1, 4, "minecraft:barrel", nbt=container("barrel", "dwarf_hut_stores"), facing="up", open="false")
    t.put(8, 1, 4, "minecraft:crafting_table")
    t.put(8, 1, 2, "minecraft:grindstone", face="floor", facing="north")
    t.put(7, 3, 3, "minecraft:lantern", hanging="true", **NOT_WET)
    # Stepped roof overhanging the walls by one block.
    stepped_roof(t, 4, 10, 0, 6, 4, 4)
    # Fenced yard on the left (west, x 0..4) with stone and an ore block.
    t.fill(0, 4, 0, 0, 1, 5, "minecraft:stone")
    t.put(1, 1, 3, "minecraft:iron_ore")
    t.put(2, 1, 4, "minecraft:coal_ore")
    t.put(3, 1, 2, "minecraft:cobblestone")
    for x in range(0, 5):
        fence(t, x, 1, 1, e=x < 4, w=x > 0)
        fence(t, x, 1, 5, e=x < 4, w=x > 0)
    for z in range(2, 5):
        fence(t, 0, 1, z, n=True, s=True)
    t.put(4, 1, 3, "minecraft:air")
    # Mossy stones and a blue flower on the grass in front.
    t.put(10, 1, 7, "minecraft:mossy_cobblestone")
    t.put(3, 1, 7, "minecraft:cornflower")
    t.entity("dwarf", 7.5, 1.0, 3.0, yaw=180.0, FromStructure=Byte(1))
    t.save("dwarf_hut")


# =============================================================================================== Wizard Tower
def wizard_tower():
    W, H, D = 33, 30, 11
    t = Template(W, H, D)
    t.fill(0, W - 1, 0, H - 1, 0, D - 1, "minecraft:air")
    cx0, cx1, cz0, cz1 = 12, 20, 1, 9                         # central block, 9 x 9

    # ------------------------------------------------------------- central block shell (floors at y 0, 7, 14)
    t.fill(cx0, cx1, 0, 0, cz0, cz1, SB)
    t.fill(cx0, cx1, 1, 13, cz0, cz1, LIGHT)
    t.fill(cx0 + 1, cx1 - 1, 1, 13, cz0 + 1, cz1 - 1, "minecraft:air")
    for x in (cx0, cx1):
        for z in (cz0, cz1):
            t.fill(x, x, 1, 21, z, z, DLOG, axis="y")          # corner log pillars, all the way up
    for y in (7, 14):                                          # dark wood frames and floors
        t.fill(cx0, cx1, y, y, cz0, cz1, DARK)
    t.fill(cx0, cx1, 1, 3, cz0, cz0, SB)                      # stone-brick base of the front

    # Entrance: a 3 wide, 3 tall opening under a stepped stone arch, steps out front.
    t.fill(15, 17, 1, 3, cz0, cz0, "minecraft:air")
    t.put(15, 4, cz0, SBS, facing="east", half="top", shape="straight", waterlogged="false")
    t.put(17, 4, cz0, SBS, facing="west", half="top", shape="straight", waterlogged="false")
    t.put(16, 4, cz0, "minecraft:chiseled_stone_bricks")
    t.put(14, 4, cz0, SB); t.put(18, 4, cz0, SB); t.put(16, 5, cz0, SB)
    for x in range(14, 19):
        t.put(x, 0, 0, SBSL, type="bottom", **NOT_WET)
    t.put(16, 3, cz0 + 1, "minecraft:lantern", hanging="true", **NOT_WET)

    # Ground floor: entry hall with the ladder up and torches.
    for y in range(1, 14):
        t.put(19, y, 8, "minecraft:ladder", facing="west", **NOT_WET)
    t.put(19, 7, 8, "minecraft:ladder", facing="west", **NOT_WET)
    t.put(13, 2, 8, "minecraft:wall_torch", facing="south")
    t.put(13, 2, 2, "minecraft:wall_torch", facing="south")

    # Middle floor (y 8..13): the study. Dark wood floor, light walls, narrow lilac windows, two lecterns side by side,
    # a shelf of books high on the back wall, the diamond window in front.
    t.fill(cx0 + 1, cx1 - 1, 7, 7, cz0 + 1, cz1 - 1, DARK)
    t.put(19, 7, 8, "minecraft:ladder", facing="west", **NOT_WET)
    for (x, z) in [(15, 8), (16, 8)]:
        t.put(x, 8, z, "minecraft:lectern", facing="north", has_book="false", powered="false")
    for x in range(13, 19):
        for y in (11, 12):
            t.put(x, y, 9, "minecraft:chiseled_bookshelf", facing="north", **{f"slot_{i}_occupied": str((x + y + i) % 3 != 0).lower() for i in range(6)})
    for z in (3, 7):                                           # narrow lilac windows either side of the bridge doors
        for y in (9, 10, 11):
            for x in (cx0, cx1):
                t.put(x, y, z, "minecraft:purple_stained_glass_pane", north="false", south="false", east="false", west="false", **NOT_WET)
    t.put(13, 10, 2, "minecraft:wall_torch", facing="east")
    # Diamond window in the front: a frame of dark wood around pink and lilac glass.
    diamond = [(16, 13), (15, 12), (16, 12), (17, 12), (14, 11), (15, 11), (16, 11), (17, 11), (18, 11),
               (15, 10), (16, 10), (17, 10), (16, 9)]
    for x in range(13, 20):
        for y in range(8, 14):
            t.put(x, y, cz0, DARK)
    for (x, y) in diamond:
        inner = abs(x - 16) + abs(y - 11) <= 1
        t.put(x, y, cz0, "minecraft:magenta_stained_glass" if inner else "minecraft:pink_stained_glass")
    for (x, y) in [(13, 8), (19, 8), (13, 13), (19, 13), (14, 8), (18, 8), (14, 13), (18, 13)]:
        t.put(x, y, cz0, LIGHT)

    # Top floor (y 15..20): the glass laboratory between light bands.
    t.fill(cx0, cx1, 14, 14, cz0, cz1, LIGHT)                 # light floor band
    t.fill(cx0 + 1, cx1 - 1, 14, 14, cz0 + 1, cz1 - 1, "minecraft:smooth_quartz")
    t.put(19, 14, 8, "minecraft:ladder", facing="west", **NOT_WET)  # the ladder hole
    t.fill(cx0, cx1, 21, 21, cz0, cz1, LIGHT)                 # light ceiling band
    for y in range(15, 21):
        for x in range(cx0 + 1, cx1):
            for z in (cz0, cz1):
                t.put(x, y, z, PANES[(x + y) % len(PANES)])
        for z in range(cz0 + 1, cz1):
            for x in (cx0, cx1):
                t.put(x, y, z, PANES[(z + y + 1) % len(PANES)])
    # Bench of dark wood along the back wall with candles and a brewing stand, a water cauldron at the end.
    for x in range(13, 19):
        t.put(x, 15, 8, DSTAIR, facing="north", half="top", shape="straight", waterlogged="false")
    t.put(14, 16, 8, "minecraft:candle", candles="3", lit="true", **NOT_WET)
    t.put(17, 16, 8, "minecraft:candle", candles="2", lit="true", **NOT_WET)
    t.put(16, 16, 8, "minecraft:brewing_stand", has_bottle_0="true", has_bottle_1="false", has_bottle_2="true")
    t.put(19, 15, 7, "minecraft:water_cauldron", level="3")
    t.put(17, 15, 6, "minecraft:barrel", nbt=container("barrel", "wizard_tower_ingredients"), facing="up", open="false")
    t.put(13, 15, 3, "minecraft:chest", nbt=container("chest", "wizard_tower"), facing="east", type="right", **NOT_WET)
    t.put(13, 15, 4, "minecraft:chest", nbt=container("chest", "wizard_tower"), facing="east", type="left", **NOT_WET)
    t.put(13, 15, 6, "minecraft:decorated_pot", facing="east", cracked="false", **NOT_WET)
    for (x, z) in [(14, 3), (18, 3), (16, 6)]:
        t.put(x, 20, z, "minecraft:lantern", hanging="true", **NOT_WET)
    t.entity("wizard", 15.5, 15.0, 5.5, yaw=180.0, FromStructure=Byte(1))

    # Wide layered roof over the central block.
    stepped_roof(t, cx0 - 2, cx1 + 2, cz0 - 1, cz1 + 1, 22, 5)

    # ------------------------------------------------------------- side rooms and bridges
    def side_room(x0, kind):
        x1 = x0 + 6
        z0, z1 = 2, 8
        base = 7
        t.fill(x0, x1, base, base, z0, z1, DARK)               # floor (no supports under it, as in the picture)
        t.fill(x0, x1, base + 1, base + 5, z0, z1, LIGHT)
        t.fill(x0 + 1, x1 - 1, base + 1, base + 5, z0 + 1, z1 - 1, "minecraft:air")
        for x in (x0, x1):
            for z in (z0, z1):
                t.fill(x, x, base, base + 5, z, z, DLOG, axis="y")
        t.fill(x0, x1, base + 5, base + 5, z0, z1, DARK)
        # small coloured windows on the front and the outer side
        wx = x0 + 3
        for y in (base + 2, base + 3):
            t.put(wx, y, z0, "minecraft:magenta_stained_glass_pane", east="true", west="true", north="false", south="false", **NOT_WET)
            t.put(wx, y, z1, "minecraft:purple_stained_glass_pane", east="true", west="true", north="false", south="false", **NOT_WET)
        outer = x0 if kind == "store" else x1
        for y in (base + 2, base + 3):
            t.put(outer, y, 5, "minecraft:pink_stained_glass_pane", north="true", south="true", east="false", west="false", **NOT_WET)
        if kind == "store":
            for z in (3, 4, 6, 7):
                t.put(x0 + 1, base + 1, z, "minecraft:chest", nbt=container("chest", "wizard_tower_store"), facing="east", type="single", **NOT_WET)
            t.put(x0 + 2, base + 1, 7, "minecraft:barrel", nbt=container("barrel", "wizard_tower_ingredients"), facing="up", open="false")
            t.put(x0 + 2, base + 1, 3, "minecraft:flower_pot")
        else:
            t.put(x1 - 1, base + 1, 4, "minecraft:blue_bed", facing="south", part="head", occupied="false")
            t.put(x1 - 1, base + 1, 5, "minecraft:blue_bed", facing="south", part="foot", occupied="false")
            t.put(x1 - 1, base + 1, 7, "minecraft:lectern", facing="west", has_book="false", powered="false")
            t.put(x1 - 3, base + 1, 7, "minecraft:bookshelf")
        t.put(x0 + 3, base + 4, 5, "minecraft:lantern", hanging="true", **NOT_WET)
        stepped_roof(t, x0 - 1, x1 + 1, z0 - 1, z1 + 1, base + 6, 3)
        return base

    base = side_room(1, "store")                              # west room x 1..7
    side_room(25, "bed")                                       # east room x 25..31

    def bridge(xa, xb):
        for x in range(xa, xb + 1):
            for z in (4, 5, 6):
                t.put(x, base, z, SB)
            t.put(x, base + 1, 4, SBSL, type="bottom", **NOT_WET)  # low edges
            t.put(x, base + 1, 6, SBSL, type="bottom", **NOT_WET)
        for x in (xa + 1, xb - 1):
            t.put(x, base + 1, 4, "minecraft:cobblestone_wall", up="true", north="none", south="none", east="none", west="none", **NOT_WET)
            t.put(x, base + 2, 4, "minecraft:torch")
        return x

    bridge(8, 11)
    bridge(21, 24)
    # Doorways from the bridges into the side rooms and the study (the bridges are level with the study floor).
    for y in (base + 1, base + 2):
        for x in (7, 25, cx0, cx1):
            t.put(x, y, 5, "minecraft:air")
    t.save("wizard_tower")


if __name__ == "__main__":
    dwarf_hut()
    wizard_tower()
