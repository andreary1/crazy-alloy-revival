package com.crazyalloy.revival.registry;

import com.crazyalloy.revival.CrazyAlloyRevival;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Sound events. Stage 1 points them at re-pitched vanilla sounds (see sounds.json); they are provisional. */
public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, CrazyAlloyRevival.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> CANDY_TUBE_DOG_AMBIENT = register("entity.candy_tube_dog.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> CANDY_TUBE_DOG_HURT = register("entity.candy_tube_dog.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> CANDY_TUBE_DOG_DEATH = register("entity.candy_tube_dog.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> CANDY_TUBE_DOG_SHED = register("entity.candy_tube_dog.shed");
    public static final DeferredHolder<SoundEvent, SoundEvent> LOLLIPOP_GUY_AMBIENT = register("entity.lollipop_guy.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> LOLLIPOP_GUY_HURT = register("entity.lollipop_guy.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> LOLLIPOP_GUY_DEATH = register("entity.lollipop_guy.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> LOLLIPOP_GUY_GIFT = register("entity.lollipop_guy.gift");
    public static final DeferredHolder<SoundEvent, SoundEvent> GRAPE_SPIDER_AMBIENT = register("entity.grape_spider.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> GRAPE_SPIDER_HURT = register("entity.grape_spider.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> GRAPE_SPIDER_DEATH = register("entity.grape_spider.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHOCOLATE_FACTORY_WORKING = register("block.chocolate_factory.working");

    // Stage 2 creatures: one ambient/hurt/death set each (provisional re-pitched vanilla sounds).
    public static final MobSounds BROWN_SUGAR_RHINO = mob("brown_sugar_rhino");
    public static final MobSounds COTTON_CANDY_TORNADO = mob("cotton_candy_tornado");
    public static final MobSounds BUBBLEGUM = mob("bubblegum");
    public static final MobSounds GINGERBREAD = mob("gingerbread");
    public static final MobSounds JELLY_BUNNY = mob("jelly_bunny");
    public static final MobSounds JELLY_SNAKE = mob("jelly_snake");
    public static final MobSounds JELLY_SHARK = mob("jelly_shark");
    public static final MobSounds ROLL_CAKE_MONSTER = mob("roll_cake_monster");
    public static final DeferredHolder<SoundEvent, SoundEvent> BUBBLEGUM_POP = register("entity.bubblegum.pop");
    public static final DeferredHolder<SoundEvent, SoundEvent> GINGERBREAD_SOLDIER_SHOOT = register("entity.gingerbread_soldier.shoot");

    // Stage 3: Gingerbread King.
    public static final MobSounds GINGERBREAD_KING = mob("gingerbread_king");
    public static final DeferredHolder<SoundEvent, SoundEvent> GINGERBREAD_KING_ROAR = register("entity.gingerbread_king.roar");
    public static final DeferredHolder<SoundEvent, SoundEvent> GINGERBREAD_KING_SLAM = register("entity.gingerbread_king.slam");
    public static final DeferredHolder<SoundEvent, SoundEvent> GINGERBREAD_KING_SUMMON = register("entity.gingerbread_king.summon");

    // Stage 5: Impostor Cake and Ice Cream Machine.
    public static final MobSounds IMPOSTOR_CAKE = mob("impostor_cake");
    public static final DeferredHolder<SoundEvent, SoundEvent> IMPOSTOR_CAKE_REVEAL = register("entity.impostor_cake.reveal");
    public static final DeferredHolder<SoundEvent, SoundEvent> IMPOSTOR_CAKE_CHOMP = register("entity.impostor_cake.chomp");
    public static final DeferredHolder<SoundEvent, SoundEvent> ICE_CREAM_MACHINE_SERVE = register("block.ice_cream_machine.serve");

    // Stage 6: Ice Cream Dimension creatures (provisional re-pitched vanilla sounds).
    public static final MobSounds ICE_CREAM_ZOMBIE = mob("ice_cream_zombie");
    public static final MobSounds ICE_CREAM_BEAST = mob("ice_cream_beast");
    public static final MobSounds ICE_CREAM_GARGOYLE = mob("ice_cream_gargoyle");
    public static final MobSounds LIVING_ICE_CREAM = mob("living_ice_cream");
    public static final MobSounds ANGRY_ICE_CREAM_CONE = mob("angry_ice_cream_cone");
    public static final MobSounds ICE_CREAM_DRAGON = mob("ice_cream_dragon");
    public static final DeferredHolder<SoundEvent, SoundEvent> ICE_CREAM_DRAGON_ROAR = register("entity.ice_cream_dragon.roar");

    // Stage 7: the Bank (provisional re-pitched vanilla sounds).
    public static final MobSounds GUARD_DINOSAUR = mob("guard_dinosaur");
    public static final DeferredHolder<SoundEvent, SoundEvent> GUARD_DINOSAUR_ALERT = register("entity.guard_dinosaur.alert");
    public static final MobSounds SEGWAY = mob("segway");
    public static final DeferredHolder<SoundEvent, SoundEvent> AK47_SHOOT = register("item.ak47.shoot");
    public static final DeferredHolder<SoundEvent, SoundEvent> MINERAL_CONVERTER_CONVERT = register("block.mineral_converter.convert");

    public record MobSounds(DeferredHolder<SoundEvent, SoundEvent> ambient, DeferredHolder<SoundEvent, SoundEvent> hurt,
                            DeferredHolder<SoundEvent, SoundEvent> death) {}

    private static MobSounds mob(String name) {
        return new MobSounds(register("entity." + name + ".ambient"), register("entity." + name + ".hurt"), register("entity." + name + ".death"));
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(CrazyAlloyRevival.id(name)));
    }

    private ModSounds() {}
}
