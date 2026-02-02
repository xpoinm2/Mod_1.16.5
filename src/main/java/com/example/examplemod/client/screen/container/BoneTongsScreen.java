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
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = BoneTongsContainer.PLAYER_INV_Y - 12;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        this.minecraft.getTextureManager().bind(TEXTURE);
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title, this.titleLabelX, this.titleLabelY, 0xFF0000);
        this.font.draw(matrixStack, this.inventory.getDisplayName(), this.inventoryLabelX, this.inventoryLabelY, 0xFF0000);
    }
}
