# -*- coding: utf-8 -*-
"""0.8.0 translations (the Mushroom Depths: its creatures, structures, the Mushroom Sword and Shield, the elemental books)."""
from lang_data import _common, N, I, B

EN = _common(
    {
        I("mushroom_spore"): "Mushroom Spore", I("mushroom_sword"): "Mushroom Sword", I("mushroom_shield"): "Mushroom Shield",
        I("fire_book"): "Fire Book", I("water_book"): "Water Book", I("poison_book"): "Poison Book",
        I("air_book"): "Air Book", I("lightning_book"): "Lightning Book", I("earth_book"): "Earth Book",
        I("mushroom_man_spawn_egg"): "Mushroom Man Spawn Egg", I("mushroom_cyclops_spawn_egg"): "Mushroom Cyclops Spawn Egg",
        I("mushroomcopter_spawn_egg"): "Mushroomcopter Spawn Egg",
    },
    {
        I("mushroom_spore") + ".desc": "What the Mushroomcopter shoots.",
        I("mushroom_sword") + ".desc": "Found in the Mushroom Depths. Now and then its spores poison the target.",
        I("mushroom_shield") + ".desc": "Found in the Mushroom Depths. Blocks like a shield; repaired with mushrooms.",
        I("fire_book") + ".desc": "Casts a fire bolt that sets the target on fire.",
        I("water_book") + ".desc": "Casts a water bolt that pushes back and puts out fire.",
        I("poison_book") + ".desc": "Casts a poison bolt.",
        I("air_book") + ".desc": "Casts a gust that throws the target into the air.",
        I("lightning_book") + ".desc": "Calls a lightning bolt where the spell lands.",
        I("earth_book") + ".desc": "Casts an earth bolt that hits hard and slows the target.",
        I("mushroom_man_spawn_egg") + ".desc": "Hostile. Lives in the Mushroom Depths; its eyes shine in the dark.",
        I("mushroom_cyclops_spawn_egg") + ".desc": "Hostile. Raises both fists before it slams: step back!",
        I("mushroomcopter_spawn_egg") + ".desc": "Hostile. Flies and shoots spores from a distance.",
    },
    {"mushroom_man": "Mushroom Man", "mushroom_cyclops": "Mushroom Cyclops", "mushroomcopter": "Mushroomcopter",
     "spore_shot": "Mushroom Spore", "spell_bolt": "Spell"},
    {
        "mushroom_man.ambient": "Mushroom Man mutters", "mushroom_man.hurt": "Mushroom Man hurts", "mushroom_man.death": "Mushroom Man dies",
        "mushroom_cyclops.ambient": "Mushroom Cyclops grunts", "mushroom_cyclops.hurt": "Mushroom Cyclops hurts", "mushroom_cyclops.death": "Mushroom Cyclops dies",
        "mushroomcopter.ambient": "Mushroomcopter whirs", "mushroomcopter.hurt": "Mushroomcopter hurts", "mushroomcopter.death": "Mushroomcopter falls",
        "mushroomcopter.shoot": "Mushroomcopter shoots", "mushroom_depths.additions": "Something rumbles far away",
    },
    {
        "into_the_mushroom_depths": ("Into The Mushroom Depths", "Find the mushroom caves under the Mystic Grove"),
        "grounded": ("Grounded", "Defeat a Mushroomcopter"),
        "one_eyed_giant": ("One-Eyed Giant", "Defeat a Mushroom Cyclops"),
        "the_top_of_the_base": ("Spellbound", "Find an elemental book"),
    },
    {},
    {
        "mushroomDepthsEnabled": "Mushroom Depths", "mushroomOutpostFrequency": "Mushroom Outpost Frequency",
        "mushroomBaseFrequency": "Mushroom Base Frequency", "mushroomManSpawnChance": "Mushroom Man Spawn Chance",
        "mushroomCyclopsSpawnChance": "Mushroom Cyclops Spawn Chance", "mushroomcopterSpawnChance": "Mushroomcopter Spawn Chance",
        "mushroomCyclopsSlam": "Mushroom Cyclops Slam Attack",
    },
    {f"biome.{N}.mushroom_depths": "Mushroom Depths"},
)

PT = _common(
    {
        I("mushroom_spore"): "Esporo de Cogumelo", I("mushroom_sword"): "Espada de Cogumelo", I("mushroom_shield"): "Escudo de Cogumelo",
        I("fire_book"): "Livro do Fogo", I("water_book"): "Livro da Água", I("poison_book"): "Livro do Veneno",
        I("air_book"): "Livro do Ar", I("lightning_book"): "Livro do Raio", I("earth_book"): "Livro da Terra",
        I("mushroom_man_spawn_egg"): "Ovo Gerador de Homem-Cogumelo", I("mushroom_cyclops_spawn_egg"): "Ovo Gerador de Ciclope-Cogumelo",
        I("mushroomcopter_spawn_egg"): "Ovo Gerador de Cogumelo-Helicóptero",
    },
    {
        I("mushroom_spore") + ".desc": "O que o Cogumelo-Helicóptero dispara.",
        I("mushroom_sword") + ".desc": "Encontrada nas Profundezas dos Cogumelos. Às vezes seus esporos envenenam o alvo.",
        I("mushroom_shield") + ".desc": "Encontrado nas Profundezas dos Cogumelos. Bloqueia como um escudo; consertado com cogumelos.",
        I("fire_book") + ".desc": "Lança um raio de fogo que incendeia o alvo.",
        I("water_book") + ".desc": "Lança um jato de água que empurra e apaga o fogo.",
        I("poison_book") + ".desc": "Lança um feitiço de veneno.",
        I("air_book") + ".desc": "Lança uma rajada que joga o alvo para o alto.",
        I("lightning_book") + ".desc": "Chama um relâmpago onde o feitiço cai.",
        I("earth_book") + ".desc": "Lança um feitiço de terra que bate forte e deixa o alvo lento.",
        I("mushroom_man_spawn_egg") + ".desc": "Hostil. Vive nas Profundezas dos Cogumelos; seus olhos brilham no escuro.",
        I("mushroom_cyclops_spawn_egg") + ".desc": "Hostil. Ergue os dois punhos antes de golpear: afaste-se!",
        I("mushroomcopter_spawn_egg") + ".desc": "Hostil. Voa e dispara esporos de longe.",
    },
    {"mushroom_man": "Homem-Cogumelo", "mushroom_cyclops": "Ciclope-Cogumelo", "mushroomcopter": "Cogumelo-Helicóptero",
     "spore_shot": "Esporo de Cogumelo", "spell_bolt": "Feitiço"},
    {
        "mushroom_man.ambient": "Homem-Cogumelo murmura", "mushroom_man.hurt": "Homem-Cogumelo se machuca", "mushroom_man.death": "Homem-Cogumelo morre",
        "mushroom_cyclops.ambient": "Ciclope-Cogumelo grunhe", "mushroom_cyclops.hurt": "Ciclope-Cogumelo se machuca", "mushroom_cyclops.death": "Ciclope-Cogumelo morre",
        "mushroomcopter.ambient": "Cogumelo-Helicóptero zumbe", "mushroomcopter.hurt": "Cogumelo-Helicóptero se machuca", "mushroomcopter.death": "Cogumelo-Helicóptero cai",
        "mushroomcopter.shoot": "Cogumelo-Helicóptero dispara", "mushroom_depths.additions": "Algo ressoa ao longe",
    },
    {
        "into_the_mushroom_depths": ("Nas Profundezas dos Cogumelos", "Encontre as cavernas de cogumelos sob a Floresta Mística"),
        "grounded": ("Pouso Forçado", "Derrote um Cogumelo-Helicóptero"),
        "one_eyed_giant": ("Gigante de Um Olho Só", "Derrote um Ciclope-Cogumelo"),
        "the_top_of_the_base": ("Enfeitiçado", "Encontre um livro elemental"),
    },
    {},
    {
        "mushroomDepthsEnabled": "Profundezas dos Cogumelos", "mushroomOutpostFrequency": "Frequência do Posto Avançado dos Cogumelos",
        "mushroomBaseFrequency": "Frequência da Base dos Cogumelos", "mushroomManSpawnChance": "Chance de Surgir Homem-Cogumelo",
        "mushroomCyclopsSpawnChance": "Chance de Surgir Ciclope-Cogumelo", "mushroomcopterSpawnChance": "Chance de Surgir Cogumelo-Helicóptero",
        "mushroomCyclopsSlam": "Golpe do Ciclope-Cogumelo",
    },
    {f"biome.{N}.mushroom_depths": "Profundezas dos Cogumelos"},
)
