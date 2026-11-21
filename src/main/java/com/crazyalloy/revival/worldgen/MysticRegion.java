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
 * (stage 8), and the ground under the groves becomes the Mushroom Depths (0.8.0, can be switched off in the config).
 * Windswept forests sit on steep, high ground, which gives the grove its slopes, valleys and stepped terraces; dark
 * forests give it the dense lowland parts with lakes. Only chunks generated after installation are affected.
 */
public class MysticRegion extends Region {
    /** Vanilla places surface biomes at depth 0; underground biomes such as Lush Caves use this depth span. */
    private static final Climate.Parameter SURFACE_DEPTH = Climate.Parameter.point(0.0F);
    private static final Climate.Parameter CAVE_DEPTH = Climate.Parameter.span(0.2F, 1.0F);
    /** Same small penalty as the Candy Cave twins: vanilla cave biomes still win where their climate matches. */
    private static final float UNDERGROUND_OFFSET = 0.1F;

    private final boolean mushroomDepths;

    public MysticRegion(Identifier name, int weight, boolean mushroomDepths) {
        super(name, RegionType.OVERWORLD, weight);
        this.mushroomDepths = mushroomDepths;
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        this.addModifiedVanillaOverworldBiomes(pair -> {
            mapper.accept(pair);
            if (!mushroomDepths) {
                return;
            }
            // Like the Candy Cave: every surface point gets an underground twin with the same climate at cave depth,
            // the Mushroom Depths under the Mystic Grove and the same biome under everything else, so the caves stay
            // below the groves.
            ResourceKey<Biome> biome = pair.getSecond();
            Climate.ParameterPoint p = pair.getFirst();
            if (p.depth().equals(SURFACE_DEPTH)) {
                mapper.accept(Pair.of(Climate.parameters(p.temperature(), p.humidity(), p.continentalness(), p.erosion(),
                        CAVE_DEPTH, p.weirdness(), UNDERGROUND_OFFSET), biome == ModWorldgen.MYSTIC_GROVE ? ModWorldgen.MUSHROOM_DEPTHS : biome));
            }
        }, builder -> {
            builder.replaceBiome(Biomes.DARK_FOREST, ModWorldgen.MYSTIC_GROVE);
            builder.replaceBiome(Biomes.WINDSWEPT_FOREST, ModWorldgen.MYSTIC_GROVE);
        });
    }
}
