package com.crazyalloy.revival.entity;

import com.crazyalloy.revival.registry.ModItems;
import com.crazyalloy.revival.registry.ModSounds;
import java.util.List;
import com.crazyalloy.revival.menu.WizardTradingMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SpawnEggItem;
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
 * Talking to him opens Wizard Trading ({@link WizardTradingMenu}): the original's two recipes, the Wizard Staff for 64
 * Shadow Fragments and 32 Ent Hearts, and an Elixir of Life for 64 Blue Mushrooms.
 * <p>
 * Revival proposals: from that window, "Other trades" opens his emerald trades (potions and fairy-catching supplies,
 * and he buys the grove's Purple and Blue Mushrooms); his idle work is reading a book and stirring (action A).
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
                new MerchantOffer(new ItemCost(Items.EMERALD, 4), PotionContents.createItemStack(Items.POTION, Potions.NIGHT_VISION), 6, 5, 0.05F));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof SpawnEggItem || !this.isAlive() || this.isTrading() || this.isBaby()) {
            return super.mobInteract(player, hand);
        }
        if (hand == InteractionHand.MAIN_HAND) {
            player.awardStat(Stats.TALKED_TO_VILLAGER);
        }
        if (player instanceof ServerPlayer sp) {
            int id = this.getId();
            sp.openMenu(new SimpleMenuProvider((containerId, inventory, p) -> new WizardTradingMenu(containerId, inventory, id),
                    Component.translatable("gui.crazyalloy_revival.wizard_trading.title")));
        }
        return InteractionResult.SUCCESS;
    }

    /** Opens the vanilla merchant window with the Wizard's emerald trades. */
    public void openOtherTrades(ServerPlayer player) {
        if (this.isTrading() || this.getOffers().isEmpty()) {
            return;
        }
        this.setTradingPlayer(player);
        this.openTradingScreen(player, this.getDisplayName(), 1);
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
