package com.crazyalloy.revival.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Giant red mushroom of the Mystic Grove (stage 8): a thick whitish stem and a wide red cap with light spots, built in
 * stacked, stepped layers (a broad brim, a narrower middle and a small dome), so it reads as a landmark from far away.
 * Heights vary from about 7 to 14 blocks and the widest layer spans 9 to 13 blocks. Uses the vanilla mushroom blocks,
 * whose red cap texture already has the white spots.
 */
public class GiantMushroomFeature extends Feature<NoneFeatureConfiguration> {
    public GiantMushroomFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        if (!level.getBlockState(origin.below()).is(BlockTags.DIRT)) {
            return false;
        }
        int stemHeight = 5 + random.nextInt(6);
        int brim = 4 + random.nextInt(3); // radius of the widest layer
        boolean thick = brim >= 5;         // big ones get a 2x2-looking stem (a plus shape)
        int top = stemHeight + 4;
        // Room check: nothing solid where the stem goes.
        for (int y = 0; y <= top; y++) {
            BlockState s = level.getBlockState(origin.above(y));
            if (!s.isAir() && !s.canBeReplaced() && !s.is(BlockTags.LEAVES)) {
                return false;
            }
        }
        BlockState stem = Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.UP, false).setValue(HugeMushroomBlock.DOWN, false);
        for (int y = 0; y < stemHeight + 2; y++) {
            set(level, origin.above(y), stem);
            if (thick && y < stemHeight - 1) {
                set(level, origin.offset(1, y, 0), stem);
                set(level, origin.offset(-1, y, 0), stem);
                set(level, origin.offset(0, y, 1), stem);
                set(level, origin.offset(0, y, -1), stem);
            }
        }
        // Roots: a few stem blocks spreading over the ground for the thick ones.
        if (thick) {
            for (int[] d : new int[][] {{2, 0}, {-2, 0}, {0, 2}, {0, -2}}) {
                if (random.nextInt(3) > 0) {
                    BlockPos p = origin.offset(d[0], 0, d[1]);
                    if (level.getBlockState(p).canBeReplaced() && level.getBlockState(p.below()).isSolid()) {
                        set(level, p, stem);
                    }
                }
            }
        }
        // Stepped cap: brim (radius brim, 1 thick, with the underside open), middle (brim - 1.5, 2 thick), dome (brim - 3).
        BlockState cap = Blocks.RED_MUSHROOM_BLOCK.defaultBlockState();
        int y0 = stemHeight;
        layer(level, origin.above(y0), brim + 0.4F, cap, true);
        layer(level, origin.above(y0 + 1), brim - 1.1F, cap, false);
        layer(level, origin.above(y0 + 2), brim - 1.6F, cap, false);
        layer(level, origin.above(y0 + 3), Math.max(1.2F, brim - 3.0F), cap, false);
        // Sides of the brim hang down a block at the edge, like the stepped volumes of the reference.
        ring(level, origin.above(y0 - 1), brim + 0.4F, brim - 0.8F, cap.setValue(HugeMushroomBlock.DOWN, false));
        return true;
    }

    private void layer(WorldGenLevel level, BlockPos centre, float radius, BlockState cap, boolean brim) {
        int r = (int) Math.ceil(radius);
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                if (dx * dx + dz * dz <= radius * radius) {
                    BlockPos p = centre.offset(dx, 0, dz);
                    if (canReplace(level, p)) {
                        set(level, p, brim ? cap.setValue(HugeMushroomBlock.DOWN, false) : cap);
                    }
                }
            }
        }
    }

    private void ring(WorldGenLevel level, BlockPos centre, float outer, float inner, BlockState cap) {
        int r = (int) Math.ceil(outer);
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                int d2 = dx * dx + dz * dz;
                if (d2 <= outer * outer && d2 > inner * inner) {
                    BlockPos p = centre.offset(dx, 0, dz);
                    if (canReplace(level, p)) {
                        set(level, p, cap);
                    }
                }
            }
        }
    }

    private static boolean canReplace(WorldGenLevel level, BlockPos p) {
        BlockState s = level.getBlockState(p);
        return s.isAir() || s.canBeReplaced() || s.is(BlockTags.LEAVES);
    }

    private void set(WorldGenLevel level, BlockPos pos, BlockState state) {
        this.setBlock(level, pos, state);
    }
}
