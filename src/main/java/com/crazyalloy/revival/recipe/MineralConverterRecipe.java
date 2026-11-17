package com.crazyalloy.revival.recipe;

import com.crazyalloy.revival.registry.ModItems;
import com.crazyalloy.revival.registry.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;

/**
 * Mineral Converter recipe: one unit of the material becomes {@code money} Money and {@code coin} Coins.
 *
 * <pre>
 * { "type": "crazyalloy_revival:mineral_converter", "material": "minecraft:iron_ingot", "money": 2, "coin": 2 }
 * </pre>
 */
public class MineralConverterRecipe implements Recipe<MineralConverterInput> {
    public static final MapCodec<MineralConverterRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("material").forGetter(r -> r.material),
            Codec.intRange(0, 64).optionalFieldOf("money", 0).forGetter(r -> r.money),
            Codec.intRange(0, 64).optionalFieldOf("coin", 0).forGetter(r -> r.coin)
    ).apply(inst, MineralConverterRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, MineralConverterRecipe> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    private final Ingredient material;
    private final int money;
    private final int coin;

    public MineralConverterRecipe(Ingredient material, int money, int coin) {
        this.material = material;
        this.money = money;
        this.coin = coin;
    }

    @Override
    public boolean matches(MineralConverterInput input, Level level) {
        return material.test(input.material());
    }

    /** The Money part of the result (the Coins are {@link #coinResult()}). */
    @Override
    public ItemStack assemble(MineralConverterInput input) {
        return moneyResult();
    }

    public ItemStack moneyResult() {
        return money > 0 ? new ItemStack(ModItems.MONEY.get(), money) : ItemStack.EMPTY;
    }

    public ItemStack coinResult() {
        return coin > 0 ? new ItemStack(ModItems.COIN.get(), coin) : ItemStack.EMPTY;
    }

    public int money() {
        return money;
    }

    public int coin() {
        return coin;
    }

    @Override
    public RecipeSerializer<MineralConverterRecipe> getSerializer() {
        return ModRecipes.MINERAL_CONVERTER_SERIALIZER.get();
    }

    @Override
    public RecipeType<MineralConverterRecipe> getType() {
        return ModRecipes.MINERAL_CONVERTER_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipes.MINERAL_CONVERTER_CATEGORY.get();
    }
}
