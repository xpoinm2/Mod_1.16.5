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
     * Генерирует рецепты крафта для всех типов полублоков
     * МАСШТАБИРУЕМО: добавьте новый тип дерева - рецепт создастся автоматически!
     */
    private void generateSlabRecipes(Consumer<IFinishedRecipe> consumer) {
        // Деревянные полублоки
        createSlabRecipe(consumer, "oak", Items.OAK_PLANKS, "minecraft:oak_planks");
        createSlabRecipe(consumer, "spruce", Items.SPRUCE_PLANKS, "minecraft:spruce_planks");
        createSlabRecipe(consumer, "birch", Items.BIRCH_PLANKS, "minecraft:birch_planks");
        createSlabRecipe(consumer, "jungle", Items.JUNGLE_PLANKS, "minecraft:jungle_planks");
        createSlabRecipe(consumer, "acacia", Items.ACACIA_PLANKS, "minecraft:acacia_planks");
        createSlabRecipe(consumer, "dark_oak", Items.DARK_OAK_PLANKS, "minecraft:dark_oak_planks");
        createSlabRecipe(consumer, "crimson", Items.CRIMSON_PLANKS, "minecraft:crimson_planks");
        createSlabRecipe(consumer, "warped", Items.WARPED_PLANKS, "minecraft:warped_planks");
        
        // Полублок хвороста
        createSlabRecipe(consumer, "brushwood", Items.STICK, "minecraft:stick");
    }

    /**
     * Создаёт рецепт для полублока
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
            criterionItem
        ));
    }

    /**
     * Кастомный рецепт для полублоков
     * Решает проблему с модовыми предметами в DataGen!
     */
    private static class CustomSlabRecipe implements IFinishedRecipe {
        private final ResourceLocation id;
        private final ResourceLocation result;
        private final String plankItem;
        private final Item criterionItem;

        public CustomSlabRecipe(ResourceLocation id, ResourceLocation result, 
                                String plankItem, Item criterionItem) {
            this.id = id;
            this.result = result;
            this.plankItem = plankItem;
            this.criterionItem = criterionItem;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("type", "minecraft:crafting_shapeless");
            
            // Ингредиенты
            JsonArray ingredients = new JsonArray();
            
            JsonObject plank = new JsonObject();
            plank.addProperty("item", plankItem);
            ingredients.add(plank);
            
            JsonObject axe = new JsonObject();
            axe.addProperty("tag", "forge:tools/axes");
            ingredients.add(axe);
            
            json.add("ingredients", ingredients);
            
            // Результат
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
