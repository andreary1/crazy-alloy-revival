package com.crazyalloy.revival;

import com.crazyalloy.revival.client.ModModelLayers;
import com.crazyalloy.revival.client.model.BrownSugarRhinoModel;
import com.crazyalloy.revival.client.model.BubblegumModel;
import com.crazyalloy.revival.client.model.CandyTubeDogModel;
import com.crazyalloy.revival.client.model.CottonCandyTornadoModel;
import com.crazyalloy.revival.client.model.GingerbreadKingModel;
import com.crazyalloy.revival.client.model.GingerbreadModel;
import com.crazyalloy.revival.client.model.JellyBunnyModel;
import com.crazyalloy.revival.client.model.JellySharkModel;
import com.crazyalloy.revival.client.model.JellySnakeModel;
import com.crazyalloy.revival.client.model.RollCakeMonsterModel;
import com.crazyalloy.revival.client.model.IceCreamVendorModel;
import com.crazyalloy.revival.client.model.ImpostorCakeModel;
import com.crazyalloy.revival.client.renderer.IceCreamMachineRenderer;
import com.crazyalloy.revival.client.screen.IceCreamMachineScreen;
import com.crazyalloy.revival.registry.ModBlockEntities;
import com.crazyalloy.revival.client.renderer.BubbalooCreeperRenderer;
import com.crazyalloy.revival.client.renderer.RevivalMobRenderer;
import com.crazyalloy.revival.client.renderer.SimpleMobRenderer;
import com.crazyalloy.revival.registry.ModFluids;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.client.resources.model.sprite.Material;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;
import com.crazyalloy.revival.client.model.GrapeSpiderModel;
import com.crazyalloy.revival.client.model.LollipopGuyModel;
import com.crazyalloy.revival.client.renderer.CandyTubeDogRenderer;
import com.crazyalloy.revival.client.renderer.GrapeSpiderRenderer;
import com.crazyalloy.revival.client.renderer.LollipopGuyRenderer;
import com.crazyalloy.revival.client.screen.ChocolateFactoryScreen;
import com.crazyalloy.revival.registry.ModEntities;
import com.crazyalloy.revival.registry.ModMenus;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/** Client-only entry point. Never loaded on a dedicated server. */
@Mod(value = CrazyAlloyRevival.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CrazyAlloyRevival.MOD_ID, value = Dist.CLIENT)
public final class CrazyAlloyRevivalClient {
    public CrazyAlloyRevivalClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.CANDY_TUBE_DOG, CandyTubeDogModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.LOLLIPOP_GUY, LollipopGuyModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.GRAPE_SPIDER, GrapeSpiderModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.BROWN_SUGAR_RHINO, BrownSugarRhinoModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.COTTON_CANDY_TORNADO, CottonCandyTornadoModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.BUBBLEGUM, BubblegumModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.GINGERBREAD_WARRIOR, GingerbreadModel::createWarriorLayer);
        event.registerLayerDefinition(ModModelLayers.GINGERBREAD_SOLDIER, GingerbreadModel::createSoldierLayer);
        event.registerLayerDefinition(ModModelLayers.GINGERBREAD_KING, GingerbreadKingModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.JELLY_BUNNY, JellyBunnyModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.JELLY_SNAKE, JellySnakeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.JELLY_SHARK, JellySharkModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.ROLL_CAKE_MONSTER, RollCakeMonsterModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.ICE_CREAM_VENDOR, IceCreamVendorModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.IMPOSTOR_CAKE, ImpostorCakeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.ICE_CREAM_MACHINE_LEVER, IceCreamMachineRenderer::createLeverLayer);
        event.registerLayerDefinition(ModModelLayers.ICE_CREAM_ZOMBIE, com.crazyalloy.revival.client.model.IceCreamZombieModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.ICE_CREAM_BEAST, com.crazyalloy.revival.client.model.IceCreamBeastModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.ICE_CREAM_GARGOYLE, com.crazyalloy.revival.client.model.IceCreamGargoyleModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.LIVING_ICE_CREAM, com.crazyalloy.revival.client.model.LivingIceCreamModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.ANGRY_ICE_CREAM_CONE, com.crazyalloy.revival.client.model.AngryIceCreamConeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.ICE_CREAM_DRAGON, com.crazyalloy.revival.client.model.IceCreamDragonModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.GUARD_DINOSAUR, com.crazyalloy.revival.client.model.GuardDinosaurModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SEGWAY, com.crazyalloy.revival.client.model.SegwayModel::createBodyLayer);
    }

    @SubscribeEvent
    static void onRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CANDY_TUBE_DOG.get(), CandyTubeDogRenderer::new);
        event.registerEntityRenderer(ModEntities.LOLLIPOP_GUY.get(), LollipopGuyRenderer::new);
        event.registerEntityRenderer(ModEntities.GRAPE_SPIDER.get(), GrapeSpiderRenderer::new);
        event.registerEntityRenderer(ModEntities.BROWN_SUGAR_RHINO.get(),
                c -> new RevivalMobRenderer<>(c, new BrownSugarRhinoModel(c.bakeLayer(ModModelLayers.BROWN_SUGAR_RHINO)), 1.0F, "brown_sugar_rhino", 1.25F));
        event.registerEntityRenderer(ModEntities.COTTON_CANDY_TORNADO.get(),
                c -> new RevivalMobRenderer<>(c, new CottonCandyTornadoModel(c.bakeLayer(ModModelLayers.COTTON_CANDY_TORNADO)), 0.5F, "cotton_candy_tornado", 1.0F));
        event.registerEntityRenderer(ModEntities.BUBBLEGUM.get(),
                c -> new SimpleMobRenderer<>(c, new BubblegumModel(c.bakeLayer(ModModelLayers.BUBBLEGUM)), 0.4F, "bubblegum"));
        event.registerEntityRenderer(ModEntities.GINGERBREAD_WARRIOR.get(), c -> new RevivalMobRenderer<>(c,
                new GingerbreadModel(c.bakeLayer(ModModelLayers.GINGERBREAD_WARRIOR), GingerbreadModel.Kind.WARRIOR), 0.4F, "gingerbread_warrior", 1.0F));
        event.registerEntityRenderer(ModEntities.GINGERBREAD_SOLDIER.get(), c -> new RevivalMobRenderer<>(c,
                new GingerbreadModel(c.bakeLayer(ModModelLayers.GINGERBREAD_SOLDIER), GingerbreadModel.Kind.SOLDIER), 0.4F, "gingerbread_soldier", 1.0F));
        event.registerEntityRenderer(ModEntities.GINGERBREAD_KING.get(), c -> new RevivalMobRenderer<>(c,
                new GingerbreadKingModel(c.bakeLayer(ModModelLayers.GINGERBREAD_KING)), 1.1F, "gingerbread_king", 1.25F));
        event.registerEntityRenderer(ModEntities.JELLY_BUNNY.get(),
                c -> new SimpleMobRenderer<>(c, new JellyBunnyModel(c.bakeLayer(ModModelLayers.JELLY_BUNNY)), 0.3F, "jelly_bunny"));
        event.registerEntityRenderer(ModEntities.JELLY_SNAKE.get(),
                c -> new SimpleMobRenderer<>(c, new JellySnakeModel(c.bakeLayer(ModModelLayers.JELLY_SNAKE)), 0.3F, "jelly_snake"));
        event.registerEntityRenderer(ModEntities.JELLY_SHARK.get(),
                c -> new RevivalMobRenderer<>(c, new JellySharkModel(c.bakeLayer(ModModelLayers.JELLY_SHARK)), 0.8F, "jelly_shark", 1.25F));
        event.registerEntityRenderer(ModEntities.ROLL_CAKE_MONSTER.get(),
                c -> new RevivalMobRenderer<>(c, new RollCakeMonsterModel(c.bakeLayer(ModModelLayers.ROLL_CAKE_MONSTER)), 0.5F, "roll_cake_monster", 1.0F));
        event.registerEntityRenderer(ModEntities.ICE_CREAM_VENDOR.get(),
                c -> new RevivalMobRenderer<>(c, new IceCreamVendorModel(c.bakeLayer(ModModelLayers.ICE_CREAM_VENDOR)), 0.5F, "ice_cream_vendor", 0.9375F));
        event.registerEntityRenderer(ModEntities.BUBBALOO_CREEPER.get(), BubbalooCreeperRenderer::new);
        event.registerEntityRenderer(ModEntities.IMPOSTOR_CAKE.get(),
                c -> new RevivalMobRenderer<>(c, new ImpostorCakeModel(c.bakeLayer(ModModelLayers.IMPOSTOR_CAKE)), 0.55F, "impostor_cake", 1.0F));
        event.registerBlockEntityRenderer(ModBlockEntities.ICE_CREAM_MACHINE.get(), IceCreamMachineRenderer::new);
        registerIceCreamDimension(event);
        event.registerEntityRenderer(ModEntities.GUARD_DINOSAUR.get(), c -> new RevivalMobRenderer<>(c,
                new com.crazyalloy.revival.client.model.GuardDinosaurModel(c.bakeLayer(ModModelLayers.GUARD_DINOSAUR)), 0.6F, "guard_dinosaur", 1.0F));
        event.registerEntityRenderer(ModEntities.SEGWAY.get(), c -> new RevivalMobRenderer<>(c,
                new com.crazyalloy.revival.client.model.SegwayModel(c.bakeLayer(ModModelLayers.SEGWAY)), 0.5F, "segway", 1.0F));
        event.registerEntityRenderer(ModEntities.BULLET.get(), c -> new ThrownItemRenderer<>(c, 0.5F, false));
        event.registerEntityRenderer(ModEntities.BROWN_SUGAR_BRICK.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.GUMDROP_SHOT.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.JELLY_SNAKE_SHOT.get(), c -> new ThrownItemRenderer<>(c, 1.5F, false));
    }

    /** Stage 6 creatures. The zombie and the living ice cream pick one of four textures from their flavour variant. */
    private static void registerIceCreamDimension(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ICE_CREAM_ZOMBIE.get(), c -> new RevivalMobRenderer<>(c,
                new com.crazyalloy.revival.client.model.IceCreamZombieModel(c.bakeLayer(ModModelLayers.ICE_CREAM_ZOMBIE)), 0.5F,
                RevivalMobRenderer.flavorTextures("ice_cream_zombie"), 1.0F));
        event.registerEntityRenderer(ModEntities.LIVING_ICE_CREAM.get(), c -> new RevivalMobRenderer<>(c,
                new com.crazyalloy.revival.client.model.LivingIceCreamModel(c.bakeLayer(ModModelLayers.LIVING_ICE_CREAM)), 0.35F,
                RevivalMobRenderer.flavorTextures("living_ice_cream"), 1.0F));
        event.registerEntityRenderer(ModEntities.ICE_CREAM_BEAST.get(), c -> new RevivalMobRenderer<>(c,
                new com.crazyalloy.revival.client.model.IceCreamBeastModel(c.bakeLayer(ModModelLayers.ICE_CREAM_BEAST)), 0.9F, "ice_cream_beast", 1.4F));
        event.registerEntityRenderer(ModEntities.ICE_CREAM_GARGOYLE.get(), c -> new RevivalMobRenderer<>(c,
                new com.crazyalloy.revival.client.model.IceCreamGargoyleModel(c.bakeLayer(ModModelLayers.ICE_CREAM_GARGOYLE)), 0.4F, "ice_cream_gargoyle", 1.2F));
        event.registerEntityRenderer(ModEntities.ANGRY_ICE_CREAM_CONE.get(), c -> new RevivalMobRenderer<>(c,
                new com.crazyalloy.revival.client.model.AngryIceCreamConeModel(c.bakeLayer(ModModelLayers.ANGRY_ICE_CREAM_CONE)), 0.35F, "angry_ice_cream_cone", 1.0F));
        event.registerEntityRenderer(ModEntities.ICE_CREAM_DRAGON.get(), c -> new RevivalMobRenderer<>(c,
                new com.crazyalloy.revival.client.model.IceCreamDragonModel(c.bakeLayer(ModModelLayers.ICE_CREAM_DRAGON)), 1.4F, "ice_cream_dragon", 2.4F));
    }

    @SubscribeEvent
    static void onFluidModels(RegisterFluidModelsEvent event) {
        event.register(new FluidModel.Unbaked(new Material(CrazyAlloyRevival.id("block/melted_chocolate_still")),
                new Material(CrazyAlloyRevival.id("block/melted_chocolate_flow")), null, null), ModFluids.MELTED_CHOCOLATE, ModFluids.FLOWING_MELTED_CHOCOLATE);
        event.register(new FluidModel.Unbaked(new Material(CrazyAlloyRevival.id("block/bubbaloo_still")),
                new Material(CrazyAlloyRevival.id("block/bubbaloo_flow")), null, null), ModFluids.BUBBALOO, ModFluids.FLOWING_BUBBALOO);
    }

    /** Thick, coloured fog when the camera is inside a candy liquid. */
    @SubscribeEvent
    static void onClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(thickFog(0.30F, 0.17F, 0.09F), ModFluids.MELTED_CHOCOLATE_TYPE.get());
        event.registerFluidType(thickFog(0.95F, 0.45F, 0.70F), ModFluids.BUBBALOO_TYPE.get());
        // The AK47 is held up in both hands, like a loaded crossbow.
        event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions() {
            @Override
            public net.minecraft.client.model.HumanoidModel.@Nullable ArmPose getArmPose(net.minecraft.world.entity.LivingEntity entity,
                                                                                    net.minecraft.world.InteractionHand hand, ItemStack stack) {
                return net.minecraft.client.model.HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }
        }, com.crazyalloy.revival.registry.ModItems.AK47.get());
    }

    private static IClientFluidTypeExtensions thickFog(float r, float g, float b) {
        return new IClientFluidTypeExtensions() {
            @Override
            public void modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
                fluidFogColor.set(r, g, b, 1.0F);
            }

            @Override
            public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance, float partialTick, FogData fogData) {
                fogData.environmentalStart = 0.0F;
                fogData.environmentalEnd = 3.0F;
                fogData.renderDistanceStart = 0.0F;
                fogData.renderDistanceEnd = 3.0F;
            }
        };
    }

    @SubscribeEvent
    static void onMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.CHOCOLATE_FACTORY.get(), ChocolateFactoryScreen::new);
        event.register(ModMenus.ICE_CREAM_MACHINE.get(), IceCreamMachineScreen::new);
        event.register(ModMenus.MINERAL_CONVERTER.get(), com.crazyalloy.revival.client.screen.MineralConverterScreen::new);
    }

    /**
     * Adds a short gray description line to every Crazy Alloy item that has a "&lt;key&gt;.desc" translation
     * (optionally followed by ".desc.2" and ".desc.3").
     * Keeps item classes free of client code.
     */
    @SubscribeEvent
    static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!CrazyAlloyRevival.MOD_ID.equals(id.getNamespace())) {
            return;
        }
        String base = stack.getItem().getDescriptionId() + ".desc";
        int line = 1;
        for (String key : new String[] {base, base + ".2", base + ".3"}) {
            if (!Language.getInstance().has(key)) {
                break;
            }
            event.getToolTip().add(Math.min(line++, event.getToolTip().size()), Component.translatable(key).withStyle(ChatFormatting.GRAY));
        }
    }
}
