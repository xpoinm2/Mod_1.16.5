package com.example.examplemod.datagen;

import com.example.examplemod.ExampleMod;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Генератор рецептов для мода
 * ОПТИМИЗИРОВАН ДЛЯ БОЛЬШИХ ПРОЕКТОВ!
 */
public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        // Генерация рецептов для всех типов полублоков
        generateSlabRecipes(consumer);
        
        // Здесь можно добавить другие массовые генерации
        // generateToolRecipes(consumer);
        // generateArmorRecipes(consumer);
        // и т.д.
    }

    /**
     * Генерирует рецепты крафта для всех типов полублоков (плитняков)
     * МАСШТАБИРУЕМО: добавьте новый тип дерева - рецепт создастся автоматически!
     */
    private void generateSlabRecipes(Consumer<IFinishedRecipe> consumer) {
        // Деревянные плитняки (доски + топор = 2 плитняка)
        // Используем теги minecraft:axes (не forge:tools/axes) для Forge 1.16.5
        createSlabRecipe(consumer, "oak", Items.OAK_PLANKS, "minecraft:oak_planks");
        createSlabRecipe(consumer, "spruce", Items.SPRUCE_PLANKS, "minecraft:spruce_planks");
        createSlabRecipe(consumer, "birch", Items.BIRCH_PLANKS, "minecraft:birch_planks");
        createSlabRecipe(consumer, "jungle", Items.JUNGLE_PLANKS, "minecraft:jungle_planks");
        createSlabRecipe(consumer, "acacia", Items.ACACIA_PLANKS, "minecraft:acacia_planks");
        createSlabRecipe(consumer, "dark_oak", Items.DARK_OAK_PLANKS, "minecraft:dark_oak_planks");
        createSlabRecipe(consumer, "crimson", Items.CRIMSON_PLANKS, "minecraft:crimson_planks");
        createSlabRecipe(consumer, "warped", Items.WARPED_PLANKS, "minecraft:warped_planks");
        
        
        // Плитняк из булыжника (булыжник + топор = 2 плитняка из булыжника)
        createSlabRecipe(consumer, "cobblestone", Items.COBBLESTONE, "minecraft:cobblestone");
    }

    /**
     * Создаёт рецепт для полублока (доски/палки + топор)
     * 
     * @param consumer Consumer для рецептов
     * @param woodType Тип дерева (oak, birch, etc.)
     * @param criterionItem Предмет для advancement
     * @param plankItem ResourceLocation досок
     */
    private void createSlabRecipe(Consumer<IFinishedRecipe> consumer, String woodType, 
                                   Item criterionItem, String plankItem) {
        consumer.accept(new CustomSlabRecipe(
            new ResourceLocation(ExampleMod.MODID, woodType + "_slab_from_axe"),
            new ResourceLocation(ExampleMod.MODID, woodType + "_slab"),
            plankItem,
            "minecraft:axes",  // Исправлено: используем minecraft:axes вместо forge:tools/axes
            criterionItem
        ));
    }

    /**
     * Создаёт рецепт для полублока из модового предмета
     * 
     * @param consumer Consumer для рецептов
     * @param slabName Имя плитняка
     * @param sourceItem Исходный предмет (из мода)
     * @param criterionItem Предмет для advancement
     */
    private void createSlabRecipeFromModItem(Consumer<IFinishedRecipe> consumer, String slabName,
                                              String sourceItem, Item criterionItem) {
        consumer.accept(new CustomSlabRecipe(
            new ResourceLocation(ExampleMod.MODID, slabName + "_from_axe"),
            new ResourceLocation(ExampleMod.MODID, slabName),
            sourceItem,
            "minecraft:axes",  // Исправлено: используем minecraft:axes вместо forge:tools/axes
            criterionItem
        ));
    }

    /**
     * Создаёт рецепт для каменного плитняка (булыжник + кирка)
     * 
     * @param consumer Consumer для рецептов
     * @param slabName Имя плитняка
     * @param criterionItem Предмет для advancement
     * @param sourceItem Исходный предмет
     */
    private void createStoneSlabRecipe(Consumer<IFinishedRecipe> consumer, String slabName,
                                        Item criterionItem, String sourceItem) {
        consumer.accept(new CustomSlabRecipe(
            new ResourceLocation(ExampleMod.MODID, slabName + "_slab_from_pickaxe"),
            new ResourceLocation(ExampleMod.MODID, slabName + "_slab"),
            sourceItem,
            "minecraft:pickaxes",  // Исправлено: используем minecraft:pickaxes вместо forge:tools/pickaxes
            criterionItem
        ));
    }

    /**
     * Кастомный рецепт для плитняков
     * Решает проблему с модовыми предметами в DataGen!
     * Поддерживает разные инструменты (топоры, кирки)
     */
    private static class CustomSlabRecipe implements IFinishedRecipe {
        private final ResourceLocation id;
        private final ResourceLocation result;
        private final String sourceItem;
        private final String toolTag;
        private final Item criterionItem;

        public CustomSlabRecipe(ResourceLocation id, ResourceLocation result, 
                                String sourceItem, String toolTag, Item criterionItem) {
            this.id = id;
            this.result = result;
            this.sourceItem = sourceItem;
            this.toolTag = toolTag;
            this.criterionItem = criterionItem;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("type", "minecraft:crafting_shapeless");
            
            // Ингредиенты
            JsonArray ingredients = new JsonArray();
            
            // Исходный предмет (доски, палки, булыжник и т.д.)
            JsonObject source = new JsonObject();
            source.addProperty("item", sourceItem);
            ingredients.add(source);
            
            // Инструмент (топор, кирка и т.д.)
            // В Forge 1.16.5 используются теги minecraft:axes и minecraft:pickaxes
            JsonObject tool = new JsonObject();
            tool.addProperty("tag", toolTag);
            ingredients.add(tool);
            
            json.add("ingredients", ingredients);
            
            // Результат (2 плитняка)
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", result.toString());
            resultObj.addProperty("count", 2);
            json.add("result", resultObj);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public IRecipeSerializer<?> getType() {
            return IRecipeSerializer.SHAPELESS_RECIPE;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            JsonObject advancement = new JsonObject();
            
            JsonObject criteria = new JsonObject();
            JsonObject hasItem = new JsonObject();
            hasItem.addProperty("trigger", "minecraft:inventory_changed");
            
            JsonObject conditions = new JsonObject();
            JsonArray items = new JsonArray();
            JsonObject item = new JsonObject();
            item.addProperty("item", criterionItem.getRegistryName().toString());
            items.add(item);
            conditions.add("items", items);
            
            hasItem.add("conditions", conditions);
            criteria.add("has_planks", hasItem);
            advancement.add("criteria", criteria);
            
            JsonArray requirements = new JsonArray();
            JsonArray req = new JsonArray();
            req.add("has_planks");
            requirements.add(req);
            advancement.add("requirements", requirements);
            
            return advancement;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath());
        }
    }
}
