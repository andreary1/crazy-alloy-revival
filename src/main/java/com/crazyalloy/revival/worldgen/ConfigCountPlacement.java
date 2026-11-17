package com.crazyalloy.revival.worldgen;

import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.registry.ModPlacementModifiers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.IntSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;

/** A count placement whose value comes from the common config, so ore frequency is adjustable without data packs. */
public class ConfigCountPlacement extends RepeatingPlacement {
    public static final MapCodec<ConfigCountPlacement> CODEC = Setting.CODEC.fieldOf("setting")
            .xmap(ConfigCountPlacement::new, p -> p.setting);

    private final Setting setting;

    public ConfigCountPlacement(Setting setting) {
        this.setting = setting;
    }

    @Override
    protected int count(RandomSource random, BlockPos pos) {
        return setting.value.getAsInt();
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModPlacementModifiers.CONFIG_COUNT.get();
    }

    public enum Setting implements StringRepresentable {
        TOURMALINE("tourmaline", RevivalConfig.TOURMALINE_VEINS_PER_CHUNK::getAsInt),
        DEEP_TOURMALINE("deep_tourmaline", RevivalConfig.DEEP_TOURMALINE_VEINS_PER_CHUNK::getAsInt),
        ICE_CREAM_PINNACLES("ice_cream_pinnacles", RevivalConfig.ICE_CREAM_PINNACLES::getAsInt),
        GIANT_MUSHROOMS("giant_mushrooms", RevivalConfig.GIANT_MUSHROOMS::getAsInt);

        public static final Codec<Setting> CODEC = StringRepresentable.fromEnum(Setting::values);

        private final String name;
        private final IntSupplier value;

        Setting(String name, IntSupplier value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
