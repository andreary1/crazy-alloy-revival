package com.crazyalloy.revival.client;

import com.crazyalloy.revival.CrazyAlloyRevival;
import net.minecraft.client.model.geom.ModelLayerLocation;

public final class ModModelLayers {
    public static final ModelLayerLocation CANDY_TUBE_DOG = layer("candy_tube_dog");
    public static final ModelLayerLocation LOLLIPOP_GUY = layer("lollipop_guy");
    public static final ModelLayerLocation GRAPE_SPIDER = layer("grape_spider");
    public static final ModelLayerLocation BROWN_SUGAR_RHINO = layer("brown_sugar_rhino");
    public static final ModelLayerLocation COTTON_CANDY_TORNADO = layer("cotton_candy_tornado");
    public static final ModelLayerLocation BUBBLEGUM = layer("bubblegum");
    public static final ModelLayerLocation GINGERBREAD_WARRIOR = layer("gingerbread_warrior");
    public static final ModelLayerLocation GINGERBREAD_SOLDIER = layer("gingerbread_soldier");
    public static final ModelLayerLocation GINGERBREAD_KING = layer("gingerbread_king");
    public static final ModelLayerLocation JELLY_BUNNY = layer("jelly_bunny");
    public static final ModelLayerLocation JELLY_SNAKE = layer("jelly_snake");
    public static final ModelLayerLocation JELLY_SHARK = layer("jelly_shark");
    public static final ModelLayerLocation ROLL_CAKE_MONSTER = layer("roll_cake_monster");
    public static final ModelLayerLocation ICE_CREAM_VENDOR = layer("ice_cream_vendor");
    public static final ModelLayerLocation IMPOSTOR_CAKE = layer("impostor_cake");
    public static final ModelLayerLocation ICE_CREAM_MACHINE_LEVER = layer("ice_cream_machine_lever");
    public static final ModelLayerLocation ICE_CREAM_ZOMBIE = layer("ice_cream_zombie");
    public static final ModelLayerLocation ICE_CREAM_BEAST = layer("ice_cream_beast");
    public static final ModelLayerLocation ICE_CREAM_GARGOYLE = layer("ice_cream_gargoyle");
    public static final ModelLayerLocation LIVING_ICE_CREAM = layer("living_ice_cream");
    public static final ModelLayerLocation ANGRY_ICE_CREAM_CONE = layer("angry_ice_cream_cone");
    public static final ModelLayerLocation ICE_CREAM_DRAGON = layer("ice_cream_dragon");
    public static final ModelLayerLocation GUARD_DINOSAUR = layer("guard_dinosaur");
    public static final ModelLayerLocation SEGWAY = layer("segway");

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(CrazyAlloyRevival.id(name), "main");
    }

    private ModModelLayers() {}
}
