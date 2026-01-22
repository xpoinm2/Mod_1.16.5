package com.example.examplemod.jei.recipe;

import net.minecraft.item.ItemStack;

import java.util.List;

public class CobblestoneAnvilRecipe {
    private final ItemStack metalInput;
    private final ItemStack output;
    private final List<ItemStack> toolOptions;
    private final int hitsRequired;

    public CobblestoneAnvilRecipe(ItemStack metalInput, ItemStack output, List<ItemStack> toolOptions, int hitsRequired) {
        this.metalInput = metalInput;
        this.output = output;
        this.toolOptions = toolOptions;
        this.hitsRequired = hitsRequired;
    }

    public ItemStack getMetalInput() {
        return metalInput;
    }

    public ItemStack getOutput() {
        return output;
    }

    public List<ItemStack> getToolOptions() {
        return toolOptions;
    }

    public int getHitsRequired() {
        return hitsRequired;
    }
}