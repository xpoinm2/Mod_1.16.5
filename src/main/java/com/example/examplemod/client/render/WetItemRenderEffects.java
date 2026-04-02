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
            drawWetOverlay(matrixStack, x, y, gameTime, slot);
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
                drawWetOverlay(matrixStack, slot.x, slot.y, gameTime, index);
            }
            index++;
        }
    }

    private static void drawWetOverlay(MatrixStack matrixStack, int x, int y, long gameTime, int seed) {
        float pulse = 0.55F + 0.35F * (float) Math.sin((gameTime + seed * 7L) * 0.17F);
        int alpha = Math.min(230, Math.max(90, (int) (pulse * 255.0F)));

        drawBubble(matrixStack, x + 4, y + 12, 3, alpha);
        drawBubble(matrixStack, x + 9, y + 11, 4, Math.min(255, alpha + 20));
        drawBubble(matrixStack, x + 12, y + 7, 2, Math.max(80, alpha - 30));
    }

    private static void drawBubble(MatrixStack matrixStack, int centerX, int centerY, int radius, int alpha) {
        int body = (alpha << 24) | 0x57B8FF;
        int glow = (Math.min(255, alpha + 30) << 24) | 0xA5DCFF;
        int sparkle = (Math.min(255, alpha + 70) << 24) | 0xDDF4FF;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                if (dx * dx + dy * dy > radius * radius) {
                    continue;
                }
                AbstractGui.fill(matrixStack, centerX + dx, centerY + dy, centerX + dx + 1, centerY + dy + 1, body);
            }
        }

        for (int dx = -radius + 1; dx <= radius - 1; dx++) {
            for (int dy = -radius + 1; dy <= radius - 1; dy++) {
                if (dx * dx + dy * dy > (radius - 1) * (radius - 1)) {
                    continue;
                }
                AbstractGui.fill(matrixStack, centerX + dx, centerY + dy, centerX + dx + 1, centerY + dy + 1, glow);
            }
        }

        AbstractGui.fill(matrixStack, centerX - radius / 2, centerY - radius / 2, centerX - radius / 2 + 1, centerY - radius / 2 + 1, sparkle);
    }
}
