package com.crazyalloy.revival.event;

import com.crazyalloy.revival.CrazyAlloyRevival;
import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.entity.BrownSugarRhino;
import com.crazyalloy.revival.entity.Bubblegum;
import com.crazyalloy.revival.entity.BubbalooCreeper;
import com.crazyalloy.revival.entity.CandyTubeDog;
import com.crazyalloy.revival.entity.CottonCandyTornado;
import com.crazyalloy.revival.entity.GingerbreadKing;
import com.crazyalloy.revival.entity.IceCreamVendor;
import com.crazyalloy.revival.entity.ImpostorCake;
import com.crazyalloy.revival.entity.GingerbreadSoldier;
import com.crazyalloy.revival.entity.GingerbreadWarrior;
import com.crazyalloy.revival.entity.GrapeSpider;
import com.crazyalloy.revival.entity.JellyBunny;
import com.crazyalloy.revival.entity.JellyShark;
import com.crazyalloy.revival.entity.JellySnake;
import com.crazyalloy.revival.entity.LollipopGuy;
import com.crazyalloy.revival.entity.RollCakeMonster;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import com.crazyalloy.revival.registry.ModBlockEntities;
import com.crazyalloy.revival.registry.ModEntities;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;

@EventBusSubscriber(modid = CrazyAlloyRevival.MOD_ID)
public final class CommonEvents {
    private static final Identifier HEALTH_MODIFIER = CrazyAlloyRevival.id("config_health");
    private static final Identifier DAMAGE_MODIFIER = CrazyAlloyRevival.id("config_damage");

    private CommonEvents() {}

    @SubscribeEvent
    static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.CANDY_TUBE_DOG.get(), CandyTubeDog.createAttributes().build());
        event.put(ModEntities.LOLLIPOP_GUY.get(), LollipopGuy.createAttributes().build());
        event.put(ModEntities.GRAPE_SPIDER.get(), GrapeSpider.createAttributes().build());
        event.put(ModEntities.BROWN_SUGAR_RHINO.get(), BrownSugarRhino.createAttributes().build());
        event.put(ModEntities.COTTON_CANDY_TORNADO.get(), CottonCandyTornado.createAttributes().build());
        event.put(ModEntities.BUBBLEGUM.get(), Bubblegum.createAttributes().build());
        event.put(ModEntities.GINGERBREAD_WARRIOR.get(), GingerbreadWarrior.createAttributes().build());
        event.put(ModEntities.GINGERBREAD_SOLDIER.get(), GingerbreadSoldier.createAttributes().build());
        event.put(ModEntities.GINGERBREAD_KING.get(), GingerbreadKing.createAttributes().build());
        event.put(ModEntities.ICE_CREAM_VENDOR.get(), IceCreamVendor.createAttributes().build());
        event.put(ModEntities.JELLY_BUNNY.get(), JellyBunny.createAttributes().build());
        event.put(ModEntities.JELLY_SNAKE.get(), JellySnake.createAttributes().build());
        event.put(ModEntities.JELLY_SHARK.get(), JellyShark.createAttributes().build());
        event.put(ModEntities.ROLL_CAKE_MONSTER.get(), RollCakeMonster.createAttributes().build());
        event.put(ModEntities.BUBBALOO_CREEPER.get(), BubbalooCreeper.createAttributes().build());
        event.put(ModEntities.IMPOSTOR_CAKE.get(), ImpostorCake.createAttributes().build());
        event.put(ModEntities.ICE_CREAM_ZOMBIE.get(), com.crazyalloy.revival.entity.IceCreamZombie.createAttributes().build());
        event.put(ModEntities.LIVING_ICE_CREAM.get(), com.crazyalloy.revival.entity.LivingIceCream.createAttributes().build());
        event.put(ModEntities.ICE_CREAM_BEAST.get(), com.crazyalloy.revival.entity.IceCreamBeast.createAttributes().build());
        event.put(ModEntities.ICE_CREAM_GARGOYLE.get(), com.crazyalloy.revival.entity.IceCreamGargoyle.createAttributes().build());
        event.put(ModEntities.ANGRY_ICE_CREAM_CONE.get(), com.crazyalloy.revival.entity.AngryIceCreamCone.createAttributes().build());
        event.put(ModEntities.ICE_CREAM_DRAGON.get(), com.crazyalloy.revival.entity.IceCreamDragon.createAttributes().build());
        event.put(ModEntities.GUARD_DINOSAUR.get(), com.crazyalloy.revival.entity.GuardDinosaur.createAttributes().build());
        event.put(ModEntities.SEGWAY.get(), com.crazyalloy.revival.entity.Segway.createAttributes().build());
    }

    @SubscribeEvent
    static void onSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntities.CANDY_TUBE_DOG.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                CandyTubeDog::checkSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.LOLLIPOP_GUY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                LollipopGuy::checkSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.GRAPE_SPIDER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GrapeSpider::checkSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        ground(event, ModEntities.BROWN_SUGAR_RHINO.get(), BrownSugarRhino::checkSpawnRules);
        ground(event, ModEntities.COTTON_CANDY_TORNADO.get(), CottonCandyTornado::checkSpawnRules);
        ground(event, ModEntities.BUBBLEGUM.get(), Bubblegum::checkSpawnRules);
        ground(event, ModEntities.GINGERBREAD_WARRIOR.get(), GingerbreadWarrior::checkSpawnRules);
        ground(event, ModEntities.GINGERBREAD_SOLDIER.get(), GingerbreadWarrior::checkSpawnRules);
        ground(event, ModEntities.JELLY_BUNNY.get(), JellyBunny::checkSpawnRules);
        ground(event, ModEntities.JELLY_SNAKE.get(), JellySnake::checkSpawnRules);
        ground(event, ModEntities.ROLL_CAKE_MONSTER.get(), RollCakeMonster::checkSpawnRules);
        ground(event, ModEntities.BUBBALOO_CREEPER.get(), BubbalooCreeper::checkSpawnRules);
        ground(event, ModEntities.IMPOSTOR_CAKE.get(), ImpostorCake::checkSpawnRules);
        ground(event, ModEntities.ICE_CREAM_ZOMBIE.get(), com.crazyalloy.revival.entity.IceCreamZombie::checkSpawnRules);
        ground(event, ModEntities.LIVING_ICE_CREAM.get(), com.crazyalloy.revival.entity.LivingIceCream::checkSpawnRules);
        ground(event, ModEntities.ICE_CREAM_BEAST.get(), com.crazyalloy.revival.entity.IceCreamBeast::checkSpawnRules);
        ground(event, ModEntities.ICE_CREAM_GARGOYLE.get(), com.crazyalloy.revival.entity.IceCreamGargoyle::checkSpawnRules);
        ground(event, ModEntities.ANGRY_ICE_CREAM_CONE.get(), com.crazyalloy.revival.entity.AngryIceCreamCone::checkSpawnRules);
        ground(event, ModEntities.GUARD_DINOSAUR.get(), com.crazyalloy.revival.entity.GuardDinosaur::checkSpawnRules);
        ground(event, ModEntities.SEGWAY.get(), com.crazyalloy.revival.entity.GuardDinosaur::checkSpawnRules);
        event.register(ModEntities.JELLY_SHARK.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                JellyShark::checkSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private static <T extends Entity> void ground(RegisterSpawnPlacementsEvent event, EntityType<T> type, SpawnPlacements.SpawnPredicate<T> rule) {
        event.register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, rule, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    @SubscribeEvent
    static void onCapabilities(RegisterCapabilitiesEvent event) {
        // Hoppers and pipes: top = ingredients, sides = fuel, bottom = output.
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.CHOCOLATE_FACTORY.get(), WorldlyContainerWrapper::new);
        // Mineral Converter: materials in from the top and sides, Money and Coins out from below.
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.MINERAL_CONVERTER.get(), WorldlyContainerWrapper::new);
    }

    /** Applies the configured difficulty multipliers to Crazy Alloy creatures when they spawn. */
    @SubscribeEvent
    static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        Mob mob = event.getEntity();
        if (!CrazyAlloyRevival.MOD_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).getNamespace())) {
            return;
        }
        double health = RevivalConfig.MOB_HEALTH_MULTIPLIER.get() - 1.0;
        double damage = RevivalConfig.MOB_DAMAGE_MULTIPLIER.get() - 1.0;
        AttributeInstance maxHealth = mob.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && health != 0.0) {
            maxHealth.addOrReplacePermanentModifier(new AttributeModifier(HEALTH_MODIFIER, health, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            mob.setHealth(mob.getMaxHealth());
        }
        AttributeInstance attack = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack != null && damage != 0.0) {
            attack.addOrReplacePermanentModifier(new AttributeModifier(DAMAGE_MODIFIER, damage, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }
}
