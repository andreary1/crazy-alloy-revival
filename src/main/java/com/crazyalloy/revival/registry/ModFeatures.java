package com.crazyalloy.revival.registry;

import com.crazyalloy.revival.CrazyAlloyRevival;
import com.crazyalloy.revival.worldgen.IceCreamNestStructure;
import com.crazyalloy.revival.worldgen.IceCreamPinnacleFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, CrazyAlloyRevival.MOD_ID);

    /** Tall three-flavour ice cream pinnacles of the Ice Cream Plains (stage 6). */
    public static final DeferredHolder<Feature<?>, IceCreamPinnacleFeature> ICE_CREAM_PINNACLE = FEATURES.register("ice_cream_pinnacle",
            () -> new IceCreamPinnacleFeature(NoneFeatureConfiguration.CODEC));

    /** Giant red mushrooms with stepped caps of the Mystic Grove (stage 8). */
    public static final DeferredHolder<Feature<?>, com.crazyalloy.revival.worldgen.GiantMushroomFeature> GIANT_MUSHROOM = FEATURES.register("giant_mushroom",
            () -> new com.crazyalloy.revival.worldgen.GiantMushroomFeature(NoneFeatureConfiguration.CODEC));

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, CrazyAlloyRevival.MOD_ID);

    /** The Ice Cream Nest: placed on the lowest point of a not-too-steep footprint (stage 6). */
    public static final DeferredHolder<StructureType<?>, StructureType<IceCreamNestStructure>> ICE_CREAM_NEST = STRUCTURE_TYPES.register("ice_cream_nest",
            () -> () -> IceCreamNestStructure.CODEC);

    private ModFeatures() {}
}
