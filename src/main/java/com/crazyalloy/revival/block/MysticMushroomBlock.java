package com.crazyalloy.revival.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The small Purple and Blue Mushrooms of the Mystic Grove: a short beige stem under a wide, square cap with a stepped
 * top and light spots. They grow on the forest floor like flowers (any block in {@code minecraft:supports_vegetation},
 * which includes Mystic Grass) and break when the ground under them goes.
 */
public class MysticMushroomBlock extends VegetationBlock {
    public static final MapCodec<MysticMushroomBlock> CODEC = simpleCodec(MysticMushroomBlock::new);
    private static final VoxelShape SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 10.0, 13.0);

    public MysticMushroomBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends VegetationBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
