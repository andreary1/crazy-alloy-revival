package com.crazyalloy.revival.registry;

import com.crazyalloy.revival.CrazyAlloyRevival;
import com.crazyalloy.revival.item.Ak47Item;
import com.crazyalloy.revival.item.BrownSugarBrickItem;
import com.crazyalloy.revival.item.BrownSugarSwordItem;
import com.crazyalloy.revival.item.IceCreamAmuletItem;
import com.crazyalloy.revival.item.JellyBazookaItem;
import com.crazyalloy.revival.item.ModFoods;
import com.crazyalloy.revival.item.ModMaterials;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CrazyAlloyRevival.MOD_ID);
    /** Creative tab order. */
    public static final List<DeferredItem<? extends Item>> TAB_ORDER = new ArrayList<>();
    /** Order of the Overworld tab: content not tied to a candy biome or dimension (the Bank, stage 7). */
    public static final List<DeferredItem<? extends Item>> OVERWORLD_TAB_ORDER = new ArrayList<>();

    // --- Block items (same order as the creative tab) -------------------------------------
    public static final DeferredItem<?> CHOCOLATE_GRASS_BLOCK = block(ModBlocks.CHOCOLATE_GRASS_BLOCK);
    public static final DeferredItem<?> CHOCOLATE_SOIL = block(ModBlocks.CHOCOLATE_SOIL);
    public static final DeferredItem<?> SWEETWOOD_LOG = block(ModBlocks.SWEETWOOD_LOG);
    public static final DeferredItem<?> SWEETWOOD_PLANKS = block(ModBlocks.SWEETWOOD_PLANKS);
    public static final DeferredItem<?> COTTON_CANDY_LEAVES = block(ModBlocks.COTTON_CANDY_LEAVES);
    public static final DeferredItem<?> SWEETWOOD_STAIRS = block(ModBlocks.SWEETWOOD_STAIRS);
    public static final DeferredItem<?> SWEETWOOD_SLAB = block(ModBlocks.SWEETWOOD_SLAB);
    public static final DeferredItem<?> SWEETWOOD_FENCE = block(ModBlocks.SWEETWOOD_FENCE);
    public static final DeferredItem<?> SWEETWOOD_FENCE_GATE = block(ModBlocks.SWEETWOOD_FENCE_GATE);
    public static final DeferredItem<?> SWEETWOOD_DOOR = tab(ITEMS.registerItem("sweetwood_door",
            p -> new DoubleHighBlockItem(ModBlocks.SWEETWOOD_DOOR.get(), p), p -> p.useBlockDescriptionPrefix()));
    public static final DeferredItem<?> SWEETWOOD_BUTTON = block(ModBlocks.SWEETWOOD_BUTTON);
    public static final DeferredItem<?> SWEETWOOD_PRESSURE_PLATE = block(ModBlocks.SWEETWOOD_PRESSURE_PLATE);
    public static final DeferredItem<?> SWEETWOOD_SAPLING = block(ModBlocks.SWEETWOOD_SAPLING);
    public static final DeferredItem<?> LOLLIPOP_FLOWER = block(ModBlocks.LOLLIPOP_FLOWER);
    public static final DeferredItem<?> RED_LICORICE_PLANT = block(ModBlocks.RED_LICORICE_PLANT);
    public static final DeferredItem<?> GINGERBREAD_BLOCK = block(ModBlocks.GINGERBREAD_BLOCK);
    public static final DeferredItem<?> FROSTED_GINGERBREAD_BLOCK = block(ModBlocks.FROSTED_GINGERBREAD_BLOCK);
    public static final DeferredItem<?> GINGERBREAD_STAIRS = block(ModBlocks.GINGERBREAD_STAIRS);
    public static final DeferredItem<?> GINGERBREAD_SLAB = block(ModBlocks.GINGERBREAD_SLAB);
    public static final DeferredItem<?> GREEN_JELLY_BEAN_BLOCK = block(ModBlocks.GREEN_JELLY_BEAN_BLOCK);
    public static final DeferredItem<?> YELLOW_JELLY_BEAN_BLOCK = block(ModBlocks.YELLOW_JELLY_BEAN_BLOCK);
    public static final DeferredItem<?> RED_JELLY_BEAN_BLOCK = block(ModBlocks.RED_JELLY_BEAN_BLOCK);
    public static final DeferredItem<?> ORANGE_JELLY_BEAN_BLOCK = block(ModBlocks.ORANGE_JELLY_BEAN_BLOCK);
    public static final DeferredItem<?> PURPLE_JELLY_BEAN_BLOCK = block(ModBlocks.PURPLE_JELLY_BEAN_BLOCK);
    public static final DeferredItem<?> INFESTED_PURPLE_JELLY_BEAN_BLOCK = block(ModBlocks.INFESTED_PURPLE_JELLY_BEAN_BLOCK);
    public static final DeferredItem<?> CHOCOLATE_BLOCK = block(ModBlocks.CHOCOLATE_BLOCK);
    public static final DeferredItem<?> CHOCOLATE_BRICKS = block(ModBlocks.CHOCOLATE_BRICKS);
    public static final DeferredItem<?> CHOCOLATE_BRICK_STAIRS = block(ModBlocks.CHOCOLATE_BRICK_STAIRS);
    public static final DeferredItem<?> CHOCOLATE_BRICK_SLAB = block(ModBlocks.CHOCOLATE_BRICK_SLAB);
    public static final DeferredItem<?> CHOCOLATE_BRICK_WALL = block(ModBlocks.CHOCOLATE_BRICK_WALL);
    public static final DeferredItem<?> CHISELED_CHOCOLATE_BRICKS = block(ModBlocks.CHISELED_CHOCOLATE_BRICKS);
    public static final DeferredItem<?> TOURMALINE_ORE = block(ModBlocks.TOURMALINE_ORE);
    public static final DeferredItem<?> DEEPSLATE_TOURMALINE_ORE = block(ModBlocks.DEEPSLATE_TOURMALINE_ORE);
    public static final DeferredItem<?> TOURMALINE_BLOCK = block(ModBlocks.TOURMALINE_BLOCK);
    public static final DeferredItem<?> CHOCOLATE_FACTORY = block(ModBlocks.CHOCOLATE_FACTORY);
    public static final DeferredItem<?> ICE_CREAM_MACHINE = block(ModBlocks.ICE_CREAM_MACHINE);
    public static final DeferredItem<?> PINK_CANDY_ROCK = block(ModBlocks.PINK_CANDY_ROCK);
    public static final DeferredItem<?> PURPLE_CANDY_ROCK = block(ModBlocks.PURPLE_CANDY_ROCK);
    public static final DeferredItem<?> SUGAR_CRYSTAL = block(ModBlocks.SUGAR_CRYSTAL);
    public static final DeferredItem<?> VANILLA_ICE_CREAM_BLOCK = block(ModBlocks.VANILLA_ICE_CREAM_BLOCK);
    public static final DeferredItem<?> CHOCOLATE_ICE_CREAM_BLOCK = block(ModBlocks.CHOCOLATE_ICE_CREAM_BLOCK);
    public static final DeferredItem<?> STRAWBERRY_ICE_CREAM_BLOCK = block(ModBlocks.STRAWBERRY_ICE_CREAM_BLOCK);
    public static final DeferredItem<?> MINT_ICE_CREAM_BLOCK = block(ModBlocks.MINT_ICE_CREAM_BLOCK);
    public static final DeferredItem<?> ICE_CREAM_DRAGON_EGG = tab(ITEMS.registerSimpleBlockItem(ModBlocks.ICE_CREAM_DRAGON_EGG, p -> p.rarity(Rarity.EPIC)));

    // --- Ingredients ----------------------------------------------------------------------
    public static final DeferredItem<Item> TOURMALINE = simple("tourmaline");
    public static final DeferredItem<Item> COCOA_POWDER = simple("cocoa_powder");
    public static final DeferredItem<Item> CANDY_TUBE = simple("candy_tube");
    public static final DeferredItem<BrownSugarBrickItem> BROWN_SUGAR_BRICK = tab(ITEMS.registerItem("brown_sugar_brick", BrownSugarBrickItem::new, p -> p.stacksTo(16)));
    public static final DeferredItem<Item> DEAD_JELLY_SNAKE = simple("dead_jelly_snake");
    public static final DeferredItem<BucketItem> MELTED_CHOCOLATE_BUCKET = tab(ITEMS.registerItem("melted_chocolate_bucket",
            p -> new BucketItem(ModFluids.MELTED_CHOCOLATE.get(), p), p -> p.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<BucketItem> BUBBALOO_BUCKET = tab(ITEMS.registerItem("bubbaloo_bucket",
            p -> new BucketItem(ModFluids.BUBBALOO.get(), p), p -> p.craftRemainder(Items.BUCKET).stacksTo(1)));

    // --- Food -----------------------------------------------------------------------------
    public static final DeferredItem<Item> CHOCOLATE_BAR = food("chocolate_bar", ModFoods.CHOCOLATE_BAR, ModFoods.CHOCOLATE_BAR_CONSUMABLE);
    public static final DeferredItem<Item> MILK_CHOCOLATE = food("milk_chocolate", ModFoods.MILK_CHOCOLATE, ModFoods.MILK_CHOCOLATE_CONSUMABLE);
    public static final DeferredItem<Item> LOLLIPOP = food("lollipop", ModFoods.LOLLIPOP, ModFoods.LOLLIPOP_CONSUMABLE);
    public static final DeferredItem<Item> COTTON_CANDY = food("cotton_candy", ModFoods.COTTON_CANDY, ModFoods.COTTON_CANDY_CONSUMABLE);
    public static final DeferredItem<Item> GRAPE = food("grape", ModFoods.GRAPE, ModFoods.GRAPE_CONSUMABLE);
    public static final DeferredItem<Item> RED_LICORICE = food("red_licorice", ModFoods.RED_LICORICE, ModFoods.RED_LICORICE_CONSUMABLE);
    public static final DeferredItem<Item> COOKED_LICORICE = food("cooked_licorice", ModFoods.COOKED_LICORICE, ModFoods.COOKED_LICORICE_CONSUMABLE);
    public static final DeferredItem<Item> GINGERBREAD = food("gingerbread", ModFoods.GINGERBREAD, ModFoods.GINGERBREAD_CONSUMABLE);
    public static final DeferredItem<Item> GUMDROP = food("gumdrop", ModFoods.GUMDROP, ModFoods.GUMDROP_CONSUMABLE);
    public static final DeferredItem<Item> ROLL_CAKE = food("roll_cake", ModFoods.ROLL_CAKE, ModFoods.ROLL_CAKE_CONSUMABLE);
    public static final DeferredItem<Item> JELLY_BEANS = food("jelly_beans", ModFoods.JELLY_BEANS, ModFoods.JELLY_BEANS_CONSUMABLE);
    public static final DeferredItem<Item> WAFER_CONE = food("wafer_cone", ModFoods.WAFER_CONE, ModFoods.WAFER_CONE_CONSUMABLE);
    public static final DeferredItem<Item> VANILLA_ICE_CREAM = food("vanilla_ice_cream", ModFoods.VANILLA_ICE_CREAM, ModFoods.VANILLA_ICE_CREAM_CONSUMABLE);
    public static final DeferredItem<Item> STRAWBERRY_ICE_CREAM = food("strawberry_ice_cream", ModFoods.STRAWBERRY_ICE_CREAM, ModFoods.STRAWBERRY_ICE_CREAM_CONSUMABLE);
    public static final DeferredItem<Item> CHOCOLATE_ICE_CREAM = food("chocolate_ice_cream", ModFoods.CHOCOLATE_ICE_CREAM, ModFoods.CHOCOLATE_ICE_CREAM_CONSUMABLE);
    public static final DeferredItem<Item> MINT_ICE_CREAM = food("mint_ice_cream", ModFoods.MINT_ICE_CREAM, ModFoods.MINT_ICE_CREAM_CONSUMABLE);
    public static final DeferredItem<Item> ULTIMATE_ICE_CREAM = tab(ITEMS.registerSimpleItem("ultimate_ice_cream", p -> p.food(ModFoods.ULTIMATE_ICE_CREAM)
            .component(DataComponents.CONSUMABLE, ModFoods.ULTIMATE_ICE_CREAM_CONSUMABLE).rarity(Rarity.RARE)));
    /** Opens the portal to the Ice Cream Dimension (stage 6). Sold by the Ice Cream Vendor at level 5. */
    public static final DeferredItem<IceCreamAmuletItem> ICE_CREAM_AMULET = tab(ITEMS.registerItem("ice_cream_amulet", IceCreamAmuletItem::new,
            p -> p.stacksTo(1).rarity(Rarity.EPIC)));

    // --- Candy weapons and gear ------------------------------------------------------------
    public static final DeferredItem<BrownSugarSwordItem> BROWN_SUGAR_SWORD = tab(ITEMS.registerItem("brown_sugar_sword",
            BrownSugarSwordItem::new, p -> p.sword(ModMaterials.BROWN_SUGAR_TOOL, 3.0F, -2.4F)));
    public static final DeferredItem<JellyBazookaItem> JELLY_BAZOOKA = tab(ITEMS.registerItem("jelly_bazooka",
            JellyBazookaItem::new, p -> p.durability(250)));

    // --- Tourmaline equipment -------------------------------------------------------------
    public static final DeferredItem<Item> TOURMALINE_SWORD = tab(ITEMS.registerItem("tourmaline_sword",
            Item::new, p -> p.sword(ModMaterials.TOURMALINE_TOOL, 3.0F, -2.4F)));
    public static final DeferredItem<Item> TOURMALINE_PICKAXE = tab(ITEMS.registerItem("tourmaline_pickaxe",
            Item::new, p -> p.pickaxe(ModMaterials.TOURMALINE_TOOL, 1.0F, -2.8F)));
    public static final DeferredItem<AxeItem> TOURMALINE_AXE = tab(ITEMS.registerItem("tourmaline_axe",
            p -> new AxeItem(ModMaterials.TOURMALINE_TOOL, 5.5F, -3.05F, p)));
    public static final DeferredItem<ShovelItem> TOURMALINE_SHOVEL = tab(ITEMS.registerItem("tourmaline_shovel",
            p -> new ShovelItem(ModMaterials.TOURMALINE_TOOL, 1.5F, -3.0F, p)));
    public static final DeferredItem<HoeItem> TOURMALINE_HOE = tab(ITEMS.registerItem("tourmaline_hoe",
            p -> new HoeItem(ModMaterials.TOURMALINE_TOOL, -2.5F, -0.5F, p)));
    public static final DeferredItem<Item> TOURMALINE_HELMET = armor("tourmaline_helmet", ArmorType.HELMET);
    public static final DeferredItem<Item> TOURMALINE_CHESTPLATE = armor("tourmaline_chestplate", ArmorType.CHESTPLATE);
    public static final DeferredItem<Item> TOURMALINE_LEGGINGS = armor("tourmaline_leggings", ArmorType.LEGGINGS);
    public static final DeferredItem<Item> TOURMALINE_BOOTS = armor("tourmaline_boots", ArmorType.BOOTS);

    // --- Spawn eggs -----------------------------------------------------------------------
    public static final DeferredItem<SpawnEggItem> CANDY_TUBE_DOG_SPAWN_EGG = tab(ITEMS.registerItem("candy_tube_dog_spawn_egg",
            p -> new SpawnEggItem(p.spawnEgg(ModEntities.CANDY_TUBE_DOG.get()))));
    public static final DeferredItem<SpawnEggItem> LOLLIPOP_GUY_SPAWN_EGG = tab(ITEMS.registerItem("lollipop_guy_spawn_egg",
            p -> new SpawnEggItem(p.spawnEgg(ModEntities.LOLLIPOP_GUY.get()))));
    public static final DeferredItem<SpawnEggItem> GRAPE_SPIDER_SPAWN_EGG = tab(ITEMS.registerItem("grape_spider_spawn_egg",
            p -> new SpawnEggItem(p.spawnEgg(ModEntities.GRAPE_SPIDER.get()))));

    public static final DeferredItem<SpawnEggItem> BROWN_SUGAR_RHINO_SPAWN_EGG = egg("brown_sugar_rhino", ModEntities.BROWN_SUGAR_RHINO);
    public static final DeferredItem<SpawnEggItem> COTTON_CANDY_TORNADO_SPAWN_EGG = egg("cotton_candy_tornado", ModEntities.COTTON_CANDY_TORNADO);
    public static final DeferredItem<SpawnEggItem> BUBBLEGUM_SPAWN_EGG = egg("bubblegum", ModEntities.BUBBLEGUM);
    public static final DeferredItem<SpawnEggItem> GINGERBREAD_WARRIOR_SPAWN_EGG = egg("gingerbread_warrior", ModEntities.GINGERBREAD_WARRIOR);
    public static final DeferredItem<SpawnEggItem> GINGERBREAD_SOLDIER_SPAWN_EGG = egg("gingerbread_soldier", ModEntities.GINGERBREAD_SOLDIER);
    public static final DeferredItem<SpawnEggItem> JELLY_BUNNY_SPAWN_EGG = egg("jelly_bunny", ModEntities.JELLY_BUNNY);
    public static final DeferredItem<SpawnEggItem> JELLY_SNAKE_SPAWN_EGG = egg("jelly_snake", ModEntities.JELLY_SNAKE);
    public static final DeferredItem<SpawnEggItem> JELLY_SHARK_SPAWN_EGG = egg("jelly_shark", ModEntities.JELLY_SHARK);
    public static final DeferredItem<SpawnEggItem> ROLL_CAKE_MONSTER_SPAWN_EGG = egg("roll_cake_monster", ModEntities.ROLL_CAKE_MONSTER);
    public static final DeferredItem<SpawnEggItem> BUBBALOO_CREEPER_SPAWN_EGG = egg("bubbaloo_creeper", ModEntities.BUBBALOO_CREEPER);
    public static final DeferredItem<SpawnEggItem> GINGERBREAD_KING_SPAWN_EGG = egg("gingerbread_king", ModEntities.GINGERBREAD_KING);
    public static final DeferredItem<SpawnEggItem> ICE_CREAM_VENDOR_SPAWN_EGG = egg("ice_cream_vendor", ModEntities.ICE_CREAM_VENDOR);
    public static final DeferredItem<SpawnEggItem> IMPOSTOR_CAKE_SPAWN_EGG = egg("impostor_cake", ModEntities.IMPOSTOR_CAKE);
    public static final DeferredItem<SpawnEggItem> ICE_CREAM_ZOMBIE_SPAWN_EGG = egg("ice_cream_zombie", ModEntities.ICE_CREAM_ZOMBIE);
    public static final DeferredItem<SpawnEggItem> ICE_CREAM_BEAST_SPAWN_EGG = egg("ice_cream_beast", ModEntities.ICE_CREAM_BEAST);
    public static final DeferredItem<SpawnEggItem> ICE_CREAM_GARGOYLE_SPAWN_EGG = egg("ice_cream_gargoyle", ModEntities.ICE_CREAM_GARGOYLE);
    public static final DeferredItem<SpawnEggItem> LIVING_ICE_CREAM_SPAWN_EGG = egg("living_ice_cream", ModEntities.LIVING_ICE_CREAM);
    public static final DeferredItem<SpawnEggItem> ANGRY_ICE_CREAM_CONE_SPAWN_EGG = egg("angry_ice_cream_cone", ModEntities.ANGRY_ICE_CREAM_CONE);
    public static final DeferredItem<SpawnEggItem> ICE_CREAM_DRAGON_SPAWN_EGG = egg("ice_cream_dragon", ModEntities.ICE_CREAM_DRAGON);

    // --- Overworld tab: the Bank and the money economy (stage 7) -----------------------------
    public static final DeferredItem<?> MINERAL_CONVERTER = overworld(ITEMS.registerSimpleBlockItem(ModBlocks.MINERAL_CONVERTER, p -> p.rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> MONEY = overworld(ITEMS.registerSimpleItem("money"));
    public static final DeferredItem<Item> COIN = overworld(ITEMS.registerSimpleItem("coin"));
    public static final DeferredItem<Ak47Item> AK47 = overworld(ITEMS.registerItem("ak47", Ak47Item::new, p -> p.durability(600).rarity(Rarity.RARE)));
    public static final DeferredItem<Item> AK47_AMMO = overworld(ITEMS.registerSimpleItem("ak47_ammo"));
    public static final DeferredItem<SpawnEggItem> GUARD_DINOSAUR_SPAWN_EGG = overworld(ITEMS.registerItem("guard_dinosaur_spawn_egg",
            p -> new SpawnEggItem(p.spawnEgg(ModEntities.GUARD_DINOSAUR.get()))));
    public static final DeferredItem<SpawnEggItem> SEGWAY_SPAWN_EGG = overworld(ITEMS.registerItem("segway_spawn_egg",
            p -> new SpawnEggItem(p.spawnEgg(ModEntities.SEGWAY.get()))));

    private static <I extends Item> DeferredItem<I> overworld(DeferredItem<I> item) {
        OVERWORLD_TAB_ORDER.add(item);
        return item;
    }

    private static DeferredItem<SpawnEggItem> egg(String name, java.util.function.Supplier<? extends net.minecraft.world.entity.EntityType<?>> type) {
        return tab(ITEMS.registerItem(name + "_spawn_egg", p -> new SpawnEggItem(p.spawnEgg(type.get()))));
    }

    private static <I extends Item> DeferredItem<I> tab(DeferredItem<I> item) {
        TAB_ORDER.add(item);
        return item;
    }

    private static DeferredItem<?> block(DeferredBlock<? extends Block> block) {
        return tab(ITEMS.registerSimpleBlockItem(block));
    }

    private static DeferredItem<Item> simple(String name) {
        return tab(ITEMS.registerSimpleItem(name));
    }

    private static DeferredItem<Item> food(String name, net.minecraft.world.food.FoodProperties food,
                                          net.minecraft.world.item.component.Consumable consumable) {
        return tab(ITEMS.registerSimpleItem(name, p -> p.food(food).component(DataComponents.CONSUMABLE, consumable)));
    }

    private static DeferredItem<Item> armor(String name, ArmorType type) {
        return tab(ITEMS.registerItem(name, Item::new, p -> p.humanoidArmor(ModMaterials.TOURMALINE_ARMOR, type).rarity(Rarity.COMMON)));
    }

    private ModItems() {}
}
