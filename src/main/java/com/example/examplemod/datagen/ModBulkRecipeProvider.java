package com.example.examplemod.datagen;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModRegistries;
import net.minecraft.advancements.criterion.ItemPredicate;
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
        
        // Полублоки из досок с топором
        generateSlabRecipes(consumer);
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
    
    /**
     * Генерация рецептов полублоков: топор + доски = 2 плитняка.
     * 
     * МАСШТАБИРУЕМОСТЬ: При добавлении нового типа дерева просто добавь его в массив.
     * 
     * ИСПРАВЛЕНО: Использует прямое создание JSON для правильной работы во время DataGen.
     */
    private void generateSlabRecipes(Consumer<IFinishedRecipe> consumer) {
        // Массив: [тип_дерева, ванильный_предмет_досок]
        SlabRecipe[] woodTypes = {
            new SlabRecipe("oak", Items.OAK_PLANKS),
            new SlabRecipe("birch", Items.BIRCH_PLANKS),
            new SlabRecipe("spruce", Items.SPRUCE_PLANKS),
            new SlabRecipe("jungle", Items.JUNGLE_PLANKS),
            new SlabRecipe("acacia", Items.ACACIA_PLANKS),
            new SlabRecipe("dark_oak", Items.DARK_OAK_PLANKS),
            new SlabRecipe("crimson", Items.CRIMSON_PLANKS),
            new SlabRecipe("warped", Items.WARPED_PLANKS),
        };
        
        for (SlabRecipe recipe : woodTypes) {
            // Топор + доски = 2 плитняка
            shapelessRecipeWithModResult(
                consumer,
                recipe.woodType + "_slab",  // результат
                2,  // количество
                new Ingredient[]{
                    Ingredient.of(recipe.planksItem),
                    Ingredient.of(ItemTags.bind("forge:tools/axes"))
                },
                "has_" + recipe.woodType + "_planks",
                recipe.planksItem,
                recipe.woodType + "_slab_from_axe"  // ID рецепта
            );
        }
        
        // Специальный рецепт для хвороста
        shapelessRecipeWithModResult(
            consumer,
            "brushwood_slab",  // результат
            2,  // количество
            new Ingredient[]{
                Ingredient.fromJson(createItemJson("examplemod:bunch_of_grass")),
                Ingredient.of(ItemTags.bind("forge:tools/axes"))
            },
            "has_bunch_of_grass",
            Items.DIRT,  // Placeholder для критерия
            "brushwood_slab_from_axe"  // ID рецепта
        );
    }
    
    /**
     * Создаёт shapeless рецепт с результатом из мода (через ResourceLocation).
     * Решает проблему DataGen когда предметы ещё не зарегистрированы.
     */
    private void shapelessRecipeWithModResult(
        Consumer<IFinishedRecipe> consumer,
        String resultItemName,
        int count,
        Ingredient[] ingredients,
        String criterionName,
        Item criterionItem,
        String recipeId
    ) {
        consumer.accept(new IFinishedRecipe() {
            @Override
            public void serializeRecipeData(com.google.gson.JsonObject json) {
                json.addProperty("type", "minecraft:crafting_shapeless");
                
                // Ingredients
                com.google.gson.JsonArray ingredientsArray = new com.google.gson.JsonArray();
                for (Ingredient ingredient : ingredients) {
                    ingredientsArray.add(ingredient.toJson());
                }
                json.add("ingredients", ingredientsArray);
                
                // Result
                com.google.gson.JsonObject resultJson = new com.google.gson.JsonObject();
                resultJson.addProperty("item", ExampleMod.MODID + ":" + resultItemName);
                resultJson.addProperty("count", count);
                json.add("result", resultJson);
            }
            
            @Override
            public ResourceLocation getId() {
                return modLoc(recipeId);
            }
            
            @Override
            public net.minecraft.item.crafting.IRecipeSerializer<?> getType() {
                return net.minecraft.item.crafting.IRecipeSerializer.SHAPELESS_RECIPE;
            }
            
            @Override
            public com.google.gson.JsonObject serializeAdvancement() {
                com.google.gson.JsonObject advancement = new com.google.gson.JsonObject();
                com.google.gson.JsonObject criteriaObj = new com.google.gson.JsonObject();
                
                // Создаём критерий вручную
                com.google.gson.JsonObject criterion = new com.google.gson.JsonObject();
                criterion.addProperty("trigger", "minecraft:inventory_changed");
                com.google.gson.JsonObject conditions = new com.google.gson.JsonObject();
                com.google.gson.JsonArray items = new com.google.gson.JsonArray();
                com.google.gson.JsonObject itemObj = new com.google.gson.JsonObject();
                itemObj.addProperty("item", criterionItem.getRegistryName().toString());
                items.add(itemObj);
                conditions.add("items", items);
                criterion.add("conditions", conditions);
                
                criteriaObj.add(criterionName, criterion);
                advancement.add("criteria", criteriaObj);
                
                com.google.gson.JsonArray reqArray = new com.google.gson.JsonArray();
                com.google.gson.JsonArray innerArray = new com.google.gson.JsonArray();
                innerArray.add(criterionName);
                reqArray.add(innerArray);
                advancement.add("requirements", reqArray);
                
                return advancement;
            }
            
            @Override
            public ResourceLocation getAdvancementId() {
                return new ResourceLocation(getId().getNamespace(), "recipes/" + getId().getPath());
            }
        });
    }
    
    /**
     * Класс для описания рецепта полублока.
     */
    private static class SlabRecipe {
        final String woodType;
        final Item planksItem;
        
        SlabRecipe(String woodType, Item planksItem) {
            this.woodType = woodType;
            this.planksItem = planksItem;
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
     * 
     * ВНИМАНИЕ: Этот метод не работает во время DataGen!
     * Используй CustomResult.builder() для рецептов с модовыми предметами.
     */
    @Deprecated
    private Item modItem(String name) {
        // Этот метод не работает во время DataGen, так как предметы ещё не зарегистрированы
        return ModRegistries.ITEMS.getEntries().stream()
            .filter(entry -> entry.getId().getPath().equals(name))
            .findFirst()
            .map(entry -> entry.get())
            .orElse(Items.AIR);
    }
    
    /**
     * Создать ResourceLocation для мода.
     */
    private ResourceLocation modLoc(String name) {
        return new ResourceLocation(ExampleMod.MODID, name);
    }
    
    /**
     * Создать JsonObject для предмета по ResourceLocation.
     * Используется для создания Ingredient через JSON.
     */
    private static com.google.gson.JsonObject createItemJson(String itemId) {
        com.google.gson.JsonObject json = new com.google.gson.JsonObject();
        json.addProperty("item", itemId);
        return json;
    }
    
    // === ВСПОМОГАТЕЛЬНЫЙ КЛАСС ДЛЯ DATAGEN ===
    
    /**
     * Вспомогательный класс для создания shapeless рецептов с результатом через ResourceLocation.
     * Решает проблему, когда предметы ещё не зарегистрированы во время DataGen.
     */
    private static class CustomResult {
        private final ResourceLocation result;
        private final int count;
        private final java.util.List<Ingredient> ingredients = new java.util.ArrayList<>();
        private final java.util.Map<String, net.minecraft.advancements.criterion.InventoryChangeTrigger.Instance> criteria = new java.util.HashMap<>();
        
        private CustomResult(ResourceLocation result, int count) {
            this.result = result;
            this.count = count;
        }
        
        public static CustomResult builder(ResourceLocation result, int count) {
            return new CustomResult(result, count);
        }
        
        public CustomResult addIngredient(Ingredient ingredient) {
            this.ingredients.add(ingredient);
            return this;
        }
        
        public CustomResult addCriterion(String name, net.minecraft.advancements.criterion.InventoryChangeTrigger.Instance criterion) {
            this.criteria.put(name, criterion);
            return this;
        }
        
        public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
            // Создаем ShapelessRecipeBuilder с временным предметом (Items.BARRIER)
            // а затем переопределяем результат через JSON
            ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(Items.BARRIER, count);
            
            for (Ingredient ingredient : ingredients) {
                builder.requires(ingredient);
            }
            
            for (java.util.Map.Entry<String, net.minecraft.advancements.criterion.InventoryChangeTrigger.Instance> entry : criteria.entrySet()) {
                builder.unlockedBy(entry.getKey(), entry.getValue());
            }
            
            // Сохраняем рецепт с кастомным результатом
            builder.save(new Consumer<IFinishedRecipe>() {
                @Override
                public void accept(IFinishedRecipe finishedRecipe) {
                    consumer.accept(new IFinishedRecipe() {
                        @Override
                        public void serializeRecipeData(com.google.gson.JsonObject json) {
                            finishedRecipe.serializeRecipeData(json);
                            // Переопределяем результат
                            com.google.gson.JsonObject resultJson = new com.google.gson.JsonObject();
                            resultJson.addProperty("item", result.toString());
                            resultJson.addProperty("count", count);
                            json.add("result", resultJson);
                        }
                        
                        @Override
                        public ResourceLocation getId() {
                            return id;
                        }
                        
                        @Override
                        public net.minecraft.item.crafting.IRecipeSerializer<?> getType() {
                            return finishedRecipe.getType();
                        }
                        
                        @Override
                        public com.google.gson.JsonObject serializeAdvancement() {
                            return finishedRecipe.serializeAdvancement();
                        }
                        
                        @Override
                        public ResourceLocation getAdvancementId() {
                            return finishedRecipe.getAdvancementId();
                        }
                    });
                }
            }, id);
        }
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

