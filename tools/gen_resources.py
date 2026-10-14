#!/usr/bin/env python3
"""Generates the JSON assets and data for Crazy Alloy: Revival.

Run from the project root:  python3 tools/gen_resources.py
Everything under src/main/resources/{assets,data}/crazyalloy_revival (and the few vanilla/neoforge
tag files listed here) is written by this script; edit the tables below rather than the JSON.
"""
import json, os, shutil

NS = "crazyalloy_revival"
ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src", "main", "resources")
A = os.path.join(ROOT, "assets", NS)
D = os.path.join(ROOT, "data", NS)

def ns(p): return p if ":" in p else f"{NS}:{p}"

def write(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        json.dump(obj, f, indent=2, ensure_ascii=False)
        f.write("\n")

def clean():
    for d in (os.path.join(A, "blockstates"), os.path.join(A, "models"), os.path.join(A, "items"),
              os.path.join(A, "equipment"), os.path.join(A, "lang"), D,
              os.path.join(ROOT, "data", "minecraft"), os.path.join(ROOT, "data", "c")):
        if os.path.isdir(d):
            shutil.rmtree(d)

# ---------------------------------------------------------------- assets: block models
def block_cube(name, tex=None):
    write(f"{A}/models/block/{name}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": f"{NS}:block/{tex or name}"}})
    simple_state(name)
    item_def(name, f"{NS}:block/{name}")

def simple_state(name, model=None):
    write(f"{A}/blockstates/{name}.json", {"variants": {"": {"model": model or f"{NS}:block/{name}"}}})

def item_def(name, model):
    write(f"{A}/items/{name}.json", {"model": {"type": "minecraft:model", "model": model}})

def flat_item(name, tex=None):
    write(f"{A}/models/item/{name}.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:{tex or 'item/' + name}"}})
    item_def(name, f"{NS}:item/{name}")

def handheld_item(name):
    write(f"{A}/models/item/{name}.json", {"parent": "minecraft:item/handheld", "textures": {"layer0": f"{NS}:item/{name}"}})
    item_def(name, f"{NS}:item/{name}")

def gen_block_assets():
    for b in ["chocolate_soil", "chocolate_block", "chocolate_bricks", "chiseled_chocolate_bricks",
              "tourmaline_ore", "deepslate_tourmaline_ore", "tourmaline_block", "sweetwood_planks"]:
        block_cube(b)

    # grass: dirt-like bottom, frosted top, side overlay baked into the side texture
    write(f"{A}/models/block/chocolate_grass_block.json", {"parent": "minecraft:block/cube_bottom_top", "textures": {
        "top": f"{NS}:block/chocolate_grass_block_top", "bottom": f"{NS}:block/chocolate_soil",
        "side": f"{NS}:block/chocolate_grass_block_side"}})
    write(f"{A}/blockstates/chocolate_grass_block.json", {"variants": {"": [
        {"model": f"{NS}:block/chocolate_grass_block"}, {"model": f"{NS}:block/chocolate_grass_block", "y": 90},
        {"model": f"{NS}:block/chocolate_grass_block", "y": 180}, {"model": f"{NS}:block/chocolate_grass_block", "y": 270}]}})
    item_def("chocolate_grass_block", f"{NS}:block/chocolate_grass_block")

    # log
    write(f"{A}/models/block/sweetwood_log.json", {"parent": "minecraft:block/cube_column", "textures": {
        "end": f"{NS}:block/sweetwood_log_top", "side": f"{NS}:block/sweetwood_log"}})
    write(f"{A}/models/block/sweetwood_log_horizontal.json", {"parent": "minecraft:block/cube_column_horizontal", "textures": {
        "end": f"{NS}:block/sweetwood_log_top", "side": f"{NS}:block/sweetwood_log"}})
    write(f"{A}/blockstates/sweetwood_log.json", {"variants": {
        "axis=x": {"model": f"{NS}:block/sweetwood_log_horizontal", "x": 90, "y": 90},
        "axis=y": {"model": f"{NS}:block/sweetwood_log"},
        "axis=z": {"model": f"{NS}:block/sweetwood_log_horizontal", "x": 90}}})
    item_def("sweetwood_log", f"{NS}:block/sweetwood_log")

    # leaves
    write(f"{A}/models/block/cotton_candy_leaves.json", {"parent": "minecraft:block/leaves", "textures": {"all": f"{NS}:block/cotton_candy_leaves"}})
    simple_state("cotton_candy_leaves")
    item_def("cotton_candy_leaves", f"{NS}:block/cotton_candy_leaves")

    # cross plants
    for p in ["sweetwood_sapling", "lollipop_flower"]:
        write(f"{A}/models/block/{p}.json", {"parent": "minecraft:block/cross", "render_type": "minecraft:cutout", "textures": {"cross": f"{NS}:block/{p}"}})
        simple_state(p)
        flat_item(p, f"block/{p}")

    # stairs / slab / wall
    t = f"{NS}:block/chocolate_bricks"
    for suffix, parent in [("", "stairs"), ("_inner", "inner_stairs"), ("_outer", "outer_stairs")]:
        write(f"{A}/models/block/chocolate_brick_stairs{suffix}.json", {"parent": f"minecraft:block/{parent}", "textures": {"bottom": t, "top": t, "side": t}})
    variants = {}
    for facing, y in [("east", 0), ("north", 270), ("south", 90), ("west", 180)]:
        for half in ["bottom", "top"]:
            for shape in ["inner_left", "inner_right", "outer_left", "outer_right", "straight"]:
                model = "chocolate_brick_stairs" + ("_inner" if shape.startswith("inner") else "_outer" if shape.startswith("outer") else "")
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
    write(f"{A}/blockstates/chocolate_brick_stairs.json", {"variants": variants})
    item_def("chocolate_brick_stairs", f"{NS}:block/chocolate_brick_stairs")

    write(f"{A}/models/block/chocolate_brick_slab.json", {"parent": "minecraft:block/slab", "textures": {"bottom": t, "top": t, "side": t}})
    write(f"{A}/models/block/chocolate_brick_slab_top.json", {"parent": "minecraft:block/slab_top", "textures": {"bottom": t, "top": t, "side": t}})
    write(f"{A}/blockstates/chocolate_brick_slab.json", {"variants": {
        "type=bottom": {"model": f"{NS}:block/chocolate_brick_slab"},
        "type=double": {"model": f"{NS}:block/chocolate_bricks"},
        "type=top": {"model": f"{NS}:block/chocolate_brick_slab_top"}}})
    item_def("chocolate_brick_slab", f"{NS}:block/chocolate_brick_slab")

    for part, parent in [("post", "template_wall_post"), ("side", "template_wall_side"), ("side_tall", "template_wall_side_tall"), ("inventory", "wall_inventory")]:
        write(f"{A}/models/block/chocolate_brick_wall_{part}.json", {"parent": f"minecraft:block/{parent}", "textures": {"wall": t}})
    mp = [{"when": {"up": "true"}, "apply": {"model": f"{NS}:block/chocolate_brick_wall_post"}}]
    for d, y in [("north", 0), ("east", 90), ("south", 180), ("west", 270)]:
        for h, m in [("low", "side"), ("tall", "side_tall")]:
            ap = {"model": f"{NS}:block/chocolate_brick_wall_{m}", "uvlock": True}
            if y:
                ap["y"] = y
            else:
                del ap["uvlock"]
            mp.append({"when": {d: h}, "apply": ap})
    write(f"{A}/blockstates/chocolate_brick_wall.json", {"multipart": mp})
    item_def("chocolate_brick_wall", f"{NS}:block/chocolate_brick_wall_inventory")

    # chocolate factory: orientable, lit variant
    for lit in [False, True]:
        name = "chocolate_factory_on" if lit else "chocolate_factory"
        write(f"{A}/models/block/{name}.json", {"parent": "minecraft:block/orientable", "textures": {
            "top": f"{NS}:block/chocolate_factory_top",
            "front": f"{NS}:block/chocolate_factory_front{'_on' if lit else ''}",
            "side": f"{NS}:block/chocolate_factory_side"}})
    v = {}
    for facing, y in [("north", 0), ("east", 90), ("south", 180), ("west", 270)]:
        for lit in ["false", "true"]:
            m = {"model": f"{NS}:block/chocolate_factory{'_on' if lit == 'true' else ''}"}
            if y:
                m["y"] = y
            v[f"facing={facing},lit={lit}"] = m
    write(f"{A}/blockstates/chocolate_factory.json", {"variants": v})
    item_def("chocolate_factory", f"{NS}:block/chocolate_factory")

ITEMS_FLAT = ["tourmaline", "cocoa_powder", "candy_tube", "chocolate_bar", "milk_chocolate", "lollipop", "cotton_candy", "grape",
              "tourmaline_helmet", "tourmaline_chestplate", "tourmaline_leggings", "tourmaline_boots"]
ITEMS_HANDHELD = ["tourmaline_sword", "tourmaline_pickaxe", "tourmaline_axe", "tourmaline_shovel", "tourmaline_hoe"]
SPAWN_EGGS = ["candy_tube_dog", "lollipop_guy", "grape_spider"]

def gen_item_assets():
    for i in ITEMS_FLAT:
        flat_item(i)
    for i in ITEMS_HANDHELD:
        handheld_item(i)
    for e in SPAWN_EGGS:
        flat_item(f"{e}_spawn_egg")
    write(f"{A}/equipment/tourmaline.json", {"layers": {
        "humanoid": [{"texture": f"{NS}:tourmaline"}],
        "humanoid_leggings": [{"texture": f"{NS}:tourmaline"}]}})

def gen_sounds():
    def ev(sounds, subtitle, pitch=1.0, volume=1.0):
        return {"subtitle": f"subtitles.{NS}.{subtitle}",
                "sounds": [{"name": s, "pitch": pitch, "volume": volume} for s in sounds]}
    write(f"{A}/sounds.json", {
        # Provisional: re-pitched vanilla sounds until original recordings exist.
        "entity.candy_tube_dog.ambient": ev(["minecraft:mob/wolf/cute/bark1", "minecraft:mob/wolf/cute/bark2", "minecraft:mob/wolf/cute/bark3"], "candy_tube_dog.ambient", 1.45, 0.7),
        "entity.candy_tube_dog.hurt": ev(["minecraft:mob/wolf/cute/hurt1", "minecraft:mob/wolf/cute/hurt2"], "candy_tube_dog.hurt", 1.45),
        "entity.candy_tube_dog.death": ev(["minecraft:mob/wolf/cute/death"], "candy_tube_dog.death", 1.45),
        "entity.candy_tube_dog.shed": ev(["minecraft:mob/chicken/plop"], "candy_tube_dog.shed", 0.8),
        "entity.lollipop_guy.ambient": ev(["minecraft:block/amethyst/shimmer"], "lollipop_guy.ambient", 1.3, 0.8),
        "entity.lollipop_guy.hurt": ev(["minecraft:block/amethyst/break1", "minecraft:block/amethyst/break2"], "lollipop_guy.hurt", 1.4),
        "entity.lollipop_guy.death": ev(["minecraft:block/amethyst_cluster/break1"], "lollipop_guy.death", 1.2),
        "entity.lollipop_guy.gift": ev(["minecraft:random/pop"], "lollipop_guy.gift", 1.2),
        "entity.grape_spider.ambient": ev(["minecraft:mob/spider/say1", "minecraft:mob/spider/say2"], "grape_spider.ambient", 1.35, 0.8),
        "entity.grape_spider.hurt": ev(["minecraft:mob/slime/small1", "minecraft:mob/slime/small2"], "grape_spider.hurt", 1.2),
        "entity.grape_spider.death": ev(["minecraft:mob/spider/death"], "grape_spider.death", 1.35),
        "block.chocolate_factory.working": ev(["minecraft:liquid/lavapop"], "chocolate_factory.working", 0.8, 0.6),
    })

# ---------------------------------------------------------------- data: loot tables
def drop_self(name):
    return {"type": "minecraft:block", "random_sequence": f"{NS}:blocks/{name}", "pools": [{"rolls": 1.0, "bonus_rolls": 0.0,
            "conditions": [{"condition": "minecraft:survives_explosion"}], "entries": [{"type": "minecraft:item", "name": ns(name)}]}]}

SILK = {"condition": "minecraft:match_tool", "predicate": {"predicates": {"minecraft:enchantments": [{"enchantments": "minecraft:silk_touch", "levels": {"min": 1}}]}}}
SHEARS_OR_SILK = {"condition": "minecraft:any_of", "terms": [{"condition": "minecraft:match_tool", "predicate": {"items": "#c:tools/shear"}}, SILK]}

def silk_or(name, alt_entry):
    return {"type": "minecraft:block", "random_sequence": f"{NS}:blocks/{name}", "pools": [{"rolls": 1.0, "bonus_rolls": 0.0, "entries": [
        {"type": "minecraft:alternatives", "children": [{"type": "minecraft:item", "conditions": [SILK], "name": ns(name)}, alt_entry]}]}]}

def gen_loot():
    for b in ["chocolate_soil", "chocolate_block", "chocolate_bricks", "chiseled_chocolate_bricks", "chocolate_brick_stairs",
              "chocolate_brick_wall", "sweetwood_log", "sweetwood_planks", "sweetwood_sapling", "lollipop_flower",
              "tourmaline_block", "chocolate_factory"]:
        write(f"{D}/loot_table/blocks/{b}.json", drop_self(b))
    write(f"{D}/loot_table/blocks/chocolate_brick_slab.json", {"type": "minecraft:block", "random_sequence": f"{NS}:blocks/chocolate_brick_slab",
        "pools": [{"rolls": 1.0, "bonus_rolls": 0.0, "entries": [{"type": "minecraft:item", "name": f"{NS}:chocolate_brick_slab", "functions": [
            {"function": "minecraft:set_count", "add": False, "count": 2.0, "conditions": [{"condition": "minecraft:block_state_property",
             "block": f"{NS}:chocolate_brick_slab", "properties": {"type": "double"}}]}, {"function": "minecraft:explosion_decay"}]}]}]})
    write(f"{D}/loot_table/blocks/chocolate_grass_block.json", silk_or("chocolate_grass_block",
        {"type": "minecraft:item", "name": f"{NS}:chocolate_soil", "conditions": [{"condition": "minecraft:survives_explosion"}]}))
    for ore in ["tourmaline_ore", "deepslate_tourmaline_ore"]:
        write(f"{D}/loot_table/blocks/{ore}.json", silk_or(ore, {"type": "minecraft:item", "name": f"{NS}:tourmaline", "functions": [
            {"function": "minecraft:apply_bonus", "enchantment": "minecraft:fortune", "formula": "minecraft:ore_drops"},
            {"function": "minecraft:explosion_decay"}]}))
    write(f"{D}/loot_table/blocks/cotton_candy_leaves.json", {"type": "minecraft:block", "random_sequence": f"{NS}:blocks/cotton_candy_leaves", "pools": [
        {"rolls": 1.0, "bonus_rolls": 0.0, "entries": [{"type": "minecraft:alternatives", "children": [
            {"type": "minecraft:item", "conditions": [SHEARS_OR_SILK], "name": f"{NS}:cotton_candy_leaves"},
            {"type": "minecraft:item", "name": f"{NS}:sweetwood_sapling", "conditions": [{"condition": "minecraft:survives_explosion"},
             {"condition": "minecraft:table_bonus", "enchantment": "minecraft:fortune", "chances": [0.05, 0.0625, 0.083333336, 0.1]}]}]}]},
        {"rolls": 1.0, "bonus_rolls": 0.0, "conditions": [{"condition": "minecraft:inverted", "term": SHEARS_OR_SILK}], "entries": [
            {"type": "minecraft:item", "name": f"{NS}:cotton_candy", "conditions": [
             {"condition": "minecraft:table_bonus", "enchantment": "minecraft:fortune", "chances": [0.08, 0.1, 0.125, 0.16, 0.2]}]}]}]})

    def entity(name, pools):
        write(f"{D}/loot_table/entities/{name}.json", {"type": "minecraft:entity", "random_sequence": f"{NS}:entities/{name}", "pools": pools})
    def pool(item, lo, hi, looting=True):
        fns = [{"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}, "add": False}]
        if looting:
            fns.append({"function": "minecraft:enchanted_count_increase", "enchantment": "minecraft:looting", "count": {"type": "minecraft:uniform", "min": 0.0, "max": 1.0}})
        return {"rolls": 1.0, "bonus_rolls": 0.0, "entries": [{"type": "minecraft:item", "name": ns(item), "functions": fns}]}
    entity("candy_tube_dog", [pool("minecraft:sugar", 0.0, 2.0)])
    entity("lollipop_guy", [pool("lollipop", 0.0, 2.0), pool("minecraft:sugar", 0.0, 2.0)])
    entity("grape_spider", [pool("grape", 1.0, 3.0), pool("minecraft:string", 0.0, 2.0),
        {"rolls": 1.0, "bonus_rolls": 0.0, "conditions": [{"condition": "minecraft:killed_by_player"},
         {"condition": "minecraft:random_chance_with_enchanted_bonus", "enchantment": "minecraft:looting", "unenchanted_chance": 0.05,
          "enchanted_chance": {"type": "minecraft:linear", "base": 0.07, "per_level_above_first": 0.02}}],
         "entries": [{"type": "minecraft:item", "name": "minecraft:spider_eye"}]}])

    def e(item, weight, lo, hi):
        return {"type": "minecraft:item", "name": ns(item), "weight": weight, "functions": [
            {"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}, "add": False}]}
    write(f"{D}/loot_table/chests/cookie_hut.json", {"type": "minecraft:chest", "random_sequence": f"{NS}:chests/cookie_hut", "pools": [
        {"rolls": {"type": "minecraft:uniform", "min": 3.0, "max": 6.0}, "bonus_rolls": 0.0, "entries": [
            e("chocolate_bar", 20, 1, 4), e("lollipop", 20, 1, 3), e("cotton_candy", 15, 1, 4), e("cocoa_powder", 15, 2, 6),
            e("sweetwood_sapling", 10, 1, 2), e("candy_tube", 8, 1, 2), e("tourmaline", 8, 1, 3), e("minecraft:iron_ingot", 8, 1, 3),
            e("minecraft:sugar", 12, 2, 6)]},
        {"rolls": 1.0, "bonus_rolls": 0.0, "entries": [
            {"type": "minecraft:empty", "weight": 6},
            {"type": "minecraft:item", "name": f"{NS}:milk_chocolate", "weight": 3},
            {"type": "minecraft:item", "name": f"{NS}:tourmaline_pickaxe", "weight": 1, "functions": [
                {"function": "minecraft:set_damage", "damage": {"type": "minecraft:uniform", "min": 0.4, "max": 0.8}}]}]}]})

# ---------------------------------------------------------------- data: recipes
def shaped(name, pattern, key, result, count=1, category="misc", group=None):
    r = {"type": "minecraft:crafting_shaped", "category": category, "key": {k: ns(v) if not v.startswith("#") else v for k, v in key.items()},
         "pattern": pattern, "result": {"id": ns(result), "count": count}}
    if group:
        r["group"] = group
    write(f"{D}/recipe/{name}.json", r)

def shapeless(name, ingredients, result, count=1, category="misc"):
    write(f"{D}/recipe/{name}.json", {"type": "minecraft:crafting_shapeless", "category": category,
        "ingredients": [ns(i) if not i.startswith("#") else i for i in ingredients], "result": {"id": ns(result), "count": count}})

def cooking(name, kind, ingredient, result, xp, time):
    write(f"{D}/recipe/{name}.json", {"type": f"minecraft:{kind}", "category": "misc", "cookingtime": time, "experience": xp,
        "ingredient": ns(ingredient) if not ingredient.startswith("#") else ingredient, "result": {"id": ns(result)}})

def stonecut(name, ingredient, result, count=1):
    write(f"{D}/recipe/{name}.json", {"type": "minecraft:stonecutting", "ingredient": ns(ingredient), "result": {"id": ns(result), "count": count}})

def factory(name, ingredients, result, count=1, time=200, xp=0.1):
    write(f"{D}/recipe/chocolate_factory/{name}.json", {"type": f"{NS}:chocolate_factory",
        "ingredients": [ns(i) if not i.startswith("#") else i for i in ingredients],
        "result": {"id": ns(result), "count": count}, "processing_time": time, "experience": xp})

def gen_recipes():
    shapeless("sweetwood_planks", [f"#{NS}:sweetwood_logs"], "sweetwood_planks", 4, "building")
    shaped("chocolate_block", ["##", "##"], {"#": "chocolate_bar"}, "chocolate_block", 1, "building")
    shapeless("chocolate_bar_from_block", ["chocolate_block"], "chocolate_bar", 4)
    shaped("chocolate_bricks", ["##", "##"], {"#": "chocolate_block"}, "chocolate_bricks", 4, "building")
    shaped("chocolate_brick_stairs", ["#  ", "## ", "###"], {"#": "chocolate_bricks"}, "chocolate_brick_stairs", 4, "building")
    shaped("chocolate_brick_slab", ["###"], {"#": "chocolate_bricks"}, "chocolate_brick_slab", 6, "building")
    shaped("chocolate_brick_wall", ["###", "###"], {"#": "chocolate_bricks"}, "chocolate_brick_wall", 6, "building")
    shaped("chiseled_chocolate_bricks", ["#", "#"], {"#": "chocolate_brick_slab"}, "chiseled_chocolate_bricks", 1, "building")
    for res, cnt in [("chocolate_bricks", 1), ("chocolate_brick_stairs", 1), ("chocolate_brick_slab", 2), ("chocolate_brick_wall", 1), ("chiseled_chocolate_bricks", 1)]:
        stonecut(f"{res}_from_chocolate_block_stonecutting", "chocolate_block", res, cnt)
    for res, cnt in [("chocolate_brick_stairs", 1), ("chocolate_brick_slab", 2), ("chocolate_brick_wall", 1), ("chiseled_chocolate_bricks", 1)]:
        stonecut(f"{res}_from_chocolate_bricks_stonecutting", "chocolate_bricks", res, cnt)

    shaped("tourmaline_block", ["###", "###", "###"], {"#": "tourmaline"}, "tourmaline_block", 1, "building")
    shapeless("tourmaline_from_block", ["tourmaline_block"], "tourmaline", 9)
    for kind, time in [("smelting", 200), ("blasting", 100)]:
        cooking(f"tourmaline_from_{kind}", kind, f"#{NS}:tourmaline_ores", "tourmaline", 0.8, time)

    eq = "equipment"
    shaped("tourmaline_sword", ["#", "#", "/"], {"#": "tourmaline", "/": "minecraft:stick"}, "tourmaline_sword", 1, eq)
    shaped("tourmaline_pickaxe", ["###", " / ", " / "], {"#": "tourmaline", "/": "minecraft:stick"}, "tourmaline_pickaxe", 1, eq)
    shaped("tourmaline_axe", ["##", "#/", " /"], {"#": "tourmaline", "/": "minecraft:stick"}, "tourmaline_axe", 1, eq)
    shaped("tourmaline_shovel", ["#", "/", "/"], {"#": "tourmaline", "/": "minecraft:stick"}, "tourmaline_shovel", 1, eq)
    shaped("tourmaline_hoe", ["##", " /", " /"], {"#": "tourmaline", "/": "minecraft:stick"}, "tourmaline_hoe", 1, eq)
    shaped("tourmaline_helmet", ["###", "# #"], {"#": "tourmaline"}, "tourmaline_helmet", 1, eq)
    shaped("tourmaline_chestplate", ["# #", "###", "###"], {"#": "tourmaline"}, "tourmaline_chestplate", 1, eq)
    shaped("tourmaline_leggings", ["###", "# #", "# #"], {"#": "tourmaline"}, "tourmaline_leggings", 1, eq)
    shaped("tourmaline_boots", ["# #", "# #"], {"#": "tourmaline"}, "tourmaline_boots", 1, eq)

    # The factory uses no chocolate, so there is no circular dependency.
    shaped("chocolate_factory", ["TIT", "IFI", "BBB"], {"T": "tourmaline", "I": "minecraft:iron_ingot", "F": "minecraft:furnace", "B": "minecraft:bricks"},
           "chocolate_factory", 1, "misc")
    shapeless("sugar_from_candy_tube", ["candy_tube"], "minecraft:sugar", 3)
    shapeless("pink_dye_from_cotton_candy", ["cotton_candy"], "minecraft:pink_dye", 1)
    shapeless("lollipop_from_flower", ["lollipop_flower", "minecraft:stick"], "lollipop", 1, "misc")

    factory("cocoa_powder", ["minecraft:cocoa_beans"], "cocoa_powder", 2, 100, 0.1)
    factory("cocoa_powder_from_soil", ["chocolate_soil"], "cocoa_powder", 1, 160, 0.05)
    factory("chocolate_bar", ["cocoa_powder", "minecraft:sugar"], "chocolate_bar", 1, 200, 0.2)
    factory("milk_chocolate", ["cocoa_powder", "minecraft:milk_bucket"], "milk_chocolate", 3, 240, 0.35)
    factory("lollipop", ["candy_tube", "minecraft:sugar"], "lollipop", 2, 160, 0.15)
    factory("cotton_candy", ["minecraft:sugar", "minecraft:pink_dye"], "cotton_candy", 2, 120, 0.1)

# ---------------------------------------------------------------- data: tags
def tag(kind, namespace, name, values, replace=False):
    """Writes a tag; calling it again for the same tag adds the new values (stage 1 and stage 2 share tags)."""
    path = os.path.join(ROOT, "data", namespace, "tags", kind, f"{name}.json")
    if os.path.isfile(path):
        old = json.load(open(path, encoding="utf-8"))["values"]
        values = old + [v for v in values if v not in old]
    write(path, {"replace": replace, "values": values})

def gen_tags():
    n = lambda *xs: [ns(x) for x in xs]
    tag("item", NS, "tourmaline_repair_materials", n("tourmaline"))
    tag("item", NS, "candy_tube_dog_food", n("chocolate_bar", "milk_chocolate", "cotton_candy", "candy_tube"))
    tag("item", NS, "sweets", n("chocolate_bar", "milk_chocolate", "lollipop", "cotton_candy", "candy_tube"))
    tag("item", NS, "sweetwood_logs", n("sweetwood_log"))
    tag("block", NS, "sweetwood_logs", n("sweetwood_log"))
    tag("item", NS, "tourmaline_ores", n("tourmaline_ore", "deepslate_tourmaline_ore"))
    tag("block", NS, "tourmaline_ores", n("tourmaline_ore", "deepslate_tourmaline_ore"))
    tag("block", NS, "sweet_ground", n("chocolate_soil", "chocolate_grass_block"))

    tag("block", "minecraft", "mineable/pickaxe", n("chocolate_block", "chocolate_bricks", "chocolate_brick_stairs", "chocolate_brick_slab",
        "chocolate_brick_wall", "chiseled_chocolate_bricks", "tourmaline_ore", "deepslate_tourmaline_ore", "tourmaline_block", "chocolate_factory"))
    tag("block", "minecraft", "mineable/shovel", n("chocolate_soil", "chocolate_grass_block"))
    tag("block", "minecraft", "mineable/axe", n("sweetwood_log", "sweetwood_planks"))
    tag("block", "minecraft", "mineable/hoe", n("cotton_candy_leaves"))
    tag("block", "minecraft", "needs_iron_tool", n("tourmaline_ore", "deepslate_tourmaline_ore", "tourmaline_block"))
    tag("block", "minecraft", "stairs", n("chocolate_brick_stairs"))
    tag("block", "minecraft", "slabs", n("chocolate_brick_slab"))
    tag("block", "minecraft", "walls", n("chocolate_brick_wall"))
    tag("block", "minecraft", "logs", n("sweetwood_log"))
    tag("block", "minecraft", "logs_that_burn", n("sweetwood_log"))
    tag("block", "minecraft", "overworld_natural_logs", n("sweetwood_log"))
    tag("block", "minecraft", "planks", n("sweetwood_planks"))
    tag("block", "minecraft", "leaves", n("cotton_candy_leaves"))
    tag("block", "minecraft", "saplings", n("sweetwood_sapling"))
    tag("block", "minecraft", "small_flowers", n("lollipop_flower"))
    tag("block", "minecraft", "dirt", n("chocolate_soil", "chocolate_grass_block"))
    tag("block", "minecraft", "grass_blocks", n("chocolate_grass_block"))
    tag("block", "minecraft", "supports_vegetation", n("chocolate_soil", "chocolate_grass_block"))
    tag("block", "minecraft", "animals_spawnable_on", n("chocolate_grass_block"))
    tag("block", "minecraft", "valid_spawn", n("chocolate_grass_block"))
    tag("block", "minecraft", "enderman_holdable", n("chocolate_soil", "chocolate_grass_block"))
    tag("block", "c", "ores", n("tourmaline_ore", "deepslate_tourmaline_ore"))
    tag("block", "c", "ores/tourmaline", n("tourmaline_ore", "deepslate_tourmaline_ore"))
    tag("block", "c", "storage_blocks/tourmaline", n("tourmaline_block"))
    tag("block", "c", "dirts", n("chocolate_soil"))

    tag("item", "minecraft", "stairs", n("chocolate_brick_stairs"))
    tag("item", "minecraft", "slabs", n("chocolate_brick_slab"))
    tag("item", "minecraft", "walls", n("chocolate_brick_wall"))
    tag("item", "minecraft", "logs", n("sweetwood_log"))
    tag("item", "minecraft", "logs_that_burn", n("sweetwood_log"))
    tag("item", "minecraft", "planks", n("sweetwood_planks"))
    tag("item", "minecraft", "leaves", n("cotton_candy_leaves"))
    tag("item", "minecraft", "saplings", n("sweetwood_sapling"))
    tag("item", "minecraft", "small_flowers", n("lollipop_flower"))
    tag("item", "minecraft", "dirt", n("chocolate_soil", "chocolate_grass_block"))
    tag("item", "minecraft", "swords", n("tourmaline_sword"))
    tag("item", "minecraft", "pickaxes", n("tourmaline_pickaxe"))
    tag("item", "minecraft", "axes", n("tourmaline_axe"))
    tag("item", "minecraft", "shovels", n("tourmaline_shovel"))
    tag("item", "minecraft", "hoes", n("tourmaline_hoe"))
    tag("item", "minecraft", "head_armor", n("tourmaline_helmet"))
    tag("item", "minecraft", "chest_armor", n("tourmaline_chestplate"))
    tag("item", "minecraft", "leg_armor", n("tourmaline_leggings"))
    tag("item", "minecraft", "foot_armor", n("tourmaline_boots"))
    tag("item", "minecraft", "trimmable_armor", n("tourmaline_helmet", "tourmaline_chestplate", "tourmaline_leggings", "tourmaline_boots"))
    tag("item", "c", "gems", n("tourmaline"))
    tag("item", "c", "gems/tourmaline", n("tourmaline"))
    tag("item", "c", "ores", n("tourmaline_ore", "deepslate_tourmaline_ore"))
    tag("item", "c", "ores/tourmaline", n("tourmaline_ore", "deepslate_tourmaline_ore"))
    tag("item", "c", "storage_blocks/tourmaline", n("tourmaline_block"))
    tag("item", "c", "foods", n("chocolate_bar", "milk_chocolate", "lollipop", "cotton_candy", "grape"))
    tag("item", "c", "foods/candy", n("chocolate_bar", "milk_chocolate", "lollipop", "cotton_candy"))
    tag("item", "c", "foods/fruit", n("grape"))

    tag("worldgen/biome", "minecraft", "is_overworld", n("sweet_forest"))
    tag("worldgen/biome", "minecraft", "is_forest", n("sweet_forest"))
    tag("worldgen/biome", "c", "is_overworld", n("sweet_forest"))
    tag("worldgen/biome", NS, "has_structure/cookie_hut", n("sweet_forest"))
    tag("worldgen/biome", NS, "is_candy", n("sweet_forest"))

# ---------------------------------------------------------------- data: worldgen
VANILLA_UNDERGROUND = ["minecraft:ore_dirt", "minecraft:ore_gravel", "minecraft:ore_granite_upper", "minecraft:ore_granite_lower",
    "minecraft:ore_diorite_upper", "minecraft:ore_diorite_lower", "minecraft:ore_andesite_upper", "minecraft:ore_andesite_lower",
    "minecraft:ore_tuff", "minecraft:ore_coal_upper", "minecraft:ore_coal_lower", "minecraft:ore_iron_upper", "minecraft:ore_iron_middle",
    "minecraft:ore_iron_small", "minecraft:ore_gold", "minecraft:ore_gold_lower", "minecraft:ore_redstone", "minecraft:ore_redstone_lower",
    "minecraft:ore_diamond", "minecraft:ore_diamond_medium", "minecraft:ore_diamond_large", "minecraft:ore_diamond_buried",
    "minecraft:ore_lapis", "minecraft:ore_lapis_buried", "minecraft:ore_copper", "minecraft:underwater_magma",
    "minecraft:disk_sand", "minecraft:disk_clay", "minecraft:disk_gravel"]

def state(name, **props):
    s = {"Name": ns(name)}
    if props:
        s["Properties"] = {k: str(v).lower() for k, v in props.items()}
    return s

def tree(base, rand, radius, height):
    return {"type": "minecraft:tree", "config": {
        "below_trunk_provider": {"type": "minecraft:rule_based_state_provider", "rules": [{"if_true": {"type": "minecraft:not", "predicate": {
            "type": "minecraft:matching_block_tag", "tag": "minecraft:cannot_replace_below_tree_trunk"}},
            "then": {"type": "minecraft:simple_state_provider", "state": state("chocolate_soil")}}]},
        "decorators": [],
        "foliage_placer": {"type": "minecraft:blob_foliage_placer", "radius": radius, "offset": 0, "height": height},
        "foliage_provider": {"type": "minecraft:simple_state_provider", "state": state("cotton_candy_leaves", distance=7, persistent=False, waterlogged=False)},
        "ignore_vines": True,
        "minimum_size": {"type": "minecraft:two_layers_feature_size", "limit": 1, "lower_size": 0, "upper_size": 1},
        "trunk_placer": {"type": "minecraft:straight_trunk_placer", "base_height": base, "height_rand_a": rand, "height_rand_b": 0},
        "trunk_provider": {"type": "minecraft:simple_state_provider", "state": state("sweetwood_log", axis="y")}}}

def surface_tree_placement(count):
    return [{"type": "minecraft:count", "count": count}, {"type": "minecraft:in_square"},
            {"type": "minecraft:surface_water_depth_filter", "max_water_depth": 0},
            {"type": "minecraft:heightmap", "heightmap": "OCEAN_FLOOR"},
            {"type": "minecraft:block_predicate_filter", "predicate": {"type": "minecraft:would_survive", "state": state("sweetwood_sapling", stage=0)}},
            {"type": "minecraft:biome"}]

def gen_worldgen():
    W = f"{D}/worldgen"
    write(f"{W}/configured_feature/sweetwood_tree.json", tree(5, 2, 2, 3))
    write(f"{W}/configured_feature/tall_sweetwood_tree.json", tree(7, 3, 3, 4))
    write(f"{W}/placed_feature/sweetwood_trees.json", {"feature": f"{NS}:sweetwood_tree", "placement": surface_tree_placement(
        {"type": "minecraft:weighted_list", "distribution": [{"data": 4, "weight": 6}, {"data": 6, "weight": 3}, {"data": 8, "weight": 1}]})})
    write(f"{W}/placed_feature/tall_sweetwood_trees.json", {"feature": f"{NS}:tall_sweetwood_tree", "placement": surface_tree_placement(
        {"type": "minecraft:weighted_list", "distribution": [{"data": 0, "weight": 1}, {"data": 1, "weight": 2}]})})

    write(f"{W}/configured_feature/lollipop_flower.json", {"type": "minecraft:simple_block", "config": {
        "to_place": {"type": "minecraft:simple_state_provider", "state": state("lollipop_flower")}}})
    write(f"{W}/placed_feature/lollipop_flowers.json", {"feature": f"{NS}:lollipop_flower", "placement": [
        {"type": "minecraft:rarity_filter", "chance": 2}, {"type": "minecraft:in_square"},
        {"type": "minecraft:heightmap", "heightmap": "MOTION_BLOCKING"}, {"type": "minecraft:biome"},
        {"type": "minecraft:count", "count": 24},
        {"type": "minecraft:random_offset", "xz_spread": {"type": "minecraft:trapezoid", "max": 6, "min": -6, "plateau": 0},
         "y_spread": {"type": "minecraft:trapezoid", "max": 2, "min": -2, "plateau": 0}},
        {"type": "minecraft:block_predicate_filter", "predicate": {"type": "minecraft:all_of", "predicates": [
            {"type": "minecraft:matching_block_tag", "tag": "minecraft:air"},
            {"type": "minecraft:would_survive", "state": state("lollipop_flower")}]}}]})

    def ore(size, discard):
        return {"type": "minecraft:ore", "config": {"discard_chance_on_air_exposure": discard, "size": size, "targets": [
            {"state": state("tourmaline_ore"), "target": {"predicate_type": "minecraft:tag_match", "tag": "minecraft:stone_ore_replaceables"}},
            {"state": state("deepslate_tourmaline_ore"), "target": {"predicate_type": "minecraft:tag_match", "tag": "minecraft:deepslate_ore_replaceables"}}]}}
    write(f"{W}/configured_feature/ore_tourmaline.json", ore(6, 0.0))
    write(f"{W}/configured_feature/ore_tourmaline_large.json", ore(10, 0.5))
    write(f"{W}/placed_feature/ore_tourmaline.json", {"feature": f"{NS}:ore_tourmaline", "placement": [
        {"type": f"{NS}:config_count", "setting": "tourmaline"}, {"type": "minecraft:in_square"},
        {"type": "minecraft:height_range", "height": {"type": "minecraft:uniform", "min_inclusive": {"absolute": -16}, "max_inclusive": {"absolute": 48}}},
        {"type": "minecraft:biome"}]})
    write(f"{W}/placed_feature/ore_tourmaline_deep.json", {"feature": f"{NS}:ore_tourmaline_large", "placement": [
        {"type": f"{NS}:config_count", "setting": "deep_tourmaline"}, {"type": "minecraft:in_square"},
        {"type": "minecraft:height_range", "height": {"type": "minecraft:trapezoid", "min_inclusive": {"above_bottom": 0}, "max_inclusive": {"absolute": 0}}},
        {"type": "minecraft:biome"}]})

    write(f"{D}/neoforge/biome_modifier/add_tourmaline_ore.json", {"type": "neoforge:add_features", "biomes": "#minecraft:is_overworld",
        "features": [f"{NS}:ore_tourmaline", f"{NS}:ore_tourmaline_deep"], "step": "underground_ores"})

    def spawn(t, w, lo, hi):
        return {"type": ns(t), "weight": w, "minCount": lo, "maxCount": hi}
    write(f"{W}/biome/sweet_forest.json", {
        "attributes": {
            "minecraft:audio/background_music": {"default": {"max_delay": 24000, "min_delay": 12000, "sound": "minecraft:music.overworld.cherry_grove"}},
            "minecraft:visual/fog_color": "#ffd9ef", "minecraft:visual/sky_color": "#9fc4ff", "minecraft:visual/water_fog_color": "#4a1838"},
        "carvers": ["minecraft:cave", "minecraft:cave_extra_underground", "minecraft:canyon"],
        "downfall": 0.8,
        "effects": {"foliage_color": "#ffb3dd", "grass_color": "#f2a6cf", "water_color": "#c25a9e"},
        "features": [[], ["minecraft:lake_lava_underground", "minecraft:lake_lava_surface"], ["minecraft:amethyst_geode"],
                     ["minecraft:monster_room", "minecraft:monster_room_deep"], [], [], VANILLA_UNDERGROUND, [],
                     ["minecraft:spring_water", "minecraft:spring_lava"],
                     ["minecraft:glow_lichen", f"{NS}:tall_sweetwood_trees", f"{NS}:sweetwood_trees", f"{NS}:lollipop_flowers"],
                     ["minecraft:freeze_top_layer"]],
        "has_precipitation": True,
        "spawn_costs": {},
        "spawners": {
            "ambient": [spawn("minecraft:bat", 10, 8, 8)], "axolotls": [],
            "creature": [spawn("candy_tube_dog", 8, 2, 4), spawn("lollipop_guy", 6, 1, 3), spawn("minecraft:chicken", 6, 2, 4), spawn("minecraft:rabbit", 4, 2, 3)],
            "misc": [],
            "monster": [spawn("grape_spider", 80, 1, 3), spawn("minecraft:zombie", 95, 4, 4), spawn("minecraft:zombie_villager", 5, 1, 1),
                        spawn("minecraft:skeleton", 100, 4, 4), spawn("minecraft:creeper", 100, 4, 4), spawn("minecraft:spider", 40, 4, 4),
                        spawn("minecraft:slime", 100, 4, 4), spawn("minecraft:enderman", 10, 1, 4), spawn("minecraft:witch", 5, 1, 1)],
            "underground_water_creature": [spawn("minecraft:glow_squid", 10, 4, 6)], "water_ambient": [], "water_creature": []},
        "temperature": 0.7})

    # Cookie Hut: a single-piece jigsaw structure, spread controlled by one of three structure sets.
    write(f"{W}/structure/cookie_hut.json", {"type": "minecraft:jigsaw", "biomes": f"#{NS}:has_structure/cookie_hut",
        "step": "surface_structures", "spawn_overrides": {}, "terrain_adaptation": "beard_thin",
        "start_pool": f"{NS}:cookie_hut/start", "size": 1, "start_height": {"absolute": 0},
        "project_start_to_heightmap": "WORLD_SURFACE_WG", "max_distance_from_center": 80, "use_expansion_hack": False})
    write(f"{W}/template_pool/cookie_hut/start.json", {"fallback": "minecraft:empty", "elements": [{"weight": 1, "element": {
        "element_type": "minecraft:single_pool_element", "location": f"{NS}:cookie_hut", "projection": "rigid", "processors": "minecraft:empty"}}]})
    for freq, spacing, separation in [("rare", 40, 16), ("normal", 28, 10), ("common", 18, 6)]:
        write(f"{W}/structure_set/cookie_huts_{freq}.json", {
            "neoforge:conditions": [{"type": f"{NS}:structure_frequency", "frequency": freq}],
            "structures": [{"structure": f"{NS}:cookie_hut", "weight": 1}],
            "placement": {"type": "minecraft:random_spread", "spacing": spacing, "separation": separation, "salt": 1873205411}})

# ---------------------------------------------------------------- data: advancements
def adv(name, parent, icon, frame, criteria, requirements=None, hidden=False, background=None, rewards=None):
    disp = {"icon": {"id": ns(icon), "count": 1}, "title": {"translate": f"advancements.{NS}.{name}.title"},
            "description": {"translate": f"advancements.{NS}.{name}.description"}, "frame": frame,
            "show_toast": True, "announce_to_chat": name != "root", "hidden": hidden}
    if background:
        disp["background"] = background
    a = {"display": disp, "criteria": criteria}
    if parent:
        a["parent"] = f"{NS}:{parent}"
    if requirements:
        a["requirements"] = requirements
    if rewards:
        a["rewards"] = rewards
    write(f"{D}/advancement/{name}.json", a)

def has(item):
    return {"trigger": "minecraft:inventory_changed", "conditions": {"items": [{"items": ns(item)}]}}

def gen_advancements():
    in_biome = {"trigger": "minecraft:location", "conditions": {"player": [{"condition": "minecraft:entity_properties", "entity": "this",
                "predicate": {"location": {"biomes": f"{NS}:sweet_forest"}}}]}}
    adv("root", None, "lollipop", "task", {"sweet_forest": in_biome, "tourmaline": has("tourmaline"), "lollipop": has("lollipop")},
        [["sweet_forest", "tourmaline", "lollipop"]], background="minecraft:block/pink_wool")
    adv("enter_sweet_forest", "root", "chocolate_grass_block", "task", {"sweet_forest": in_biome})
    adv("sugar_rush", "enter_sweet_forest", "lollipop", "task", {"eat": {"trigger": "minecraft:consume_item", "conditions": {"item": {"items": f"{NS}:lollipop"}}}})
    adv("tame_candy_tube_dog", "enter_sweet_forest", "candy_tube", "goal", {"tame": {"trigger": "minecraft:tame_animal", "conditions": {
        "entity": [{"condition": "minecraft:entity_properties", "entity": "this", "predicate": {"type": f"{NS}:candy_tube_dog"}}]}}})
    adv("grape_crusher", "enter_sweet_forest", "grape", "task", {"kill": {"trigger": "minecraft:player_killed_entity", "conditions": {
        "entity": [{"condition": "minecraft:entity_properties", "entity": "this", "predicate": {"type": f"{NS}:grape_spider"}}]}}})
    adv("visit_cookie_hut", "enter_sweet_forest", "chocolate_bricks", "task", {"hut": {"trigger": "minecraft:location", "conditions": {"player": [
        {"condition": "minecraft:entity_properties", "entity": "this", "predicate": {"location": {"structures": f"{NS}:cookie_hut"}}}]}}})
    adv("pink_gem", "root", "tourmaline", "task", {"tourmaline": has("tourmaline")})
    adv("tourmaline_tools", "pink_gem", "tourmaline_pickaxe", "task", {
        "sword": has("tourmaline_sword"), "pickaxe": has("tourmaline_pickaxe"), "axe": has("tourmaline_axe"), "shovel": has("tourmaline_shovel")},
        [["sword", "pickaxe", "axe", "shovel"]])
    adv("tourmaline_armor", "tourmaline_tools", "tourmaline_chestplate", "goal", {
        "helmet": has("tourmaline_helmet"), "chestplate": has("tourmaline_chestplate"), "leggings": has("tourmaline_leggings"), "boots": has("tourmaline_boots")},
        rewards={"experience": 50})
    adv("factory_floor", "pink_gem", "chocolate_factory", "task", {"factory": has("chocolate_factory")})
    adv("milk_chocolate", "factory_floor", "milk_chocolate", "task", {"milk_chocolate": has("milk_chocolate")})
    adv("chocolate_architect", "factory_floor", "chiseled_chocolate_bricks", "task", {
        "bricks": has("chocolate_bricks"), "stairs": has("chocolate_brick_stairs"), "slab": has("chocolate_brick_slab"),
        "wall": has("chocolate_brick_wall"), "chiseled": has("chiseled_chocolate_bricks")})

# ---------------------------------------------------------------- lang
def gen_lang():
    from lang_data import EN, PT  # tools/lang_data.py
    import lang_stage2  # tools/lang_stage2.py: stage 2 keys, replacing stage 1 names where they overlap
    EN = {k: v for k, v in (EN | lang_stage2.EN).items() if k not in lang_stage2.REMOVED}
    PT = {k: v for k, v in (PT | lang_stage2.PT).items() if k not in lang_stage2.REMOVED}
    import lang_stage3  # tools/lang_stage3.py: stage 3 keys (Gingerbread King, remodelled creatures)
    EN = EN | lang_stage3.EN
    PT = PT | lang_stage3.PT
    import lang_stage4  # tools/lang_stage4.py: stage 4 keys (fortress, ice cream truck, remodelled creatures)
    EN = EN | lang_stage4.EN
    PT = PT | lang_stage4.PT
    import lang_stage5  # tools/lang_stage5.py: stage 5 keys (Candy Cave, Impostor Cake, Ice Cream Machine)
    EN = EN | lang_stage5.EN
    PT = PT | lang_stage5.PT
    import lang_stage6  # tools/lang_stage6.py: stage 6 keys (Ice Cream Dimension, its creatures and the Ice Cream Dragon)
    EN = EN | lang_stage6.EN
    PT = PT | lang_stage6.PT
    import lang_stage7  # tools/lang_stage7.py: stage 7 keys (Overworld tab: Bank, guards, money, Mineral Converter)
    EN = EN | lang_stage7.EN
    PT = PT | lang_stage7.PT
    for code, data in [("en_us", EN), ("pt_br", PT)]:
        missing = set(EN) - set(data)
        if missing:
            raise SystemExit(f"{code} is missing keys: {sorted(missing)}")
        write(f"{A}/lang/{code}.json", data)

if __name__ == "__main__":
    import sys
    sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
    clean()
    gen_block_assets(); gen_item_assets(); gen_sounds()
    gen_loot(); gen_recipes(); gen_tags(); gen_worldgen(); gen_advancements()
    import gen_stage2  # tools/gen_stage2.py: Sweet Forest rework and Jelly Bean Fields
    gen_stage2.generate()
    import gen_stage3  # tools/gen_stage3.py: remodelled creatures and the Gingerbread King
    gen_stage3.generate()
    import gen_stage4  # tools/gen_stage4.py: Gingerbread Fortress, Ice Cream Truck and ice creams
    gen_stage4.generate()
    import gen_stage5  # tools/gen_stage5.py: Candy Cave, Impostor Cake and Ice Cream Machine
    gen_stage5.generate()
    import gen_stage6  # tools/gen_stage6.py: Ice Cream Dimension, portal, Ice Cream Nest and Ice Cream Dragon
    gen_stage6.generate()
    import gen_stage7  # tools/gen_stage7.py: the Bank, Guard Dinosaur, Segway, money, AK47 and Mineral Converter
    gen_stage7.generate()
    gen_lang()
    print("resources generated")
