package com.crazyalloy.revival.registry;

import com.crazyalloy.revival.CrazyAlloyRevival;
import com.crazyalloy.revival.entity.BrownSugarRhino;
import com.crazyalloy.revival.entity.Bubblegum;
import com.crazyalloy.revival.entity.BubbalooCreeper;
import com.crazyalloy.revival.entity.CandyTubeDog;
import com.crazyalloy.revival.entity.CottonCandyTornado;
import com.crazyalloy.revival.entity.GingerbreadKing;
import com.crazyalloy.revival.entity.AngryIceCreamCone;
import com.crazyalloy.revival.entity.IceCreamBeast;
import com.crazyalloy.revival.entity.IceCreamDragon;
import com.crazyalloy.revival.entity.IceCreamGargoyle;
import com.crazyalloy.revival.entity.IceCreamVendor;
import com.crazyalloy.revival.entity.IceCreamZombie;
import com.crazyalloy.revival.entity.LivingIceCream;
import com.crazyalloy.revival.entity.ImpostorCake;
import com.crazyalloy.revival.entity.GingerbreadSoldier;
import com.crazyalloy.revival.entity.GingerbreadWarrior;
import com.crazyalloy.revival.entity.GrapeSpider;
import com.crazyalloy.revival.entity.JellyBunny;
import com.crazyalloy.revival.entity.JellyShark;
import com.crazyalloy.revival.entity.JellySnake;
import com.crazyalloy.revival.entity.LollipopGuy;
import com.crazyalloy.revival.entity.RollCakeMonster;
import com.crazyalloy.revival.entity.GuardDinosaur;
import com.crazyalloy.revival.entity.Segway;
import com.crazyalloy.revival.entity.projectile.BrownSugarBrickEntity;
import com.crazyalloy.revival.entity.projectile.BulletEntity;
import com.crazyalloy.revival.entity.projectile.GumdropShot;
import com.crazyalloy.revival.entity.projectile.JellySnakeShot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(CrazyAlloyRevival.MOD_ID);

    // --- Sweet Forest -----------------------------------------------------------------------
    public static final DeferredHolder<EntityType<?>, EntityType<CandyTubeDog>> CANDY_TUBE_DOG = ENTITIES.registerEntityType(
            "candy_tube_dog", CandyTubeDog::new, MobCategory.CREATURE, b -> b.sized(0.6F, 0.8F).eyeHeight(0.6F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<LollipopGuy>> LOLLIPOP_GUY = ENTITIES.registerEntityType(
            "lollipop_guy", LollipopGuy::new, MobCategory.CREATURE, b -> b.sized(0.6F, 1.9F).eyeHeight(1.2F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<BrownSugarRhino>> BROWN_SUGAR_RHINO = ENTITIES.registerEntityType(
            "brown_sugar_rhino", BrownSugarRhino::new, MobCategory.CREATURE, b -> b.sized(1.75F, 1.75F).eyeHeight(1.25F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<CottonCandyTornado>> COTTON_CANDY_TORNADO = ENTITIES.registerEntityType(
            "cotton_candy_tornado", CottonCandyTornado::new, MobCategory.MONSTER, b -> b.sized(0.9F, 1.8F).eyeHeight(1.5F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<Bubblegum>> BUBBLEGUM = ENTITIES.registerEntityType(
            "bubblegum", Bubblegum::new, MobCategory.MONSTER, b -> b.sized(0.7F, 0.7F).eyeHeight(0.4F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<GingerbreadWarrior>> GINGERBREAD_WARRIOR = ENTITIES.registerEntityType(
            "gingerbread_warrior", GingerbreadWarrior::new, MobCategory.MONSTER, b -> b.sized(0.6F, 1.45F).eyeHeight(1.2F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<GingerbreadSoldier>> GINGERBREAD_SOLDIER = ENTITIES.registerEntityType(
            "gingerbread_soldier", GingerbreadSoldier::new, MobCategory.MONSTER, b -> b.sized(0.6F, 1.45F).eyeHeight(1.2F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<GingerbreadKing>> GINGERBREAD_KING = ENTITIES.registerEntityType(
            "gingerbread_king", GingerbreadKing::new, MobCategory.MONSTER, b -> b.sized(1.8F, 3.4F).eyeHeight(2.6F).clientTrackingRange(10).fireImmune());

    public static final DeferredHolder<EntityType<?>, EntityType<IceCreamVendor>> ICE_CREAM_VENDOR = ENTITIES.registerEntityType(
            "ice_cream_vendor", IceCreamVendor::new, MobCategory.CREATURE, b -> b.sized(0.6F, 1.95F).eyeHeight(1.62F).clientTrackingRange(10));

    // --- Jelly Bean Fields ------------------------------------------------------------------
    public static final DeferredHolder<EntityType<?>, EntityType<GrapeSpider>> GRAPE_SPIDER = ENTITIES.registerEntityType(
            "grape_spider", GrapeSpider::new, MobCategory.MONSTER, b -> b.sized(1.0F, 0.65F).eyeHeight(0.45F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<JellyBunny>> JELLY_BUNNY = ENTITIES.registerEntityType(
            "jelly_bunny", JellyBunny::new, MobCategory.CREATURE, b -> b.sized(0.45F, 0.6F).eyeHeight(0.45F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<JellySnake>> JELLY_SNAKE = ENTITIES.registerEntityType(
            "jelly_snake", JellySnake::new, MobCategory.MONSTER, b -> b.sized(0.6F, 0.35F).eyeHeight(0.2F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<JellyShark>> JELLY_SHARK = ENTITIES.registerEntityType(
            "jelly_shark", JellyShark::new, MobCategory.MONSTER, b -> b.sized(1.9F, 1.2F).eyeHeight(0.62F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<RollCakeMonster>> ROLL_CAKE_MONSTER = ENTITIES.registerEntityType(
            "roll_cake_monster", RollCakeMonster::new, MobCategory.MONSTER, b -> b.sized(0.8F, 2.3F).eyeHeight(2.0F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<BubbalooCreeper>> BUBBALOO_CREEPER = ENTITIES.registerEntityType(
            "bubbaloo_creeper", BubbalooCreeper::new, MobCategory.MONSTER, b -> b.sized(0.6F, 1.7F).clientTrackingRange(8));

    // --- Candy Cave (stage 5) -----------------------------------------------------------------
    public static final DeferredHolder<EntityType<?>, EntityType<ImpostorCake>> IMPOSTOR_CAKE = ENTITIES.registerEntityType(
            "impostor_cake", ImpostorCake::new, MobCategory.MONSTER, b -> b.sized(0.9F, 1.05F).eyeHeight(0.7F).clientTrackingRange(8));

    // --- Ice Cream Dimension (stage 6) --------------------------------------------------------
    /** One type; the flavour (chocolate, vanilla, strawberry, mint) is a variant. */
    public static final DeferredHolder<EntityType<?>, EntityType<IceCreamZombie>> ICE_CREAM_ZOMBIE = ENTITIES.registerEntityType(
            "ice_cream_zombie", IceCreamZombie::new, MobCategory.MONSTER, b -> b.sized(0.6F, 1.95F).eyeHeight(1.74F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<IceCreamBeast>> ICE_CREAM_BEAST = ENTITIES.registerEntityType(
            "ice_cream_beast", IceCreamBeast::new, MobCategory.MONSTER, b -> b.sized(1.8F, 3.8F).eyeHeight(3.1F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<IceCreamGargoyle>> ICE_CREAM_GARGOYLE = ENTITIES.registerEntityType(
            "ice_cream_gargoyle", IceCreamGargoyle::new, MobCategory.MONSTER, b -> b.sized(0.9F, 1.0F).eyeHeight(0.7F).clientTrackingRange(10));
    /** One type; the flavour is a variant, as for the zombie. */
    public static final DeferredHolder<EntityType<?>, EntityType<LivingIceCream>> LIVING_ICE_CREAM = ENTITIES.registerEntityType(
            "living_ice_cream", LivingIceCream::new, MobCategory.CREATURE, b -> b.sized(0.6F, 1.1F).eyeHeight(0.85F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<AngryIceCreamCone>> ANGRY_ICE_CREAM_CONE = ENTITIES.registerEntityType(
            "angry_ice_cream_cone", AngryIceCreamCone::new, MobCategory.MONSTER, b -> b.sized(0.6F, 1.0F).eyeHeight(0.8F).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<IceCreamDragon>> ICE_CREAM_DRAGON = ENTITIES.registerEntityType(
            "ice_cream_dragon", IceCreamDragon::new, MobCategory.MONSTER, b -> b.sized(3.9F, 4.8F).eyeHeight(4.5F).clientTrackingRange(10).fireImmune());

    // --- The Bank (stage 7, Overworld) ----------------------------------------------------------
    public static final DeferredHolder<EntityType<?>, EntityType<GuardDinosaur>> GUARD_DINOSAUR = ENTITIES.registerEntityType(
            "guard_dinosaur", GuardDinosaur::new, MobCategory.MONSTER, b -> b.sized(0.9F, 2.3F).eyeHeight(2.0F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<Segway>> SEGWAY = ENTITIES.registerEntityType(
            "segway", Segway::new, MobCategory.MONSTER, b -> b.sized(0.9F, 1.5F).eyeHeight(1.2F).clientTrackingRange(10));

    // --- Projectiles ------------------------------------------------------------------------
    public static final DeferredHolder<EntityType<?>, EntityType<BrownSugarBrickEntity>> BROWN_SUGAR_BRICK = ENTITIES.registerEntityType(
            "brown_sugar_brick", BrownSugarBrickEntity::new, MobCategory.MISC, b -> b.noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
    public static final DeferredHolder<EntityType<?>, EntityType<GumdropShot>> GUMDROP_SHOT = ENTITIES.registerEntityType(
            "gumdrop_shot", GumdropShot::new, MobCategory.MISC, b -> b.noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
    public static final DeferredHolder<EntityType<?>, EntityType<JellySnakeShot>> JELLY_SNAKE_SHOT = ENTITIES.registerEntityType(
            "jelly_snake_shot", JellySnakeShot::new, MobCategory.MISC, b -> b.noLootTable().sized(0.35F, 0.35F).clientTrackingRange(4).updateInterval(10));

    public static final DeferredHolder<EntityType<?>, EntityType<BulletEntity>> BULLET = ENTITIES.registerEntityType(
            "bullet", BulletEntity::new, MobCategory.MISC, b -> b.noLootTable().sized(0.15F, 0.15F).clientTrackingRange(4).updateInterval(2));

    private ModEntities() {}
}
