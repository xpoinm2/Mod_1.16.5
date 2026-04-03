package com.example.examplemod.client.render;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.item.WetItemData;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WetItemRenderEffects {
    private static final ResourceLocation WET_DROPS_OVERLAY =
            new ResourceLocation(ExampleMod.MODID, "textures/environment/wet_drops_overlay.png");
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
            drawWetOverlay(matrixStack, x, y);
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
        int guiLeft = screen.getGuiLeft();
        int guiTop = screen.getGuiTop();

        for (Slot slot : screen.getMenu().slots) {
            if (slot == null || !slot.hasItem()) {
                continue;
            }

            if (WetItemData.isWet(slot.getItem(), gameTime)) {
                drawWetOverlay(matrixStack, guiLeft + slot.x, guiTop + slot.y);
            }
        }
    }

    private static void drawWetOverlay(MatrixStack matrixStack, int x, int y) {
        Minecraft.getInstance().getTextureManager().bind(WET_DROPS_OVERLAY);
        AbstractGui.blit(matrixStack, x, y, 0, 0, 16, 16, 16, 16);
    }
}
