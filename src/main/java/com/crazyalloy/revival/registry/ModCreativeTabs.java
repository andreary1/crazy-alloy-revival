package com.crazyalloy.revival.registry;

import com.crazyalloy.revival.CrazyAlloyRevival;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CrazyAlloyRevival.MOD_ID);

    public static final Supplier<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.crazyalloy_revival"))
            .icon(() -> new ItemStack(ModItems.LOLLIPOP.get()))
            .displayItems((params, output) -> ModItems.TAB_ORDER.forEach(item -> output.accept(item.get())))
            .build());

    /** Overworld content not tied to any candy "part": the Bank, its guards, money and the Mineral Converter. */
    public static final Supplier<CreativeModeTab> OVERWORLD = TABS.register("overworld", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.crazyalloy_revival.overworld"))
            .icon(() -> new ItemStack(ModItems.MONEY.get()))
            .withTabsBefore(CrazyAlloyRevival.id("main"))
            .displayItems((params, output) -> ModItems.OVERWORLD_TAB_ORDER.forEach(item -> output.accept(item.get())))
            .build());

    private ModCreativeTabs() {}
}
