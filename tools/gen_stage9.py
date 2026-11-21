#!/usr/bin/env python3
"""0.8.0 data: the Mushroom Depths (the mushroom caves under the Mystic Grove), the Mushroom Man, the Mushroom Cyclops,
the Mushroomcopter and its spore, the Mushroom Sword, the Mushroom Shield, the six elemental books, the Mushroom
Outpost and the Mushroom Base (templates built by tools/gen_stage9_structures.py) and their chest loot.

Called by tools/gen_resources.py after gen_stage8b (tags are merged; files of the same name are replaced).
"""
import json
from gen_resources import NS, A, D, write, ns, item_def, flat_item, handheld_item, tag, adv, has
import gen_resources as G

W = f"{D}/worldgen"
BOOKS = ["fire", "water", "poison", "air", "lightning", "earth"]
MOBS = ["mushroom_man", "mushroom_cyclops", "mushroomcopter"]


# =================================================================== assets
def shield_models():
    """Same shape and hand positions as the vanilla shield (its special renderer flips Y and Z; the elements here are
    already flipped), drawn from one 32x32 texture: front, back, edges and the handle."""
    t = f"{NS}:item/mushroom_shield"
    def faces(north, south, east, west, up, down):
        return {"north": {"uv": north, "texture": "#s"}, "south": {"uv": south, "texture": "#s"},
                "east": {"uv": east, "texture": "#s"}, "west": {"uv": west, "texture": "#s"},
                "up": {"uv": up, "texture": "#s"}, "down": {"uv": down, "texture": "#s"}}
    elements = [
        # The board: 12 x 22 x 1. Its outer face (north, toward -z) carries the cap pattern.
        {"from": [-6, -11, 1], "to": [6, 11, 2], "faces": faces([0, 0, 6, 11], [6, 0, 12, 11], [12, 0, 12.5, 11], [12.5, 0, 13, 11],
                                                             [0, 11, 6, 11.5], [0, 11.5, 6, 12])},
        # The handle: 2 x 6 x 6 behind the board.
        {"from": [-1, -3, -5], "to": [1, 3, 1], "faces": faces([13, 0, 14, 3], [13, 0, 14, 3], [13, 3, 16, 6], [13, 3, 16, 6],
                                                            [14, 0, 15, 3], [15, 0, 16, 3])},
    ]
    normal = {"gui_light": "front", "textures": {"s": t, "particle": t}, "elements": elements, "display": {
        "thirdperson_righthand": {"rotation": [0, 90, 0], "translation": [10, 6, -4], "scale": [1, 1, 1]},
        "thirdperson_lefthand": {"rotation": [0, 90, 0], "translation": [10, 6, 12], "scale": [1, 1, 1]},
        "firstperson_righthand": {"rotation": [0, 180, 5], "translation": [-10, 1.75, -10], "scale": [1.25, 1.25, 1.25]},
        "firstperson_lefthand": {"rotation": [0, 180, 5], "translation": [10, 0, -10], "scale": [1.25, 1.25, 1.25]},
        "gui": {"rotation": [15, -25, -5], "translation": [2, 3, 0], "scale": [0.65, 0.65, 0.65]},
        "fixed": {"rotation": [0, 180, 0], "translation": [-4.5, 4.5, -5], "scale": [0.55, 0.55, 0.55]},
        "ground": {"rotation": [0, 0, 0], "translation": [2, 4, 2], "scale": [0.25, 0.25, 0.25]}}}
    blocking = {"parent": f"{NS}:item/mushroom_shield", "display": {
        "thirdperson_righthand": {"rotation": [45, 155, 0], "translation": [-3.49, 11, -2], "scale": [1, 1, 1]},
        "thirdperson_lefthand": {"rotation": [45, 155, 0], "translation": [11.51, 7, 2.5], "scale": [1, 1, 1]},
        "firstperson_righthand": {"rotation": [0, 180, -5], "translation": [-15, 3.25, -11], "scale": [1.25, 1.25, 1.25]},
        "firstperson_lefthand": {"rotation": [0, 180, -5], "translation": [5, 5, -11], "scale": [1.25, 1.25, 1.25]},
        "gui": {"rotation": [15, -25, -5], "translation": [2, 3, 0], "scale": [0.65, 0.65, 0.65]}}}
    write(f"{A}/models/item/mushroom_shield.json", normal)
    write(f"{A}/models/item/mushroom_shield_blocking.json", blocking)
    write(f"{A}/items/mushroom_shield.json", {"model": {"type": "minecraft:condition", "property": "minecraft:using_item",
        "on_false": {"type": "minecraft:model", "model": f"{NS}:item/mushroom_shield"},
        "on_true": {"type": "minecraft:model", "model": f"{NS}:item/mushroom_shield_blocking"}}})


def gen_assets():
    for i in ["mushroom_spore"] + [f"{b}_book" for b in BOOKS] + [f"{m}_spawn_egg" for m in MOBS]:
        flat_item(i)
    handheld_item("mushroom_sword")
    shield_models()


def gen_sounds():
    path = f"{A}/sounds.json"
    sounds = json.load(open(path, encoding="utf-8"))

    def ev(names, subtitle, pitch, volume=1.0):
        return {"subtitle": f"subtitles.{NS}.{subtitle}", "sounds": [{"name": s, "pitch": pitch, "volume": volume} for s in names]}

    def mob(name, ambient, hurt, death, pitch, volume=1.0, ambient_pitch=None):
        sounds[f"entity.{name}.ambient"] = ev(ambient, f"{name}.ambient", ambient_pitch or pitch, volume)
        sounds[f"entity.{name}.hurt"] = ev(hurt, f"{name}.hurt", pitch, volume)
        sounds[f"entity.{name}.death"] = ev(death, f"{name}.death", pitch, volume)
    # Provisional: re-pitched vanilla sounds until original recordings exist.
    mob("mushroom_man", [f"minecraft:mob/husk/idle{i}" for i in (1, 2, 3)], ["minecraft:mob/husk/hurt1", "minecraft:mob/husk/hurt2"],
        ["minecraft:mob/husk/death1", "minecraft:mob/husk/death2"], 1.35, 0.8)
    mob("mushroom_cyclops", [f"minecraft:mob/zoglin/idle{i}" for i in (1, 2, 3)], [f"minecraft:mob/zoglin/hurt{i}" for i in (1, 2, 3)],
        [f"minecraft:mob/zoglin/death{i}" for i in (1, 2, 3)], 0.6, 1.0)
    # The Mushroomcopter's "ambient" sound is the low whir of its propeller (revival proposal), so you hear it coming.
    mob("mushroomcopter", [f"minecraft:mob/bee/loop{i}" for i in (1, 2, 3, 4, 5)], [f"minecraft:mob/breeze/hurt{i}" for i in (1, 2, 3)],
        ["minecraft:mob/breeze/death1", "minecraft:mob/breeze/death2"], 1.3, 0.7, ambient_pitch=0.5)
    sounds["entity.mushroomcopter.shoot"] = ev([f"minecraft:block/fungus/break{i}" for i in (1, 2, 3)], "mushroomcopter.shoot", 1.5)
    # Far-off sounds of the depths: re-pitched cave sounds, rarer and lower than the vanilla mood sound.
    sounds["ambient.mushroom_depths.additions"] = ev([f"minecraft:ambient/cave/cave{i}" for i in (3, 7, 13, 14, 17)],
                                                     "mushroom_depths.additions", 0.65, 0.5)
    write(path, sounds)


# =================================================================== loot
def entry(item, weight, lo, hi):
    e = {"type": "minecraft:item", "name": ns(item), "weight": weight}
    if (lo, hi) != (1, 1):
        e["functions"] = [{"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": float(lo), "max": float(hi)}, "add": False}]
    return e


def outpost_entries():
    # Counts are the ones of the original tables; the weights follow their order (red and brown mushrooms most
    # likely, the sword and the shield least likely).
    return [entry("minecraft:red_mushroom", 25, 6, 12), entry("minecraft:brown_mushroom", 25, 6, 12), entry("blue_mushroom", 20, 6, 12),
            entry("minecraft:iron_ingot", 15, 6, 9), entry("minecraft:gold_ingot", 12, 5, 8), entry("minecraft:emerald", 10, 4, 7),
            entry("minecraft:mushroom_stew", 8, 1, 1), entry("mushroom_sword", 3, 1, 1), entry("mushroom_shield", 3, 1, 1)]


def base_entries():
    return [entry("minecraft:red_mushroom", 25, 10, 13), entry("minecraft:brown_mushroom", 25, 10, 13), entry("blue_mushroom", 20, 10, 13),
            entry("minecraft:iron_ingot", 15, 10, 15), entry("minecraft:gold_ingot", 12, 7, 10), entry("minecraft:emerald", 10, 8, 11),
            entry("minecraft:diamond", 8, 6, 9), entry("minecraft:mushroom_stew", 8, 1, 1), entry("mushroom_sword", 3, 1, 1),
            entry("mushroom_shield", 3, 1, 1)] + [entry(f"{b}_book", 2, 1, 1) for b in BOOKS]


def gen_loot():
    def chest(name, lo, hi, entries):
        write(f"{D}/loot_table/chests/{name}.json", {"type": "minecraft:chest", "random_sequence": f"{NS}:chests/{name}",
            "pools": [{"rolls": {"type": "minecraft:uniform", "min": float(lo), "max": float(hi)}, "bonus_rolls": 1.0, "entries": entries}]})
    chest("mushroom_outpost", 4, 6, outpost_entries())
    chest("mushroom_base", 6, 8, base_entries())

    def pool(item, lo, hi, chance=None):
        p = {"rolls": 1.0, "bonus_rolls": 0.0, "entries": [{"type": "minecraft:item", "name": ns(item), "functions": [
            {"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}, "add": False},
            {"function": "minecraft:enchanted_count_increase", "enchantment": "minecraft:looting", "count": {"type": "minecraft:uniform", "min": 0.0, "max": 1.0}}]}]}
        if chance is not None:
            p["conditions"] = [{"condition": "minecraft:killed_by_player"}, {"condition": "minecraft:random_chance", "chance": chance}]
        return p

    def entity(name, pools):
        write(f"{D}/loot_table/entities/{name}.json", {"type": "minecraft:entity", "random_sequence": f"{NS}:entities/{name}", "pools": pools})
    # Revival choices: the creatures drop the mushrooms they are made of.
    entity("mushroom_man", [pool("minecraft:red_mushroom", 0.0, 2.0), pool("minecraft:brown_mushroom", 0.0, 1.0)])
    entity("mushroom_cyclops", [pool("blue_mushroom", 1.0, 3.0), pool("minecraft:brown_mushroom", 0.0, 2.0)])
    entity("mushroomcopter", [pool("mushroom_spore", 0.0, 2.0), pool("purple_mushroom", 0.0, 1.0)])


# =================================================================== tags
def gen_tags():
    n = lambda *xs: [ns(x) for x in xs]
    tag("item", NS, "mushroom_repair_materials", n("minecraft:red_mushroom", "minecraft:brown_mushroom", "blue_mushroom", "purple_mushroom"))
    tag("item", "minecraft", "swords", n("mushroom_sword"))
    tag("item", "minecraft", "enchantable/durability", n("mushroom_shield") + [ns(f"{b}_book") for b in BOOKS])
    tag("item", "c", "tools/shield", n("mushroom_shield"))
    tag("entity_type", "minecraft", "fall_damage_immune", n("mushroomcopter"))
    tag("entity_type", NS, "mushroom_creatures", n(*MOBS))
    for t in [("minecraft", "is_overworld"), ("c", "is_overworld"), ("c", "is_cave"), ("c", "is_underground"), (NS, "is_mystic"),
              ("minecraft", "has_structure/mineshaft"), ("minecraft", "has_structure/stronghold"), ("minecraft", "has_structure/trial_chambers"),
              (NS, "has_structure/mushroom_outpost"), (NS, "has_structure/mushroom_base")]:
        tag("worldgen/biome", t[0], t[1], n("mushroom_depths"))


# =================================================================== worldgen
def state(name, **props):
    s = {"Name": ns(name)}
    if props:
        s["Properties"] = {k: str(v).lower() for k, v in props.items()}
    return s


def cave_height(lo=-58, hi=60):
    return {"type": "minecraft:height_range", "height": {"type": "minecraft:uniform", "min_inclusive": {"absolute": lo}, "max_inclusive": {"absolute": hi}}}


def scan(direction, steps=12):
    return {"type": "minecraft:environment_scan", "direction_of_search": direction, "max_steps": steps,
            "target_condition": {"type": "minecraft:solid"}, "allowed_search_condition": {"type": "minecraft:matching_block_tag", "tag": "minecraft:air"}}


def gen_worldgen():
    stone_targets = lambda st: [
        {"state": st, "target": {"predicate_type": "minecraft:tag_match", "tag": "minecraft:stone_ore_replaceables"}},
        {"state": st, "target": {"predicate_type": "minecraft:tag_match", "tag": "minecraft:deepslate_ore_replaceables"}}]
    # Dark rock bands across the walls (big blobs of tuff and deepslate) and brown stretches on the slopes (packed mud).
    for name, block, size, count in (("tuff_band", "minecraft:tuff", 48, 10), ("dark_band", "minecraft:deepslate", 56, 8),
                                     ("brown_band", "minecraft:packed_mud", 40, 6)):
        write(f"{W}/configured_feature/ore_depths_{name}.json", {"type": "minecraft:ore", "config": {
            "discard_chance_on_air_exposure": 0.0, "size": size, "targets": stone_targets({"Name": block})}})
        write(f"{W}/placed_feature/ore_depths_{name}.json", {"feature": f"{NS}:ore_depths_{name}", "placement": [
            {"type": "minecraft:count", "count": count}, {"type": "minecraft:in_square"}, cave_height(-60, 64), {"type": "minecraft:biome"}]})

    # Red mushroom growths: stepped shelves on the walls (Java feature) and wide patches of red mushroom blocks on the floor.
    write(f"{W}/configured_feature/wall_mushroom.json", {"type": f"{NS}:wall_mushroom", "config": {}})
    write(f"{W}/placed_feature/wall_mushrooms.json", {"feature": f"{NS}:wall_mushroom", "placement": [
        {"type": "minecraft:count", "count": 40}, {"type": "minecraft:in_square"}, cave_height(), {"type": "minecraft:biome"}]})
    floor_blocks = ["minecraft:stone", "minecraft:deepslate", "minecraft:tuff", "minecraft:brown_mushroom_block", "minecraft:coarse_dirt",
                    "minecraft:andesite", "minecraft:diorite", "minecraft:granite", "minecraft:packed_mud", "minecraft:dirt", "minecraft:gravel"]
    write(f"{W}/configured_feature/red_mushroom_floor_patch.json", {"type": "minecraft:disk", "config": {
        "half_height": 1, "radius": {"type": "minecraft:uniform", "min_inclusive": 2, "max_inclusive": 5},
        "state_provider": {"type": "minecraft:simple_state_provider", "state": {"Name": "minecraft:red_mushroom_block"}},
        "target": {"type": "minecraft:matching_blocks", "blocks": floor_blocks}}})
    write(f"{W}/placed_feature/red_mushroom_floor_patches.json", {"feature": f"{NS}:red_mushroom_floor_patch", "placement": [
        {"type": "minecraft:count", "count": 14}, {"type": "minecraft:in_square"}, cave_height(), scan("down"),
        {"type": "minecraft:biome"}]})

    # Small mushrooms on the floor (brown, red and the grove's own).
    def small(name, block, count):
        write(f"{W}/configured_feature/{name}.json", {"type": "minecraft:simple_block", "config": {
            "to_place": {"type": "minecraft:simple_state_provider", "state": {"Name": ns(block)}}}})
        write(f"{W}/placed_feature/{name}.json", {"feature": f"{NS}:{name}", "placement": [
            {"type": "minecraft:count", "count": count}, {"type": "minecraft:in_square"}, cave_height(), scan("down"),
            {"type": "minecraft:random_offset", "xz_spread": 0, "y_spread": 1},
            {"type": "minecraft:block_predicate_filter", "predicate": {"type": "minecraft:all_of", "predicates": [
                {"type": "minecraft:matching_block_tag", "tag": "minecraft:air"},
                {"type": "minecraft:would_survive", "state": {"Name": ns(block)}}]}},
            {"type": "minecraft:biome"}]})
    small("depths_brown_mushrooms", "minecraft:brown_mushroom", 24)
    small("depths_red_mushrooms", "minecraft:red_mushroom", 16)

    # Fungal lights (revival proposal): a few shroomlights set into the ceilings, and glow lichen, so paths and
    # silhouettes show here and there without lifting the darkness.
    write(f"{W}/configured_feature/ceiling_shroomlight.json", {"type": "minecraft:simple_block", "config": {
        "to_place": {"type": "minecraft:simple_state_provider", "state": {"Name": "minecraft:shroomlight"}}}})
    write(f"{W}/placed_feature/ceiling_shroomlights.json", {"feature": f"{NS}:ceiling_shroomlight", "placement": [
        {"type": "minecraft:count", "count": 3}, {"type": "minecraft:in_square"}, cave_height(), scan("up", 16),
        {"type": "minecraft:block_predicate_filter", "predicate": {"type": "minecraft:matching_block_tag", "tag": "minecraft:base_stone_overworld"}},
        {"type": "minecraft:biome"}]})

    # Pointed formations on the ceilings and a few stone pillars across the halls (the vanilla dripstone features,
    # far fewer than in Dripstone Caves).
    write(f"{W}/placed_feature/depths_pillars.json", {"feature": "minecraft:large_dripstone", "placement": [
        {"type": "minecraft:count", "count": {"type": "minecraft:uniform", "min_inclusive": 2, "max_inclusive": 6}},
        {"type": "minecraft:in_square"}, cave_height(), {"type": "minecraft:biome"}]})
    write(f"{W}/placed_feature/depths_stalactites.json", {"feature": "minecraft:pointed_dripstone", "placement": [
        {"type": "minecraft:count", "count": {"type": "minecraft:uniform", "min_inclusive": 24, "max_inclusive": 40}},
        {"type": "minecraft:in_square"}, cave_height(),
        {"type": "minecraft:count", "count": {"type": "minecraft:uniform", "min_inclusive": 1, "max_inclusive": 3}},
        {"type": "minecraft:random_offset", "xz_spread": {"type": "minecraft:clamped_normal", "deviation": 3.0, "max_inclusive": 8, "mean": 0.0, "min_inclusive": -8},
         "y_spread": {"type": "minecraft:clamped_normal", "deviation": 0.6, "max_inclusive": 2, "mean": 0.0, "min_inclusive": -2}},
        {"type": "minecraft:biome"}]})

    # Big halls: a cave carver with much wider and taller tunnels than vanilla's.
    write(f"{W}/configured_carver/mushroom_hall.json", {"type": "minecraft:cave", "config": {
        "floor_level": {"type": "minecraft:uniform", "min_inclusive": -1.0, "max_exclusive": -0.6},
        "horizontal_radius_multiplier": {"type": "minecraft:uniform", "min_inclusive": 2.0, "max_exclusive": 3.5},
        "vertical_radius_multiplier": {"type": "minecraft:uniform", "min_inclusive": 1.8, "max_exclusive": 2.8},
        "lava_level": {"above_bottom": 8}, "probability": 0.09, "replaceable": "#minecraft:overworld_carver_replaceables",
        "y": {"type": "minecraft:uniform", "min_inclusive": {"absolute": -40}, "max_inclusive": {"absolute": 40}},
        "yScale": {"type": "minecraft:uniform", "min_inclusive": 0.6, "max_exclusive": 1.0}}})

    def spawn(t, w, lo, hi):
        return {"type": ns(t), "weight": w, "minCount": lo, "maxCount": hi}
    write(f"{W}/biome/mushroom_depths.json", {
        "attributes": {
            "minecraft:audio/background_music": {"default": {"max_delay": 24000, "min_delay": 12000, "sound": "minecraft:music.overworld.dripstone_caves"}},
            # Distant sounds (revival proposal): the usual cave mood plus rare, low far-off rumbles.
            "minecraft:audio/ambient_sounds": {
                "additions": {"sound": f"{NS}:ambient.mushroom_depths.additions", "tick_chance": 0.003},
                "mood": {"block_search_extent": 8, "offset": 2.0, "sound": "minecraft:ambient.cave", "tick_delay": 6000}},
            # Faint brown spores drifting in the dark.
            "minecraft:visual/ambient_particles": [{"particle": {"type": "minecraft:dust", "color": 0xB08A64, "scale": 0.6}, "probability": 0.003}],
            "minecraft:visual/fog_color": "#3a2a26", "minecraft:visual/sky_color": "#78a7ff", "minecraft:visual/water_fog_color": "#2a1e1a"},
        "carvers": ["minecraft:cave", "minecraft:cave_extra_underground", "minecraft:canyon", f"{NS}:mushroom_hall"],
        "downfall": 0.8,
        "effects": {"foliage_color": "#6b4a2a", "grass_color": "#7a5a3a", "water_color": "#3f5a6a"},
        "features": [[], ["minecraft:lake_lava_underground"], [f"{NS}:depths_pillars"],
                     ["minecraft:monster_room", "minecraft:monster_room_deep"], [], [],
                     [f"{NS}:ore_depths_tuff_band", f"{NS}:ore_depths_dark_band", f"{NS}:ore_depths_brown_band"] + G.VANILLA_UNDERGROUND,
                     [f"{NS}:depths_stalactites"],
                     ["minecraft:spring_water", "minecraft:spring_lava"],
                     [f"{NS}:red_mushroom_floor_patches", f"{NS}:wall_mushrooms", f"{NS}:ceiling_shroomlights", "minecraft:glow_lichen",
                      f"{NS}:depths_brown_mushrooms", f"{NS}:depths_red_mushrooms"],
                     []],
        "has_precipitation": True,
        "spawn_costs": {},
        "spawners": {
            "ambient": [spawn("minecraft:bat", 10, 8, 8)], "axolotls": [], "creature": [], "misc": [],
            # As in the original: the three mushroom creatures (the Cyclops rarer than the Mushroom Man), zombies,
            # skeletons and witches.
            "monster": [spawn("mushroom_man", 100, 1, 2), spawn("mushroomcopter", 50, 1, 2), spawn("mushroom_cyclops", 20, 1, 1),
                        spawn("minecraft:zombie", 70, 2, 4), spawn("minecraft:skeleton", 70, 2, 4), spawn("minecraft:witch", 5, 1, 1)],
            "underground_water_creature": [spawn("minecraft:glow_squid", 10, 4, 6)], "water_ambient": [], "water_creature": []},
        "temperature": 0.6})

    def structure(name, salt, spacing, min_y, max_y, clearance):
        write(f"{W}/structure/{name}.json", {"type": f"{NS}:mushroom_cave_structure", "biomes": f"#{NS}:has_structure/{name}",
            "step": "underground_structures", "spawn_overrides": {}, "terrain_adaptation": "beard_box",
            "template": f"{NS}:{name}", "min_y": min_y, "max_y": max_y, "clearance": clearance})
        for freq, factor in {"rare": 1.4, "normal": 1.0, "common": 0.7}.items():
            sp = int(spacing * factor)
            write(f"{W}/structure_set/{name}_{freq}.json", {
                "neoforge:conditions": [{"type": f"{NS}:structure_frequency", "frequency": freq, "structure": name}],
                "structures": [{"structure": f"{NS}:{name}", "weight": 1}],
                "placement": {"type": "minecraft:random_spread", "spacing": sp, "separation": max(4, sp // 3), "salt": salt}})
    structure("mushroom_outpost", 802114551, 14, -44, 36, 6)
    structure("mushroom_base", 802114552, 24, -40, 30, 8)


# =================================================================== advancements
def gen_advancements():
    def kill(mob):
        return {"trigger": "minecraft:player_killed_entity", "conditions": {"entity": [
            {"condition": "minecraft:entity_properties", "entity": "this", "predicate": {"type": ns(mob)}}]}}
    adv("into_the_mushroom_depths", "land_of_fantastic_things", "minecraft:red_mushroom_block", "task", {"enter": {
        "trigger": "minecraft:location", "conditions": {"player": [{"condition": "minecraft:entity_properties", "entity": "this",
            "predicate": {"location": {"biomes": f"{NS}:mushroom_depths"}}}]}}})
    adv("grounded", "into_the_mushroom_depths", "mushroom_spore", "task", {"kill": kill("mushroomcopter")})
    adv("one_eyed_giant", "into_the_mushroom_depths", "mushroom_cyclops_spawn_egg", "goal", {"kill": kill("mushroom_cyclops")})
    adv("the_top_of_the_base", "into_the_mushroom_depths", "fire_book", "goal",
        {b: has(f"{b}_book") for b in BOOKS}, requirements=[BOOKS])


def generate():
    gen_assets(); gen_sounds(); gen_loot(); gen_tags(); gen_worldgen(); gen_advancements()
