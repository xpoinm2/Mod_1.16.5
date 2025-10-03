package com.example.examplemod.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;

import javax.annotation.Nullable;

public class BranchItem extends Item {
    private static final int BURN_TIME_TICKS = 50;

    public BranchItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable IRecipeType<?> recipeType) {
        return BURN_TIME_TICKS;
    }
}