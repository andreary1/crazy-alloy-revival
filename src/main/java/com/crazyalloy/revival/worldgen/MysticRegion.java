package com.crazyalloy.revival.worldgen;

import com.mojang.datafixers.util.Pair;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

/**
 * A TerraBlender region identical to vanilla except that dark forests and windswept forests become Mystic Groves
 * (stage 8). Windswept forests sit on steep, high ground, which gives the grove its slopes, valleys and stepped
 * terraces; dark forests give it the dense lowland parts with lakes. Only chunks generated after installation are
 * affected.
 */
public class MysticRegion extends Region {
    public MysticRegion(Identifier name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
            builder.replaceBiome(Biomes.DARK_FOREST, ModWorldgen.MYSTIC_GROVE);
            builder.replaceBiome(Biomes.WINDSWEPT_FOREST, ModWorldgen.MYSTIC_GROVE);
        });
    }
}
