package com.example.examplemod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.ModList;

/** Utility methods for rendering quest items with tooltips and JEI integration. */
public final class GuiUtil {
    private GuiUtil() {}

    /**
     * Renders an item stack and shows its tooltip when the mouse is over it.
     *
     * @return {@code true} if the stack is under the mouse cursor.
     */
    public static boolean renderItemWithTooltip(Screen screen, MatrixStack ms, ItemStack stack,
                                                int x, int y, int mouseX, int mouseY) {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
        if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
            // directly to Screen#renderComponentTooltip, which handles rendering a list
            // of lines. Attempting to cast the list to a single ITextComponent causes
            // a ClassCastException, so use the proper overload instead.
            screen.renderComponentTooltip(ms, screen.getTooltipFromItem(stack), mouseX, mouseY);
            return true;
        }
        return false;
    }

    /**
     * Opens the crafting recipes for the given stack using JEI, if available.
     */
    public static void openRecipe(ItemStack stack) {
        if (stack.isEmpty() || !ModList.get().isLoaded("jei")) {
            return;
        }
        try {
            Class<?> internal = Class.forName("mezz.jei.Internal");
            Object runtime = internal.getMethod("getRuntime").invoke(null);
            if (runtime == null) {
                return;
            }
            Class<?> jeiRuntimeClass = Class.forName("mezz.jei.api.runtime.IJeiRuntime");
            Object recipeManager = jeiRuntimeClass.getMethod("getRecipeManager").invoke(runtime);
            Class<?> recipeManagerClass = Class.forName("mezz.jei.api.recipe.IRecipeManager");
            Class<?> modeClass = Class.forName("mezz.jei.api.recipe.IFocus$Mode");
            Object mode = Enum.valueOf(modeClass.asSubclass(Enum.class), "OUTPUT");
            Object focus = recipeManagerClass
                    .getMethod("createFocus", modeClass, Object.class)
                    .invoke(recipeManager, mode, stack);
            Object recipesGui = jeiRuntimeClass.getMethod("getRecipesGui").invoke(runtime);
            Class<?> recipesGuiClass = Class.forName("mezz.jei.api.runtime.IRecipesGui");
            Class<?> focusClass = Class.forName("mezz.jei.api.recipe.IFocus");
            recipesGuiClass.getMethod("show", focusClass).invoke(recipesGui, focus);
        } catch (Exception ignored) {
        }
    }
}