package com.crazyalloy.revival.worldgen;

import com.crazyalloy.revival.CrazyAlloyRevival;
import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.registry.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

public final class ModWorldgen {
    public static final ResourceKey<Biome> SWEET_FOREST = ResourceKey.create(Registries.BIOME, CrazyAlloyRevival.id("sweet_forest"));
    public static final ResourceKey<Biome> JELLY_BEAN_FIELDS = ResourceKey.create(Registries.BIOME, CrazyAlloyRevival.id("jelly_bean_fields"));
    public static final ResourceKey<Biome> CANDY_CAVE = ResourceKey.create(Registries.BIOME, CrazyAlloyRevival.id("candy_cave"));
    /** The Mystic Grove (stage 8), placed by {@link MysticRegion}. */
    public static final ResourceKey<Biome> MYSTIC_GROVE = ResourceKey.create(Registries.BIOME, CrazyAlloyRevival.id("mystic_grove"));
    /** The Mushroom Depths (0.8.0): mushroom caves under the Mystic Grove, placed by {@link MysticRegion}. */
    public static final ResourceKey<Biome> MUSHROOM_DEPTHS = ResourceKey.create(Registries.BIOME, CrazyAlloyRevival.id("mushroom_depths"));
    public static final ResourceKey<Biome> ICE_CREAM_PLAINS = ResourceKey.create(Registries.BIOME, CrazyAlloyRevival.id("ice_cream_plains"));
    /** The Ice Cream Dimension (stage 6), defined by data/crazyalloy_revival/dimension/ice_cream.json. */
    public static final ResourceKey<net.minecraft.world.level.Level> ICE_CREAM_DIMENSION =
            ResourceKey.create(Registries.DIMENSION, CrazyAlloyRevival.id("ice_cream"));

    private ModWorldgen() {}

    /** Called on the main thread during common setup. Adds the candy region and the surface of its biomes. */
    public static void registerTerraBlender() {
        int weight = RevivalConfig.CANDY_REGION_WEIGHT.get();
        boolean sweet = RevivalConfig.SWEET_FOREST_ENABLED.get();
        boolean jelly = RevivalConfig.JELLY_BEAN_FIELDS_ENABLED.get();
        boolean cave = RevivalConfig.CANDY_CAVE_ENABLED.get();
        if (weight > 0 && (sweet || jelly)) {
            Regions.register(new CandyRegion(CrazyAlloyRevival.id("overworld_candy"), weight, sweet, jelly, cave));
        } else {
            CrazyAlloyRevival.LOGGER.info("Crazy Alloy candy region disabled by config");
        }
        int mysticWeight = RevivalConfig.MYSTIC_REGION_WEIGHT.get();
        if (mysticWeight > 0 && RevivalConfig.MYSTIC_GROVE_ENABLED.get()) {
            Regions.register(new MysticRegion(CrazyAlloyRevival.id("overworld_mystic"), mysticWeight, RevivalConfig.MUSHROOM_DEPTHS_ENABLED.get()));
        } else {
            CrazyAlloyRevival.LOGGER.info("Crazy Alloy mystic region disabled by config");
        }
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, CrazyAlloyRevival.MOD_ID,
                SurfaceRules.sequence(candyCaveSurface(), mushroomDepthsSurface(), sweetForestSurface(), jellyBeanFieldsSurface(), mysticGroveSurface()));
    }

    /**
     * Candy Cave floors and ceilings: patches of pink and purple candy rock in between plain stone, so the sweet rock
     * reads as part of the ground. Walls get their candy rock from the candy_rock blob features instead (surface rules
     * only see floors and ceilings).
     */
    private static SurfaceRules.RuleSource candyCaveSurface() {
        SurfaceRules.RuleSource pink = SurfaceRules.state(ModBlocks.PINK_CANDY_ROCK.get().defaultBlockState());
        SurfaceRules.RuleSource purple = SurfaceRules.state(ModBlocks.PURPLE_CANDY_ROCK.get().defaultBlockState());
        SurfaceRules.RuleSource rock = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.CALCITE, -0.05D), pink),
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.GRAVEL, -0.2D), purple));
        SurfaceRules.ConditionSource floorLayers = SurfaceRules.stoneDepthCheck(1, false, CaveSurface.FLOOR);
        SurfaceRules.ConditionSource ceilingLayers = SurfaceRules.stoneDepthCheck(0, false, CaveSurface.CEILING);
        return SurfaceRules.ifTrue(SurfaceRules.isBiome(CANDY_CAVE), SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.abovePreliminarySurface()),
                SurfaceRules.sequence(SurfaceRules.ifTrue(floorLayers, rock), SurfaceRules.ifTrue(ceilingLayers, rock))));
    }

    /**
     * Mushroom Depths floors and ceilings: wide brown stretches on the floors and slopes (brown mushroom blocks, with
     * coarse dirt where they thin out) and dark rock (tuff) in patches on the ceilings. The dark bands across the walls
     * and the red growths come from features (surface rules only see floors and ceilings).
     */
    private static SurfaceRules.RuleSource mushroomDepthsSurface() {
        SurfaceRules.RuleSource brown = SurfaceRules.state(net.minecraft.world.level.block.Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState());
        SurfaceRules.RuleSource coarse = SurfaceRules.state(net.minecraft.world.level.block.Blocks.COARSE_DIRT.defaultBlockState());
        SurfaceRules.RuleSource tuff = SurfaceRules.state(net.minecraft.world.level.block.Blocks.TUFF.defaultBlockState());
        SurfaceRules.RuleSource floor = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.CALCITE, -0.35D), brown),
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.GRAVEL, -0.35D), coarse));
        SurfaceRules.RuleSource ceiling = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, -0.1D), tuff);
        SurfaceRules.ConditionSource floorLayers = SurfaceRules.stoneDepthCheck(1, false, CaveSurface.FLOOR);
        SurfaceRules.ConditionSource ceilingLayers = SurfaceRules.stoneDepthCheck(0, false, CaveSurface.CEILING);
        return SurfaceRules.ifTrue(SurfaceRules.isBiome(MUSHROOM_DEPTHS), SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.abovePreliminarySurface()),
                SurfaceRules.sequence(SurfaceRules.ifTrue(floorLayers, floor), SurfaceRules.ifTrue(ceilingLayers, ceiling))));
    }

    /** Gummy Grass on top of Chocolate Dirt. */
    private static SurfaceRules.RuleSource sweetForestSurface() {
        SurfaceRules.RuleSource grass = SurfaceRules.state(ModBlocks.CHOCOLATE_GRASS_BLOCK.get().defaultBlockState());
        SurfaceRules.RuleSource soil = SurfaceRules.state(ModBlocks.CHOCOLATE_SOIL.get().defaultBlockState());
        SurfaceRules.ConditionSource aboveWater = SurfaceRules.waterBlockCheck(-1, 0);

        return SurfaceRules.ifTrue(SurfaceRules.isBiome(SWEET_FOREST), SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(aboveWater, grass), soil)),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, soil)));
    }

    /**
     * Turquoise Mystic Grass on top of Mystic Dirt. Steep slopes show bare dirt and, in patches, stone, like the small
     * openings in the hillsides of the reference.
     */
    private static SurfaceRules.RuleSource mysticGroveSurface() {
        SurfaceRules.RuleSource grass = SurfaceRules.state(ModBlocks.MYSTIC_GRASS_BLOCK.get().defaultBlockState());
        SurfaceRules.RuleSource dirt = SurfaceRules.state(ModBlocks.MYSTIC_DIRT.get().defaultBlockState());
        SurfaceRules.RuleSource stone = SurfaceRules.state(net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
        SurfaceRules.ConditionSource aboveWater = SurfaceRules.waterBlockCheck(-1, 0);
        SurfaceRules.RuleSource steep = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.GRAVEL, 0.1D), stone), dirt);

        return SurfaceRules.ifTrue(SurfaceRules.isBiome(MYSTIC_GROVE), SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.steep(), steep),
                        SurfaceRules.ifTrue(aboveWater, grass), dirt)),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, dirt)));
    }

    /** Green jelly on top, a yellow layer below it, red jelly at the bottom of lakes and rivers. */
    private static SurfaceRules.RuleSource jellyBeanFieldsSurface() {
        SurfaceRules.RuleSource green = SurfaceRules.state(ModBlocks.GREEN_JELLY_BEAN_BLOCK.get().defaultBlockState());
        SurfaceRules.RuleSource yellow = SurfaceRules.state(ModBlocks.YELLOW_JELLY_BEAN_BLOCK.get().defaultBlockState());
        SurfaceRules.RuleSource red = SurfaceRules.state(ModBlocks.RED_JELLY_BEAN_BLOCK.get().defaultBlockState());
        SurfaceRules.ConditionSource aboveWater = SurfaceRules.waterBlockCheck(-1, 0);

        return SurfaceRules.ifTrue(SurfaceRules.isBiome(JELLY_BEAN_FIELDS), SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(aboveWater, green), red)),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(aboveWater, yellow), red))));
    }
}
