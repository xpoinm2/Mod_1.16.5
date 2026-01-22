package com.example.examplemod.jei.category;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import com.example.examplemod.jei.ExampleModJEIPlugin;
import com.example.examplemod.jei.recipe.CobblestoneAnvilRecipe;
import com.example.examplemod.tileentity.CobblestoneAnvilTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CobblestoneAnvilRecipeCategory implements IRecipeCategory<CobblestoneAnvilRecipe> {
    private static final ResourceLocation[] PROGRESS_FRAMES = new ResourceLocation[CobblestoneAnvilTileEntity.MAX_PROGRESS];

    static {
        for (int i = 0; i < CobblestoneAnvilTileEntity.MAX_PROGRESS; i++) {
            PROGRESS_FRAMES[i] = new ResourceLocation(ExampleMod.MODID,
                    String.format(Locale.ROOT, "textures/gui/cobblestone_anvil_progress/frame_%02d.png", i + 1));
        }
    }

    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public CobblestoneAnvilRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(140, 80);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.COBBLESTONE_ANVIL.get()));
        this.localizedName = I18n.get("jei.examplemod.category.cobblestone_anvil");
    }

    @Override
    public ResourceLocation getUid() {
        return ExampleModJEIPlugin.COBBLESTONE_ANVIL_CATEGORY_UID;
    }

    @Override
    public Class<? extends CobblestoneAnvilRecipe> getRecipeClass() {
        return CobblestoneAnvilRecipe.class;
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
    public void setIngredients(CobblestoneAnvilRecipe recipe, IIngredients ingredients) {
        List<List<ItemStack>> inputs = java.util.Arrays.asList(
                Collections.singletonList(recipe.getMetalInput()),
                recipe.getToolOptions()
        );
        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CobblestoneAnvilRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 15, 25);
        guiItemStacks.init(1, true, 45, 25);
        guiItemStacks.init(2, false, 100, 25);

        guiItemStacks.set(ingredients);
    }

    @Override
    public void draw(CobblestoneAnvilRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        int progressX = 75;
        int progressY = 33;

        long gameTime = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0;
        int frameIndex = (int) (gameTime % PROGRESS_FRAMES.length);

        Minecraft.getInstance().getTextureManager().bind(PROGRESS_FRAMES[frameIndex]);
        Minecraft.getInstance().gui.blit(matrixStack, progressX - 8, progressY - 8, 0, 0, 16, 16, 16, 16);

        String hitsText = recipe.getHitsRequired() + " " + I18n.get("jei.examplemod.hits");
        int textX = 70;
        int textY = 55;
        Minecraft.getInstance().font.draw(matrixStack, hitsText,
                textX - Minecraft.getInstance().font.width(hitsText) / 2, textY, 0xFF404040);
    }
}
