package com.crazyalloy.revival.item;

import com.crazyalloy.revival.CrazyAlloyRevival;
import com.crazyalloy.revival.registry.ModTags;
import java.util.EnumMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

/**
 * Tourmaline sits between iron and diamond: iron-tier mining level, more durable than iron,
 * and the most enchantable of the mid-tier materials (it is a gem).
 *
 * <pre>
 *              durability  speed  dmg bonus  enchant
 * iron            250       6.0     2.0        14
 * tourmaline      600       7.0     2.5        18
 * diamond        1561       8.0     3.0        10
 * </pre>
 */
public final class ModMaterials {
    public static final ToolMaterial TOURMALINE_TOOL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL, 600, 7.0F, 2.5F, 18, ModTags.Items.TOURMALINE_REPAIR);

    public static final ResourceKey<EquipmentAsset> TOURMALINE_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, CrazyAlloyRevival.id("tourmaline"));

    /** Armor points 2/5/7/2 (iron 2/5/6/2, diamond 3/6/8/3), toughness 1, durability factor 22. */
    public static final ArmorMaterial TOURMALINE_ARMOR = new ArmorMaterial(22, Util.make(new EnumMap<>(ArmorType.class), map -> {
        map.put(ArmorType.BOOTS, 2);
        map.put(ArmorType.LEGGINGS, 5);
        map.put(ArmorType.CHESTPLATE, 7);
        map.put(ArmorType.HELMET, 2);
        map.put(ArmorType.BODY, 6);
    }), 18, SoundEvents.ARMOR_EQUIP_IRON, 1.0F, 0.0F, ModTags.Items.TOURMALINE_REPAIR, TOURMALINE_ASSET);

    /** Brown Sugar Sword: brittle (110 uses, like the original) but hits hard enough to knock enemies back. */
    public static final ToolMaterial BROWN_SUGAR_TOOL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 110, 4.0F, 1.0F, 10, ModTags.Items.BROWN_SUGAR_REPAIR);

    /**
     * Mushroom Sword (Mushroom Depths loot): iron-tier, a little more durable than iron (350 uses), repaired with
     * mushrooms.
     */
    public static final ToolMaterial MUSHROOM_TOOL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL, 350, 6.0F, 2.0F, 16, ModTags.Items.MUSHROOM_REPAIR);

    private ModMaterials() {}
}
