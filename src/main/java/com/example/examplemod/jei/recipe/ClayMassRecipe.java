package com.example.examplemod.jei.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Arrays;
import java.util.List;

public class ClayMassRecipe {
    private final List<ItemStack> inputs;
    private final ItemStack output;
    private final int waterRequired; // мл воды

    public ClayMassRecipe(List<ItemStack> inputs, ItemStack output, int waterRequired) {
        this.inputs = inputs;
        this.output = output;
        this.waterRequired = waterRequired;
    }

    public static ClayMassRecipe create() {
        return new ClayMassRecipe(
                Arrays.asList(
                        new ItemStack(Items.CLAY_BALL, 4),
                        new ItemStack(com.example.examplemod.ModItems.HANDFUL_OF_SAND.get(), 1),
                        new ItemStack(Items.GRAVEL, 1)
                ),
                new ItemStack(com.example.examplemod.ModItems.CLAY_MASS.get(), 1),
                500
        );
    }

    public List<ItemStack> getInputs() {
        return inputs;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getWaterRequired() {
        return waterRequired;
    }
}
