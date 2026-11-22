package com.crazyalloy.revival;

import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.registry.ModAttachments;
import com.crazyalloy.revival.registry.ModBlockEntities;
import com.crazyalloy.revival.registry.ModDataComponents;
import com.crazyalloy.revival.registry.ModBlocks;
import com.crazyalloy.revival.registry.ModConditions;
import com.crazyalloy.revival.registry.ModCreativeTabs;
import com.crazyalloy.revival.registry.ModEntities;
import com.crazyalloy.revival.registry.ModFeatures;
import com.crazyalloy.revival.registry.ModFluids;
import com.crazyalloy.revival.registry.ModItems;
import com.crazyalloy.revival.registry.ModMenus;
import com.crazyalloy.revival.registry.ModPlacementModifiers;
import com.crazyalloy.revival.registry.ModPoiTypes;
import com.crazyalloy.revival.registry.ModRecipes;
import com.crazyalloy.revival.registry.ModSounds;
import com.crazyalloy.revival.worldgen.ModWorldgen;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(CrazyAlloyRevival.MOD_ID)
public final class CrazyAlloyRevival {
    public static final String MOD_ID = "crazyalloy_revival";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CrazyAlloyRevival(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, RevivalConfig.SPEC);

        ModSounds.SOUNDS.register(modBus);
        ModFluids.FLUID_TYPES.register(modBus);
        ModFluids.FLUIDS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModDataComponents.COMPONENTS.register(modBus);
        ModAttachments.ATTACHMENTS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModEntities.ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModRecipes.RECIPE_TYPES.register(modBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modBus);
        ModRecipes.RECIPE_BOOK_CATEGORIES.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        ModPlacementModifiers.PLACEMENT_MODIFIERS.register(modBus);
        ModConditions.CONDITION_CODECS.register(modBus);
        ModPoiTypes.POI_TYPES.register(modBus);
        ModFeatures.FEATURES.register(modBus);
        ModFeatures.STRUCTURE_TYPES.register(modBus);

        modBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModWorldgen::registerTerraBlender);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
