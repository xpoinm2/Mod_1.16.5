package com.example.examplemod.jei.category;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import com.example.examplemod.jei.ExampleModJEIPlugin;
import com.example.examplemod.jei.recipe.ClayPotRecipe;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ClayPotRecipeCategory implements IRecipeCategory<ClayPotRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public ClayPotRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(120, 60);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.CLAY_POT.get()));
        this.localizedName = I18n.get("jei.examplemod.category.clay_pot_washing");
    }

    @Override
    public ResourceLocation getUid() {
        return ExampleModJEIPlugin.CLAY_POT_CATEGORY_UID;
    }

    @Override
    public Class<? extends ClayPotRecipe> getRecipeClass() {
        return ClayPotRecipe.class;
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
    public void setIngredients(ClayPotRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(java.util.Collections.singletonList(Ingredient.of(recipe.getInput())));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ClayPotRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 15, 21);
        guiItemStacks.init(1, false, 85, 21);

        guiItemStacks.set(ingredients);
    }

    @Override
    public void draw(ClayPotRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        // Можно добавить дополнительную отрисовку если нужно
    }
}
