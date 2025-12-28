package com.example.examplemod.jei.recipe;

import net.minecraft.item.ItemStack;

public class SlabDryingRecipe {
    private final ItemStack input;
    private final ItemStack output;
    private final int dryingTime; // Время сушки в тиках

    public SlabDryingRecipe(ItemStack input, ItemStack output, int dryingTime) {
        this.input = input;
        this.output = output;
        this.dryingTime = dryingTime;
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getDryingTime() {
        return dryingTime;
    }
}

