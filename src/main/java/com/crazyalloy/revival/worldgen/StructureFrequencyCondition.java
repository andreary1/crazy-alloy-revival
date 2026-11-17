package com.crazyalloy.revival.worldgen;

import com.crazyalloy.revival.config.RevivalConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.neoforged.neoforge.common.conditions.ICondition;

/**
 * Data pack load condition: true when the configured frequency of {@code structure} equals {@code frequency}.
 * Each frequency has its own structure set file, so exactly one of them loads per structure.
 */
public record StructureFrequencyCondition(RevivalConfig.StructureFrequency frequency, String structure) implements ICondition {
    private static final Codec<RevivalConfig.StructureFrequency> FREQUENCY = Codec.STRING
            .xmap(s -> RevivalConfig.StructureFrequency.valueOf(s.toUpperCase(Locale.ROOT)), f -> f.name().toLowerCase(Locale.ROOT));
    public static final MapCodec<StructureFrequencyCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            FREQUENCY.fieldOf("frequency").forGetter(StructureFrequencyCondition::frequency),
            Codec.STRING.optionalFieldOf("structure", "cookie_hut").forGetter(StructureFrequencyCondition::structure)
    ).apply(i, StructureFrequencyCondition::new));

    @Override
    public boolean test(IContext context) {
        var setting = switch (structure) {
            case "gingerbread_tower" -> RevivalConfig.GINGERBREAD_TOWER_FREQUENCY;
            case "gingerbread_fortress" -> RevivalConfig.GINGERBREAD_FORTRESS_FREQUENCY;
            case "ice_cream_truck" -> RevivalConfig.ICE_CREAM_TRUCK_FREQUENCY;
            case "ice_cream_nest" -> RevivalConfig.ICE_CREAM_NEST_FREQUENCY;
            case "bank" -> RevivalConfig.BANK_FREQUENCY;
            default -> RevivalConfig.COOKIE_HUT_FREQUENCY;
        };
        return setting.get() == frequency;
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
