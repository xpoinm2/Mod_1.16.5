package com.example.examplemod.jei.category;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.jei.ExampleModJEIPlugin;
import com.example.examplemod.jei.recipe.SlabDryingRecipe;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SlabDryingRecipeCategory implements IRecipeCategory<SlabDryingRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public SlabDryingRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(120, 80);
        // Используем дубовую плиту как иконку
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.OAK_SLAB.get()));
        this.localizedName = I18n.get("jei.examplemod.category.slab_drying");
    }

    @Override
    public ResourceLocation getUid() {
        return ExampleModJEIPlugin.SLAB_DRYING_CATEGORY_UID;
    }

    @Override
    public Class<? extends SlabDryingRecipe> getRecipeClass() {
        return SlabDryingRecipe.class;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(SlabDryingRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(java.util.Collections.singletonList(Ingredient.of(recipe.getInput())));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SlabDryingRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 15, 21);
        guiItemStacks.init(1, false, 85, 21);

        guiItemStacks.set(ingredients);
    }

    @Override
    public void draw(SlabDryingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        // Отображаем информацию о времени сушки в секундах
        int seconds = recipe.getDryingTime() / 20; // Конвертируем тики в секунды
        String timeText = seconds + " " + I18n.get("jei.examplemod.seconds");
        int textX = 60;
        int textY = 55;
        Minecraft.getInstance().font.draw(matrixStack, timeText, textX - Minecraft.getInstance().font.width(timeText) / 2, textY, 0xFF404040);
    }
}

