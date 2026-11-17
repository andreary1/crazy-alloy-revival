package com.crazyalloy.revival.block.entity;

import com.crazyalloy.revival.config.RevivalConfig;
import com.crazyalloy.revival.menu.MineralConverterMenu;
import com.crazyalloy.revival.recipe.MineralConverterInput;
import com.crazyalloy.revival.recipe.MineralConverterRecipe;
import com.crazyalloy.revival.registry.ModBlockEntities;
import com.crazyalloy.revival.registry.ModItems;
import com.crazyalloy.revival.registry.ModRecipes;
import com.crazyalloy.revival.registry.ModSounds;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Mineral Converter: one material slot and two output slots (Money and Coin). Pressing Convert on its screen turns
 * one unit of an accepted material into Money and Coins at once; no fuel, no waiting. What each material gives is
 * data-driven ({@code crazyalloy_revival:mineral_converter} recipes); the table in 0.7.0 is the original mod's.
 * Hoppers put materials in from the top and sides and take money out from below.
 */
public class MineralConverterBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_MONEY = 1;
    public static final int SLOT_COIN = 2;
    public static final int SLOT_COUNT = 3;

    /** Money and Coins one unit of the current material gives (both 0 when it is not accepted). */
    public static final int DATA_MONEY = 0;
    public static final int DATA_COIN = 1;
    /** 1 when a conversion is possible right now (material accepted and room in both output slots). */
    public static final int DATA_READY = 2;
    public static final int DATA_COUNT = 3;

    private static final int[] SLOTS_IN = {SLOT_INPUT};
    private static final int[] SLOTS_OUT = {SLOT_MONEY, SLOT_COIN};

    private NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private final RecipeManager.CachedCheck<MineralConverterInput, MineralConverterRecipe> quickCheck;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            Optional<MineralConverterRecipe> recipe = currentRecipe();
            return switch (index) {
                case DATA_MONEY -> recipe.map(MineralConverterRecipe::money).orElse(0);
                case DATA_COIN -> recipe.map(MineralConverterRecipe::coin).orElse(0);
                case DATA_READY -> recipe.isPresent() && hasRoomFor(recipe.get()) ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // Values are server-authoritative.
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public MineralConverterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MINERAL_CONVERTER.get(), pos, state);
        this.quickCheck = RecipeManager.createCheck(ModRecipes.MINERAL_CONVERTER_TYPE.get());
    }

    private Optional<MineralConverterRecipe> currentRecipe() {
        ItemStack material = items.get(SLOT_INPUT);
        if (material.isEmpty() || !(level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }
        return quickCheck.getRecipeFor(new MineralConverterInput(material), serverLevel).map(RecipeHolder::value);
    }

    private boolean hasRoomFor(MineralConverterRecipe recipe) {
        return fits(items.get(SLOT_MONEY), recipe.moneyResult()) && fits(items.get(SLOT_COIN), recipe.coinResult());
    }

    private static boolean fits(ItemStack slot, ItemStack result) {
        return result.isEmpty() || slot.isEmpty()
                || ItemStack.isSameItemSameComponents(slot, result) && slot.getCount() + result.getCount() <= slot.getMaxStackSize();
    }

    /** Converts one unit of the material. Returns false (and changes nothing) when it is not accepted or the output is full. */
    public boolean convert() {
        if (!RevivalConfig.MINERAL_CONVERTER_ENABLED.get()) {
            return false;
        }
        Optional<MineralConverterRecipe> found = currentRecipe();
        if (found.isEmpty() || !hasRoomFor(found.get())) {
            return false;
        }
        MineralConverterRecipe recipe = found.get();
        add(SLOT_MONEY, recipe.moneyResult());
        add(SLOT_COIN, recipe.coinResult());
        items.get(SLOT_INPUT).shrink(1);
        setChanged();
        if (level != null) {
            level.playSound(null, worldPosition, ModSounds.MINERAL_CONVERTER_CONVERT.get(), SoundSource.BLOCKS, 0.8F,
                    0.9F + level.getRandom().nextFloat() * 0.2F);
        }
        return true;
    }

    private void add(int slot, ItemStack result) {
        if (result.isEmpty()) {
            return;
        }
        ItemStack current = items.get(slot);
        if (current.isEmpty()) {
            items.set(slot, result.copy());
        } else {
            current.grow(result.getCount());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.crazyalloy_revival.mineral_converter");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new MineralConverterMenu(containerId, inventory, this, data);
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == SLOT_INPUT && !stack.is(ModItems.MONEY.get()) && !stack.is(ModItems.COIN.get());
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? SLOTS_OUT : SLOTS_IN;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == SLOT_MONEY || slot == SLOT_COIN;
    }
}
