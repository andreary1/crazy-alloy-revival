#!/usr/bin/env python3
"""Enchanted tab, second batch: the Purple and Blue Mushrooms, the Wizard and the Dwarf, the Dwarf Hut and the Wizard
Tower (templates built by tools/gen_stage8_structures.py).

Called by tools/gen_resources.py right after gen_stage8 (the Mystic Grove biome file, written there, lists the
mushroom patches placed here).
"""
import json
from gen_resources import NS, A, D, write, ns, item_def, flat_item, tag, shapeless, drop_self

W = f"{D}/worldgen"


# =================================================================== assets
def mushroom_model(color):
    """Short stem, a wide square cap and two thin steps on top (16-unit block space)."""
    top, side, stem = f"{NS}:block/{color}_mushroom_top", f"{NS}:block/{color}_mushroom_side", f"{NS}:block/mystic_mushroom_stem"

    def box(frm, to, top_uv, side_uv, down_tex, down_uv):
        faces = {d: {"texture": "#side", "uv": side_uv} for d in ("north", "south", "east", "west")}
        faces["up"] = {"texture": "#top", "uv": top_uv}
        faces["down"] = {"texture": down_tex, "uv": down_uv}
        return {"from": frm, "to": to, "faces": faces}

    stem_faces = {d: {"texture": "#stem", "uv": [6, 4, 10, 9]} for d in ("north", "south", "east", "west")}
    stem_faces["down"] = {"texture": "#stem", "uv": [6, 6, 10, 10]}
    return {"parent": "minecraft:block/block", "ambientocclusion": False,
            "textures": {"top": top, "side": side, "stem": stem, "particle": side},
            "elements": [
                {"from": [6, 0, 6], "to": [10, 5, 10], "faces": stem_faces},
                box([3, 5, 3], [13, 8, 13], [3, 3, 13, 13], [3, 8, 13, 11], "#stem", [3, 12, 13, 16]),
                box([4, 8, 4], [12, 9, 12], [4, 4, 12, 12], [4, 7, 12, 8], "#top", [4, 4, 12, 12]),
                box([6, 9, 6], [10, 10, 10], [6, 6, 10, 10], [6, 6, 10, 7], "#top", [6, 6, 10, 10]),
            ],
            "display": {"gui": {"rotation": [30, 225, 0], "translation": [0, 2, 0], "scale": [1.0, 1.0, 1.0]},
                        "ground": {"rotation": [0, 0, 0], "translation": [0, 3, 0], "scale": [0.5, 0.5, 0.5]},
                        "fixed": {"rotation": [0, 0, 0], "translation": [0, 0, 0], "scale": [0.8, 0.8, 0.8]},
                        "thirdperson_righthand": {"rotation": [75, 45, 0], "translation": [0, 2.5, 0], "scale": [0.5, 0.5, 0.5]},
                        "firstperson_righthand": {"rotation": [0, 45, 0], "translation": [0, 3, 0], "scale": [0.6, 0.6, 0.6]}}}


def gen_assets():
    for color in ("purple", "blue"):
        name = f"{color}_mushroom"
        write(f"{A}/models/block/{name}.json", mushroom_model(color))
        # Four turned variants so patches don't all face the same way.
        write(f"{A}/blockstates/{name}.json", {"variants": {"": [{"model": f"{NS}:block/{name}", "y": r} for r in (0, 90, 180, 270)]}})
        item_def(name, f"{NS}:block/{name}")
    for egg in ("wizard_spawn_egg", "dwarf_spawn_egg"):
        flat_item(egg)


def gen_sounds():
    path = f"{A}/sounds.json"
    sounds = json.load(open(path, encoding="utf-8"))

    def ev(names, subtitle, pitch, volume=1.0):
        return {"subtitle": f"subtitles.{NS}.{subtitle}", "sounds": [{"name": s, "pitch": pitch, "volume": volume} for s in names]}
    # Provisional: re-pitched villager voices until original recordings exist.
    for name, pitch in (("wizard", 0.8), ("dwarf", 0.62)):
        sounds[f"entity.{name}.ambient"] = ev([f"minecraft:mob/villager/idle{i}" for i in range(1, 4)], f"{name}.ambient", pitch)
        sounds[f"entity.{name}.hurt"] = ev([f"minecraft:mob/villager/hit{i}" for i in range(1, 5)], f"{name}.hurt", pitch)
        sounds[f"entity.{name}.death"] = ev(["minecraft:mob/villager/death"], f"{name}.death", pitch)
    write(path, sounds)


# =================================================================== loot
def e(item, weight, lo, hi, extra=None):
    fns = [{"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}, "add": False}]
    return {"type": "minecraft:item", "name": ns(item), "weight": weight, "functions": fns + (extra or [])}


def potion(p, weight):
    return {"type": "minecraft:item", "name": "minecraft:potion", "weight": weight,
            "functions": [{"function": "minecraft:set_potion", "id": f"minecraft:{p}"}]}


def gen_loot():
    for name in ("purple_mushroom", "blue_mushroom"):
        write(f"{D}/loot_table/blocks/{name}.json", drop_self(name))

    def chest(name, rolls, entries):
        write(f"{D}/loot_table/chests/{name}.json", {"type": "minecraft:chest", "random_sequence": f"{NS}:chests/{name}",
            "pools": [{"rolls": rolls, "bonus_rolls": 0.0, "entries": entries}]})
    # Revival choices: what the chests hold is not shown in the references.
    chest("dwarf_hut", {"type": "minecraft:uniform", "min": 3.0, "max": 6.0}, [
        e("minecraft:raw_iron", 12, 1, 5), e("minecraft:coal", 14, 2, 8), e("minecraft:raw_copper", 10, 2, 6),
        e("minecraft:raw_gold", 5, 1, 3), e("minecraft:torch", 8, 4, 12), e("minecraft:bread", 8, 1, 3),
        e("minecraft:iron_pickaxe", 2, 1, 1, [{"function": "minecraft:set_damage", "damage": {"type": "minecraft:uniform", "min": 0.3, "max": 0.9}}]),
        e("tourmaline", 2, 1, 2)])
    chest("dwarf_hut_stores", {"type": "minecraft:uniform", "min": 2.0, "max": 4.0}, [
        e("minecraft:cobblestone", 10, 4, 16), e("minecraft:stick", 8, 2, 6), e("minecraft:coal", 8, 1, 4),
        e("minecraft:iron_nugget", 6, 2, 8), e("minecraft:baked_potato", 6, 1, 3)])
    chest("wizard_tower", {"type": "minecraft:uniform", "min": 4.0, "max": 7.0}, [
        potion("healing", 8), potion("night_vision", 6), potion("regeneration", 4), potion("invisibility", 3),
        e("minecraft:glowstone_dust", 10, 2, 6), e("minecraft:redstone", 8, 2, 6), e("minecraft:book", 8, 1, 3),
        e("minecraft:glass_bottle", 8, 1, 4), e("jar", 6, 1, 2), e("purple_mushroom", 6, 1, 3), e("blue_mushroom", 6, 1, 3),
        e("minecraft:emerald", 4, 1, 3), e("elixir_of_life", 1, 1, 1),
        {"type": "minecraft:item", "name": "minecraft:book", "weight": 3,
         "functions": [{"function": "minecraft:enchant_randomly"}]}])
    chest("wizard_tower_ingredients", {"type": "minecraft:uniform", "min": 3.0, "max": 6.0}, [
        e("purple_mushroom", 10, 1, 4), e("blue_mushroom", 10, 1, 4), e("minecraft:nether_wart", 6, 1, 4),
        e("minecraft:spider_eye", 5, 1, 2), e("minecraft:sugar", 6, 1, 4), e("minecraft:glistering_melon_slice", 3, 1, 2),
        e("minecraft:glass_bottle", 8, 1, 4), e("mystic_leaves", 4, 1, 4)])
    chest("wizard_tower_store", {"type": "minecraft:uniform", "min": 2.0, "max": 5.0}, [
        e("minecraft:paper", 10, 2, 8), e("minecraft:book", 8, 1, 3), e("minecraft:candle", 8, 1, 3),
        e("minecraft:glass_bottle", 8, 1, 4), e("minecraft:lapis_lazuli", 6, 2, 6), e("minecraft:amethyst_shard", 5, 1, 4),
        e("minecraft:emerald", 3, 1, 2), potion("night_vision", 3)])
    # The Wizard and the Dwarf drop nothing (they are people, not game).
    for name in ("wizard", "dwarf"):
        write(f"{D}/loot_table/entities/{name}.json", {"type": "minecraft:entity", "random_sequence": f"{NS}:entities/{name}", "pools": []})


# =================================================================== recipes
def gen_recipes():
    # Revival choice (no use is shown in the references): the mushrooms give dye of their colour; the Wizard also buys them.
    shapeless("purple_dye_from_purple_mushroom", ["purple_mushroom"], "minecraft:purple_dye", 1, "misc")
    shapeless("blue_dye_from_blue_mushroom", ["blue_mushroom"], "minecraft:blue_dye", 1, "misc")


# =================================================================== tags
def gen_tags():
    n = lambda *xs: [ns(x) for x in xs]
    tag("block", "minecraft", "replaceable_by_trees", n("purple_mushroom", "blue_mushroom"))
    for s in ("wizard_tower", "dwarf_hut"):
        tag("worldgen/biome", NS, f"has_structure/{s}", [f"{NS}:mystic_grove"])
    tag("entity_type", NS, "mystic_dwellers", n("wizard", "dwarf"))


# =================================================================== worldgen
def gen_worldgen():
    # Small patches of each mushroom on the forest floor (turned randomly by the blockstate).
    for color, rarity in (("purple", 3), ("blue", 3)):
        name = f"{color}_mushroom"
        write(f"{W}/configured_feature/{name}_patch.json", {"type": "minecraft:simple_block",
            "config": {"to_place": {"type": "minecraft:simple_state_provider", "state": {"Name": ns(name)}}}})
        write(f"{W}/placed_feature/{name}_patch.json", {"feature": f"{NS}:{name}_patch", "placement": [
            {"type": "minecraft:rarity_filter", "chance": rarity}, {"type": "minecraft:in_square"},
            {"type": "minecraft:heightmap", "heightmap": "MOTION_BLOCKING"}, {"type": "minecraft:biome"},
            {"type": "minecraft:count", "count": 6},
            {"type": "minecraft:random_offset", "xz_spread": {"type": "minecraft:trapezoid", "max": 4, "min": -4, "plateau": 0},
             "y_spread": {"type": "minecraft:trapezoid", "max": 2, "min": -2, "plateau": 0}},
            {"type": "minecraft:block_predicate_filter", "predicate": {"type": "minecraft:all_of", "predicates": [
                {"type": "minecraft:matching_blocks", "blocks": "minecraft:air"},
                {"type": "minecraft:would_survive", "state": {"Name": ns(name)}}]}}]})

    def structure(name, adaptation, salt, spacing):
        # A single pool element is placed one block lower than the surface (its ground level delta is 1), which sank
        # the hut and the tower into the ground; start one block higher so the bottom layer rests on the surface.
        write(f"{W}/structure/{name}.json", {"type": "minecraft:jigsaw", "biomes": f"#{NS}:has_structure/{name}",
            "step": "surface_structures", "spawn_overrides": {}, "terrain_adaptation": adaptation,
            "start_pool": f"{NS}:{name}/start", "size": 1, "start_height": {"absolute": 1},
            "project_start_to_heightmap": "WORLD_SURFACE_WG", "max_distance_from_center": 40, "use_expansion_hack": False})
        write(f"{W}/template_pool/{name}/start.json", {"fallback": "minecraft:empty", "elements": [{"weight": 1, "element": {
            "element_type": "minecraft:single_pool_element", "location": f"{NS}:{name}", "projection": "rigid", "processors": "minecraft:empty"}}]})
        for freq, factor in {"rare": 1.4, "normal": 1.0, "common": 0.7}.items():
            sp = int(spacing * factor)
            write(f"{W}/structure_set/{name}_{freq}.json", {
                "neoforge:conditions": [{"type": f"{NS}:structure_frequency", "frequency": freq, "structure": name}],
                "structures": [{"structure": f"{NS}:{name}", "weight": 1}],
                "placement": {"type": "minecraft:random_spread", "spacing": sp, "separation": max(4, sp // 3), "salt": salt}})
    # The hut sits on a little filled platform; the tower gets a solid foundation under its central block.
    structure("dwarf_hut", "beard_thin", 731902441, 18)
    structure("wizard_tower", "beard_box", 731902442, 34)


def generate():
    gen_assets(); gen_sounds(); gen_loot(); gen_recipes(); gen_tags(); gen_worldgen()
