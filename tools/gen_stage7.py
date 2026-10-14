#!/usr/bin/env python3
"""Stage 7 data: the Overworld tab. The Bank (in Plains) with its vault loot, the Guard Dinosaur and the Segway,
Money, Coins, the AK47 and its ammo, and the Mineral Converter with the original mod's conversion table.

Called by tools/gen_resources.py after gen_stage6 (files written here replace earlier files of the same name, and
tags are merged).
"""
import json
from gen_resources import NS, A, D, write, ns, flat_item, handheld_item, item_def, tag, shapeless, adv, has, drop_self

W = f"{D}/worldgen"

# Mineral Converter: one unit of the material gives (Money, Coins). The original mod's table.
CONVERSIONS = [
    ("copper_ingot", "minecraft:copper_ingot", 1, 1),
    ("iron_ingot", "minecraft:iron_ingot", 2, 2),
    ("gold_ingot", "minecraft:gold_ingot", 3, 5),
    ("emerald", "minecraft:emerald", 7, 10),
    ("diamond", "minecraft:diamond", 10, 15),
    ("tourmaline", "tourmaline", 20, 20),
    ("netherite_ingot", "minecraft:netherite_ingot", 64, 64),
]

# =================================================================== assets
def gen_assets():
    write(f"{A}/models/block/mineral_converter.json", {"parent": "minecraft:block/orientable", "textures": {
        "top": f"{NS}:block/mineral_converter_top", "front": f"{NS}:block/mineral_converter_front",
        "side": f"{NS}:block/mineral_converter_side"}})
    variants = {}
    for facing, y in [("north", 0), ("east", 90), ("south", 180), ("west", 270)]:
        v = {"model": f"{NS}:block/mineral_converter"}
        if y:
            v["y"] = y
        variants[f"facing={facing}"] = v
    write(f"{A}/blockstates/mineral_converter.json", {"variants": variants})
    item_def("mineral_converter", f"{NS}:block/mineral_converter")
    for i in ("money", "coin", "ak47_ammo", "guard_dinosaur_spawn_egg", "segway_spawn_egg"):
        flat_item(i)
    handheld_item("ak47")

def gen_sounds():
    path = f"{A}/sounds.json"
    sounds = json.load(open(path, encoding="utf-8"))
    def ev(names, subtitle, pitch=1.0, volume=1.0):
        return {"subtitle": f"subtitles.{NS}.{subtitle}", "sounds": [{"name": s, "pitch": pitch, "volume": volume} for s in names]}
    # Provisional: re-pitched vanilla sounds until original recordings exist.
    sounds["entity.guard_dinosaur.ambient"] = ev(["minecraft:mob/sniffer/idle1", "minecraft:mob/sniffer/idle2", "minecraft:mob/sniffer/idle3"],
                                                  "guard_dinosaur.ambient", 0.8)
    sounds["entity.guard_dinosaur.hurt"] = ev(["minecraft:mob/sniffer/hurt1", "minecraft:mob/sniffer/hurt2", "minecraft:mob/sniffer/hurt3"],
                                               "guard_dinosaur.hurt", 0.85)
    sounds["entity.guard_dinosaur.death"] = ev(["minecraft:mob/sniffer/death1", "minecraft:mob/sniffer/death2"], "guard_dinosaur.death", 0.8)
    sounds["entity.guard_dinosaur.alert"] = ev(["minecraft:mob/ravager/roar1", "minecraft:mob/ravager/roar2"], "guard_dinosaur.alert", 1.3, 1.2)
    sounds["entity.segway.ambient"] = ev(["minecraft:note/pling"], "segway.ambient", 1.8, 0.4)
    sounds["entity.segway.hurt"] = ev(["minecraft:mob/irongolem/hit1", "minecraft:mob/irongolem/hit2"], "segway.hurt", 1.6, 0.8)
    sounds["entity.segway.death"] = ev(["minecraft:mob/irongolem/death"], "segway.death", 1.7, 0.8)
    sounds["item.ak47.shoot"] = ev(["minecraft:fireworks/blast1"], "ak47.shoot", 1.6, 0.7)
    sounds["block.mineral_converter.convert"] = ev(["minecraft:note/bell"], "mineral_converter.convert", 1.5, 0.8)
    write(path, sounds)

# =================================================================== loot
def e(item, weight, lo, hi, extra=None):
    fns = [{"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}, "add": False}]
    return {"type": "minecraft:item", "name": ns(item), "weight": weight, "functions": fns + (extra or [])}

def gen_loot():
    write(f"{D}/loot_table/blocks/mineral_converter.json", drop_self("mineral_converter"))
    def chest(name, pools):
        write(f"{D}/loot_table/chests/{name}.json", {"type": "minecraft:chest", "random_sequence": f"{NS}:chests/{name}", "pools": pools})
    # The vault: four draws. Money and Coins weigh the most, the Mineral Converter the least.
    chest("bank", [{"rolls": 4.0, "bonus_rolls": 0.0, "entries": [
        e("money", 20, 4, 12), e("coin", 20, 6, 16), e("minecraft:gold_ingot", 12, 2, 6), e("minecraft:emerald", 8, 1, 4),
        e("minecraft:diamond", 5, 1, 3), e("ak47_ammo", 8, 8, 32),
        e("ak47", 3, 1, 1, [{"function": "minecraft:set_damage", "damage": {"type": "minecraft:uniform", "min": 0.5, "max": 1.0}}]),
        e("mineral_converter", 2, 1, 1)]}])
    # Revival addition: the teller's barrel behind the glass, small change only.
    chest("bank_teller", [{"rolls": {"type": "minecraft:uniform", "min": 2.0, "max": 4.0}, "bonus_rolls": 0.0, "entries": [
        e("money", 12, 1, 4), e("coin", 15, 2, 8), e("minecraft:paper", 10, 1, 5), e("minecraft:gold_nugget", 8, 2, 6),
        e("minecraft:book", 4, 1, 1), e("ak47_ammo", 3, 4, 8)]}])

    def pool(item, lo, hi):
        return {"rolls": 1.0, "bonus_rolls": 0.0, "entries": [{"type": "minecraft:item", "name": ns(item), "functions": [
            {"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}, "add": False},
            {"function": "minecraft:enchanted_count_increase", "enchantment": "minecraft:looting", "count": {"type": "minecraft:uniform", "min": 0.0, "max": 1.0}}]}]}
    def entity(name, pools):
        write(f"{D}/loot_table/entities/{name}.json", {"type": "minecraft:entity", "random_sequence": f"{NS}:entities/{name}", "pools": pools})
    # Revival choice: a guard carries a little pay; a broken Segway leaves scrap.
    entity("guard_dinosaur", [pool("money", 0.0, 2.0), pool("coin", 0.0, 3.0)])
    entity("segway", [pool("minecraft:iron_nugget", 1.0, 4.0), pool("minecraft:redstone", 0.0, 1.0)])

# =================================================================== recipes
def gen_recipes():
    for name, material, money, coin in CONVERSIONS:
        write(f"{D}/recipe/mineral_converter/{name}.json", {"type": f"{NS}:mineral_converter", "material": ns(material),
                                                            "money": money, "coin": coin})
    # Revival choice: ammo can be crafted so a vault AK47 stays useful; the gun and the converter are vault loot.
    shapeless("ak47_ammo", ["minecraft:iron_ingot", "minecraft:copper_ingot", "minecraft:gunpowder"], "ak47_ammo", 16, "equipment")

# =================================================================== tags
def gen_tags():
    n = lambda *xs: [ns(x) for x in xs]
    tag("block", "minecraft", "mineable/pickaxe", n("mineral_converter"))
    tag("block", "minecraft", "needs_stone_tool", n("mineral_converter"))
    tag("worldgen/biome", NS, "has_structure/bank", ["minecraft:plains"])
    tag("entity_type", NS, "bank_guards", n("guard_dinosaur", "segway"))
    tag("item", NS, "money", n("money", "coin"))

# =================================================================== worldgen
def gen_worldgen():
    name = "bank"
    # A big, flat-floored building: beard_box gives it a solid foundation when the ground dips a little.
    write(f"{W}/structure/{name}.json", {"type": "minecraft:jigsaw", "biomes": f"#{NS}:has_structure/{name}",
        "step": "surface_structures", "spawn_overrides": {}, "terrain_adaptation": "beard_box",
        "start_pool": f"{NS}:{name}/start", "size": 1, "start_height": {"absolute": 0},
        "project_start_to_heightmap": "WORLD_SURFACE_WG", "max_distance_from_center": 80, "use_expansion_hack": False})
    write(f"{W}/template_pool/{name}/start.json", {"fallback": "minecraft:empty", "elements": [{"weight": 1, "element": {
        "element_type": "minecraft:single_pool_element", "location": f"{NS}:{name}", "projection": "rigid", "processors": "minecraft:empty"}}]})
    for freq, (spacing, separation) in {"rare": (56, 24), "normal": (40, 16), "common": (28, 10)}.items():
        write(f"{W}/structure_set/banks_{freq}.json", {
            "neoforge:conditions": [{"type": f"{NS}:structure_frequency", "frequency": freq, "structure": name}],
            "structures": [{"structure": f"{NS}:{name}", "weight": 1}],
            "placement": {"type": "minecraft:random_spread", "spacing": spacing, "separation": separation, "salt": 518273645}})

# =================================================================== advancements
def gen_advancements():
    adv("visit_bank", "root", "money", "task", {"visit": {"trigger": "minecraft:location", "conditions": {"player": [
        {"condition": "minecraft:entity_properties", "entity": "this", "predicate": {"location": {"structures": f"{NS}:bank"}}}]}}})
    adv("guard_down", "visit_bank", "guard_dinosaur_spawn_egg", "goal", {"kill": {"trigger": "minecraft:player_killed_entity", "conditions": {
        "entity": [{"condition": "minecraft:entity_properties", "entity": "this", "predicate": {"type": f"{NS}:guard_dinosaur"}}]}}})
    adv("cash_out", "visit_bank", "coin", "task", {"money": has("money")})
    adv("full_auto", "guard_down", "ak47", "goal", {"ak47": has("ak47")})

def generate():
    gen_assets(); gen_sounds(); gen_loot(); gen_recipes(); gen_tags(); gen_worldgen(); gen_advancements()
