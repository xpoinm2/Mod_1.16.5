package com.example.examplemod.client.screen;

import com.example.examplemod.container.FirepitContainer;
import com.example.examplemod.tileentity.FirepitTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.math.MathHelper;

/**
 * GUI screen for the Firepit container.
 *
 * <p>This screen only renders the background texture. Slot frames and items are
 * rendered by the underlying {@link ContainerScreen} to ensure that inventory
 * slots stay aligned regardless of resolution or scaling.</p>
 */
public class FirepitScreen extends ContainerScreen<FirepitContainer> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("examplemod", "textures/gui/firepit.png");
    private static final ResourceLocation FURNACE_TEXTURE =
            new ResourceLocation("minecraft", "textures/gui/container/furnace.png");

    public FirepitScreen(FirepitContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(TEXTURE);
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        int barWidth = 6;
        int barTop = this.topPos + 8;
        int inventoryTop = this.topPos + this.inventoryLabelY + 10;
        int barBottom = inventoryTop - 2;
        int barHeight = barBottom - barTop;
        int heatBarHeight = this.menu.getHeatScaled(barHeight);
        int barX = this.leftPos + this.imageWidth - barWidth - 4;
        fill(matrixStack, barX - 1, barTop - 1, barX + barWidth + 1, barBottom + 1, 0xFF1A1A1A);
        fill(matrixStack, barX, barTop, barX + barWidth, barBottom, 0xFF3C3C3C);
        if (heatBarHeight > 0) {
            int filledTop = barBottom - heatBarHeight;
            fill(matrixStack, barX, filledTop, barX + barWidth, barBottom, 0xFFCC2A2A);
        }
        int thresholdHeight = MathHelper.ceil(
                (double) FirepitTileEntity.MIN_HEAT_FOR_SMELTING * barHeight / FirepitTileEntity.MAX_HEAT);
        int thresholdY = barBottom - thresholdHeight;
        fill(matrixStack, barX - 1, thresholdY, barX + barWidth + 1, thresholdY + 1, 0xFF757575);

        int progressWidth = this.menu.getProcessingScaled(24);
        if (progressWidth > 0) {
            this.minecraft.getTextureManager().bind(FURNACE_TEXTURE);
            int progressX = this.leftPos + this.imageWidth - 24 - 8;
            int progressY = this.topPos + 6;
            this.blit(matrixStack, progressX, progressY, 176, 14, progressWidth + 1, 16);
        }
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        // Draw the title ("Кострище") in red
        this.font.draw(matrixStack, this.title, this.titleLabelX, this.titleLabelY, 0xFF0000);
        // Draw the player inventory label in red
        this.font.draw(matrixStack, this.inventory.getDisplayName(), this.inventoryLabelX, this.inventoryLabelY, 0xFF0000);
    }
}