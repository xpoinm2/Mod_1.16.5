package com.example.examplemod.datagen;

import com.example.examplemod.ExampleMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

/**
 * Генератор рецептов для мода
 */
public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        // Генерация рецептов для всех типов полублоков
        generateSlabRecipes(consumer);
    }

    /**
     * Генерирует рецепты крафта для всех типов полублоков
     */
    private void generateSlabRecipes(Consumer<IFinishedRecipe> consumer) {
        // Список всех типов досок
        String[] woodTypes = {
            "oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "crimson", "warped"
        };

        for (String woodType : woodTypes) {
            String plankItem = "minecraft:" + woodType + "_planks";
            String slabResult = ExampleMod.MODID + ":" + woodType + "_slab";

            // Рецепт: доски + топор = 2 полублока
            ShapelessRecipeBuilder.shapeless(Items.OAK_PLANKS, 2) // Пока используем oak planks как результат
                    .requires(Items.OAK_PLANKS)
                    .requires(Items.IRON_AXE) // Используем конкретный предмет вместо тега
                    .unlockedBy("has_planks", has(Items.OAK_PLANKS))
                    .save(consumer, ExampleMod.MODID + ":" + woodType + "_slab");
        }

        // Каменный полублок
        ShapelessRecipeBuilder.shapeless(Items.COBBLESTONE, 2) // Пока используем cobblestone как результат
                .requires(Items.COBBLESTONE)
                .requires(Items.IRON_PICKAXE) // Используем конкретный предмет
                .unlockedBy("has_cobblestone", has(Items.COBBLESTONE))
                .save(consumer, ExampleMod.MODID + ":stone_slab");

        // Полублок хвороста
        ShapelessRecipeBuilder.shapeless(Items.STICK, 2) // Пока используем stick как результат
                .requires(Items.STICK)
                .requires(Items.IRON_AXE) // Используем конкретный предмет
                .unlockedBy("has_stick", has(Items.STICK))
                .save(consumer, ExampleMod.MODID + ":brushwood_slab");
    }
}
