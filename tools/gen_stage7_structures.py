#!/usr/bin/env python3
"""Builds the stage 7 structure template: the Bank (data/crazyalloy_revival/structure/bank.nbt), found in Plains.

Outside: a tall white rectangle of quartz bricks (a regular grid of light blocks) with two fluted columns (quartz
pillars) in front, one on each side, holding up a stepped triangular pediment; the stepped roof rows run the whole
length of the building. A huge gold "$" in relief sits high in the middle of the facade. A small iron door with a
button opens onto a light platform with low side borders.
Inside, front to back: the reception (white walls, floor and ceiling, dark wooden benches along both side walls, a
free central aisle and a chandelier of fences and hanging lanterns); the counter, a white counter with a glass
partition on top, behind which are plants, the teller's barrel and another iron door with a button; a tall room
whose pillars split it into narrow passages, with a hanging lantern and a Segway spawner near the floor; the
low-ceilinged security room with two Guard Dinosaur spawners and placed guards (two Guard Dinosaurs and a Segway);
and at the very back an obsidian barrier sealing the vault with four chests (loot table chests/bank).
Revival choices: a staff door with a button at the end of the counter (so the back rooms can be reached without
breaking the glass), the teller's barrel, and what the vault holds behind the obsidian (the original images do not
show it).
Front = north (z 0) in template space; the game rotates the whole template when it places it.
Run: python3 tools/gen_stage7_structures.py   (requires nbtlib)
"""
from nbtlib import Byte
from gen_stage4_structures import Template, spawner, container, STAIR, NOT_WET

W, H, D = 21, 22, 48
QB = "minecraft:quartz_bricks"
QK = "minecraft:quartz_block"
SQ = "minecraft:smooth_quartz"
PILLAR = "minecraft:quartz_pillar"
CHISELED = "minecraft:chiseled_quartz_block"
DOOR = dict(hinge="left", open="false", powered="false")

DOLLAR = [  # top row first; '#' = gold block, as seen from the front
    "..#..",
    ".####",
    "#.#..",
    "#.#..",
    ".###.",
    "..#.#",
    "..#.#",
    "####.",
    "..#..",
]


def bank():
    t = Template(W, H, D)
    t.fill(0, W - 1, 0, H - 1, 0, D - 1, "minecraft:air")

    # ---------------------------------------------------------------- shell
    t.fill(0, W - 1, 0, 0, 0, D - 1, SQ)                       # platform and floor
    for z in range(0, 4):                                        # low borders along the sides of the platform
        for x in (0, W - 1):
            t.put(x, 1, z, "minecraft:smooth_quartz_slab", type="bottom", **NOT_WET)
    t.fill(0, W - 1, 1, 14, 4, D - 1, QB)                      # outer walls (the inside is carved below)
    t.fill(1, W - 2, 1, 14, 5, D - 2, QK)                      # solid core: ceilings, attic and dividing walls
    for x in (0, W - 1):
        for z in (4, D - 1):
            t.fill(x, x, 1, 14, z, z, PILLAR, axis="y")         # corner pilasters

    # ---------------------------------------------------------------- facade: door, columns, the "$"
    for y in (1, 2):
        t.put(10, y, 4, "minecraft:iron_door", facing="north", half="lower" if y == 1 else "upper", **DOOR)
    t.fill(9, 11, 3, 3, 4, 4, CHISELED)                         # lintel
    t.put(11, 2, 3, "minecraft:stone_button", face="wall", facing="north", powered="false")
    for x0 in (2, 17):                                          # two fluted columns, 2 x 2, in front of the facade
        for x in (x0, x0 + 1):
            for z in (2, 3):
                t.put(x, 1, z, CHISELED)
                t.fill(x, x, 2, 13, z, z, PILLAR, axis="y")
                t.put(x, 14, z, CHISELED)
    for row, line in enumerate(DOLLAR):
        y = 14 - row
        for col, ch in enumerate(line):
            if ch == "#":
                t.put(12 - col, y, 3, "minecraft:gold_block")    # viewed from the north, +x is on the left

    # ---------------------------------------------------------------- pediment and stepped roof
    t.fill(0, W - 1, 15, 15, 1, D - 1, QK)                      # entablature over the columns, full length
    for k in range(6):
        y, lo, hi = 16 + k, 2 * k, W - 1 - 2 * k
        for z in range(1, D):
            for x in range(lo, hi + 1):
                if lo == hi:
                    t.put(x, y, z, SQ)
                elif x == lo:
                    t.put(x, y, z, "minecraft:quartz_stairs", facing="east", **STAIR)
                elif x == hi:
                    t.put(x, y, z, "minecraft:quartz_stairs", facing="west", **STAIR)
                else:
                    t.put(x, y, z, QK)

    # ---------------------------------------------------------------- reception
    t.fill(1, 19, 1, 9, 5, 20, "minecraft:air")
    t.put(9, 2, 5, "minecraft:stone_button", face="wall", facing="south", powered="false")  # to open the front door from inside
    for z0, z1 in ((6, 10), (14, 18)):                          # dark wooden benches against both side walls
        for z in range(z0, z1 + 1):
            t.put(1, 1, z, "minecraft:dark_oak_stairs", facing="west", **STAIR)
            t.put(19, 1, z, "minecraft:dark_oak_stairs", facing="east", **STAIR)
    # Chandelier: fences hanging from the ceiling with a cross of fences and lanterns hanging from it.
    for y in (8, 9):
        t.put(10, y, 12, "minecraft:dark_oak_fence", north="false", south="false", east="false", west="false", **NOT_WET)
    t.put(10, 7, 12, "minecraft:dark_oak_fence", north="true", south="true", east="true", west="true", **NOT_WET)
    for (x, z) in [(9, 12), (11, 12), (10, 11), (10, 13)]:
        t.put(x, 7, z, "minecraft:dark_oak_fence", north=str(z == 13).lower(), south=str(z == 11).lower(),
              east=str(x == 9).lower(), west=str(x == 11).lower(), **NOT_WET)
        t.put(x, 6, z, "minecraft:lantern", hanging="true", **NOT_WET)
    t.put(10, 6, 12, "minecraft:lantern", hanging="true", **NOT_WET)

    # ---------------------------------------------------------------- counter with its glass partition (z 20)
    for x in range(1, 17):
        t.put(x, 1, 20, QK)
        for y in range(2, 6):
            t.put(x, y, 20, "minecraft:glass_pane", north="false", south="false", east=str(x < 16).lower(),
                  west=str(x > 1).lower(), **NOT_WET)
    t.fill(1, 19, 6, 9, 20, 20, QB)
    t.fill(17, 17, 1, 5, 20, 20, QB)
    t.fill(19, 19, 1, 5, 20, 20, QB)
    t.fill(18, 18, 3, 5, 20, 20, QB)
    for y in (1, 2):                                            # staff door at the end of the counter
        t.put(18, y, 20, "minecraft:iron_door", facing="north", half="lower" if y == 1 else "upper", **DOOR)
    t.put(17, 2, 19, "minecraft:stone_button", face="wall", facing="north", powered="false")
    t.put(19, 2, 21, "minecraft:stone_button", face="wall", facing="south", powered="false")

    # ---------------------------------------------------------------- behind the glass: plants and the inner door
    t.fill(1, 19, 1, 6, 21, 24, "minecraft:air")
    for (x, plant) in [(2, "potted_fern"), (5, "potted_azalea_bush"), (8, "potted_fern"), (12, "potted_flowering_azalea_bush"),
                       (15, "potted_fern")]:
        t.put(x, 1, 21, f"minecraft:{plant}")
    for x in (3, 17):
        t.put(x, 1, 24, "minecraft:potted_bamboo")
    t.put(1, 1, 24, "minecraft:barrel", nbt=container("barrel", "bank_teller"), facing="up", open="false")
    t.put(10, 5, 22, "minecraft:lantern", hanging="true", **NOT_WET)
    for y in (1, 2):
        t.put(10, y, 25, "minecraft:iron_door", facing="north", half="lower" if y == 1 else "upper", **DOOR)
    t.put(11, 2, 24, "minecraft:stone_button", face="wall", facing="north", powered="false")
    t.put(9, 2, 26, "minecraft:stone_button", face="wall", facing="south", powered="false")

    # ---------------------------------------------------------------- the tall pillar room
    t.fill(1, 19, 1, 13, 26, 34, "minecraft:air")
    for x in (4, 7, 13, 16):
        for z in (27, 29, 31, 33):
            t.fill(x, x, 1, 13, z, z, PILLAR, axis="y")
    t.put(10, 13, 30, "minecraft:lantern", hanging="true", **NOT_WET)
    for (x, z) in [(1, 26), (19, 26), (1, 34), (19, 34)]:
        t.put(x, 1, z, "minecraft:lantern", hanging="false", **NOT_WET)
    t.put(2, 1, 30, "minecraft:spawner", nbt=spawner("segway", count=1, nearby=2))
    t.fill(9, 11, 1, 3, 35, 35, "minecraft:air")                # doorway to the security room

    # ---------------------------------------------------------------- the security room (low ceiling)
    t.fill(1, 19, 1, 3, 36, 42, "minecraft:air")
    for x in (4, 16):
        t.put(x, 1, 38, "minecraft:spawner", nbt=spawner("guard_dinosaur", count=1, nearby=3))
    for (x, z) in [(1, 36), (19, 36), (1, 42), (19, 42)]:
        t.put(x, 3, z, "minecraft:lantern", hanging="true", **NOT_WET)
    t.entity("guard_dinosaur", 7.5, 1.0, 40.5, yaw=180.0, FromBank=Byte(1))
    t.entity("guard_dinosaur", 13.5, 1.0, 40.5, yaw=180.0, FromBank=Byte(1))
    t.entity("segway", 10.5, 1.0, 41.0, yaw=180.0, FromBank=Byte(1))

    # ---------------------------------------------------------------- obsidian barrier and the vault behind it
    t.fill(1, 19, 1, 3, 43, 43, "minecraft:obsidian")
    t.fill(1, 19, 1, 3, 44, 46, "minecraft:air")
    for x in (4, 8, 12, 16):
        t.put(x, 1, 46, "minecraft:chest", nbt=container("chest", "bank"), facing="north", type="single", **NOT_WET)
    for x in (1, 2, 18, 19):
        t.put(x, 1, 46, "minecraft:gold_block")
    t.put(10, 3, 45, "minecraft:lantern", hanging="true", **NOT_WET)
    t.save("bank")


if __name__ == "__main__":
    bank()
