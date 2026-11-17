#!/usr/bin/env python3
"""Stage 2 data: Sweet Forest rework and Jelly Bean Fields.

Called by tools/gen_resources.py after the stage 1 generators (files written here replace stage 1 files
of the same name, and tags are merged). Edit the tables here rather than the generated JSON.
"""
import json, os
import gen_resources as G
from gen_resources import NS, A, D, write, ns, simple_state, item_def, flat_item, handheld_item, state, tag, shaped, shapeless, cooking, factory, adv, has, drop_self, silk_or

W = f"{D}/worldgen"

# Orientation tables for doors, fence gates and buttons: state -> [model suffix, x, y, uvlock].
# Same rotations vanilla uses for its wood sets (block orientation facts, no art).
ROTATIONS = json.load(open(os.path.join(os.path.dirname(os.path.abspath(__file__)), "rotations.json")))

# =================================================================== assets
def stairs_slab(prefix, full, tex):
    t = f"{NS}:block/{tex}"
    for suffix, parent in [("", "stairs"), ("_inner", "inner_stairs"), ("_outer", "outer_stairs")]:
        write(f"{A}/models/block/{prefix}_stairs{suffix}.json", {"parent": f"minecraft:block/{parent}", "textures": {"bottom": t, "top": t, "side": t}})
    variants = {}
    for facing, y in [("east", 0), ("north", 270), ("south", 90), ("west", 180)]:
        for half in ["bottom", "top"]:
            for shape in ["inner_left", "inner_right", "outer_left", "outer_right", "straight"]:
                model = f"{prefix}_stairs" + ("_inner" if shape.startswith("inner") else "_outer" if shape.startswith("outer") else "")
                yy = y
                if shape in ("inner_left", "outer_left"):
                    yy = (y + 270) % 360 if half == "bottom" else y
                elif shape in ("inner_right", "outer_right") and half == "top":
                    yy = (y + 90) % 360
                v = {"model": f"{NS}:block/{model}"}
                if half == "top":
                    v["x"] = 180
                if yy:
                    v["y"] = yy
                if half == "top" or yy:
                    v["uvlock"] = True
                variants[f"facing={facing},half={half},shape={shape}"] = v
    write(f"{A}/blockstates/{prefix}_stairs.json", {"variants": variants})
    item_def(f"{prefix}_stairs", f"{NS}:block/{prefix}_stairs")
    write(f"{A}/models/block/{prefix}_slab.json", {"parent": "minecraft:block/slab", "textures": {"bottom": t, "top": t, "side": t}})
    write(f"{A}/models/block/{prefix}_slab_top.json", {"parent": "minecraft:block/slab_top", "textures": {"bottom": t, "top": t, "side": t}})
    write(f"{A}/blockstates/{prefix}_slab.json", {"variants": {
        "type=bottom": {"model": f"{NS}:block/{prefix}_slab"},
        "type=double": {"model": f"{NS}:block/{full}"},
        "type=top": {"model": f"{NS}:block/{prefix}_slab_top"}}})
    item_def(f"{prefix}_slab", f"{NS}:block/{prefix}_slab")

def rotated_state(name, table):
    v = {}
    for key, (suffix, x, y, uv) in table.items():
        m = {"model": f"{NS}:block/{name}{suffix}"}
        if x:
            m["x"] = x
        if y:
            m["y"] = y
        if uv:
            m["uvlock"] = True
        v[key] = m
    write(f"{A}/blockstates/{name}.json", {"variants": v})

def wood_set(wood="sweetwood"):
    """Stairs, slab, fence, gate, door, button and pressure plate models for a wood set (also used by stage 8)."""
    planks = f"{NS}:block/{wood}_planks"
    stairs_slab(wood, f"{wood}_planks", f"{wood}_planks")
    # fence
    for part, parent in [("post", "fence_post"), ("side", "fence_side"), ("inventory", "fence_inventory")]:
        write(f"{A}/models/block/{wood}_fence_{part}.json", {"parent": f"minecraft:block/{parent}", "textures": {"texture": planks}})
    mp = [{"apply": {"model": f"{NS}:block/{wood}_fence_post"}}]
    for d, y in [("north", 0), ("east", 90), ("south", 180), ("west", 270)]:
        ap = {"model": f"{NS}:block/{wood}_fence_side", "uvlock": True}
        if y:
            ap["y"] = y
        mp.append({"apply": ap, "when": {d: "true"}})
    write(f"{A}/blockstates/{wood}_fence.json", {"multipart": mp})
    item_def(f"{wood}_fence", f"{NS}:block/{wood}_fence_inventory")
    # fence gate
    for suffix, parent in [("", "template_fence_gate"), ("_open", "template_fence_gate_open"),
                           ("_wall", "template_fence_gate_wall"), ("_wall_open", "template_fence_gate_wall_open")]:
        write(f"{A}/models/block/{wood}_fence_gate{suffix}.json", {"parent": f"minecraft:block/{parent}", "textures": {"texture": planks}})
    rotated_state(f"{wood}_fence_gate", ROTATIONS["fence_gate"])
    item_def(f"{wood}_fence_gate", f"{NS}:block/{wood}_fence_gate")
    # door
    for half in ["bottom", "top"]:
        for side in ["left", "right"]:
            for op in ["", "_open"]:
                write(f"{A}/models/block/{wood}_door_{half}_{side}{op}.json", {"parent": f"minecraft:block/door_{half}_{side}{op}",
                    "render_type": "minecraft:cutout",
                    "textures": {"bottom": f"{NS}:block/{wood}_door_bottom", "top": f"{NS}:block/{wood}_door_top"}})
    rotated_state(f"{wood}_door", ROTATIONS["door"])
    flat_item(f"{wood}_door")
    # button and pressure plate
    for suffix, parent in [("", "button"), ("_pressed", "button_pressed"), ("_inventory", "button_inventory")]:
        write(f"{A}/models/block/{wood}_button{suffix}.json", {"parent": f"minecraft:block/{parent}", "textures": {"texture": planks}})
    rotated_state(f"{wood}_button", ROTATIONS["button"])
    item_def(f"{wood}_button", f"{NS}:block/{wood}_button_inventory")
    write(f"{A}/models/block/{wood}_pressure_plate.json", {"parent": "minecraft:block/pressure_plate_up", "textures": {"texture": planks}})
    write(f"{A}/models/block/{wood}_pressure_plate_down.json", {"parent": "minecraft:block/pressure_plate_down", "textures": {"texture": planks}})
    write(f"{A}/blockstates/{wood}_pressure_plate.json", {"variants": {
        "powered=false": {"model": f"{NS}:block/{wood}_pressure_plate"},
        "powered=true": {"model": f"{NS}:block/{wood}_pressure_plate_down"}}})
    item_def(f"{wood}_pressure_plate", f"{NS}:block/{wood}_pressure_plate")

JELLY_COLOURS = ["green", "yellow", "red", "orange", "purple"]

def gen_assets():
    wood_set()
    # gingerbread
    G.block_cube("gingerbread_block")
    write(f"{A}/models/block/frosted_gingerbread_block.json", {"parent": "minecraft:block/cube_bottom_top", "textures": {
        "top": f"{NS}:block/gingerbread_block", "bottom": f"{NS}:block/gingerbread_block", "side": f"{NS}:block/frosted_gingerbread_block"}})
    simple_state("frosted_gingerbread_block")
    item_def("frosted_gingerbread_block", f"{NS}:block/frosted_gingerbread_block")
    stairs_slab("gingerbread", "gingerbread_block", "gingerbread_block")
    # licorice plant
    write(f"{A}/models/block/red_licorice_plant.json", {"parent": "minecraft:block/cross", "render_type": "minecraft:cutout",
        "textures": {"cross": f"{NS}:block/red_licorice_plant"}})
    simple_state("red_licorice_plant")
    flat_item("red_licorice_plant", "block/red_licorice_plant")
    # liquids: the block model only provides the break particle; the fluid itself is drawn by the fluid model
    for liquid in ["melted_chocolate", "bubbaloo"]:
        write(f"{A}/models/block/{liquid}.json", {"textures": {"particle": f"{NS}:block/{liquid}_still"}})
        write(f"{A}/blockstates/{liquid}.json", {"variants": {"": {"model": f"{NS}:block/{liquid}"}}})
        flat_item(f"{liquid}_bucket")
    # jelly bean blocks
    for c in JELLY_COLOURS:
        G.block_cube(f"{c}_jelly_bean_block")
    write(f"{A}/blockstates/orange_jelly_bean_block.json", {"variants": {
        "primed=false": {"model": f"{NS}:block/orange_jelly_bean_block"},
        "primed=true": {"model": f"{NS}:block/orange_jelly_bean_block"}}})
    simple_state("infested_purple_jelly_bean_block", f"{NS}:block/purple_jelly_bean_block")
    item_def("infested_purple_jelly_bean_block", f"{NS}:block/purple_jelly_bean_block")
    # items
    for i in ["red_licorice", "cooked_licorice", "gingerbread", "gumdrop", "roll_cake", "jelly_beans", "brown_sugar_brick",
              "dead_jelly_snake"]:
        flat_item(i)
    for i in ["brown_sugar_sword", "jelly_bazooka"]:
        handheld_item(i)
    for e in STAGE2_EGGS:
        flat_item(f"{e}_spawn_egg")

STAGE2_EGGS = ["brown_sugar_rhino", "cotton_candy_tornado", "bubblegum", "gingerbread_warrior", "gingerbread_soldier",
               "jelly_bunny", "jelly_snake", "jelly_shark", "roll_cake_monster", "bubbaloo_creeper"]

def gen_sounds():
    path = f"{A}/sounds.json"
    sounds = json.load(open(path, encoding="utf-8"))
    def ev(names, subtitle, pitch=1.0, volume=1.0):
        return {"subtitle": f"subtitles.{NS}.{subtitle}", "sounds": [{"name": s, "pitch": pitch, "volume": volume} for s in names]}
    def mob(name, ambient, hurt, death, pitch, volume=1.0):
        sounds[f"entity.{name}.ambient"] = ev(ambient, f"{name}.ambient", pitch, volume)
        sounds[f"entity.{name}.hurt"] = ev(hurt, f"{name}.hurt", pitch, volume)
        sounds[f"entity.{name}.death"] = ev(death, f"{name}.death", pitch, volume)
    # Provisional: re-pitched vanilla sounds until original recordings exist.
    mob("brown_sugar_rhino", ["minecraft:mob/hoglin/idle1", "minecraft:mob/hoglin/idle2"], ["minecraft:mob/hoglin/hurt1", "minecraft:mob/hoglin/hurt2"],
        ["minecraft:mob/hoglin/death1"], 0.8)
    mob("cotton_candy_tornado", ["minecraft:mob/breeze/idle1", "minecraft:mob/breeze/idle2"], ["minecraft:mob/breeze/hurt1", "minecraft:mob/breeze/hurt2"],
        ["minecraft:mob/breeze/death1"], 1.3, 0.8)
    mob("bubblegum", ["minecraft:mob/slime/small1", "minecraft:mob/slime/small2"], ["minecraft:mob/slime/small3", "minecraft:mob/slime/small4"],
        ["minecraft:mob/slime/small5"], 1.4)
    mob("gingerbread", ["minecraft:dig/wood1", "minecraft:dig/wood2"], ["minecraft:dig/wood3", "minecraft:dig/wood4"],
        ["minecraft:random/break"], 1.5, 0.7)
    mob("jelly_bunny", ["minecraft:mob/rabbit/idle1", "minecraft:mob/rabbit/idle2"], ["minecraft:mob/rabbit/hurt1", "minecraft:mob/rabbit/hurt2"],
        ["minecraft:mob/rabbit/bunnymurder"], 1.2)
    mob("jelly_snake", ["minecraft:mob/silverfish/say1", "minecraft:mob/silverfish/say2"], ["minecraft:mob/silverfish/hit1", "minecraft:mob/silverfish/hit2"],
        ["minecraft:mob/silverfish/kill"], 0.8)
    mob("jelly_shark", ["minecraft:mob/guardian/land_idle1", "minecraft:mob/guardian/land_idle2"], ["minecraft:mob/guardian/guardian_hit1", "minecraft:mob/guardian/guardian_hit2"],
        ["minecraft:mob/guardian/guardian_death"], 0.7)
    mob("roll_cake_monster", ["minecraft:mob/slime/big1", "minecraft:mob/slime/big2"], ["minecraft:mob/slime/big3", "minecraft:mob/slime/big4"],
        ["minecraft:mob/slime/attack1"], 0.6)
    sounds["entity.bubblegum.pop"] = ev(["minecraft:random/pop"], "bubblegum.pop", 0.5, 1.0)
    sounds["entity.gingerbread_soldier.shoot"] = ev(["minecraft:random/bow"], "gingerbread_soldier.shoot", 1.6, 0.8)
    write(path, sounds)

# =================================================================== loot
def gen_loot():
    for b in ["sweetwood_stairs", "sweetwood_fence", "sweetwood_fence_gate", "sweetwood_button", "sweetwood_pressure_plate",
              "gingerbread_block", "frosted_gingerbread_block", "gingerbread_stairs",
              "green_jelly_bean_block", "yellow_jelly_bean_block", "red_jelly_bean_block", "orange_jelly_bean_block", "purple_jelly_bean_block"]:
        write(f"{D}/loot_table/blocks/{b}.json", drop_self(b))
    for slab in ["sweetwood_slab", "gingerbread_slab"]:
        write(f"{D}/loot_table/blocks/{slab}.json", {"type": "minecraft:block", "random_sequence": f"{NS}:blocks/{slab}",
            "pools": [{"rolls": 1.0, "bonus_rolls": 0.0, "entries": [{"type": "minecraft:item", "name": f"{NS}:{slab}", "functions": [
                {"function": "minecraft:set_count", "add": False, "count": 2.0, "conditions": [{"condition": "minecraft:block_state_property",
                 "block": f"{NS}:{slab}", "properties": {"type": "double"}}]}, {"function": "minecraft:explosion_decay"}]}]}]})
    write(f"{D}/loot_table/blocks/sweetwood_door.json", {"type": "minecraft:block", "random_sequence": f"{NS}:blocks/sweetwood_door",
        "pools": [{"rolls": 1.0, "bonus_rolls": 0.0, "conditions": [{"condition": "minecraft:survives_explosion"}], "entries": [
            {"type": "minecraft:item", "name": f"{NS}:sweetwood_door", "conditions": [{"condition": "minecraft:block_state_property",
             "block": f"{NS}:sweetwood_door", "properties": {"half": "lower"}}]}]}]})
    write(f"{D}/loot_table/blocks/infested_purple_jelly_bean_block.json", {"type": "minecraft:block",
        "random_sequence": f"{NS}:blocks/infested_purple_jelly_bean_block", "pools": [{"rolls": 1.0, "bonus_rolls": 0.0,
        "conditions": [{"condition": "minecraft:survives_explosion"}], "entries": [{"type": "minecraft:item", "name": f"{NS}:purple_jelly_bean_block"}]}]})
    write(f"{D}/loot_table/blocks/red_licorice_plant.json", {"type": "minecraft:block", "random_sequence": f"{NS}:blocks/red_licorice_plant",
        "pools": [{"rolls": 1.0, "bonus_rolls": 0.0, "entries": [{"type": "minecraft:alternatives", "children": [
            {"type": "minecraft:item", "name": f"{NS}:red_licorice_plant", "conditions": [G.SHEARS_OR_SILK]},
            {"type": "minecraft:item", "name": f"{NS}:red_licorice", "functions": [
                {"function": "minecraft:set_count", "add": False, "count": {"type": "minecraft:uniform", "min": 1.0, "max": 3.0}},
                {"function": "minecraft:apply_bonus", "enchantment": "minecraft:fortune", "formula": "minecraft:uniform_bonus_count", "parameters": {"bonusMultiplier": 1}},
                {"function": "minecraft:explosion_decay"}]}]}]}]})
    for liquid in ["melted_chocolate", "bubbaloo"]:
        write(f"{D}/loot_table/blocks/{liquid}.json", {"type": "minecraft:block", "pools": []})

    def entity(name, pools):
        write(f"{D}/loot_table/entities/{name}.json", {"type": "minecraft:entity", "random_sequence": f"{NS}:entities/{name}", "pools": pools})
    def pool(item, lo, hi, looting=True, chance=None):
        fns = [{"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}, "add": False}]
        if looting:
            fns.append({"function": "minecraft:enchanted_count_increase", "enchantment": "minecraft:looting", "count": {"type": "minecraft:uniform", "min": 0.0, "max": 1.0}})
        p = {"rolls": 1.0, "bonus_rolls": 0.0, "entries": [{"type": "minecraft:item", "name": ns(item), "functions": fns}]}
        if chance is not None:
            p["conditions"] = [{"condition": "minecraft:random_chance", "chance": chance}]
        return p
    entity("brown_sugar_rhino", [pool("brown_sugar_brick", 1.0, 3.0)])
    entity("cotton_candy_tornado", [pool("cotton_candy", 0.0, 1.0), pool("minecraft:string", 0.0, 1.0)])
    entity("bubblegum", [pool("minecraft:pink_dye", 0.0, 1.0), pool("minecraft:slime_ball", 0.0, 1.0)])
    entity("gingerbread_warrior", [pool("gingerbread", 1.0, 2.0)])
    entity("gingerbread_soldier", [pool("gingerbread", 1.0, 2.0), pool("gumdrop", 0.0, 2.0)])
    entity("jelly_bunny", [pool("jelly_beans", 0.0, 2.0)])
    entity("jelly_snake", [pool("dead_jelly_snake", 1.0, 1.0)])
    entity("jelly_shark", [pool("jelly_beans", 1.0, 4.0), pool("minecraft:prismarine_shard", 0.0, 1.0, chance=0.2)])
    entity("roll_cake_monster", [pool("roll_cake", 1.0, 2.0)])
    entity("bubbaloo_creeper", [pool("minecraft:gunpowder", 0.0, 2.0), pool("minecraft:gunpowder", 1.0, 1.0, looting=False)])

    def e(item, weight, lo, hi):
        return {"type": "minecraft:item", "name": ns(item), "weight": weight, "functions": [
            {"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}, "add": False}]}
    # Like the original gingerbreadtowerchest: three draws among sweets, gingerbread and some metal.
    write(f"{D}/loot_table/chests/gingerbread_tower.json", {"type": "minecraft:chest", "random_sequence": f"{NS}:chests/gingerbread_tower", "pools": [
        {"rolls": 3.0, "bonus_rolls": 0.0, "entries": [
            e("lollipop", 15, 1, 3), e("chocolate_bar", 15, 1, 4), e("chocolate_block", 6, 1, 2), e("minecraft:cookie", 12, 2, 6),
            e("gingerbread", 12, 1, 4), e("frosted_gingerbread_block", 8, 1, 3), e("minecraft:iron_ingot", 8, 1, 4),
            e("minecraft:gold_ingot", 5, 1, 3), e("gumdrop", 8, 2, 5)]},
        {"rolls": 1.0, "bonus_rolls": 0.0, "entries": [
            {"type": "minecraft:empty", "weight": 8},
            {"type": "minecraft:item", "name": f"{NS}:brown_sugar_sword", "weight": 2, "functions": [
                {"function": "minecraft:set_damage", "damage": {"type": "minecraft:uniform", "min": 0.3, "max": 0.9}}]},
            {"type": "minecraft:item", "name": f"{NS}:melted_chocolate_bucket", "weight": 1}]}]})

# =================================================================== recipes
def gen_recipes():
    b = "building"
    shaped("sweetwood_stairs", ["#  ", "## ", "###"], {"#": "sweetwood_planks"}, "sweetwood_stairs", 4, b, "wooden_stairs")
    shaped("sweetwood_slab", ["###"], {"#": "sweetwood_planks"}, "sweetwood_slab", 6, b, "wooden_slab")
    shaped("sweetwood_fence", ["W#W", "W#W"], {"W": "sweetwood_planks", "#": "minecraft:stick"}, "sweetwood_fence", 3, "misc", "wooden_fence")
    shaped("sweetwood_fence_gate", ["#W#", "#W#"], {"W": "sweetwood_planks", "#": "minecraft:stick"}, "sweetwood_fence_gate", 1, "redstone", "wooden_fence_gate")
    shaped("sweetwood_door", ["##", "##", "##"], {"#": "sweetwood_planks"}, "sweetwood_door", 3, "redstone", "wooden_door")
    shapeless("sweetwood_button", ["sweetwood_planks"], "sweetwood_button", 1, "redstone")
    shaped("sweetwood_pressure_plate", ["##"], {"#": "sweetwood_planks"}, "sweetwood_pressure_plate", 1, "redstone")

    # Gingerbread (revival recipes: the original only drops it from gingerbread mobs).
    shapeless("gingerbread", ["minecraft:wheat", "minecraft:wheat", "minecraft:sugar"], "gingerbread", 2, "misc")
    shaped("gingerbread_block", ["##", "##"], {"#": "gingerbread"}, "gingerbread_block", 1, b)
    shapeless("gingerbread_from_block", ["gingerbread_block"], "gingerbread", 4)
    shapeless("frosted_gingerbread_block", ["gingerbread_block", "minecraft:sugar"], "frosted_gingerbread_block", 1, b)
    shaped("gingerbread_stairs", ["#  ", "## ", "###"], {"#": "gingerbread_block"}, "gingerbread_stairs", 4, b)
    shaped("gingerbread_slab", ["###"], {"#": "gingerbread_block"}, "gingerbread_slab", 6, b)

    for kind, time in [("smelting", 200), ("smoking", 100), ("campfire_cooking", 600)]:
        cooking(f"cooked_licorice_from_{kind}", kind, "red_licorice", "cooked_licorice", 0.35, time)
    shaped("brown_sugar_sword", ["#", "#", "/"], {"#": "brown_sugar_brick", "/": "minecraft:stick"}, "brown_sugar_sword", 1, "equipment")
    shaped("jelly_bazooka", ["GRO", "PYS"], {"G": "green_jelly_bean_block", "R": "red_jelly_bean_block", "O": "orange_jelly_bean_block",
        "P": "purple_jelly_bean_block", "Y": "yellow_jelly_bean_block", "S": "dead_jelly_snake"}, "jelly_bazooka", 1, "equipment")
    shapeless("jelly_beans", [f"#{NS}:jelly_bean_blocks"], "jelly_beans", 4)

    factory("chocolate_bars_from_melted_chocolate", ["melted_chocolate_bucket"], "chocolate_bar", 10, 300, 0.5)
    factory("gumdrops", ["minecraft:sugar", "minecraft:red_dye"], "gumdrop", 4, 120, 0.1)

# =================================================================== tags
def gen_tags():
    n = lambda *xs: [ns(x) for x in xs]
    jelly = [f"{c}_jelly_bean_block" for c in JELLY_COLOURS] + ["infested_purple_jelly_bean_block"]
    tag("block", NS, "jelly_bean_blocks", n(*jelly))
    tag("item", NS, "jelly_bean_blocks", n(*jelly[:5]))
    tag("item", NS, "brown_sugar_repair_materials", n("brown_sugar_brick"))
    tag("item", NS, "jelly_bunny_food", n("jelly_beans"))
    tag("item", NS, "sweets", n("red_licorice", "cooked_licorice", "gingerbread", "gumdrop", "roll_cake", "jelly_beans"))
    tag("item", NS, "candy_tube_dog_food", n("red_licorice", "cooked_licorice", "gumdrop", "jelly_beans"))
    tag("block", NS, "sweet_ground", n())
    tag("worldgen/biome", NS, "has_structure/gingerbread_tower", n("sweet_forest"))

    tag("block", "minecraft", "mineable/axe", n("sweetwood_stairs", "sweetwood_slab", "sweetwood_fence", "sweetwood_fence_gate", "sweetwood_door",
        "sweetwood_button", "sweetwood_pressure_plate", "gingerbread_block", "frosted_gingerbread_block", "gingerbread_stairs", "gingerbread_slab"))
    tag("block", "minecraft", "mineable/shovel", n(*jelly))
    for kind in ["block", "item"]:
        tag(kind, "minecraft", "wooden_stairs", n("sweetwood_stairs"))
        tag(kind, "minecraft", "wooden_slabs", n("sweetwood_slab"))
        tag(kind, "minecraft", "wooden_fences", n("sweetwood_fence"))
        tag(kind, "minecraft", "fence_gates", n("sweetwood_fence_gate"))
        tag(kind, "minecraft", "wooden_doors", n("sweetwood_door"))
        tag(kind, "minecraft", "wooden_buttons", n("sweetwood_button"))
        tag(kind, "minecraft", "wooden_pressure_plates", n("sweetwood_pressure_plate"))
        tag(kind, "minecraft", "stairs", n("gingerbread_stairs"))
        tag(kind, "minecraft", "slabs", n("gingerbread_slab"))
    tag("block", "minecraft", "unstable_bottom_center", n("sweetwood_fence_gate"))
    tag("block", "minecraft", "animals_spawnable_on", n("green_jelly_bean_block"))
    tag("block", "minecraft", "enderman_holdable", n("red_licorice_plant"))
    tag("block", "minecraft", "sword_efficient", n("red_licorice_plant"))
    tag("item", "minecraft", "swords", n("brown_sugar_sword"))
    tag("item", "c", "foods", n("red_licorice", "cooked_licorice", "gingerbread", "gumdrop", "roll_cake", "jelly_beans"))
    tag("item", "c", "foods/candy", n("red_licorice", "cooked_licorice", "gumdrop", "jelly_beans"))
    tag("item", "c", "buckets/melted_chocolate", n("melted_chocolate_bucket"))

    for t in [("minecraft", "is_overworld"), ("c", "is_overworld"), ("c", "is_plains"), (NS, "is_candy"),
              ("minecraft", "has_structure/mineshaft"), ("minecraft", "has_structure/stronghold"), ("minecraft", "has_structure/ruined_portal_standard"),
              ("minecraft", "has_structure/trail_ruins"), ("minecraft", "has_structure/trial_chambers")]:
        tag("worldgen/biome", t[0], t[1], n("jelly_bean_fields"))
    for t in [("minecraft", "has_structure/mineshaft"), ("minecraft", "has_structure/stronghold"), ("minecraft", "has_structure/ruined_portal_standard"),
              ("minecraft", "has_structure/trial_chambers")]:
        tag("worldgen/biome", t[0], t[1], n("sweet_forest"))

# =================================================================== worldgen
def gen_worldgen():
    # --- Sweet Forest -----------------------------------------------------------------
    # Candy trees like the original: straight trunk 7 + up to 2 blocks, rounded magenta crown.
    write(f"{W}/configured_feature/tall_sweetwood_tree.json", G.tree(7, 2, 3, 4))
    write(f"{W}/configured_feature/sweetwood_tree.json", G.tree(5, 2, 2, 3))
    write(f"{W}/placed_feature/tall_sweetwood_trees.json", {"feature": f"{NS}:tall_sweetwood_tree", "placement": G.surface_tree_placement(
        {"type": "minecraft:weighted_list", "distribution": [{"data": 0, "weight": 2}, {"data": 1, "weight": 5}, {"data": 2, "weight": 3}]})})
    write(f"{W}/placed_feature/sweetwood_trees.json", {"feature": f"{NS}:sweetwood_tree", "placement": G.surface_tree_placement(
        {"type": "minecraft:weighted_list", "distribution": [{"data": 0, "weight": 3}, {"data": 1, "weight": 3}, {"data": 2, "weight": 1}]})})

    write(f"{W}/configured_feature/red_licorice_plant.json", {"type": "minecraft:simple_block", "config": {
        "to_place": {"type": "minecraft:simple_state_provider", "state": state("red_licorice_plant")}}})
    write(f"{W}/placed_feature/red_licorice_plants.json", {"feature": f"{NS}:red_licorice_plant", "placement": [
        {"type": "minecraft:rarity_filter", "chance": 3}, {"type": "minecraft:in_square"},
        {"type": "minecraft:heightmap", "heightmap": "MOTION_BLOCKING"}, {"type": "minecraft:biome"},
        {"type": "minecraft:count", "count": 20},
        {"type": "minecraft:random_offset", "xz_spread": {"type": "minecraft:trapezoid", "max": 5, "min": -5, "plateau": 0},
         "y_spread": {"type": "minecraft:trapezoid", "max": 2, "min": -2, "plateau": 0}},
        {"type": "minecraft:block_predicate_filter", "predicate": {"type": "minecraft:all_of", "predicates": [
            {"type": "minecraft:matching_block_tag", "tag": "minecraft:air"},
            {"type": "minecraft:would_survive", "state": state("red_licorice_plant")}]}}]})

    write(f"{W}/configured_feature/lake_melted_chocolate.json", {"type": "minecraft:lake", "config": {
        "fluid": {"type": "minecraft:simple_state_provider", "state": state("melted_chocolate", level=0)},
        "barrier": {"type": "minecraft:simple_state_provider", "state": state("chocolate_soil")}}})
    write(f"{W}/placed_feature/lake_melted_chocolate.json", {"feature": f"{NS}:lake_melted_chocolate", "placement": [
        {"type": "minecraft:rarity_filter", "chance": 12}, {"type": "minecraft:in_square"},
        {"type": "minecraft:heightmap", "heightmap": "WORLD_SURFACE_WG"}, {"type": "minecraft:biome"}]})

    def spawn(t, w, lo, hi):
        return {"type": ns(t), "weight": w, "minCount": lo, "maxCount": hi}
    vanilla_monsters = [spawn("minecraft:zombie", 95, 4, 4), spawn("minecraft:zombie_villager", 5, 1, 1), spawn("minecraft:skeleton", 100, 4, 4),
                        spawn("minecraft:creeper", 100, 4, 4), spawn("minecraft:spider", 100, 4, 4), spawn("minecraft:enderman", 10, 1, 4),
                        spawn("minecraft:witch", 5, 1, 1)]
    write(f"{W}/biome/sweet_forest.json", {
        "attributes": {
            "minecraft:audio/background_music": {"default": {"max_delay": 24000, "min_delay": 12000, "sound": "minecraft:music.overworld.cherry_grove"}},
            # Original colours: pink sky and fog #FF99FF, pink water #FF33CC.
            "minecraft:visual/fog_color": "#ff99ff", "minecraft:visual/sky_color": "#ff99ff", "minecraft:visual/water_fog_color": "#a3207f"},
        "carvers": ["minecraft:cave", "minecraft:cave_extra_underground", "minecraft:canyon"],
        "downfall": 0.8,
        "effects": {"foliage_color": "#c35bb8", "grass_color": "#ff6dff", "water_color": "#ff33cc"},
        "features": [[], [f"{NS}:lake_melted_chocolate", "minecraft:lake_lava_underground"], ["minecraft:amethyst_geode"],
                     ["minecraft:monster_room", "minecraft:monster_room_deep"], [], [], G.VANILLA_UNDERGROUND, [],
                     ["minecraft:spring_water", "minecraft:spring_lava"],
                     ["minecraft:glow_lichen", f"{NS}:tall_sweetwood_trees", f"{NS}:sweetwood_trees", f"{NS}:red_licorice_plants", f"{NS}:lollipop_flowers"],
                     ["minecraft:freeze_top_layer"]],
        "has_precipitation": True,
        "spawn_costs": {},
        "spawners": {
            "ambient": [spawn("minecraft:bat", 10, 8, 8)], "axolotls": [],
            "creature": [spawn("candy_tube_dog", 10, 1, 2), spawn("lollipop_guy", 10, 1, 1), spawn("brown_sugar_rhino", 5, 1, 1)],
            "misc": [],
            "monster": [spawn("cotton_candy_tornado", 20, 1, 1), spawn("bubblegum", 20, 1, 1)] + vanilla_monsters,
            "underground_water_creature": [spawn("minecraft:glow_squid", 10, 4, 6)], "water_ambient": [], "water_creature": []},
        "temperature": 0.7})

    # Gingerbread Tower: single-piece jigsaw like the Cookie Hut; spacing follows the original (18 chunks).
    write(f"{W}/structure/gingerbread_tower.json", {"type": "minecraft:jigsaw", "biomes": f"#{NS}:has_structure/gingerbread_tower",
        "step": "surface_structures", "spawn_overrides": {}, "terrain_adaptation": "beard_thin",
        "start_pool": f"{NS}:gingerbread_tower/start", "size": 1, "start_height": {"absolute": 0},
        "project_start_to_heightmap": "WORLD_SURFACE_WG", "max_distance_from_center": 80, "use_expansion_hack": False})
    write(f"{W}/template_pool/gingerbread_tower/start.json", {"fallback": "minecraft:empty", "elements": [{"weight": 1, "element": {
        "element_type": "minecraft:single_pool_element", "location": f"{NS}:gingerbread_tower", "projection": "rigid", "processors": "minecraft:empty"}}]})
    for freq, spacing, separation in [("rare", 28, 12), ("normal", 18, 8), ("common", 12, 5)]:
        write(f"{W}/structure_set/gingerbread_towers_{freq}.json", {
            "neoforge:conditions": [{"type": f"{NS}:structure_frequency", "frequency": freq, "structure": "gingerbread_tower"}],
            "structures": [{"structure": f"{NS}:gingerbread_tower", "weight": 1}],
            "placement": {"type": "minecraft:random_spread", "spacing": spacing, "separation": separation, "salt": 541127893}})

    # --- Jelly Bean Fields -----------------------------------------------------------
    write(f"{W}/configured_feature/orange_jelly_patch.json", {"type": "minecraft:disk", "config": {
        "half_height": 1, "radius": {"type": "minecraft:uniform", "min_inclusive": 2, "max_inclusive": 4},
        "state_provider": {"type": "minecraft:simple_state_provider", "state": state("orange_jelly_bean_block", primed=False)},
        "target": {"type": "minecraft:matching_blocks", "blocks": [ns("green_jelly_bean_block")]}}})
    write(f"{W}/placed_feature/orange_jelly_patches.json", {"feature": f"{NS}:orange_jelly_patch", "placement": [
        {"type": "minecraft:rarity_filter", "chance": 5}, {"type": "minecraft:in_square"},
        {"type": "minecraft:heightmap", "heightmap": "WORLD_SURFACE_WG"}, {"type": "minecraft:biome"}]})
    write(f"{W}/configured_feature/purple_jelly_pile.json", {"type": "minecraft:block_pile", "config": {
        "state_provider": {"type": "minecraft:weighted_state_provider", "entries": [
            {"data": state("purple_jelly_bean_block"), "weight": 3}, {"data": state("infested_purple_jelly_bean_block"), "weight": 1}]}}})
    write(f"{W}/placed_feature/purple_jelly_piles.json", {"feature": f"{NS}:purple_jelly_pile", "placement": [
        {"type": "minecraft:rarity_filter", "chance": 5}, {"type": "minecraft:in_square"},
        {"type": "minecraft:heightmap", "heightmap": "WORLD_SURFACE_WG"}, {"type": "minecraft:biome"}]})

    write(f"{W}/biome/jelly_bean_fields.json", {
        "attributes": {
            "minecraft:audio/background_music": {"default": {"max_delay": 24000, "min_delay": 12000, "sound": "minecraft:music.overworld.meadow"}},
            # Original colours: light green sky #99FF99, green fog #66FF33, red water #FF3333 with red underwater fog.
            "minecraft:visual/fog_color": "#66ff33", "minecraft:visual/sky_color": "#99ff99", "minecraft:visual/water_fog_color": "#ff0000"},
        "carvers": ["minecraft:cave", "minecraft:cave_extra_underground", "minecraft:canyon"],
        "downfall": 0.5,
        "effects": {"foliage_color": "#4bbf3f", "grass_color": "#4bbf3f", "water_color": "#ff3333"},
        "features": [[], ["minecraft:lake_lava_underground", "minecraft:lake_lava_surface"], ["minecraft:amethyst_geode"],
                     ["minecraft:monster_room", "minecraft:monster_room_deep"], [], [], G.VANILLA_UNDERGROUND, [],
                     ["minecraft:spring_water", "minecraft:spring_lava"],
                     ["minecraft:glow_lichen", f"{NS}:orange_jelly_patches", f"{NS}:purple_jelly_piles"],
                     ["minecraft:freeze_top_layer"]],
        "has_precipitation": True,
        "spawn_costs": {},
        "spawners": {
            "ambient": [spawn("minecraft:bat", 10, 8, 8)], "axolotls": [],
            "creature": [spawn("jelly_bunny", 20, 1, 2)],
            "misc": [],
            "monster": [spawn("jelly_snake", 10, 1, 2), spawn("roll_cake_monster", 5, 1, 1), spawn("bubbaloo_creeper", 20, 1, 1),
                        spawn("jelly_shark", 5, 1, 1)],
            "underground_water_creature": [spawn("minecraft:glow_squid", 10, 4, 6)], "water_ambient": [], "water_creature": []},
        "temperature": 0.5})

# =================================================================== advancements and damage
def gen_advancements():
    def biome(b):
        return {"trigger": "minecraft:location", "conditions": {"player": [{"condition": "minecraft:entity_properties", "entity": "this",
                "predicate": {"location": {"biomes": f"{NS}:{b}"}}}]}}
    def hit_with(projectile):
        return {"trigger": "minecraft:player_hurt_entity", "conditions": {"damage": {"type": {"direct_entity": {"type": f"{NS}:{projectile}"}}}}}
    adv("ouch", "enter_sweet_forest", "brown_sugar_brick", "task", {"hit": hit_with("brown_sugar_brick")})
    adv("cotton_candy_catch", "enter_sweet_forest", "cotton_candy", "goal", {"catch": {"trigger": "minecraft:impossible"}})
    adv("licorice_harvest", "enter_sweet_forest", "red_licorice", "task", {"licorice": has("red_licorice")})
    adv("chocolate_river", "factory_floor", "melted_chocolate_bucket", "task", {"bucket": has("melted_chocolate_bucket")})
    adv("visit_gingerbread_tower", "enter_sweet_forest", "frosted_gingerbread_block", "task", {"tower": {"trigger": "minecraft:location", "conditions": {"player": [
        {"condition": "minecraft:entity_properties", "entity": "this", "predicate": {"location": {"structures": f"{NS}:gingerbread_tower"}}}]}}})
    adv("enter_jelly_bean_fields", "root", "green_jelly_bean_block", "task", {"jelly": biome("jelly_bean_fields")})
    adv("boing", "enter_jelly_bean_fields", "jelly_bazooka", "goal", {"hit": hit_with("jelly_snake_shot")})
    write(f"{D}/damage_type/bubbaloo.json", {"exhaustion": 0.1, "message_id": f"{NS}.bubbaloo", "scaling": "when_caused_by_living_non_player"})

def generate():
    gen_assets(); gen_sounds(); gen_loot(); gen_recipes(); gen_tags(); gen_worldgen(); gen_advancements()
