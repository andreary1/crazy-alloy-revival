# -*- coding: utf-8 -*-
"""Stage 7 translations (the Overworld tab: Bank, Guard Dinosaur, Segway, money, AK47 and Mineral Converter)."""
from lang_data import _common, N, I, B

EN = _common(
    {
        "itemGroup." + N + ".overworld": "Crazy Alloy: Overworld",
        B("mineral_converter"): "Mineral Converter",
        I("money"): "Money", I("coin"): "Coin", I("ak47"): "AK47", I("ak47_ammo"): "AK47 Ammo",
        I("guard_dinosaur_spawn_egg"): "Guard Dinosaur Spawn Egg", I("segway_spawn_egg"): "Segway Spawn Egg",
    },
    {
        B("mineral_converter") + ".desc": "Turns ingots and gems into Money and Coins.",
        I("money") + ".desc": "Banknotes. The mod's paper currency.",
        I("coin") + ".desc": "A gold coin. The mod's small change.",
        I("ak47") + ".desc": "Hold use to fire. Each shot uses one AK47 Ammo.",
        I("ak47_ammo") + ".desc": "Bullets for the AK47.",
        I("guard_dinosaur_spawn_egg") + ".desc": "Hostile. Guards the Bank and raises the alarm.",
        I("segway_spawn_egg") + ".desc": "Hostile. Drives itself and rams you.",
    },
    {"guard_dinosaur": "Guard Dinosaur", "segway": "Segway", "bullet": "Bullet"},
    {
        "guard_dinosaur.ambient": "Guard Dinosaur grumbles", "guard_dinosaur.hurt": "Guard Dinosaur hurts",
        "guard_dinosaur.death": "Guard Dinosaur dies", "guard_dinosaur.alert": "Guard Dinosaur raises the alarm",
        "segway.ambient": "Segway beeps", "segway.hurt": "Segway clanks", "segway.death": "Segway breaks",
        "ak47.shoot": "AK47 fires", "mineral_converter.convert": "Mineral Converter pays out",
    },
    {
        "visit_bank": ("Opening Hours", "Find a Bank in the Plains"),
        "guard_down": ("Security Breach", "Defeat a Guard Dinosaur"),
        "cash_out": ("Cash Out", "Get Money from a Mineral Converter or a Bank vault"),
        "full_auto": ("Full Auto", "Find an AK47 in a Bank vault"),
    },
    {
        f"container.{N}.mineral_converter": "Mineral Converter",
        f"gui.{N}.mineral_converter.convert": "Convert",
        f"gui.{N}.mineral_converter.rejected": "Not accepted",
        f"gui.{N}.mineral_converter.help.title": "One unit gives Money + Coins:",
        f"gui.{N}.mineral_converter.help.1": "Copper Ingot: 1 + 1",
        f"gui.{N}.mineral_converter.help.2": "Iron Ingot: 2 + 2",
        f"gui.{N}.mineral_converter.help.3": "Gold Ingot: 3 + 5",
        f"gui.{N}.mineral_converter.help.4": "Emerald: 7 + 10",
        f"gui.{N}.mineral_converter.help.5": "Diamond: 10 + 15",
        f"gui.{N}.mineral_converter.help.6": "Tourmaline: 20 + 20",
        f"gui.{N}.mineral_converter.help.7": "Netherite Ingot: 64 + 64",
        f"gui.{N}.mineral_converter.help.8": "Put a material in, then press Convert.",
    },
    {
        "bankFrequency": "Bank Frequency", "bankGuards": "Guards in New Banks",
        "guardDinosaurAlert": "Guard Dinosaur Alarm", "guardDinosaurAlertRadius": "Guard Dinosaur Alarm Radius",
        "ak47BulletDamage": "AK47 Bullet Damage", "mineralConverterEnabled": "Mineral Converter",
    },
    {},
)

PT = _common(
    {
        "itemGroup." + N + ".overworld": "Crazy Alloy: Overworld",
        B("mineral_converter"): "Conversor de Minerais",
        I("money"): "Dinheiro", I("coin"): "Moeda", I("ak47"): "AK47", I("ak47_ammo"): "Munição de AK47",
        I("guard_dinosaur_spawn_egg"): "Ovo Gerador de Dinossauro Segurança", I("segway_spawn_egg"): "Ovo Gerador de Segway",
    },
    {
        B("mineral_converter") + ".desc": "Transforma barras e gemas em Dinheiro e Moedas.",
        I("money") + ".desc": "Cédulas. O dinheiro de papel do mod.",
        I("coin") + ".desc": "Uma moeda de ouro. O troco do mod.",
        I("ak47") + ".desc": "Segure o botão de usar para atirar. Cada tiro gasta uma Munição de AK47.",
        I("ak47_ammo") + ".desc": "Balas para a AK47.",
        I("guard_dinosaur_spawn_egg") + ".desc": "Hostil. Vigia o Banco e dá o alarme.",
        I("segway_spawn_egg") + ".desc": "Hostil. Anda sozinho e atropela você.",
    },
    {"guard_dinosaur": "Dinossauro Segurança", "segway": "Segway", "bullet": "Bala"},
    {
        "guard_dinosaur.ambient": "Dinossauro Segurança resmunga", "guard_dinosaur.hurt": "Dinossauro Segurança se machuca",
        "guard_dinosaur.death": "Dinossauro Segurança morre", "guard_dinosaur.alert": "Dinossauro Segurança dá o alarme",
        "segway.ambient": "Segway apita", "segway.hurt": "Segway range", "segway.death": "Segway quebra",
        "ak47.shoot": "AK47 dispara", "mineral_converter.convert": "Conversor de Minerais paga",
    },
    {
        "visit_bank": ("Horário de Atendimento", "Encontre um Banco nas Planícies"),
        "guard_down": ("Falha na Segurança", "Derrote um Dinossauro Segurança"),
        "cash_out": ("Saque Feito", "Consiga Dinheiro num Conversor de Minerais ou no cofre de um Banco"),
        "full_auto": ("Rajada", "Encontre uma AK47 no cofre de um Banco"),
    },
    {
        f"container.{N}.mineral_converter": "Conversor de Minerais",
        f"gui.{N}.mineral_converter.convert": "Converter",
        f"gui.{N}.mineral_converter.rejected": "Não aceito",
        f"gui.{N}.mineral_converter.help.title": "Uma unidade dá Dinheiro + Moedas:",
        f"gui.{N}.mineral_converter.help.1": "Barra de Cobre: 1 + 1",
        f"gui.{N}.mineral_converter.help.2": "Barra de Ferro: 2 + 2",
        f"gui.{N}.mineral_converter.help.3": "Barra de Ouro: 3 + 5",
        f"gui.{N}.mineral_converter.help.4": "Esmeralda: 7 + 10",
        f"gui.{N}.mineral_converter.help.5": "Diamante: 10 + 15",
        f"gui.{N}.mineral_converter.help.6": "Turmalina: 20 + 20",
        f"gui.{N}.mineral_converter.help.7": "Barra de Netherita: 64 + 64",
        f"gui.{N}.mineral_converter.help.8": "Coloque um material e aperte Converter.",
    },
    {
        "bankFrequency": "Frequência de Bancos", "bankGuards": "Seguranças em Bancos Novos",
        "guardDinosaurAlert": "Alarme do Dinossauro Segurança", "guardDinosaurAlertRadius": "Alcance do Alarme do Dinossauro",
        "ak47BulletDamage": "Dano da Bala da AK47", "mineralConverterEnabled": "Conversor de Minerais",
    },
    {},
)
