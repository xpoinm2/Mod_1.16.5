package com.example.examplemod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
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
            // Screen#getTooltipFromItem returns a list of components, which can be passed
            // directly to Screen#renderTooltip. Casting the list to a single component
            // causes a ClassCastException. Simply render the tooltip for the stack.
            screen.renderTooltip(ms, stack, mouseX, mouseY);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> E enumValueOf(Class<?> enumClass, String name) {
        return Enum.valueOf((Class<E>) enumClass.asSubclass(Enum.class), name);
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
            Object mode = enumValueOf(modeClass, "OUTPUT");
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