package com.crazyalloy.revival.menu;

import com.crazyalloy.revival.entity.Wizard;
import com.crazyalloy.revival.registry.ModItems;
import com.crazyalloy.revival.registry.ModMenus;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * Wizard Trading: the Wizard's own trade window with the original's two recipes, above the player's inventory.
 * <ul>
 * <li>64 Shadow Fragments + 32 Ent Hearts: the Wizard Staff (button 0);</li>
 * <li>64 Blue Mushrooms: an Elixir of Life (button 1).</li>
 * </ul>
 * The materials are taken straight from the inventory when the player presses Trade. Button 2 opens the Wizard's
 * other trades (the emerald trades proposed by the revival) in the vanilla merchant window.
 */
public class WizardTradingMenu extends AbstractContainerMenu {
    public static final int BUTTON_OTHER_TRADES = 2;
    public static final int INV_Y = 140;

    public record Cost(Supplier<? extends Item> item, int count) {}

    public record Recipe(List<Cost> costs, Supplier<ItemStack> result) {}

    public static final List<Recipe> RECIPES = List.of(
            new Recipe(List.of(new Cost(ModItems.SHADOW_FRAGMENT, 64), new Cost(ModItems.ENT_HEART, 32)), () -> new ItemStack(ModItems.WIZARD_STAFF.get())),
            new Recipe(List.of(new Cost(ModItems.BLUE_MUSHROOM::asItem, 64)), () -> new ItemStack(ModItems.ELIXIR_OF_LIFE.get())));

    private final Player player;
    private final int wizardId;

    public static WizardTradingMenu clientSide(int id, Inventory inventory, @Nullable RegistryFriendlyByteBuf extra) {
        // The client screen only needs the recipes and the player's inventory; the server keeps the Wizard.
        return new WizardTradingMenu(id, inventory, -1);
    }

    public WizardTradingMenu(int id, Inventory inventory, int wizardId) {
        super(ModMenus.WIZARD_TRADING.get(), id);
        this.player = inventory.player;
        this.wizardId = wizardId;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, INV_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, INV_Y + 58));
        }
    }

    /** How many of {@code item} the player carries. */
    public static int carried(Player player, Item item) {
        return player.getInventory().clearOrCountMatchingItems(s -> s.is(item), 0, player.inventoryMenu.getCraftSlots());
    }

    public static boolean canAfford(Player player, Recipe recipe) {
        return recipe.costs().stream().allMatch(c -> carried(player, c.item().get()) >= c.count());
    }

    /** Takes the materials and hands over the result; false (nothing taken) if something is missing. */
    public static boolean trade(ServerPlayer player, Recipe recipe) {
        if (!canAfford(player, recipe)) {
            return false;
        }
        for (Cost c : recipe.costs()) {
            Item item = c.item().get();
            player.getInventory().clearOrCountMatchingItems(s -> s.is(item), c.count(), player.inventoryMenu.getCraftSlots());
        }
        ItemStack result = recipe.result().get();
        Component name = result.getHoverName();
        player.getInventory().placeItemBackInInventory(result);
        player.containerMenu.broadcastChanges();
        // Revival addition: a clear confirmation of what was received.
        player.sendOverlayMessage(Component.translatable("message.crazyalloy_revival.wizard_trading.received", name));
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0F, 1.2F);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5F, 1.6F);
        ((ServerLevel) player.level()).sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1.0, player.getZ(), 12, 0.5, 0.5, 0.5, 0.0);
        return true;
    }

    public @Nullable Wizard wizard() {
        return this.player.level().getEntity(this.wizardId) instanceof Wizard w ? w : null;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (!(player instanceof ServerPlayer sp)) {
            return buttonId >= 0 && buttonId <= BUTTON_OTHER_TRADES;
        }
        if (buttonId == BUTTON_OTHER_TRADES) {
            Wizard wizard = this.wizard();
            if (wizard != null) {
                sp.closeContainer();
                wizard.openOtherTrades(sp);
            }
            return true;
        }
        if (buttonId < 0 || buttonId >= RECIPES.size()) {
            return false;
        }
        Wizard wizard = this.wizard();
        boolean done = trade(sp, RECIPES.get(buttonId));
        if (done && wizard != null) {
            ((ServerLevel) sp.level()).sendParticles(ParticleTypes.ENCHANT, wizard.getX(), wizard.getY() + 1.6, wizard.getZ(), 30, 0.4, 0.5, 0.4, 0.5);
        }
        return done;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        boolean moved = index < 27 ? this.moveItemStackTo(stack, 27, 36, false) : this.moveItemStackTo(stack, 0, 27, false);
        if (!moved) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        Wizard wizard = this.wizard();
        return wizard != null && wizard.isAlive() && player.distanceToSqr(wizard) < 64.0;
    }
}
