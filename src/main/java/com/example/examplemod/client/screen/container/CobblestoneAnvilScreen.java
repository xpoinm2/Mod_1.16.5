package com.example.examplemod.client.screen.container;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.container.CobblestoneAnvilContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CobblestoneAnvilScreen extends ContainerScreen<CobblestoneAnvilContainer> {
    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(ExampleMod.MODID, "textures/gui/cobblestone_anvil.png");

    public CobblestoneAnvilScreen(CobblestoneAnvilContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        // Заголовок
        this.font.draw(matrixStack, this.title, (float)(this.imageWidth / 2 - this.font.width(this.title) / 2), 6.0F, 4210752);
        // Инвентарь игрока
        this.font.draw(matrixStack, this.inventory.getDisplayName(), 8.0F, (float)(this.imageHeight - 96 + 2), 4210752);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }
}