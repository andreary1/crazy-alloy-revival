package com.crazyalloy.revival.worldgen;

import com.crazyalloy.revival.CrazyAlloyRevival;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public final class ModTreeGrowers {
    public static final ResourceKey<ConfiguredFeature<?, ?>> SWEETWOOD_TREE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, CrazyAlloyRevival.id("sweetwood_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> TALL_SWEETWOOD_TREE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, CrazyAlloyRevival.id("tall_sweetwood_tree"));

    public static final TreeGrower SWEETWOOD = new TreeGrower(
            CrazyAlloyRevival.MOD_ID + ":sweetwood",
            0.15F,
            Optional.empty(), Optional.empty(),
            Optional.of(SWEETWOOD_TREE), Optional.of(TALL_SWEETWOOD_TREE),
            Optional.empty(), Optional.empty());

    public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTIC_TREE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, CrazyAlloyRevival.id("mystic_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_MYSTIC_TREE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, CrazyAlloyRevival.id("giant_mystic_tree"));

    /** One sapling: a tall mystic tree. Four in a square: a giant one with a 2x2 trunk. */
    public static final TreeGrower MYSTIC = new TreeGrower(
            CrazyAlloyRevival.MOD_ID + ":mystic",
            0.0F,
            Optional.of(GIANT_MYSTIC_TREE), Optional.empty(),
            Optional.of(MYSTIC_TREE), Optional.empty(),
            Optional.empty(), Optional.empty());

    private ModTreeGrowers() {}
}
