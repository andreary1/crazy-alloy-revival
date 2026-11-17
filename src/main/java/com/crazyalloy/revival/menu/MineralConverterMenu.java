package com.crazyalloy.revival.menu;

import com.crazyalloy.revival.block.entity.MineralConverterBlockEntity;
import com.crazyalloy.revival.registry.ModItems;
import com.crazyalloy.revival.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/** Mineral Converter screen contents. Button 0 (the Convert button) converts one unit on the server. */
public class MineralConverterMenu extends AbstractContainerMenu {
    public static final int BUTTON_CONVERT = 0;
    public static final int INPUT_X = 35, INPUT_Y = 35;
    public static final int MONEY_X = 107, MONEY_Y = 26;
    public static final int COIN_X = 133, COIN_Y = 26;

    private static final int PLAYER_INV_START = MineralConverterBlockEntity.SLOT_COUNT;
    private static final int PLAYER_INV_END = PLAYER_INV_START + 36;

    private final Container container;
    private final ContainerData data;

    public static MineralConverterMenu clientSide(int id, Inventory inventory, @Nullable RegistryFriendlyByteBuf extra) {
        return new MineralConverterMenu(id, inventory, new SimpleContainer(MineralConverterBlockEntity.SLOT_COUNT),
                new SimpleContainerData(MineralConverterBlockEntity.DATA_COUNT));
    }

    public MineralConverterMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.MINERAL_CONVERTER.get(), id);
        checkContainerSize(container, MineralConverterBlockEntity.SLOT_COUNT);
        checkContainerDataCount(data, MineralConverterBlockEntity.DATA_COUNT);
        this.container = container;
        this.data = data;
        container.startOpen(inventory.player);

        this.addSlot(new Slot(container, MineralConverterBlockEntity.SLOT_INPUT, INPUT_X, INPUT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !stack.is(ModItems.MONEY.get()) && !stack.is(ModItems.COIN.get());
            }
        });
        this.addSlot(new OutputSlot(container, MineralConverterBlockEntity.SLOT_MONEY, MONEY_X, MONEY_Y));
        this.addSlot(new OutputSlot(container, MineralConverterBlockEntity.SLOT_COIN, COIN_X, COIN_Y));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
        this.addDataSlots(data);
    }

    private static final class OutputSlot extends Slot {
        OutputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId == BUTTON_CONVERT && container instanceof MineralConverterBlockEntity converter) {
            if (converter.convert()) {
                this.broadcastChanges();
            }
            return true;
        }
        return buttonId == BUTTON_CONVERT;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index < PLAYER_INV_START) {
            if (!this.moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.slots.get(MineralConverterBlockEntity.SLOT_INPUT).mayPlace(stack)
                || !this.moveItemStackTo(stack, MineralConverterBlockEntity.SLOT_INPUT, MineralConverterBlockEntity.SLOT_INPUT + 1, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == copy.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    public int money() {
        return data.get(MineralConverterBlockEntity.DATA_MONEY);
    }

    public int coin() {
        return data.get(MineralConverterBlockEntity.DATA_COIN);
    }

    public boolean ready() {
        return data.get(MineralConverterBlockEntity.DATA_READY) != 0;
    }

    public boolean hasInput() {
        return container.getItem(MineralConverterBlockEntity.SLOT_INPUT).isEmpty() == false;
    }
}
