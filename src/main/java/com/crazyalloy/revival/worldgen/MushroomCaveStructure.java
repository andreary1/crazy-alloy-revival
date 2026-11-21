package com.crazyalloy.revival.worldgen;

import com.crazyalloy.revival.registry.ModFeatures;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

/**
 * An underground structure of the Mushroom Depths (0.8.0): the Mushroom Outpost and the Mushroom Base. One template,
 * centred on the chunk, standing on the floor of a cave hall.
 * <p>
 * It looks down the centre column of the chunk (between {@code max_y} and {@code min_y}, never closer than 12 blocks to
 * the surface) for a cave floor with at least {@code clearance} blocks of open air above it, and stands the template on
 * the highest such floor. Where the column has no hall that tall, it picks a height in that range anyway; the
 * structure's terrain adaptation (beard_box: a foundation under it and the ground cleared inside its box) and the air
 * written in the template then hollow out a hall around it. The biome check runs at the chosen centre, so it only
 * appears where that point is in the Mushroom Depths.
 */
public class MushroomCaveStructure extends Structure {
    public static final MapCodec<MushroomCaveStructure> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            settingsCodec(i),
            Identifier.CODEC.fieldOf("template").forGetter(s -> s.template),
            com.mojang.serialization.Codec.INT.optionalFieldOf("min_y", -40).forGetter(s -> s.minY),
            com.mojang.serialization.Codec.INT.optionalFieldOf("max_y", 30).forGetter(s -> s.maxY),
            com.mojang.serialization.Codec.INT.optionalFieldOf("clearance", 6).forGetter(s -> s.clearance)
    ).apply(i, MushroomCaveStructure::new));

    private final Identifier template;
    private final int minY, maxY, clearance;

    public MushroomCaveStructure(Structure.StructureSettings settings, Identifier template, int minY, int maxY, int clearance) {
        super(settings);
        this.template = template;
        this.minY = minY;
        this.maxY = maxY;
        this.clearance = clearance;
    }

    @Override
    protected Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        int cx = context.chunkPos().getMiddleBlockX(), cz = context.chunkPos().getMiddleBlockZ();
        int surface = context.chunkGenerator().getFirstOccupiedHeight(cx, cz, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
        int top = Math.min(this.maxY, surface - 12);
        int bottom = Math.max(this.minY, context.heightAccessor().getMinY() + 8);
        if (top <= bottom) {
            return Optional.empty();
        }
        NoiseColumn column = context.chunkGenerator().getBaseColumn(cx, cz, context.heightAccessor(), context.randomState());
        int floor = findFloor(column, top, bottom, this.clearance);
        if (floor == Integer.MIN_VALUE) {
            floor = bottom + context.random().nextInt(top - bottom + 1);
        }
        Rotation rotation = Rotation.getRandom(context.random());
        StructurePoolElement element = StructurePoolElement.single(this.template.toString()).apply(StructureTemplatePool.Projection.RIGID);
        var size = element.getSize(context.structureTemplateManager(), Rotation.NONE);
        BlockPos centreOffset = new BlockPos(size.getX() / 2, 0, size.getZ() / 2).rotate(rotation);
        BlockPos origin = new BlockPos(cx - centreOffset.getX(), floor, cz - centreOffset.getZ());
        BlockPos centre = new BlockPos(cx, floor + 1, cz);
        return Optional.of(new Structure.GenerationStub(centre, builder -> {
            BoundingBox box = element.getBoundingBox(context.structureTemplateManager(), origin, rotation);
            builder.addPiece(new PoolElementStructurePiece(context.structureTemplateManager(), element, origin, 0, rotation, box,
                    LiquidSettings.IGNORE_WATERLOGGING));
        }));
    }

    /** Highest y in [bottom, top] whose block is solid with {@code clearance} blocks of air (no fluid) above, or MIN_VALUE. */
    static int findFloor(NoiseColumn column, int top, int bottom, int clearance) {
        int air = 0;
        for (int y = top + clearance; y >= bottom; y--) {
            BlockState state = column.getBlock(y);
            if (state.isAir()) {
                air++;
            } else {
                if (air >= clearance && y <= top && state.getFluidState().isEmpty()) {
                    return y;
                }
                air = 0;
            }
        }
        return Integer.MIN_VALUE;
    }

    @Override
    public StructureType<?> type() {
        return ModFeatures.MUSHROOM_CAVE_STRUCTURE.get();
    }
}
