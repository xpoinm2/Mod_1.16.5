package com.example.examplemod.client.screen.container;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.container.BoneTongsContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class BoneTongsScreen extends ContainerScreen<BoneTongsContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ExampleMod.MODID, "textures/gui/bone_tongs.png");

    public BoneTongsScreen(BoneTongsContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth = BoneTongsContainer.GUI_WIDTH;
        this.imageHeight = BoneTongsContainer.GUI_HEIGHT;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        this.minecraft.getTextureManager().bind(TEXTURE);
        matrixStack.pushPose();
        matrixStack.translate(leftPos, topPos, 0);
        matrixStack.scale(BoneTongsContainer.GUI_SCALE, BoneTongsContainer.GUI_SCALE, 1F);
        blit(matrixStack, 0, 0, 0, 0, BoneTongsContainer.BASE_GUI_WIDTH, BoneTongsContainer.BASE_GUI_HEIGHT);
        matrixStack.popPose();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);
    }
}
