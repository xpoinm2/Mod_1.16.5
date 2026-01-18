package com.example.examplemod.client.screen.container;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.container.EnhancedDualContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class EnhancedDualScreen extends ContainerScreen<EnhancedDualContainer> {

    private static final ResourceLocation FIREPIT_TEXTURE =
            new ResourceLocation("examplemod", "textures/gui/firepit.png");
    private static final ResourceLocation PECHUGA_TEXTURE =
            new ResourceLocation("examplemod", "textures/gui/pechuga.png");        
    private static final ResourceLocation TONGS_TEXTURE =
            new ResourceLocation(ExampleMod.MODID, "textures/gui/bone_tongs.png");

    public EnhancedDualScreen(EnhancedDualContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, new StringTextComponent("Двойной интерфейс"));
        this.imageWidth = 256; // 176 (firepit) + 80 (tongs + spacing)
        this.imageHeight = 188;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1F, 1F, 1F, 1F);

        // Рисуем фон щипцов слева
        this.minecraft.getTextureManager().bind(TONGS_TEXTURE);
        blit(matrixStack, leftPos, topPos + 8, 0, 0, 70, 80);

        // Рисуем фон основного контейнера справа
        this.minecraft.getTextureManager().bind(getMainTexture());
        blit(matrixStack, leftPos + EnhancedDualContainer.MAIN_GUI_OFFSET_X, topPos, 0, 0, 176, 166);
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
        this.font.draw(matrixStack, "Shift+клик для быстрого перемещения", 8, 175, 11184810);
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderTooltip(matrixStack, mouseX, mouseY);

        // Дополнительные подсказки
        if (isHoveringOverTongs(mouseX, mouseY)) {
            renderTooltip(matrixStack, new StringTextComponent("Слоты костяных щипцов"), mouseX, mouseY);
        } else if (isHoveringOverMainGui(mouseX, mouseY)) {
            renderTooltip(matrixStack, new StringTextComponent("Слоты основного контейнера"), mouseX, mouseY);
        }
    }

    private boolean isHoveringOverTongs(int mouseX, int mouseY) {
        return mouseX >= leftPos && mouseX <= leftPos + 70 &&
               mouseY >= topPos + 8 && mouseY <= topPos + 88;
    }

    private boolean isHoveringOverMainGui(int mouseX, int mouseY) {
        return mouseX >= leftPos + EnhancedDualContainer.MAIN_GUI_OFFSET_X &&
               mouseX <= leftPos + EnhancedDualContainer.MAIN_GUI_OFFSET_X + 176 &&
               mouseY >= topPos && mouseY <= topPos + 166;
    }

    private ResourceLocation getMainTexture() {
        if (menu.getMainContainer() instanceof com.example.examplemod.container.PechugaContainer) {
            return PECHUGA_TEXTURE;
        }
        return FIREPIT_TEXTURE;
    }
}