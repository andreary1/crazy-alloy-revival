#!/usr/bin/env python3
"""Stage 8 data: the Enchanted tab. The Mystic Grove biome (turquoise grass, mystic dirt, very tall dark mystic trees,
giant red mushrooms, mossy rocks, a green fog), the mystic wood set, the Troll, the Fairy (with the Jar and the Jar With
A Fairy), the Evil Fairy (with the Elixir of Life) and the Ent.

Called by tools/gen_resources.py after gen_stage7 (files written here replace earlier files of the same name, and
tags are merged).
"""
import json
import gen_resources as G
import gen_stage2
from gen_resources import NS, A, D, write, ns, simple_state, item_def, flat_item, state, tag, shaped, shapeless, adv, has, drop_self, silk_or

W = f"{D}/worldgen"
WOOD = ["mystic_stairs", "mystic_slab", "mystic_fence", "mystic_fence_gate", "mystic_door", "mystic_button", "mystic_pressure_plate"]

# =================================================================== assets
def gen_assets():
    G.block_cube("mystic_dirt")
    G.block_cube("mystic_planks")
    # Grass: light turquoise top, darker sides over mystic dirt.
    write(f"{A}/models/block/mystic_grass_block.json", {"parent": "minecraft:block/cube_bottom_top", "textures": {
        "top": f"{NS}:block/mystic_grass_block_top", "bottom": f"{NS}:block/mystic_dirt", "side": f"{NS}:block/mystic_grass_block_side"}})
    write(f"{A}/blockstates/mystic_grass_block.json", {"variants": {"": [
        {"model": f"{NS}:block/mystic_grass_block"}, {"model": f"{NS}:block/mystic_grass_block", "y": 90},
        {"model": f"{NS}:block/mystic_grass_block", "y": 180}, {"model": f"{NS}:block/mystic_grass_block", "y": 270}]}})
    item_def("mystic_grass_block", f"{NS}:block/mystic_grass_block")
    # Log.
    write(f"{A}/models/block/mystic_log.json", {"parent": "minecraft:block/cube_column", "textures": {
        "end": f"{NS}:block/mystic_log_top", "side": f"{NS}:block/mystic_log"}})
    write(f"{A}/models/block/mystic_log_horizontal.json", {"parent": "minecraft:block/cube_column_horizontal", "textures": {
        "end": f"{NS}:block/mystic_log_top", "side": f"{NS}:block/mystic_log"}})
    write(f"{A}/blockstates/mystic_log.json", {"variants": {
        "axis=x": {"model": f"{NS}:block/mystic_log_horizontal", "x": 90, "y": 90},
        "axis=y": {"model": f"{NS}:block/mystic_log"},
        "axis=z": {"model": f"{NS}:block/mystic_log_horizontal", "x": 90}}})
    item_def("mystic_log", f"{NS}:block/mystic_log")
    # Leaves and sapling.
    write(f"{A}/models/block/mystic_leaves.json", {"parent": "minecraft:block/leaves", "textures": {"all": f"{NS}:block/mystic_leaves"}})
    simple_state("mystic_leaves")
    item_def("mystic_leaves", f"{NS}:block/mystic_leaves")
    write(f"{A}/models/block/mystic_sapling.json", {"parent": "minecraft:block/cross", "render_type": "minecraft:cutout",
        "textures": {"cross": f"{NS}:block/mystic_sapling"}})
    write(f"{A}/blockstates/mystic_sapling.json", {"variants": {"stage=0": {"model": f"{NS}:block/mystic_sapling"},
                                                               "stage=1": {"model": f"{NS}:block/mystic_sapling"}}})
    flat_item("mystic_sapling", "block/mystic_sapling")
    gen_stage2.wood_set("mystic")
    # Items.
    for i in ("jar", "jar_with_a_fairy", "elixir_of_life", "troll_spawn_egg", "fairy_spawn_egg", "evil_fairy_spawn_egg", "ent_spawn_egg",
              "shadow_eye_spawn_egg", "shadow_wanderer_spawn_egg"):
        flat_item(i)

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
    mob("troll", ["minecraft:mob/ravager/idle1", "minecraft:mob/ravager/idle2", "minecraft:mob/ravager/idle3"],
        ["minecraft:mob/ravager/hurt1", "minecraft:mob/ravager/hurt2", "minecraft:mob/ravager/hurt3"],
        ["minecraft:mob/ravager/death1", "minecraft:mob/ravager/death2"], 1.35, 0.8)
    mob("fairy", ["minecraft:mob/allay/idle_without_item1", "minecraft:mob/allay/idle_without_item2", "minecraft:mob/allay/idle_without_item3"],
        ["minecraft:mob/allay/hurt1", "minecraft:mob/allay/hurt2"], ["minecraft:mob/allay/death1", "minecraft:mob/allay/death2"], 1.3, 0.7)
    mob("evil_fairy", ["minecraft:mob/vex/idle1", "minecraft:mob/vex/idle2", "minecraft:mob/vex/idle3"],
        ["minecraft:mob/vex/hurt1", "minecraft:mob/vex/hurt2"], ["minecraft:mob/vex/death1", "minecraft:mob/vex/death2"], 1.2, 0.7)
    mob("ent", ["minecraft:random/door_open", "minecraft:random/door_close"],
        ["minecraft:dig/wood1", "minecraft:dig/wood2", "minecraft:dig/wood3", "minecraft:dig/wood4"],
        ["minecraft:random/break"], 0.45, 1.2)
    mob("shadow_eye", [f"minecraft:mob/phantom/idle{i}" for i in range(1, 6)], [f"minecraft:mob/phantom/hurt{i}" for i in range(1, 4)],
        [f"minecraft:mob/phantom/death{i}" for i in range(1, 4)], 0.6, 0.6)
    mob("shadow_wanderer", [f"minecraft:mob/endermen/idle{i}" for i in range(1, 6)], [f"minecraft:mob/endermen/hit{i}" for i in range(1, 5)],
        ["minecraft:mob/endermen/death"], 0.65, 0.9)
    write(path, sounds)

# =================================================================== loot
def gen_loot():
    for b in ["mystic_dirt", "mystic_log", "mystic_planks", "mystic_sapling"] + [w for w in WOOD if w not in ("mystic_slab", "mystic_door")]:
        write(f"{D}/loot_table/blocks/{b}.json", drop_self(b))
    write(f"{D}/loot_table/blocks/mystic_slab.json", {"type": "minecraft:block", "random_sequence": f"{NS}:blocks/mystic_slab",
        "pools": [{"rolls": 1.0, "bonus_rolls": 0.0, "entries": [{"type": "minecraft:item", "name": f"{NS}:mystic_slab", "functions": [
            {"function": "minecraft:set_count", "add": False, "count": 2.0, "conditions": [{"condition": "minecraft:block_state_property",
             "block": f"{NS}:mystic_slab", "properties": {"type": "double"}}]}, {"function": "minecraft:explosion_decay"}]}]}]})
    write(f"{D}/loot_table/blocks/mystic_door.json", {"type": "minecraft:block", "random_sequence": f"{NS}:blocks/mystic_door",
        "pools": [{"rolls": 1.0, "bonus_rolls": 0.0, "conditions": [{"condition": "minecraft:survives_explosion"}], "entries": [
            {"type": "minecraft:item", "name": f"{NS}:mystic_door", "conditions": [{"condition": "minecraft:block_state_property",
             "block": f"{NS}:mystic_door", "properties": {"half": "lower"}}]}]}]})
    write(f"{D}/loot_table/blocks/mystic_grass_block.json", silk_or("mystic_grass_block",
        {"type": "minecraft:item", "name": f"{NS}:mystic_dirt", "conditions": [{"condition": "minecraft:survives_explosion"}]}))
    # Like vanilla leaves: shears or silk touch keep the block; otherwise a sapling now and then, and sticks.
    write(f"{D}/loot_table/blocks/mystic_leaves.json", {"type": "minecraft:block", "random_sequence": f"{NS}:blocks/mystic_leaves", "pools": [
        {"rolls": 1.0, "bonus_rolls": 0.0, "entries": [{"type": "minecraft:alternatives", "children": [
            {"type": "minecraft:item", "conditions": [G.SHEARS_OR_SILK], "name": f"{NS}:mystic_leaves"},
            {"type": "minecraft:item", "name": f"{NS}:mystic_sapling", "conditions": [{"condition": "minecraft:survives_explosion"},
             {"condition": "minecraft:table_bonus", "enchantment": "minecraft:fortune", "chances": [0.05, 0.0625, 0.083333336, 0.1]}]}]}]},
        {"rolls": 1.0, "bonus_rolls": 0.0, "conditions": [{"condition": "minecraft:inverted", "term": G.SHEARS_OR_SILK}], "entries": [
            {"type": "minecraft:item", "name": "minecraft:stick", "conditions": [
             {"condition": "minecraft:table_bonus", "enchantment": "minecraft:fortune", "chances": [0.02, 0.022222223, 0.025, 0.033333335, 0.1]}],
             "functions": [{"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": 1.0, "max": 2.0}, "add": False},
                           {"function": "minecraft:explosion_decay"}]}]}]})

    def pool(item, lo, hi, chance=None):
        p = {"rolls": 1.0, "bonus_rolls": 0.0, "entries": [{"type": "minecraft:item", "name": ns(item), "functions": [
            {"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}, "add": False},
            {"function": "minecraft:enchanted_count_increase", "enchantment": "minecraft:looting", "count": {"type": "minecraft:uniform", "min": 0.0, "max": 1.0}}]}]}
        if chance is not None:
            p["conditions"] = [{"condition": "minecraft:random_chance", "chance": chance}]
        return p
    def entity(name, pools):
        write(f"{D}/loot_table/entities/{name}.json", {"type": "minecraft:entity", "random_sequence": f"{NS}:entities/{name}", "pools": pools})
    # Revival choices: the troll's moss and hide, fairy dust (glowstone), the Ent's wood.
    entity("troll", [pool("minecraft:leather", 0.0, 2.0), pool("minecraft:moss_carpet", 0.0, 2.0)])
    entity("fairy", [pool("minecraft:glowstone_dust", 0.0, 1.0)])
    entity("evil_fairy", [pool("minecraft:glowstone_dust", 0.0, 2.0), pool("minecraft:redstone", 0.0, 1.0)])
    entity("shadow_eye", [pool("minecraft:black_dye", 0.0, 2.0), pool("minecraft:ender_pearl", 1.0, 1.0, chance=0.25)])
    entity("shadow_wanderer", [pool("minecraft:black_dye", 0.0, 2.0), pool("minecraft:ender_pearl", 0.0, 1.0)])
    entity("ent", [pool("mystic_log", 1.0, 3.0), pool("minecraft:stick", 1.0, 3.0), pool("mystic_sapling", 1.0, 1.0, chance=0.3)])

# =================================================================== recipes
def gen_recipes():
    b = "building"
    # One log gives four planks; planks are in #minecraft:planks, so vanilla sticks, chests and the rest accept them.
    shapeless("mystic_planks", ["mystic_log"], "mystic_planks", 4, b)
    shaped("mystic_stairs", ["#  ", "## ", "###"], {"#": "mystic_planks"}, "mystic_stairs", 4, b, "wooden_stairs")
    shaped("mystic_slab", ["###"], {"#": "mystic_planks"}, "mystic_slab", 6, b, "wooden_slab")
    shaped("mystic_fence", ["W#W", "W#W"], {"W": "mystic_planks", "#": "minecraft:stick"}, "mystic_fence", 3, "misc", "wooden_fence")
    shaped("mystic_fence_gate", ["#W#", "#W#"], {"W": "mystic_planks", "#": "minecraft:stick"}, "mystic_fence_gate", 1, "redstone", "wooden_fence_gate")
    shaped("mystic_door", ["##", "##", "##"], {"#": "mystic_planks"}, "mystic_door", 3, "redstone", "wooden_door")
    shapeless("mystic_button", ["mystic_planks"], "mystic_button", 1, "redstone")
    shaped("mystic_pressure_plate", ["##"], {"#": "mystic_planks"}, "mystic_pressure_plate", 1, "redstone")
    # Revival recipes (the original's recipes for these are not known): a glass jar with a wooden lid, and an elixir
    # brewed from things of the grove.
    shaped("jar", [" W ", "G G", "GGG"], {"W": "#minecraft:wooden_slabs", "G": "minecraft:glass"}, "jar", 2, "misc")
    shapeless("elixir_of_life", ["minecraft:glass_bottle", "minecraft:glistering_melon_slice", "minecraft:red_mushroom", "mystic_leaves"],
              "elixir_of_life", 1, "misc")

# =================================================================== tags
def gen_tags():
    n = lambda *xs: [ns(x) for x in xs]
    tag("block", "minecraft", "mineable/axe", n("mystic_log", "mystic_planks", *WOOD))
    tag("block", "minecraft", "mineable/shovel", n("mystic_dirt", "mystic_grass_block"))
    tag("block", "minecraft", "mineable/hoe", n("mystic_leaves"))
    tag("block", "minecraft", "dirt", n("mystic_dirt", "mystic_grass_block"))
    tag("block", "minecraft", "grass_blocks", n("mystic_grass_block"))
    tag("block", "minecraft", "supports_vegetation", n("mystic_dirt", "mystic_grass_block"))
    tag("block", "minecraft", "animals_spawnable_on", n("mystic_grass_block"))
    tag("block", "minecraft", "valid_spawn", n("mystic_grass_block"))
    tag("block", "minecraft", "enderman_holdable", n("mystic_dirt", "mystic_grass_block"))
    tag("block", "minecraft", "forest_rock_can_place_on", n("mystic_dirt", "mystic_grass_block"))
    tag("block", "minecraft", "unstable_bottom_center", n("mystic_fence_gate"))
    for kind in ["block", "item"]:
        tag(kind, "minecraft", "logs", n("mystic_log"))
        tag(kind, "minecraft", "logs_that_burn", n("mystic_log"))
        tag(kind, "minecraft", "planks", n("mystic_planks"))
        tag(kind, "minecraft", "leaves", n("mystic_leaves"))
        tag(kind, "minecraft", "saplings", n("mystic_sapling"))
        tag(kind, "minecraft", "wooden_stairs", n("mystic_stairs"))
        tag(kind, "minecraft", "wooden_slabs", n("mystic_slab"))
        tag(kind, "minecraft", "wooden_fences", n("mystic_fence"))
        tag(kind, "minecraft", "fence_gates", n("mystic_fence_gate"))
        tag(kind, "minecraft", "wooden_doors", n("mystic_door"))
        tag(kind, "minecraft", "wooden_buttons", n("mystic_button"))
        tag(kind, "minecraft", "wooden_pressure_plates", n("mystic_pressure_plate"))
    tag("item", "minecraft", "dirt", n("mystic_dirt", "mystic_grass_block"))
    tag("item", "minecraft", "grass_blocks", n("mystic_grass_block"))
    # The original's Trolls target the Shadow Eye and the Shadow Wanderer. Neither exists in the revival yet, so the
    # entries are optional: the tag loads today and starts matching as soon as those creatures are registered.
    tag("entity_type", NS, "troll_enemies", n("shadow_eye", "shadow_wanderer"))
    tag("entity_type", NS, "fairies", n("fairy", "evil_fairy"))
    tag("entity_type", "minecraft", "fall_damage_immune", n("fairy", "evil_fairy", "shadow_eye"))
    for t in [("minecraft", "is_overworld"), ("c", "is_overworld"), ("minecraft", "is_forest"), ("c", "is_forest"), (NS, "is_mystic"),
              ("minecraft", "has_structure/mineshaft"), ("minecraft", "has_structure/stronghold"),
              ("minecraft", "has_structure/ruined_portal_standard"), ("minecraft", "has_structure/trial_chambers"),
              ("minecraft", "has_structure/woodland_mansion"), ("minecraft", "has_structure/trail_ruins")]:
        tag("worldgen/biome", t[0], t[1], n("mystic_grove"))

# =================================================================== worldgen
def tree(trunk, foliage, size, below="mystic_dirt"):
    return {"type": "minecraft:tree", "config": {
        "below_trunk_provider": {"type": "minecraft:rule_based_state_provider", "rules": [{"if_true": {"type": "minecraft:not", "predicate": {
            "type": "minecraft:matching_block_tag", "tag": "minecraft:cannot_replace_below_tree_trunk"}},
            "then": {"type": "minecraft:simple_state_provider", "state": state(below)}}]},
        "decorators": [],
        "foliage_placer": foliage,
        "foliage_provider": {"type": "minecraft:simple_state_provider", "state": state("mystic_leaves", distance=7, persistent=False, waterlogged=False)},
        "ignore_vines": True,
        "minimum_size": size,
        "trunk_placer": trunk,
        "trunk_provider": {"type": "minecraft:simple_state_provider", "state": state("mystic_log", axis="y")}}}

def scatter(count, spread=6, rarity=None, heightmap="MOTION_BLOCKING", predicate=None):
    """Patch placement used in 26.1 (random_patch is gone): pick a spot, then repeat around it with random offsets."""
    p = []
    if rarity:
        p.append({"type": "minecraft:rarity_filter", "chance": rarity})
    p += [{"type": "minecraft:in_square"}, {"type": "minecraft:heightmap", "heightmap": heightmap}, {"type": "minecraft:biome"},
          {"type": "minecraft:count", "count": count},
          {"type": "minecraft:random_offset", "xz_spread": {"type": "minecraft:trapezoid", "max": spread, "min": -spread, "plateau": 0},
           "y_spread": {"type": "minecraft:trapezoid", "max": 2, "min": -2, "plateau": 0}}]
    if predicate:
        p.append({"type": "minecraft:block_predicate_filter", "predicate": predicate})
    return p

def gen_worldgen():
    # --- Trees: very tall, thick, dark trunks with layered crowns near the top ------------------------------------
    # Giant: a 2x2 trunk 20 to 32 blocks tall; branches near the top carry their own clusters of leaves.
    write(f"{W}/configured_feature/giant_mystic_tree.json", tree(
        {"type": "minecraft:mega_jungle_trunk_placer", "base_height": 20, "height_rand_a": 4, "height_rand_b": 8},
        {"type": "minecraft:jungle_foliage_placer", "radius": 3, "offset": 0, "height": 2},
        {"type": "minecraft:two_layers_feature_size", "limit": 1, "lower_size": 1, "upper_size": 2}))
    # Tall: a single trunk 13 to 20 blocks with branches and several stacked leaf blobs.
    write(f"{W}/configured_feature/mystic_tree.json", tree(
        {"type": "minecraft:fancy_trunk_placer", "base_height": 13, "height_rand_a": 5, "height_rand_b": 2},
        {"type": "minecraft:fancy_foliage_placer", "radius": 3, "offset": 4, "height": 4},
        {"type": "minecraft:two_layers_feature_size", "limit": 1, "lower_size": 0, "upper_size": 2}))
    def tree_placement(count):
        return [{"type": "minecraft:count", "count": count}, {"type": "minecraft:in_square"},
                {"type": "minecraft:surface_water_depth_filter", "max_water_depth": 0},
                {"type": "minecraft:heightmap", "heightmap": "OCEAN_FLOOR"},
                {"type": "minecraft:block_predicate_filter", "predicate": {"type": "minecraft:would_survive", "state": state("mystic_sapling", stage=0)}},
                {"type": "minecraft:biome"}]
    # Fewer, bigger trees than a vanilla forest: long stretches of bare trunk form corridors between them.
    write(f"{W}/placed_feature/giant_mystic_trees.json", {"feature": f"{NS}:giant_mystic_tree", "placement": tree_placement(
        {"type": "minecraft:weighted_list", "distribution": [{"data": 1, "weight": 5}, {"data": 2, "weight": 4}, {"data": 0, "weight": 2}]})})
    write(f"{W}/placed_feature/mystic_trees.json", {"feature": f"{NS}:mystic_tree", "placement": tree_placement(
        {"type": "minecraft:weighted_list", "distribution": [{"data": 2, "weight": 4}, {"data": 3, "weight": 3}, {"data": 1, "weight": 2}]})})

    # --- Giant red mushrooms (Java feature: stepped caps) --------------------------------------------------------
    write(f"{W}/configured_feature/giant_red_mushroom.json", {"type": f"{NS}:giant_mushroom", "config": {}})
    write(f"{W}/placed_feature/giant_red_mushrooms.json", {"feature": f"{NS}:giant_red_mushroom", "placement": [
        {"type": "minecraft:rarity_filter", "chance": 2}, {"type": f"{NS}:config_count", "setting": "giant_mushrooms"},
        {"type": "minecraft:in_square"}, {"type": "minecraft:surface_water_depth_filter", "max_water_depth": 0},
        {"type": "minecraft:heightmap", "heightmap": "OCEAN_FLOOR"}, {"type": "minecraft:biome"}]})

    # --- Mossy rocks: small blobs of mossy cobblestone and mossy stone bricks, with moss carpet around ---------------
    for name, block in [("mossy_cobblestone_rock", "minecraft:mossy_cobblestone"), ("mossy_stone_brick_rock", "minecraft:mossy_stone_bricks")]:
        write(f"{W}/configured_feature/{name}.json", {"type": "minecraft:block_blob", "config": {
            "can_place_on": {"type": "minecraft:matching_block_tag", "tag": "minecraft:forest_rock_can_place_on"},
            "state": {"Name": block}}})
    write(f"{W}/placed_feature/mossy_rocks.json", {"feature": f"{NS}:mossy_cobblestone_rock", "placement": [
        {"type": "minecraft:count", "count": {"type": "minecraft:uniform", "min_inclusive": 0, "max_inclusive": 2}},
        {"type": "minecraft:in_square"}, {"type": "minecraft:heightmap", "heightmap": "MOTION_BLOCKING"}, {"type": "minecraft:biome"}]})
    write(f"{W}/placed_feature/old_mossy_ruins.json", {"feature": f"{NS}:mossy_stone_brick_rock", "placement": [
        {"type": "minecraft:rarity_filter", "chance": 4}, {"type": "minecraft:in_square"},
        {"type": "minecraft:heightmap", "heightmap": "MOTION_BLOCKING"}, {"type": "minecraft:biome"}]})
    write(f"{W}/configured_feature/moss_carpet.json", {"type": "minecraft:simple_block", "config": {
        "to_place": {"type": "minecraft:simple_state_provider", "state": {"Name": "minecraft:moss_carpet"}}}})
    air_and_survives = lambda s: {"type": "minecraft:all_of", "predicates": [
        {"type": "minecraft:matching_block_tag", "tag": "minecraft:air"}, {"type": "minecraft:would_survive", "state": s}]}
    write(f"{W}/placed_feature/moss_carpets.json", {"feature": f"{NS}:moss_carpet", "placement": scatter(
        16, 4, rarity=3, predicate=air_and_survives({"Name": "minecraft:moss_carpet"}))})

    # --- Ground cover: small flowers, ferns and grass (tinted turquoise by the biome) ------------------------------
    write(f"{W}/configured_feature/mystic_flowers.json", {"type": "minecraft:simple_block", "config": {
        "to_place": {"type": "minecraft:weighted_state_provider", "entries": [
            {"data": {"Name": "minecraft:cornflower"}, "weight": 3}, {"data": {"Name": "minecraft:allium"}, "weight": 2},
            {"data": {"Name": "minecraft:azure_bluet"}, "weight": 2}, {"data": {"Name": "minecraft:lily_of_the_valley"}, "weight": 2},
            {"data": {"Name": "minecraft:blue_orchid"}, "weight": 1}]}}})
    write(f"{W}/placed_feature/mystic_flowers.json", {"feature": f"{NS}:mystic_flowers", "placement": scatter(
        16, 6, rarity=2, predicate=air_and_survives({"Name": "minecraft:cornflower"}))})
    write(f"{W}/configured_feature/mystic_grass.json", {"type": "minecraft:simple_block", "config": {
        "to_place": {"type": "minecraft:weighted_state_provider", "entries": [
            {"data": {"Name": "minecraft:short_grass"}, "weight": 3}, {"data": {"Name": "minecraft:fern"}, "weight": 2}]}}})
    write(f"{W}/placed_feature/mystic_grass.json", {"feature": f"{NS}:mystic_grass", "placement": [
        {"type": "minecraft:count", "count": 3}] + scatter(24, 7, heightmap="WORLD_SURFACE_WG",
        predicate=air_and_survives({"Name": "minecraft:short_grass"}))})

    # --- A water lake now and then, in the low parts --------------------------------------------------------------
    write(f"{W}/configured_feature/lake_mystic_water.json", {"type": "minecraft:lake", "config": {
        "fluid": {"type": "minecraft:simple_state_provider", "state": {"Name": "minecraft:water", "Properties": {"level": "0"}}},
        "barrier": {"type": "minecraft:simple_state_provider", "state": state("mystic_dirt")}}})
    write(f"{W}/placed_feature/lake_mystic_water.json", {"feature": f"{NS}:lake_mystic_water", "placement": [
        {"type": "minecraft:rarity_filter", "chance": 10}, {"type": "minecraft:in_square"},
        {"type": "minecraft:heightmap", "heightmap": "WORLD_SURFACE_WG"}, {"type": "minecraft:biome"}]})

    def spawn(t, w, lo, hi):
        return {"type": ns(t), "weight": w, "minCount": lo, "maxCount": hi}
    vanilla_monsters = [spawn("minecraft:zombie", 95, 4, 4), spawn("minecraft:zombie_villager", 5, 1, 1), spawn("minecraft:skeleton", 100, 4, 4),
                        spawn("minecraft:creeper", 100, 4, 4), spawn("minecraft:spider", 100, 4, 4), spawn("minecraft:enderman", 10, 1, 4),
                        spawn("minecraft:witch", 5, 1, 1)]
    write(f"{W}/biome/mystic_grove.json", {
        "attributes": {
            "minecraft:audio/background_music": {"default": {"max_delay": 24000, "min_delay": 12000, "sound": "minecraft:music.overworld.old_growth_taiga"}},
            # A green haze: fog starts close and closes in at 96 blocks, so far trees and hills lose their outline.
            "minecraft:visual/fog_color": "#7fc9b0", "minecraft:visual/sky_color": "#86c7c2", "minecraft:visual/water_fog_color": "#0f3b3a",
            "minecraft:visual/fog_start_distance": 8.0, "minecraft:visual/fog_end_distance": 96.0,
            # Spores drifting in the air.
            "minecraft:visual/ambient_particles": [{"particle": {"type": "minecraft:spore_blossom_air"}, "probability": 0.004}]},
        "carvers": ["minecraft:cave", "minecraft:cave_extra_underground", "minecraft:canyon"],
        "downfall": 0.8,
        # Turquoise grass and teal foliage for the vanilla plants (short grass, ferns, vines) growing here.
        "effects": {"foliage_color": "#2f9a8a", "grass_color": "#3fcfb4", "water_color": "#2fa39a", "dry_foliage_color": "#5b6b4a"},
        "features": [[], [f"{NS}:lake_mystic_water", "minecraft:lake_lava_underground"], [f"{NS}:mossy_rocks", f"{NS}:old_mossy_ruins"],
                     ["minecraft:monster_room", "minecraft:monster_room_deep"], [], [], G.VANILLA_UNDERGROUND, [],
                     ["minecraft:spring_water", "minecraft:spring_lava"],
                     ["minecraft:glow_lichen", f"{NS}:giant_red_mushrooms", f"{NS}:giant_mystic_trees", f"{NS}:mystic_trees",
                      f"{NS}:moss_carpets", f"{NS}:mystic_flowers", f"{NS}:mystic_grass",
                      "minecraft:brown_mushroom_normal", "minecraft:red_mushroom_normal"],
                     ["minecraft:freeze_top_layer"]],
        "has_precipitation": True,
        "spawn_costs": {},
        "spawners": {
            "ambient": [spawn("minecraft:bat", 10, 8, 8)], "axolotls": [],
            "creature": [spawn("fairy", 12, 1, 2), spawn("troll", 6, 1, 1), spawn("ent", 3, 1, 1)],
            "misc": [],
            # The shadow creatures only spawn naturally while the sky is dark (see ShadowWanderer.nightOrNotNatural).
            "monster": [spawn("evil_fairy", 30, 1, 1), spawn("shadow_eye", 25, 1, 1), spawn("shadow_wanderer", 20, 1, 1)] + vanilla_monsters,
            "underground_water_creature": [spawn("minecraft:glow_squid", 10, 4, 6)], "water_ambient": [], "water_creature": []},
        "temperature": 0.6})

# =================================================================== advancements
def gen_advancements():
    # Own tab, like the creative tab: Land Of Fantastic Things is its root.
    disp = {"icon": {"id": f"{NS}:mystic_grass_block", "count": 1},
            "title": {"translate": f"advancements.{NS}.land_of_fantastic_things.title"},
            "description": {"translate": f"advancements.{NS}.land_of_fantastic_things.description"}, "frame": "task",
            "show_toast": True, "announce_to_chat": True, "hidden": False, "background": "minecraft:block/moss_block"}
    write(f"{D}/advancement/land_of_fantastic_things.json", {"display": disp, "criteria": {"enter": {"trigger": "minecraft:location",
        "conditions": {"player": [{"condition": "minecraft:entity_properties", "entity": "this",
                                   "predicate": {"location": {"biomes": f"{NS}:mystic_grove"}}}]}}}})
    # Granted from EvilFairy when the cure ends.
    adv("the_miracle_of_life", "land_of_fantastic_things", "elixir_of_life", "goal", {"cure": {"trigger": "minecraft:impossible"}})
    adv("fairy_in_a_jar", "land_of_fantastic_things", "jar_with_a_fairy", "task", {"jar": has("jar_with_a_fairy")})

def generate():
    gen_assets(); gen_sounds(); gen_loot(); gen_recipes(); gen_tags(); gen_worldgen(); gen_advancements()
