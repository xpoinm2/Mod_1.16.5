package com.example.examplemod.jei.category;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.jei.recipe.ClayMassRecipe;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ClayMassRecipeCategory implements IRecipeCategory<ClayMassRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(ExampleMod.MODID, "clay_mass_crafting");

    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public ClayMassRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(120, 80);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.CLAY_POT.get()));
        this.localizedName = I18n.get("jei.examplemod.category.clay_mass_crafting");
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends ClayMassRecipe> getRecipeClass() {
        return ClayMassRecipe.class;
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
    public void setIngredients(ClayMassRecipe recipe, IIngredients ingredients) {
        // Каждый ингредиент должен быть в отдельном списке
        java.util.List<java.util.List<net.minecraft.item.ItemStack>> inputs = new java.util.ArrayList<>();
        for (net.minecraft.item.ItemStack input : recipe.getInputs()) {
            inputs.add(java.util.Arrays.asList(input));
        }
        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ClayMassRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        // Ингредиенты (3 слота: глина, песок, гравий)
        guiItemStacks.init(0, true, 15, 15); // Глина
        guiItemStacks.init(1, true, 15, 35); // Песок
        guiItemStacks.init(2, true, 35, 25); // Гравий

        // Результат
        guiItemStacks.init(3, false, 85, 25);

        guiItemStacks.set(ingredients);
    }

    @Override
    public void draw(ClayMassRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        // Отображаем информацию о воде
        String waterText = recipe.getWaterRequired() + " " + I18n.get("jei.examplemod.water_required");
        int textX = 60;
        int textY = 55;
        Minecraft.getInstance().font.draw(matrixStack, waterText,
                textX - Minecraft.getInstance().font.width(waterText) / 2, textY, 0xFF404040);
    }
}
