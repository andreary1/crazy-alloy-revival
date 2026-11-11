# -*- coding: utf-8 -*-
"""Stage 8 translations (the Enchanted tab: Mystic Grove, mystic wood, Troll, Fairy, Evil Fairy, Ent)."""
from lang_data import _common, N, I, B

EN = _common(
    {
        "itemGroup." + N + ".enchanted": "Crazy Alloy: Enchanted",
        B("mystic_grass_block"): "Mystic Grass", B("mystic_dirt"): "Mystic Dirt", B("mystic_log"): "Mystic Log",
        B("mystic_planks"): "Mystic Planks", B("mystic_leaves"): "Mystic Leaves", B("mystic_sapling"): "Mystic Sapling",
        B("mystic_stairs"): "Mystic Stairs", B("mystic_slab"): "Mystic Slab", B("mystic_fence"): "Mystic Fence",
        B("mystic_fence_gate"): "Mystic Fence Gate", B("mystic_door"): "Mystic Door", B("mystic_button"): "Mystic Button",
        B("mystic_pressure_plate"): "Mystic Pressure Plate",
        I("jar"): "Jar", I("jar_with_a_fairy"): "Jar With A Fairy", I("elixir_of_life"): "Elixir of Life",
        I("troll_spawn_egg"): "Troll Spawn Egg", I("fairy_spawn_egg"): "Fairy Spawn Egg",
        I("evil_fairy_spawn_egg"): "Evil Fairy Spawn Egg", I("ent_spawn_egg"): "Ent Spawn Egg",
    },
    {
        B("mystic_grass_block") + ".desc": "The turquoise ground of the Mystic Grove.",
        B("mystic_sapling") + ".desc": "One grows a tall mystic tree; four in a square grow a giant one.",
        I("jar") + ".desc": "Use it on a Fairy to catch it.",
        I("jar_with_a_fairy") + ".desc": "Use it on a block to let the fairy out.",
        I("elixir_of_life") + ".desc": "Use it on an Evil Fairy to turn it into a Fairy.",
        I("troll_spawn_egg") + ".desc": "Neutral. Fights back, and hunts the shadow creatures.",
        I("fairy_spawn_egg") + ".desc": "Passive. Flees from danger. Catch it with a Jar.",
        I("evil_fairy_spawn_egg") + ".desc": "Hostile. Attacks players and fairies.",
        I("ent_spawn_egg") + ".desc": "Neutral. A living tree that fights back.",
    },
    {"troll": "Troll", "fairy": "Fairy", "evil_fairy": "Evil Fairy", "ent": "Ent"},
    {
        "troll.ambient": "Troll grunts", "troll.hurt": "Troll hurts", "troll.death": "Troll dies",
        "fairy.ambient": "Fairy hums", "fairy.hurt": "Fairy hurts", "fairy.death": "Fairy dies",
        "evil_fairy.ambient": "Evil Fairy hisses", "evil_fairy.hurt": "Evil Fairy hurts", "evil_fairy.death": "Evil Fairy dies",
        "ent.ambient": "Ent creaks", "ent.hurt": "Ent hurts", "ent.death": "Ent falls",
    },
    {
        "land_of_fantastic_things": ("Land Of Fantastic Things", "Enter the Mystic Grove"),
        "the_miracle_of_life": ("The Miracle Of Life", "Use an Elixir of Life on an Evil Fairy"),
        "fairy_in_a_jar": ("Bottled Magic", "Catch a Fairy in a Jar"),
    },
    {},
    {
        "mysticRegionWeight": "Mystic Region Weight", "mysticGroveEnabled": "Mystic Grove",
        "giantMushrooms": "Giant Mushrooms per Chunk",
        "trollSpawnChance": "Troll Spawn Chance", "fairySpawnChance": "Fairy Spawn Chance",
        "evilFairySpawnChance": "Evil Fairy Spawn Chance", "entSpawnChance": "Ent Spawn Chance",
        "entRooting": "Ents Take Root",
    },
    {f"biome.{N}.mystic_grove": "Mystic Grove"},
)

PT = _common(
    {
        "itemGroup." + N + ".enchanted": "Crazy Alloy: Encantado",
        B("mystic_grass_block"): "Grama Mística", B("mystic_dirt"): "Terra Mística", B("mystic_log"): "Tronco Místico",
        B("mystic_planks"): "Tábuas Místicas", B("mystic_leaves"): "Folhas Místicas", B("mystic_sapling"): "Muda Mística",
        B("mystic_stairs"): "Escada Mística", B("mystic_slab"): "Laje Mística", B("mystic_fence"): "Cerca Mística",
        B("mystic_fence_gate"): "Portão Místico", B("mystic_door"): "Porta Mística", B("mystic_button"): "Botão Místico",
        B("mystic_pressure_plate"): "Placa de Pressão Mística",
        I("jar"): "Pote", I("jar_with_a_fairy"): "Pote com Fada", I("elixir_of_life"): "Elixir da Vida",
        I("troll_spawn_egg"): "Ovo Gerador de Troll", I("fairy_spawn_egg"): "Ovo Gerador de Fada",
        I("evil_fairy_spawn_egg"): "Ovo Gerador de Fada Maligna", I("ent_spawn_egg"): "Ovo Gerador de Ent",
    },
    {
        B("mystic_grass_block") + ".desc": "O chão turquesa da Floresta Mística.",
        B("mystic_sapling") + ".desc": "Uma cresce uma árvore mística alta; quatro em quadrado crescem uma gigante.",
        I("jar") + ".desc": "Use numa Fada para capturá-la.",
        I("jar_with_a_fairy") + ".desc": "Use num bloco para soltar a fada.",
        I("elixir_of_life") + ".desc": "Use numa Fada Maligna para transformá-la em Fada.",
        I("troll_spawn_egg") + ".desc": "Neutro. Revida e caça as criaturas de sombra.",
        I("fairy_spawn_egg") + ".desc": "Passiva. Foge do perigo. Capture com um Pote.",
        I("evil_fairy_spawn_egg") + ".desc": "Hostil. Ataca jogadores e fadas.",
        I("ent_spawn_egg") + ".desc": "Neutro. Uma árvore viva que revida.",
    },
    {"troll": "Troll", "fairy": "Fada", "evil_fairy": "Fada Maligna", "ent": "Ent"},
    {
        "troll.ambient": "Troll grunhe", "troll.hurt": "Troll se machuca", "troll.death": "Troll morre",
        "fairy.ambient": "Fada cantarola", "fairy.hurt": "Fada se machuca", "fairy.death": "Fada morre",
        "evil_fairy.ambient": "Fada Maligna sibila", "evil_fairy.hurt": "Fada Maligna se machuca", "evil_fairy.death": "Fada Maligna morre",
        "ent.ambient": "Ent range", "ent.hurt": "Ent se machuca", "ent.death": "Ent tomba",
    },
    {
        "land_of_fantastic_things": ("Terra de Coisas Fantásticas", "Entre na Floresta Mística"),
        "the_miracle_of_life": ("O Milagre da Vida", "Use um Elixir da Vida numa Fada Maligna"),
        "fairy_in_a_jar": ("Magia Engarrafada", "Capture uma Fada num Pote"),
    },
    {},
    {
        "mysticRegionWeight": "Peso da Região Mística", "mysticGroveEnabled": "Floresta Mística",
        "giantMushrooms": "Cogumelos Gigantes por Chunk",
        "trollSpawnChance": "Chance de Surgir Troll", "fairySpawnChance": "Chance de Surgir Fada",
        "evilFairySpawnChance": "Chance de Surgir Fada Maligna", "entSpawnChance": "Chance de Surgir Ent",
        "entRooting": "Ents Criam Raízes",
    },
    {f"biome.{N}.mystic_grove": "Floresta Mística"},
)
