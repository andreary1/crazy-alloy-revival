package com.crazyalloy.revival.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/** The input slot of a Mineral Converter. */
public record MineralConverterInput(ItemStack material) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("No slot " + index);
        }
        return material;
    }

    @Override
    public int size() {
        return 1;
    }
}
