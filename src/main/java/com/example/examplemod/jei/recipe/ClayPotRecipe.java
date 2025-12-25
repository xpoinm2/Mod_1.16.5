package com.example.examplemod.jei.recipe;

import net.minecraft.item.ItemStack;

public class ClayPotRecipe {
    private final ItemStack input;
    private final ItemStack output;
    private final int washClicks; // Количество кликов для промывки

    public ClayPotRecipe(ItemStack input, ItemStack output) {
        this(input, output, 8); // По умолчанию 8 кликов для промывки
    }

    public ClayPotRecipe(ItemStack input, ItemStack output, int washClicks) {
        this.input = input;
        this.output = output;
        this.washClicks = washClicks;
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getWashClicks() {
        return washClicks;
    }

    public int getProcessingTime() {
        return washClicks; // Для глиняного горшка время = количеству кликов
    }
}
