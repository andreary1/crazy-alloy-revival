#!/usr/bin/env python3
"""Builds the Mushroom Depths structure templates (0.8.0): the Mushroom Outpost and the Mushroom Base
(data/crazyalloy_revival/structure/mushroom_outpost.nbt and mushroom_base.nbt).

Mushroom Outpost, 14 x 13 x 13 (from the owner's description of the original's last reference picture): a small
organic tower of stacked, wide brown mushroom platforms at three heights, each with stepped edges and a slightly
different outline, joined by a pale mushroom stem and a ladder, under a red cap with light spots on top. No walls.
Two chests (Mushroom Outpost loot) and four spawners: two for the Mushroom Man, two for the Mushroomcopter.
Revival additions: shroomlights in the underside of the cap; the ladder runs up one side of the stem through holes in
the platforms, so the climb is the one narrow way up.

Mushroom Base, 24 x 19 x 25 (the original's first reference picture): a wider, open structure of raised platforms
with red mushroom floors, pale stem pillars that leave open space underneath, dark mystic-wood stairs flanked by brown
mushroom blocks and mystic fences along the edges, rising over three levels to a top platform. Five chests (four with
Mushroom Outpost loot on the lower levels, the highest one with Mushroom Base loot) and five spawners (two Mushroom Man,
two Mushroomcopter, one Mushroom Cyclops at the top, next to the best chest).
Revival additions: the top platform gets its own look (mystic log posts and a red cap roof with shroomlights), a few
shroomlights set into the floors light the stairs, and the spawners sit away from the stair landings so a fight on
the stairs stays readable.

Only the blocks of the structure and the air inside it are written; everything else is left as it was (the
structure's terrain adaptation clears the hall around it). Front = north (z 0); the game rotates the template.
Run: python3 tools/gen_stage9_structures.py   (requires nbtlib)
"""
from gen_stage4_structures import Template, container, spawner, NOT_WET
from gen_stage8_structures import connect

BROWN = "minecraft:brown_mushroom_block"
RED = "minecraft:red_mushroom_block"
STEM = "minecraft:mushroom_stem"
LIGHT = "minecraft:shroomlight"
FENCE = "mystic_fence"
STAIRS = "mystic_stairs"
MLOG = "mystic_log"


def inside(x, z, cx, cz, rx, rz):
    return ((x - cx) / (rx + 0.35)) ** 2 + ((z - cz) / (rz + 0.35)) ** 2 <= 1.0


def disc(t, cx, cz, rx, rz, y, block):
    cells = []
    for x in range(t.w):
        for z in range(t.d):
            if inside(x, z, cx, cz, rx, rz):
                t.put(x, y, z, block)
                cells.append((x, z))
    return cells


def edges(cells):
    s = set(cells)
    return [(x, z) for (x, z) in cells if any((x + dx, z + dz) not in s for dx, dz in ((1, 0), (-1, 0), (0, 1), (0, -1)))]


def air_cylinder(t, cx, cz, rx, rz, y0, y1):
    for y in range(y0, y1 + 1):
        for x in range(t.w):
            for z in range(t.d):
                if inside(x, z, cx, cz, rx, rz):
                    t.put(x, y, z, "minecraft:air")


def chest(t, x, y, z, loot, facing):
    t.put(x, y, z, "minecraft:chest", nbt=container("chest", loot), facing=facing, type="single", **NOT_WET)


# =============================================================================================== Mushroom Outpost
def outpost():
    W, H, D = 14, 13, 13
    t = Template(W, H, D)
    air_cylinder(t, 6.5, 6.0, 6.5, 6.0, 1, 10)
    # Three brown platforms, each a little different in outline and centre, with stepped (bumpy) edges.
    levels = [(0, 6.5, 6.0, 6.2, 5.8), (4, 7.5, 6.0, 4.8, 5.0), (8, 6.0, 6.5, 3.8, 3.6)]
    for i, (y, cx, cz, rx, rz) in enumerate(levels):
        cells = disc(t, cx, cz, rx, rz, y, BROWN)
        for k, (x, z) in enumerate(sorted(edges(cells))):
            if (x + z + i) % 3 == 0 and t.get(x, y + 1, z) in (None, "minecraft:air"):
                t.put(x, y + 1, z, BROWN)
    # The pale stem through the middle, up to the cap.
    for y in range(1, 11):
        for x in (6, 7):
            for z in (5, 6):
                t.put(x, y, z, STEM)
    # Red cap with light spots on top (the vanilla red mushroom block), shroomlights in its underside.
    disc(t, 6.5, 6.0, 4.6, 4.6, 11, RED)
    disc(t, 6.5, 6.0, 3.0, 3.0, 12, RED)
    for (x, z) in [(4, 4), (9, 8), (9, 4)]:
        t.put(x, 11, z, LIGHT)
    # One ladder up the south side of the stem, through holes in the platforms.
    for y in range(1, 9):
        t.put(6, y, 7, "minecraft:ladder", facing="south", **NOT_WET)
    for y in (2, 3, 6, 7, 9, 10):   # keep the climb clear of bumps
        if t.get(6, y, 8) == BROWN:
            t.put(6, y, 8, "minecraft:air")
    # Two chests (Mushroom Outpost loot) and four spawners.
    chest(t, 10, 5, 6, "mushroom_outpost", "west")
    chest(t, 4, 9, 6, "mushroom_outpost", "east")
    t.put(3, 1, 3, "minecraft:spawner", nbt=spawner("mushroom_man", count=2, nearby=4))
    t.put(10, 5, 9, "minecraft:spawner", nbt=spawner("mushroom_man", count=2, nearby=4))
    t.put(5, 5, 3, "minecraft:spawner", nbt=spawner("mushroomcopter", count=1, nearby=3))
    t.put(8, 9, 8, "minecraft:spawner", nbt=spawner("mushroomcopter", count=1, nearby=3))
    connect(t)
    t.save("mushroom_outpost")
    return t


# =============================================================================================== Mushroom Base
def platform(t, x0, x1, z0, z1, y, block=RED, cut=1):
    cells = []
    for x in range(x0, x1 + 1):
        for z in range(z0, z1 + 1):
            corner = min(x - x0, x1 - x) + min(z - z0, z1 - z) < cut
            if not corner:
                t.put(x, y, z, block)
                cells.append((x, z))
    return cells


def pillar(t, x, z, y0, y1, block=STEM, **props):
    for y in range(y0, y1 + 1):
        t.put(x, y, z, block, **props)


def fences(t, cells, y, gaps):
    for (x, z) in edges(cells):
        if (x, z) in gaps or t.get(x, y + 1, z) not in (None, "minecraft:air"):
            continue
        t.put(x, y + 1, z, FENCE, north="false", south="false", east="false", west="false", **NOT_WET)


def stair_run(t, cells, facing, y0):
    """Mystic stairs rising one block per step along the run, brown mushroom blocks under them and along both sides.
    cells: list of (x, z) rows, one per step, bottom step first; each row lists the stair positions of that step."""
    dx, dz = {"east": (1, 0), "west": (-1, 0), "south": (0, 1), "north": (0, -1)}[facing]
    for k, row in enumerate(cells):
        y = y0 + k
        for (x, z) in row:
            t.put(x, y, z, STAIRS, facing=facing, half="bottom", shape="straight", waterlogged="false")
            for yy in range(y0 - 1 if k else y0, y):
                t.put(x, yy, z, BROWN)
            for yy in range(y + 1, y + 4):
                t.put(x, yy, z, "minecraft:air")
        # Side walls of brown mushroom blocks, one block above the step (a low rail).
        xs = [p[0] for p in row]
        zs = [p[1] for p in row]
        if dx == 0:   # run along z: sides are x min - 1 and x max + 1
            sides = [(min(xs) - 1, zs[0]), (max(xs) + 1, zs[0])]
        else:
            sides = [(xs[0], min(zs) - 1), (xs[0], max(zs) + 1)]
        for (x, z) in sides:
            for yy in range(y0 - 1 if k else y0, y + 2):
                if 0 <= x < t.w and 0 <= z < t.d:
                    t.put(x, yy, z, BROWN)


def base():
    W, H, D = 24, 19, 25
    t = Template(W, H, D)
    air_cylinder(t, 11.5, 12.0, 11.5, 12.0, 1, 17)
    # Brown ground under the whole base.
    disc(t, 11.5, 12.0, 11.5, 12.0, 0, BROWN)

    # Level 1 (floor y 5): red floor 20 x 18 on stem pillars, open underneath.
    l1 = platform(t, 2, 21, 5, 22, 5, cut=2)
    for (x, z) in [(3, 6), (20, 6), (3, 21), (20, 21), (11, 6), (12, 21), (3, 13), (20, 13), (11, 13)]:
        pillar(t, x, z, 1, 4)
    # Level 2 (floor y 10): red floor 12 x 11, its pillars stand on level 1.
    l2 = platform(t, 8, 19, 9, 19, 10, cut=2)
    for (x, z) in [(9, 10), (18, 10), (9, 18), (18, 18), (14, 10)]:
        pillar(t, x, z, 6, 9)
    # Top (floor y 14): a 7 x 7 platform on mystic log posts, with its own red cap roof.
    top = platform(t, 13, 19, 12, 18, 14, cut=0)
    for (x, z) in [(13, 12), (19, 12), (13, 18), (19, 18)]:
        pillar(t, x, z, 11, 13)
        pillar(t, x, z, 15, 17, MLOG, axis="y")
    platform(t, 12, 20, 11, 19, 18, cut=2)
    for (x, z) in [(16, 15), (14, 13), (18, 17)]:
        t.put(x, 18, z, LIGHT)

    # Stairs: ground -> level 1 (front, rising south), level 1 -> level 2 and level 2 -> top (both rising east).
    stair_run(t, [[(x, z) for x in (10, 11, 12)] for z in range(0, 5)], "south", 1)
    stair_run(t, [[(x, z) for z in (13, 14, 15)] for x in range(3, 8)], "east", 6)
    stair_run(t, [[(x, z) for z in (14, 15, 16)] for x in range(9, 13)], "east", 11)
    # Openings in the fences where the stairs arrive.
    gaps1 = {(x, 5) for x in (10, 11, 12)} | {(2, z) for z in (13, 14, 15)}
    gaps2 = {(8, z) for z in (13, 14, 15)}
    gaps3 = {(13, z) for z in (14, 15, 16)}
    # Shroomlights set into the floors near the stairs.
    for (x, y, z) in [(9, 5, 7), (14, 5, 18), (6, 5, 17), (12, 10, 11), (16, 10, 18)]:
        t.put(x, y, z, LIGHT)
    fences(t, l1, 5, gaps1)
    fences(t, l2, 10, gaps2)
    fences(t, top, 14, gaps3)

    # Chests: four with Outpost loot on the lower levels, the highest one with Mushroom Base loot.
    chest(t, 6, 1, 15, "mushroom_outpost", "east")
    chest(t, 17, 1, 3, "mushroom_outpost", "north")
    chest(t, 20, 6, 19, "mushroom_outpost", "west")
    chest(t, 18, 11, 11, "mushroom_outpost", "west")
    chest(t, 18, 15, 16, "mushroom_base", "west")
    # Spawners: Mushroom Man under the platforms and on level 1, Mushroomcopters on levels 1 and 2, the Cyclops on top.
    t.put(14, 1, 12, "minecraft:spawner", nbt=spawner("mushroom_man", count=2, nearby=4))
    t.put(17, 6, 8, "minecraft:spawner", nbt=spawner("mushroom_man", count=2, nearby=4))
    t.put(5, 6, 19, "minecraft:spawner", nbt=spawner("mushroomcopter", count=1, nearby=3))
    t.put(11, 11, 18, "minecraft:spawner", nbt=spawner("mushroomcopter", count=1, nearby=3))
    t.put(15, 15, 13, "minecraft:spawner", nbt=spawner("mushroom_cyclops", count=1, nearby=2))
    connect(t)
    t.save("mushroom_base")
    return t


def count(t, name):
    return sum(1 for b in t.blocks.values() if b[0] == name)


if __name__ == "__main__":
    o = outpost()
    b = base()
    for name, t in (("outpost", o), ("base", b)):
        print(name, "chests", count(t, "minecraft:chest"), "spawners", count(t, "minecraft:spawner"))
