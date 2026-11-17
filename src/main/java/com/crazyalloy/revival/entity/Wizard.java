package com.crazyalloy.revival.entity;

import com.crazyalloy.revival.registry.ModItems;
import com.crazyalloy.revival.registry.ModSounds;
import java.util.List;
import java.util.Optional;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;

/**
 * Wizard of the Mystic Grove: an old wizard with a long white beard and a blue robe dotted with white, under a tall
 * blue pointed hat with a bent tip and a wide brim. He lives on the glass top floor of the Wizard Tower, among his
 * candles, brewing stand and cauldron.
 * <p>
 * Revival proposals: he trades potions and fairy-catching supplies for emeralds and buys the grove's Purple and Blue
 * Mushrooms (his ingredients); his idle work is reading a book and stirring (action A).
 */
public class Wizard extends MysticDweller {
    public Wizard(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }

    @Override
    protected List<MerchantOffer> tradeList() {
        return List.of(
                new MerchantOffer(new ItemCost(ModItems.PURPLE_MUSHROOM.get(), 4), new ItemStack(Items.EMERALD), 16, 2, 0.05F),
                new MerchantOffer(new ItemCost(ModItems.BLUE_MUSHROOM.get(), 4), new ItemStack(Items.EMERALD), 16, 2, 0.05F),
                new MerchantOffer(new ItemCost(Items.EMERALD, 1), new ItemStack(ModItems.JAR.get(), 2), 12, 1, 0.05F),
                new MerchantOffer(new ItemCost(Items.EMERALD, 2), new ItemStack(Items.GLOWSTONE_DUST, 6), 12, 1, 0.05F),
                new MerchantOffer(new ItemCost(Items.EMERALD, 3), PotionContents.createItemStack(Items.POTION, Potions.HEALING), 8, 5, 0.05F),
                new MerchantOffer(new ItemCost(Items.EMERALD, 4), PotionContents.createItemStack(Items.POTION, Potions.NIGHT_VISION), 6, 5, 0.05F),
                new MerchantOffer(new ItemCost(Items.EMERALD, 8), Optional.of(new ItemCost(Items.GLASS_BOTTLE, 1)),
                        new ItemStack(ModItems.ELIXIR_OF_LIFE.get()), 3, 10, 0.05F));
    }

    @Override
    protected int homeRadius() {
        return 6;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.WIZARD.ambient().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.WIZARD.hurt().get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.WIZARD.death().get();
    }
}
