package com.crazyalloy.revival.menu;

import com.crazyalloy.revival.item.ElementalBookItem;
import com.crazyalloy.revival.item.ElementalBookItem.Element;
import com.crazyalloy.revival.item.WizardStaffItem;
import com.crazyalloy.revival.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * The Wizard Staff's power menu (no slots). Button {@code n} selects element {@code n} (ordinal) on the staff held in
 * {@link #hand}, if the player has learned it. What is learned comes from the player's synced attachment.
 */
public class StaffMenu extends AbstractContainerMenu {
    private final Player player;
    private final InteractionHand hand;

    public static StaffMenu clientSide(int id, Inventory inventory, @Nullable RegistryFriendlyByteBuf extra) {
        // The client finds the staff itself: the main hand first, as on the server.
        boolean main = inventory.player.getMainHandItem().getItem() instanceof WizardStaffItem;
        return new StaffMenu(id, inventory, main ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }

    public StaffMenu(int id, Inventory inventory, InteractionHand hand) {
        super(ModMenus.WIZARD_STAFF.get(), id);
        this.player = inventory.player;
        this.hand = hand;
    }

    public ItemStack staff() {
        return this.player.getItemInHand(this.hand);
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        Element[] all = Element.values();
        if (buttonId < 0 || buttonId >= all.length) {
            return false;
        }
        Element element = all[buttonId];
        ItemStack staff = this.staff();
        if (!(staff.getItem() instanceof WizardStaffItem) || !ElementalBookItem.hasLearned(player, element)) {
            return false;
        }
        WizardStaffItem.select(staff, element);
        if (player instanceof ServerPlayer sp) {
            sp.level().playSound(null, sp.getX(), sp.getY(), sp.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.8F, 1.3F);
            sp.sendOverlayMessage(Component.translatable("message.crazyalloy_revival.staff.selected", element.displayName())
                    .withStyle(s -> s.withColor(element.color & 0xFFFFFF)));
        }
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getItemInHand(this.hand).getItem() instanceof WizardStaffItem;
    }
}
