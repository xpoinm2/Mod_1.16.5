package com.example.examplemod.jei.category;

import com.example.examplemod.ModBlocks;
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
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Locale;

public class ClayPotRecipeCategory implements IRecipeCategory<ClayPotRecipe> {
    private static final int WASH_PROGRESS_FRAME_COUNT = 8;
    private static final ResourceLocation[] WASH_PROGRESS_FRAMES = new ResourceLocation[WASH_PROGRESS_FRAME_COUNT];

    static {
        for (int i = 0; i < WASH_PROGRESS_FRAME_COUNT; i++) {
            WASH_PROGRESS_FRAMES[i] = new ResourceLocation("examplemod",
                    String.format(Locale.ROOT, "textures/gui/clay_pot_wash_progress/frame_%02d.png", i + 1));
        }
    }

    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public ClayPotRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(120, 80);
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
        // Отображаем анимированный индикатор прогресса
        int progressX = 50; // Центр между входом и выходом
        int progressY = 35;

        // Анимируем прогресс на основе времени
        long gameTime = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0;
        int frameIndex = (int) ((gameTime / 4) % WASH_PROGRESS_FRAME_COUNT); // Меняем кадр каждые 4 тика

        Minecraft.getInstance().getTextureManager().bind(WASH_PROGRESS_FRAMES[frameIndex]);
        Minecraft.getInstance().gui.blit(matrixStack, progressX - 8, progressY - 8, 0, 0, 16, 16, 16, 16);

        // Отображаем информацию о времени
        String timeText = recipe.getWashClicks() + " " + I18n.get("jei.examplemod.clicks");
        int textX = 60;
        int textY = 55;
        Minecraft.getInstance().font.draw(matrixStack, timeText, textX - Minecraft.getInstance().font.width(timeText) / 2, textY, 0xFF404040);
    }
}
