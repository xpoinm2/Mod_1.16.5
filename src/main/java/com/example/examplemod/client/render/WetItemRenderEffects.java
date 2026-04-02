package com.example.examplemod.client.render;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.item.WetItemData;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WetItemRenderEffects {
    private WetItemRenderEffects() {
    }

    @SubscribeEvent
    public static void onHotbarRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) {
            return;
        }

        MatrixStack matrixStack = event.getMatrixStack();
        int centerX = event.getWindow().getGuiScaledWidth() / 2;
        int y = event.getWindow().getGuiScaledHeight() - 22;
        long gameTime = player.level.getGameTime();

        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = player.inventory.getItem(slot);
            if (!WetItemData.isWet(stack, gameTime)) {
                continue;
            }

            int x = centerX - 90 + slot * 20;
            drawWetOverlay(matrixStack, x, y, slot);
        }
    }

    @SubscribeEvent
    public static void onContainerRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!(event.getGui() instanceof ContainerScreen<?>)) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) {
            return;
        }

        ContainerScreen<?> screen = (ContainerScreen<?>) event.getGui();
        MatrixStack matrixStack = event.getMatrixStack();
        long gameTime = player.level.getGameTime();
        int index = 0;

        for (Slot slot : screen.getMenu().slots) {
            if (slot == null || !slot.hasItem()) {
                index++;
                continue;
            }

            if (WetItemData.isWet(slot.getItem(), gameTime)) {
                drawWetOverlay(matrixStack, slot.x, slot.y, index);
            }
            index++;
        }
    }

    private static void drawWetOverlay(MatrixStack matrixStack, int x, int y, int seed) {
        int baseAlpha = 165;
        int edgeColor = (baseAlpha << 24) | 0x57B8FF;
        int innerColor = (Math.min(255, baseAlpha + 35) << 24) | 0xA5DCFF;

        // Небольшие "квадраты воды" с каждой стороны слота.
        drawPatchOnSide(matrixStack, x, y, seed * 37 + 3, 0, edgeColor, innerColor);   // top
        drawPatchOnSide(matrixStack, x, y, seed * 37 + 11, 1, edgeColor, innerColor);  // bottom
        drawPatchOnSide(matrixStack, x, y, seed * 37 + 19, 2, edgeColor, innerColor);  // left
        drawPatchOnSide(matrixStack, x, y, seed * 37 + 29, 3, edgeColor, innerColor);  // right
    }

    private static void drawPatchOnSide(MatrixStack matrixStack, int x, int y, int seed, int side, int edgeColor, int innerColor) {
        int size = 3 + positiveMod(seed, 4); // 3..6
        int inset = 1 + positiveMod(seed / 3, 2); // 1..2

        int startX;
        int startY;

        switch (side) {
            case 0: // top
                startX = x + 1 + positiveMod(seed, 18 - size);
                startY = y + inset;
                break;
            case 1: // bottom
                startX = x + 1 + positiveMod(seed / 5, 18 - size);
                startY = y + 17 - inset - size;
                break;
            case 2: // left
                startX = x + inset;
                startY = y + 1 + positiveMod(seed / 7, 18 - size);
                break;
            default: // right
                startX = x + 17 - inset - size;
                startY = y + 1 + positiveMod(seed / 11, 18 - size);
                break;
        }

        AbstractGui.fill(matrixStack, startX, startY, startX + size, startY + size, edgeColor);
        if (size >= 4) {
            AbstractGui.fill(matrixStack, startX + 1, startY + 1, startX + size - 1, startY + size - 1, innerColor);
        }
    }

    private static int positiveMod(int value, int mod) {
        int result = value % mod;
        return result < 0 ? result + mod : result;
    }
}
