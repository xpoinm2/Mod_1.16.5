package com.example.examplemod.jei.recipe;

import net.minecraft.item.ItemStack;

public class ClayPotRecipe {
    private final ItemStack input;
    private final ItemStack output;

    public ClayPotRecipe(ItemStack input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }
}
