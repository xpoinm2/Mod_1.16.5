package com.example.examplemod.datagen;

import com.example.examplemod.ExampleMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

/**
 * Генератор массовых рецептов для SmartItemRegistry.
 * 
 * МАСШТАБИРУЕМОСТЬ:
 * При добавлении группы предметов через SmartItemRegistry,
 * рецепты для всей группы генерируются автоматически.
 * 
 * ПРИМЕРЫ:
 * - 20 типов руд → 1 метод генерирует все рецепты
 * - 10 типов слитков → 1 метод генерирует все рецепты
 * - 5 наборов инструментов → 1 метод генерирует все рецепты
 */
public class ModBulkRecipeProvider extends RecipeProvider {

    public ModBulkRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        // === МАССОВАЯ ГЕНЕРАЦИЯ РЕЦЕПТОВ ===
        
        // Металлургические процессы (руды → слитки → пластины)
        generateMetallurgyRecipes(consumer);
        
        // Наборы инструментов (камень, кость, бронза, железо...)
        generateToolsetRecipes(consumer);
        
        // Блоки хранения (9 слитков = блок)
        generateStorageBlockRecipes(consumer);
        
        // Реверсивные рецепты (блок → 9 слитков)
        generateReverseStorageRecipes(consumer);
    }

    /**
     * ПРИМЕР: Металлургические рецепты для всех металлов.
     * 
     * Когда добавишь SmartItemRegistry.variants("copper", "tin", "bronze", "steel"),
     * этот метод автоматически создаст рецепты для всех вариантов.
     */
    private void generateMetallurgyRecipes(Consumer<IFinishedRecipe> consumer) {
        // Список всех металлов в моде (расширяй по мере добавления)
        String[] metals = {
            // Базовые ванильные
            "iron",
            "gold",
            
            // Модовые металлы (добавь когда появятся)
            "tin"
            // "copper", "bronze", "steel", "aluminum", etc.
        };
        
        for (String metal : metals) {
            // Пропускаем если предмет не существует (еще не добавлен)
            // Uncomment когда начнёшь добавлять эти предметы:
            
            // Обжиг руды: roasted_ore → sponge_metal
            // smeltingRecipe(
            //     modItem("roasted_" + metal + "_ore"),
            //     modItem("sponge_" + metal),
            //     0.7f, 200, consumer,
            //     metal + "_sponge_from_roasting"
            // );
            
            // Ковка: sponge_metal + hammer → ingot
            // ShapelessRecipeBuilder.shapeless(modItem(metal + "_ingot"))
            //     .requires(modItem("sponge_" + metal))
            //     .requires(Tags.Items.TOOLS) // Любой молот
            //     .unlockedBy("has_sponge", has(modItem("sponge_" + metal)))
            //     .save(consumer, modLoc(metal + "_ingot_from_sponge"));
            
            // Слиток → пластина (если добавишь пластины)
            // ShapelessRecipeBuilder.shapeless(modItem(metal + "_plate"))
            //     .requires(modItem(metal + "_ingot"))
            //     .requires(Tags.Items.TOOLS)
            //     .unlockedBy("has_ingot", has(modItem(metal + "_ingot")))
            //     .save(consumer, modLoc(metal + "_plate_from_ingot"));
        }
    }

    /**
     * ПРИМЕР: Генерация рецептов для наборов инструментов.
     * 
     * Для каждого материала (камень, кость, бронза...) создаёт рецепты
     * для всех 5 инструментов (кирка, топор, лопата, мотыга, меч).
     */
    private void generateToolsetRecipes(Consumer<IFinishedRecipe> consumer) {
        // Список материалов для инструментов (расширяй по мере добавления)
        ToolMaterial[] materials = {
            // new ToolMaterial("copper", "copper_ingot"),
            // new ToolMaterial("bronze", "bronze_ingot"),
            // new ToolMaterial("steel", "steel_ingot"),
        };
        
        for (ToolMaterial material : materials) {
            // Кирка
            ShapedRecipeBuilder.shaped(modItem(material.name + "_pickaxe"))
                .pattern("XXX")
                .pattern(" S ")
                .pattern(" S ")
                .define('X', modItem(material.ingredient))
                .define('S', Items.STICK)
                .unlockedBy("has_" + material.ingredient, has(modItem(material.ingredient)))
                .save(consumer);
            
            // Топор
            ShapedRecipeBuilder.shaped(modItem(material.name + "_axe"))
                .pattern("XX ")
                .pattern("XS ")
                .pattern(" S ")
                .define('X', modItem(material.ingredient))
                .define('S', Items.STICK)
                .unlockedBy("has_" + material.ingredient, has(modItem(material.ingredient)))
                .save(consumer);
            
            // Лопата
            ShapedRecipeBuilder.shaped(modItem(material.name + "_shovel"))
                .pattern(" X ")
                .pattern(" S ")
                .pattern(" S ")
                .define('X', modItem(material.ingredient))
                .define('S', Items.STICK)
                .unlockedBy("has_" + material.ingredient, has(modItem(material.ingredient)))
                .save(consumer);
            
            // Мотыга
            ShapedRecipeBuilder.shaped(modItem(material.name + "_hoe"))
                .pattern("XX ")
                .pattern(" S ")
                .pattern(" S ")
                .define('X', modItem(material.ingredient))
                .define('S', Items.STICK)
                .unlockedBy("has_" + material.ingredient, has(modItem(material.ingredient)))
                .save(consumer);
            
            // Меч
            ShapedRecipeBuilder.shaped(modItem(material.name + "_sword"))
                .pattern(" X ")
                .pattern(" X ")
                .pattern(" S ")
                .define('X', modItem(material.ingredient))
                .define('S', Items.STICK)
                .unlockedBy("has_" + material.ingredient, has(modItem(material.ingredient)))
                .save(consumer);
        }
    }

    /**
     * ПРИМЕР: Блоки хранения (9 предметов → 1 блок).
     */
    private void generateStorageBlockRecipes(Consumer<IFinishedRecipe> consumer) {
        String[] storageItems = {
            // "copper", "tin", "bronze", "steel"
        };
        
        for (String item : storageItems) {
            // 9 слитков → 1 блок
            // ShapedRecipeBuilder.shaped(modItem(item + "_block"))
            //     .pattern("XXX")
            //     .pattern("XXX")
            //     .pattern("XXX")
            //     .define('X', modItem(item + "_ingot"))
            //     .unlockedBy("has_" + item + "_ingot", has(modItem(item + "_ingot")))
            //     .save(consumer);
        }
    }

    /**
     * ПРИМЕР: Реверсивные рецепты (1 блок → 9 предметов).
     */
    private void generateReverseStorageRecipes(Consumer<IFinishedRecipe> consumer) {
        String[] storageItems = {
            // "copper", "tin", "bronze", "steel"
        };
        
        for (String item : storageItems) {
            // 1 блок → 9 слитков
            // ShapelessRecipeBuilder.shapeless(modItem(item + "_ingot"), 9)
            //     .requires(modItem(item + "_block"))
            //     .unlockedBy("has_" + item + "_block", has(modItem(item + "_block")))
            //     .save(consumer, modLoc(item + "_ingot_from_block"));
        }
    }

    // === ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ И МЕТОДЫ ===
    
    /**
     * Класс для описания материала инструментов.
     */
    private static class ToolMaterial {
        final String name;
        final String ingredient;
        
        ToolMaterial(String name, String ingredient) {
            this.name = name;
            this.ingredient = ingredient;
        }
    }
    
    /**
     * Получить Item из ModItems по имени.
     * Предполагается что предмет существует.
     */
    private Item modItem(String name) {
        // Временная заглушка - возвращаем воздух если предмет не найден
        // В реальном коде используй ModItems.ITEMS.getEntries() для поиска
        return Items.AIR;
    }
    
    /**
     * Создать ResourceLocation для мода.
     */
    private ResourceLocation modLoc(String name) {
        return new ResourceLocation(ExampleMod.MODID, name);
    }
    
    // === ИНСТРУКЦИИ ПО ИСПОЛЬЗОВАНИЮ ===
    
    /*
     * КАК ДОБАВИТЬ МАССОВЫЕ РЕЦЕПТЫ ДЛЯ НОВОЙ ГРУППЫ ПРЕДМЕТОВ:
     * 
     * 1. Добавь предметы через SmartItemRegistry:
     *    SmartItemRegistry.variants("copper", "tin", "bronze", "steel")
     *                     .category("ingot")
     *                     .properties(new Item.Properties().tab(TAB))
     *                     .registerAll();
     * 
     * 2. Раскомментируй соответствующий метод выше (например, generateMetallurgyRecipes)
     * 
     * 3. Добавь названия металлов в массив metals[]
     * 
     * 4. Запусти генерацию: gradlew runData
     * 
     * 5. Готово! Рецепты для всех металлов сгенерированы автоматически.
     * 
     * ===
     * 
     * ПРЕИМУЩЕСТВА:
     * - Добавление 10 металлов = 1 строка в массив
     * - Изменение рецепта = изменить 1 место (применится ко всем)
     * - Нет копипасты JSON файлов
     * - Легко поддерживать при 100+ предметах
     */
}

