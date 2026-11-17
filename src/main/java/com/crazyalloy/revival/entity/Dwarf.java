package com.crazyalloy.revival.entity;

import com.crazyalloy.revival.registry.ModItems;
import com.crazyalloy.revival.registry.ModSounds;
import java.util.List;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;

/**
 * Dwarf of the Mystic Grove: a compact fellow with a big head, a bushy red beard, red sleeves, a brown front, dark
 * grey trousers, brown boots and a dark cap with a square yellow piece on the front, like a miner's lamp. He lives in
 * the small dark-wood Dwarf Hut with its fenced stone yard.
 * <p>
 * Revival proposals: a miner who buys raw ore and coal and sells mining gear and tourmaline; his idle work is
 * straightening his cap and stroking his beard (action A). The lamp on his cap does not give light (the original
 * image does not show whether it does).
 */
public class Dwarf extends MysticDweller {
    public Dwarf(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }

    @Override
    protected List<MerchantOffer> tradeList() {
        return List.of(
                new MerchantOffer(new ItemCost(Items.COAL, 15), new ItemStack(Items.EMERALD), 16, 2, 0.05F),
                new MerchantOffer(new ItemCost(Items.RAW_IRON, 6), new ItemStack(Items.EMERALD), 16, 2, 0.05F),
                new MerchantOffer(new ItemCost(Items.RAW_GOLD, 3), new ItemStack(Items.EMERALD), 12, 2, 0.05F),
                new MerchantOffer(new ItemCost(Items.EMERALD, 1), new ItemStack(Items.TORCH, 8), 16, 1, 0.05F),
                new MerchantOffer(new ItemCost(Items.EMERALD, 3), new ItemStack(Items.IRON_PICKAXE), 4, 5, 0.05F),
                new MerchantOffer(new ItemCost(Items.EMERALD, 2), new ItemStack(Items.LANTERN, 2), 8, 2, 0.05F),
                new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.TOURMALINE.get()), 4, 10, 0.05F));
    }

    @Override
    protected int homeRadius() {
        return 10;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.DWARF.ambient().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.DWARF.hurt().get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DWARF.death().get();
    }
}
