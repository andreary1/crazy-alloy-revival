package com.crazyalloy.revival.registry;

import com.crazyalloy.revival.CrazyAlloyRevival;
import com.crazyalloy.revival.block.BubbalooBlock;
import com.crazyalloy.revival.block.ChocolateFactoryBlock;
import com.crazyalloy.revival.block.IceCreamDragonEggBlock;
import com.crazyalloy.revival.block.IceCreamMachineBlock;
import com.crazyalloy.revival.block.IceCreamPortalBlock;
import com.crazyalloy.revival.block.InfestedJellyBeanBlock;
import com.crazyalloy.revival.block.JellyBeanBlock;
import com.crazyalloy.revival.block.LicoricePlantBlock;
import com.crazyalloy.revival.block.MineralConverterBlock;
import com.crazyalloy.revival.block.OrangeJellyBeanBlock;
import com.crazyalloy.revival.block.RedJellyBeanBlock;
import com.crazyalloy.revival.block.YellowJellyBeanBlock;

import com.crazyalloy.revival.worldgen.ModTreeGrowers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CrazyAlloyRevival.MOD_ID);

    // --- Sweet Forest ground -------------------------------------------------------------
    public static final DeferredBlock<Block> CHOCOLATE_SOIL = BLOCKS.registerSimpleBlock("chocolate_soil",
            p -> p.mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.ROOTED_DIRT));
    public static final DeferredBlock<Block> CHOCOLATE_GRASS_BLOCK = BLOCKS.registerSimpleBlock("chocolate_grass_block",
            p -> p.mapColor(MapColor.COLOR_PINK).strength(0.6F).sound(SoundType.GRASS));

    // --- Chocolate building family ------------------------------------------------------
    private static BlockBehaviour.Properties chocolateStone(BlockBehaviour.Properties p) {
        return p.mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.MUD_BRICKS);
    }

    public static final DeferredBlock<Block> CHOCOLATE_BLOCK = BLOCKS.registerSimpleBlock("chocolate_block", ModBlocks::chocolateStone);
    public static final DeferredBlock<Block> CHOCOLATE_BRICKS = BLOCKS.registerSimpleBlock("chocolate_bricks", ModBlocks::chocolateStone);
    public static final DeferredBlock<StairBlock> CHOCOLATE_BRICK_STAIRS = BLOCKS.registerBlock("chocolate_brick_stairs",
            p -> new StairBlock(CHOCOLATE_BRICKS.get().defaultBlockState(), p), ModBlocks::chocolateStone);
    public static final DeferredBlock<SlabBlock> CHOCOLATE_BRICK_SLAB = BLOCKS.registerBlock("chocolate_brick_slab",
            SlabBlock::new, ModBlocks::chocolateStone);
    public static final DeferredBlock<WallBlock> CHOCOLATE_BRICK_WALL = BLOCKS.registerBlock("chocolate_brick_wall",
            WallBlock::new, p -> chocolateStone(p).forceSolidOn());
    public static final DeferredBlock<Block> CHISELED_CHOCOLATE_BRICKS = BLOCKS.registerSimpleBlock("chiseled_chocolate_bricks", ModBlocks::chocolateStone);

    // --- Sweetwood trees ----------------------------------------------------------------
    public static final DeferredBlock<RotatedPillarBlock> SWEETWOOD_LOG = BLOCKS.registerBlock("sweetwood_log",
            RotatedPillarBlock::new,
            p -> p.mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.CHERRY_WOOD).ignitedByLava());
    public static final DeferredBlock<Block> SWEETWOOD_PLANKS = BLOCKS.registerSimpleBlock("sweetwood_planks",
            p -> p.mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.CHERRY_WOOD).ignitedByLava());
    public static final DeferredBlock<UntintedParticleLeavesBlock> COTTON_CANDY_LEAVES = BLOCKS.registerBlock("cotton_candy_leaves",
            p -> new UntintedParticleLeavesBlock(0.02F, ParticleTypes.CHERRY_LEAVES, p),
            p -> p.mapColor(MapColor.COLOR_PINK).strength(0.2F).randomTicks().sound(SoundType.WOOL).noOcclusion()
                    .isSuffocating((s, l, pos) -> false).isViewBlocking((s, l, pos) -> false)
                    .isRedstoneConductor((s, l, pos) -> false).ignitedByLava().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<SaplingBlock> SWEETWOOD_SAPLING = BLOCKS.registerBlock("sweetwood_sapling",
            p -> new SaplingBlock(ModTreeGrowers.SWEETWOOD, p),
            p -> p.mapColor(MapColor.COLOR_PINK).noCollision().randomTicks().instabreak().sound(SoundType.CHERRY_SAPLING).pushReaction(PushReaction.DESTROY));

    private static BlockBehaviour.Properties candyWood(BlockBehaviour.Properties p) {
        return p.mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.CHERRY_WOOD).ignitedByLava();
    }

    // Candy wood set. It reuses the cherry block set and wood type for its sounds, so no new types are registered.
    public static final DeferredBlock<StairBlock> SWEETWOOD_STAIRS = BLOCKS.registerBlock("sweetwood_stairs",
            p -> new StairBlock(SWEETWOOD_PLANKS.get().defaultBlockState(), p), ModBlocks::candyWood);
    public static final DeferredBlock<SlabBlock> SWEETWOOD_SLAB = BLOCKS.registerBlock("sweetwood_slab", SlabBlock::new, ModBlocks::candyWood);
    public static final DeferredBlock<FenceBlock> SWEETWOOD_FENCE = BLOCKS.registerBlock("sweetwood_fence", FenceBlock::new, ModBlocks::candyWood);
    public static final DeferredBlock<FenceGateBlock> SWEETWOOD_FENCE_GATE = BLOCKS.registerBlock("sweetwood_fence_gate",
            p -> new FenceGateBlock(WoodType.CHERRY, p), p -> candyWood(p).forceSolidOn());
    public static final DeferredBlock<DoorBlock> SWEETWOOD_DOOR = BLOCKS.registerBlock("sweetwood_door",
            p -> new DoorBlock(BlockSetType.CHERRY, p), p -> candyWood(p).strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ButtonBlock> SWEETWOOD_BUTTON = BLOCKS.registerBlock("sweetwood_button",
            p -> new ButtonBlock(BlockSetType.CHERRY, 30, p), p -> p.noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<PressurePlateBlock> SWEETWOOD_PRESSURE_PLATE = BLOCKS.registerBlock("sweetwood_pressure_plate",
            p -> new PressurePlateBlock(BlockSetType.CHERRY, p),
            p -> p.mapColor(MapColor.TERRACOTTA_WHITE).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollision().strength(0.5F).ignitedByLava().pushReaction(PushReaction.DESTROY));

    // --- Gingerbread ---------------------------------------------------------------------
    private static BlockBehaviour.Properties gingerbread(BlockBehaviour.Properties p) {
        return p.mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASS).strength(1.2F, 3.0F).sound(SoundType.PACKED_MUD);
    }

    public static final DeferredBlock<Block> GINGERBREAD_BLOCK = BLOCKS.registerSimpleBlock("gingerbread_block", ModBlocks::gingerbread);
    public static final DeferredBlock<Block> FROSTED_GINGERBREAD_BLOCK = BLOCKS.registerSimpleBlock("frosted_gingerbread_block", ModBlocks::gingerbread);
    public static final DeferredBlock<StairBlock> GINGERBREAD_STAIRS = BLOCKS.registerBlock("gingerbread_stairs",
            p -> new StairBlock(GINGERBREAD_BLOCK.get().defaultBlockState(), p), ModBlocks::gingerbread);
    public static final DeferredBlock<SlabBlock> GINGERBREAD_SLAB = BLOCKS.registerBlock("gingerbread_slab", SlabBlock::new, ModBlocks::gingerbread);

    // --- Vegetation ----------------------------------------------------------------------
    public static final DeferredBlock<FlowerBlock> LOLLIPOP_FLOWER = BLOCKS.registerBlock("lollipop_flower",
            p -> new FlowerBlock(MobEffects.SPEED, 6.0F, p),
            p -> p.mapColor(MapColor.COLOR_RED).noCollision().instabreak().sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<LicoricePlantBlock> RED_LICORICE_PLANT = BLOCKS.registerBlock("red_licorice_plant",
            LicoricePlantBlock::new,
            p -> p.mapColor(MapColor.COLOR_MAGENTA).noCollision().instabreak().sound(SoundType.SWEET_BERRY_BUSH)
                    .offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));

    // --- Liquids -------------------------------------------------------------------------
    public static final DeferredBlock<LiquidBlock> MELTED_CHOCOLATE = BLOCKS.registerBlock("melted_chocolate",
            p -> new LiquidBlock(ModFluids.MELTED_CHOCOLATE.get(), p),
            p -> p.mapColor(MapColor.COLOR_BROWN).replaceable().noCollision().strength(100.0F).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY));
    public static final DeferredBlock<BubbalooBlock> BUBBALOO = BLOCKS.registerBlock("bubbaloo",
            p -> new BubbalooBlock(ModFluids.BUBBALOO.get(), p),
            p -> p.mapColor(MapColor.COLOR_PINK).replaceable().noCollision().strength(100.0F).lightLevel(s -> 8).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY));

    // --- Jelly Bean Fields -----------------------------------------------------------------
    private static BlockBehaviour.Properties jelly(BlockBehaviour.Properties p, MapColor color) {
        return p.mapColor(color).strength(0.8F).sound(SoundType.SLIME_BLOCK)
                .speedFactor(JellyBeanBlock.SPEED_FACTOR).jumpFactor(JellyBeanBlock.JUMP_FACTOR);
    }

    public static final DeferredBlock<JellyBeanBlock> GREEN_JELLY_BEAN_BLOCK = BLOCKS.registerBlock("green_jelly_bean_block",
            JellyBeanBlock::new, p -> jelly(p, MapColor.COLOR_LIGHT_GREEN));
    public static final DeferredBlock<YellowJellyBeanBlock> YELLOW_JELLY_BEAN_BLOCK = BLOCKS.registerBlock("yellow_jelly_bean_block",
            YellowJellyBeanBlock::new, p -> jelly(p, MapColor.COLOR_YELLOW));
    public static final DeferredBlock<RedJellyBeanBlock> RED_JELLY_BEAN_BLOCK = BLOCKS.registerBlock("red_jelly_bean_block",
            RedJellyBeanBlock::new, p -> jelly(p, MapColor.COLOR_RED));
    public static final DeferredBlock<OrangeJellyBeanBlock> ORANGE_JELLY_BEAN_BLOCK = BLOCKS.registerBlock("orange_jelly_bean_block",
            OrangeJellyBeanBlock::new, p -> jelly(p, MapColor.COLOR_ORANGE));
    public static final DeferredBlock<JellyBeanBlock> PURPLE_JELLY_BEAN_BLOCK = BLOCKS.registerBlock("purple_jelly_bean_block",
            JellyBeanBlock::new, p -> jelly(p, MapColor.COLOR_PURPLE));
    public static final DeferredBlock<InfestedJellyBeanBlock> INFESTED_PURPLE_JELLY_BEAN_BLOCK = BLOCKS.registerBlock("infested_purple_jelly_bean_block",
            InfestedJellyBeanBlock::new, p -> jelly(p, MapColor.COLOR_PURPLE));

    // --- Tourmaline ----------------------------------------------------------------------
    public static final DeferredBlock<DropExperienceBlock> TOURMALINE_ORE = BLOCKS.registerBlock("tourmaline_ore",
            p -> new DropExperienceBlock(UniformInt.of(2, 5), p),
            p -> p.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F));
    public static final DeferredBlock<DropExperienceBlock> DEEPSLATE_TOURMALINE_ORE = BLOCKS.registerBlock("deepslate_tourmaline_ore",
            p -> new DropExperienceBlock(UniformInt.of(2, 5), p),
            p -> p.mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE));
    public static final DeferredBlock<Block> TOURMALINE_BLOCK = BLOCKS.registerSimpleBlock("tourmaline_block",
            p -> p.mapColor(MapColor.COLOR_MAGENTA).instrument(NoteBlockInstrument.BELL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.AMETHYST));

    // --- Machines ------------------------------------------------------------------------
    public static final DeferredBlock<ChocolateFactoryBlock> CHOCOLATE_FACTORY = BLOCKS.registerBlock("chocolate_factory",
            ChocolateFactoryBlock::new,
            p -> p.mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)
                    .lightLevel(state -> state.getValue(ChocolateFactoryBlock.LIT) ? 9 : 0));

    public static final DeferredBlock<IceCreamMachineBlock> ICE_CREAM_MACHINE = BLOCKS.registerBlock("ice_cream_machine",
            IceCreamMachineBlock::new,
            p -> p.mapColor(MapColor.QUARTZ).requiresCorrectToolForDrops().strength(3.0F).sound(SoundType.METAL).noOcclusion());

    // --- Candy Cave (stage 5) ------------------------------------------------------------
    private static BlockBehaviour.Properties candyRock(BlockBehaviour.Properties p, MapColor color) {
        return p.mapColor(color).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.CALCITE);
    }

    public static final DeferredBlock<Block> PINK_CANDY_ROCK = BLOCKS.registerSimpleBlock("pink_candy_rock", p -> candyRock(p, MapColor.COLOR_PINK));
    public static final DeferredBlock<Block> PURPLE_CANDY_ROCK = BLOCKS.registerSimpleBlock("purple_candy_rock", p -> candyRock(p, MapColor.COLOR_PURPLE));
    /** Small sugar crystals on cave floors, walls and ceilings (revival proposal): a faint light in the dark. */
    public static final DeferredBlock<AmethystClusterBlock> SUGAR_CRYSTAL = BLOCKS.registerBlock("sugar_crystal",
            p -> new AmethystClusterBlock(5.0F, 6.0F, p),
            p -> p.mapColor(MapColor.COLOR_PINK).forceSolidOn().noOcclusion().sound(SoundType.AMETHYST_CLUSTER).strength(1.0F)
                    .lightLevel(state -> 4).pushReaction(PushReaction.DESTROY));

    // --- Ice Cream Dimension (stage 6) ---------------------------------------------------
    /** Solid scoops of ice cream: the ground of the Ice Cream Dimension and, in chocolate, the frame of its portal. */
    private static BlockBehaviour.Properties iceCream(BlockBehaviour.Properties p, MapColor color) {
        return p.mapColor(color).strength(0.8F).sound(SoundType.SNOW).instrument(NoteBlockInstrument.FLUTE);
    }

    public static final DeferredBlock<Block> VANILLA_ICE_CREAM_BLOCK = BLOCKS.registerSimpleBlock("vanilla_ice_cream_block", p -> iceCream(p, MapColor.TERRACOTTA_WHITE));
    public static final DeferredBlock<Block> CHOCOLATE_ICE_CREAM_BLOCK = BLOCKS.registerSimpleBlock("chocolate_ice_cream_block", p -> iceCream(p, MapColor.COLOR_BROWN));
    public static final DeferredBlock<Block> STRAWBERRY_ICE_CREAM_BLOCK = BLOCKS.registerSimpleBlock("strawberry_ice_cream_block", p -> iceCream(p, MapColor.COLOR_PINK));
    public static final DeferredBlock<Block> MINT_ICE_CREAM_BLOCK = BLOCKS.registerSimpleBlock("mint_ice_cream_block", p -> iceCream(p, MapColor.COLOR_LIGHT_GREEN));
    public static final DeferredBlock<IceCreamPortalBlock> ICE_CREAM_PORTAL = BLOCKS.registerBlock("ice_cream_portal", IceCreamPortalBlock::new,
            p -> p.noCollision().randomTicks().strength(-1.0F).sound(SoundType.GLASS).lightLevel(s -> 11).pushReaction(PushReaction.BLOCK).noLootTable());
    public static final DeferredBlock<IceCreamDragonEggBlock> ICE_CREAM_DRAGON_EGG = BLOCKS.registerBlock("ice_cream_dragon_egg", IceCreamDragonEggBlock::new,
            p -> p.mapColor(MapColor.COLOR_PINK).strength(3.0F, 9.0F).sound(SoundType.CALCITE).lightLevel(s -> 3).noOcclusion().pushReaction(PushReaction.BLOCK));

    // --- The Bank (stage 7, Overworld) ---------------------------------------------------------
    public static final DeferredBlock<MineralConverterBlock> MINERAL_CONVERTER = BLOCKS.registerBlock("mineral_converter", MineralConverterBlock::new,
            p -> p.mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(3.5F, 6.0F).sound(SoundType.METAL));

    private ModBlocks() {}
}
