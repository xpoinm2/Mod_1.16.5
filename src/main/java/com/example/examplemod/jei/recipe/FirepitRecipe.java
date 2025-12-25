package com.example.examplemod.jei.recipe;

import net.minecraft.item.ItemStack;

public class FirepitRecipe {
    private final ItemStack input;
    private final ItemStack output;
    private final int processingTime; // Время приготовления в тиках

    public FirepitRecipe(ItemStack input, ItemStack output) {
        this(input, output, 400); // По умолчанию 400 тиков (20 секунд)
    }

    public FirepitRecipe(ItemStack input, ItemStack output, int processingTime) {
        this.input = input;
        this.output = output;
        this.processingTime = processingTime;
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getProcessingTime() {
        return processingTime;
    }
}
