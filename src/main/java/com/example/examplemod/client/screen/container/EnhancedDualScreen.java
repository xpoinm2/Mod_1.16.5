package com.example.examplemod.client.screen.container;

import com.example.examplemod.container.EnhancedDualContainer;
import com.example.examplemod.tileentity.FirepitTileEntity;
import com.example.examplemod.tileentity.PechugaTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class EnhancedDualScreen extends ContainerScreen<EnhancedDualContainer> {

    private static final ResourceLocation FIREPIT_TEXTURE =
            new ResourceLocation("examplemod", "textures/gui/firepit.png");
    private static final ResourceLocation PECHUGA_TEXTURE =
            new ResourceLocation("examplemod", "textures/gui/pechuga.png");        
    private static final int MAIN_GUI_WIDTH = 176;
    private static final int MAIN_GUI_HEIGHT = 166;
    private static final int TONGS_BG_WIDTH = EnhancedDualContainer.TONGS_GUI_WIDTH;
    private static final int TONGS_BG_HEIGHT = EnhancedDualContainer.TONGS_GUI_HEIGHT;
    private static final int TONGS_BG_OFFSET_Y = EnhancedDualContainer.TONGS_GUI_OFFSET_Y;

    public EnhancedDualScreen(EnhancedDualContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, new StringTextComponent("Двойной интерфейс"));
        this.imageWidth = EnhancedDualContainer.MAIN_GUI_OFFSET_X + MAIN_GUI_WIDTH;
        this.imageHeight = MAIN_GUI_HEIGHT;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1F, 1F, 1F, 1F);

        // Рисуем компактный фон щипцов слева
        int tongsLeft = leftPos;
        int tongsTop = topPos + TONGS_BG_OFFSET_Y;
        AbstractGui.fill(matrixStack, tongsLeft, tongsTop,
                tongsLeft + TONGS_BG_WIDTH, tongsTop + TONGS_BG_HEIGHT, 0xFF2C2C2C);
        AbstractGui.fill(matrixStack, tongsLeft + 1, tongsTop + 1,
                tongsLeft + TONGS_BG_WIDTH - 1, tongsTop + TONGS_BG_HEIGHT - 1, 0xFF3A3A3A);

        // Рисуем фон основного контейнера справа
        this.minecraft.getTextureManager().bind(getMainTexture());
        blit(matrixStack, leftPos + EnhancedDualContainer.MAIN_GUI_OFFSET_X, topPos, 0, 0, MAIN_GUI_WIDTH, MAIN_GUI_HEIGHT);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);

        // Рендерим слоты
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        // Рендерим тултипы
        renderTooltip(matrixStack, mouseX, mouseY);

        // Рисуем дополнительные элементы основного GUI (прогресс-бары и т.д.)
        renderMainGuiElements(matrixStack, partialTicks, mouseX, mouseY);
    }

    private void renderMainGuiElements(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        // Здесь можно добавить рендеринг прогресс-баров, огня и других элементов
        // из оригинального FirepitScreen

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(getMainTexture());

        // Пример: рендеринг огня в топке
        int fireHeight = getFireProgress();
        if (fireHeight > 0) {
            blit(matrixStack,
                 leftPos + EnhancedDualContainer.MAIN_GUI_OFFSET_X + 81,
                 topPos + 55 - fireHeight,
                 176, 12 - fireHeight, 14, fireHeight);
        }

        // Пример: рендеринг прогресса плавки
        int progressWidth = getSmeltProgress();
        if (progressWidth > 0) {
            blit(matrixStack,
                 leftPos + EnhancedDualContainer.MAIN_GUI_OFFSET_X + 79,
                 topPos + 34,
                 176, 0, progressWidth, 16);
        }

        renderHeatBar(matrixStack);
    }

    private int getFireProgress() {
        if (menu.getMainContainer() instanceof com.example.examplemod.container.FirepitContainer) {
            return ((com.example.examplemod.container.FirepitContainer) menu.getMainContainer()).getHeatScaled(12);
        }
        if (menu.getMainContainer() instanceof com.example.examplemod.container.PechugaContainer) {
            return ((com.example.examplemod.container.PechugaContainer) menu.getMainContainer()).getHeatScaled(12);
        }
        return 0;
    }

    private void renderHeatBar(MatrixStack matrixStack) {
        int barWidth = 6;
        int barTop = topPos + 8;
        int inventoryTop = topPos + (MAIN_GUI_HEIGHT - 94) + 10;
        int barBottom = inventoryTop - 2;
        int barHeight = barBottom - barTop;
        int heatBarHeight = getHeatProgress(barHeight);
        int barX = leftPos + EnhancedDualContainer.MAIN_GUI_OFFSET_X + MAIN_GUI_WIDTH - barWidth - 4;

        AbstractGui.fill(matrixStack, barX - 1, barTop - 1, barX + barWidth + 1, barBottom + 1, 0xFF1A1A1A);
        AbstractGui.fill(matrixStack, barX, barTop, barX + barWidth, barBottom, 0xFF3C3C3C);
        if (heatBarHeight > 0) {
            int filledTop = barBottom - heatBarHeight;
            AbstractGui.fill(matrixStack, barX, filledTop, barX + barWidth, barBottom, 0xFFCC2A2A);
        }

        int thresholdHeight = getHeatThresholdHeight(barHeight);
        if (thresholdHeight > 0) {
            int thresholdY = barBottom - thresholdHeight;
            AbstractGui.fill(matrixStack, barX - 1, thresholdY, barX + barWidth + 1, thresholdY + 1, 0xFF757575);
        }
    }

    private int getHeatProgress(int barHeight) {
        if (menu.getMainContainer() instanceof com.example.examplemod.container.FirepitContainer) {
            return ((com.example.examplemod.container.FirepitContainer) menu.getMainContainer()).getHeatScaled(barHeight);
        }
        if (menu.getMainContainer() instanceof com.example.examplemod.container.PechugaContainer) {
            return ((com.example.examplemod.container.PechugaContainer) menu.getMainContainer()).getHeatScaled(barHeight);
        }
        return 0;
    }

    private int getHeatThresholdHeight(int barHeight) {
        if (menu.getMainContainer() instanceof com.example.examplemod.container.FirepitContainer) {
            return MathHelper.ceil(
                    (double) FirepitTileEntity.MIN_HEAT_FOR_SMELTING * barHeight / FirepitTileEntity.MAX_HEAT);
        }
        if (menu.getMainContainer() instanceof com.example.examplemod.container.PechugaContainer) {
            return MathHelper.ceil(
                    (double) PechugaTileEntity.MIN_HEAT_FOR_SMELTING * barHeight / PechugaTileEntity.MAX_HEAT);
        }
        return 0;
    }

    private int getSmeltProgress() {
        if (menu.getMainContainer() instanceof com.example.examplemod.container.FirepitContainer) {
            return ((com.example.examplemod.container.FirepitContainer) menu.getMainContainer()).getProcessingScaled(24);
        }
        if (menu.getMainContainer() instanceof com.example.examplemod.container.PechugaContainer) {
            return ((com.example.examplemod.container.PechugaContainer) menu.getMainContainer()).getProcessingScaled(24);
        }
        return 0;
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        // Заголовки для обоих GUI
        this.font.draw(matrixStack, "Костяные щипцы", 12, 6, 4210752);

        // Заголовок основного контейнера
        String mainTitle = "Кострище";
        if (menu.getMainContainer() instanceof com.example.examplemod.container.PechugaContainer) {
            mainTitle = "Кирпичная печь";
        }
        this.font.draw(matrixStack, mainTitle,
                      EnhancedDualContainer.MAIN_GUI_OFFSET_X + 8, 6, 4210752);

        // Подсказки
        this.font.draw(matrixStack, "Shift+клик для быстрого перемещения", 8, MAIN_GUI_HEIGHT - 10, 11184810);
    }

    private ResourceLocation getMainTexture() {
        if (menu.getMainContainer() instanceof com.example.examplemod.container.PechugaContainer) {
            return PECHUGA_TEXTURE;
        }
        return FIREPIT_TEXTURE;
    }
}
