package com.crazyalloy.revival.registry;

import com.crazyalloy.revival.CrazyAlloyRevival;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CrazyAlloyRevival.MOD_ID);

    /** The element selected on a Wizard Staff (an {@code ElementalBookItem.Element} id: fire, water, ...). */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> STAFF_ELEMENT = COMPONENTS.registerComponentType("staff_element",
            b -> b.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));

    private ModDataComponents() {}
}
