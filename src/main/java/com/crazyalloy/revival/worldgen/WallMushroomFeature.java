package com.crazyalloy.revival.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Red mushroom growths on the cave walls of the Mushroom Depths (0.8.0): stepped shelves of red mushroom blocks stuck
 * to a wall, two to four tiers, each one narrower and less deep than the one below, like bracket fungi. From the open
 * air at the origin it looks up to 8 blocks along each horizontal direction for a wall, and builds only into air, on
 * columns that touch the wall, so the shelves never float.
 */
public class WallMushroomFeature extends Feature<NoneFeatureConfiguration> {
    public WallMushroomFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        if (!level.getBlockState(origin).isAir()) {
            return false;
        }
        Direction start = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        for (int turn = 0; turn < 4; turn++) {
            Direction dir = Direction.from2DDataValue((start.get2DDataValue() + turn) % 4);
            BlockPos.MutableBlockPos p = origin.mutable();
            for (int step = 0; step < 8; step++) {
                p.move(dir);
                BlockState s = level.getBlockState(p);
                if (s.isAir()) {
                    continue;
                }
                if (!s.isSolidRender()) {
                    break;
                }
                // p is the wall; build out from it, back toward the origin.
                return build(level, random, p.immutable(), dir.getOpposite()) > 0;
            }
        }
        return false;
    }

    private int build(WorldGenLevel level, RandomSource random, BlockPos wall, Direction out) {
        BlockState cap = Blocks.RED_MUSHROOM_BLOCK.defaultBlockState();
        Direction side = out.getClockWise();
        int tiers = 2 + random.nextInt(3);
        int depth = 2 + random.nextInt(2);
        int half = 2 + random.nextInt(2);
        int placed = 0;
        int y = 0;
        for (int t = 0; t < tiers && depth > 0; t++) {
            for (int a = -half; a <= half; a++) {
                BlockPos face = wall.relative(side, a).above(y);
                if (!level.getBlockState(face).isSolidRender()) {
                    continue; // no wall behind this column
                }
                for (int d = 1; d <= depth; d++) {
                    double e = (a * a) / ((half + 0.5) * (half + 0.5)) + (d * d) / ((depth + 0.5) * (depth + 0.5));
                    if (e > 1.0) {
                        break;
                    }
                    BlockPos q = face.relative(out, d);
                    if (level.getBlockState(q).isAir()) {
                        level.setBlock(q, cap, 2);
                        placed++;
                    }
                }
            }
            // Next tier: one or two blocks up, one block less deep, narrower now and then.
            y += 1 + random.nextInt(2);
            depth--;
            if (random.nextBoolean()) {
                half = Math.max(1, half - 1);
            }
        }
        return placed;
    }
}
