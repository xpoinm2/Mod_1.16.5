package com.example.examplemod.client.screen;

import com.example.examplemod.container.FirepitContainer;
import com.example.examplemod.tileentity.FirepitTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Locale;

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
    private static final int PROGRESS_FRAME_COUNT = 16;
    private static final int PROGRESS_FRAME_SIZE = 16;
    private static final int FUEL_SLOT_LEFT_X = 136;
    private static final int FUEL_SLOT_COUNT = 2;
    private static final int SLOT_SPACING = 18;
    /**
     * Individual 16×16 frame textures stored at
     * assets/examplemod/textures/gui/firepit_progress/frame_XX.png.
     */
    private static final ResourceLocation[] PROGRESS_FRAMES = new ResourceLocation[PROGRESS_FRAME_COUNT];

    static {
        for (int i = 0; i < PROGRESS_FRAME_COUNT; i++) {
            PROGRESS_FRAMES[i] = new ResourceLocation("examplemod",
                    String.format(Locale.ROOT, "textures/gui/firepit_progress/frame_%02d.png", i + 1));
        }
    }

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

        if (this.menu.hasTongsSlots()) {
            int slotAreaLeft = this.leftPos + FirepitContainer.TONGS_SLOT_X - 2;
            int slotAreaTop = this.topPos + FirepitContainer.TONGS_SLOT_Y - 2;
            int slotAreaRight = slotAreaLeft + 22;
            int slotAreaBottom = slotAreaTop + 2 * 18 + 2;
            // Simple frame to highlight the attached tongs inventory.
            fill(matrixStack, slotAreaLeft, slotAreaTop, slotAreaRight, slotAreaBottom, 0x88303030);
            fill(matrixStack, slotAreaLeft + 1, slotAreaTop + 1, slotAreaRight - 1, slotAreaBottom - 1, 0xAA4A4A4A);
        }

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

        float progress = this.menu.getProcessingProgress();
        if (progress > 0.0F) {
            int frameIndex = MathHelper.clamp((int) (progress * PROGRESS_FRAME_COUNT), 0,
                    PROGRESS_FRAME_COUNT - 1);
            ResourceLocation frameTexture = PROGRESS_FRAMES[frameIndex];
            this.minecraft.getTextureManager().bind(frameTexture);
            int fuelSlotsWidth = FUEL_SLOT_COUNT * SLOT_SPACING;
            int progressCenterOffset = FUEL_SLOT_LEFT_X + fuelSlotsWidth / 2;
            int progressX = this.leftPos + progressCenterOffset - PROGRESS_FRAME_SIZE / 2;
            int progressY = this.topPos + 6;
            this.blit(matrixStack, progressX, progressY, 0, 0, PROGRESS_FRAME_SIZE, PROGRESS_FRAME_SIZE,
                    PROGRESS_FRAME_SIZE, PROGRESS_FRAME_SIZE);
        }
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        // Draw the title ("Кострище") in red
        this.font.draw(matrixStack, this.title, this.titleLabelX, this.titleLabelY, 0xFF0000);
        // Draw the player inventory label in red
        this.font.draw(matrixStack, this.inventory.getDisplayName(), this.inventoryLabelX, this.inventoryLabelY, 0xFF0000);
        
        if (this.menu.hasTongsSlots()) {
            ITextComponent tongsLabel = new TranslationTextComponent("container.examplemod.bone_tongs");
            this.font.draw(matrixStack, tongsLabel,
                    FirepitContainer.TONGS_SLOT_X + 2,
                    FirepitContainer.TONGS_SLOT_Y - 10,
                    0xFF7A7A7A);
        }
    }
}