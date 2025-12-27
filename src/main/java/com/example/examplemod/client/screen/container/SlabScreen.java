package com.example.examplemod.client.screen.container;

import com.example.examplemod.container.SlabContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class SlabScreen extends ContainerScreen<SlabContainer> {
    private static final ResourceLocation CRAFTING_TABLE_TEXTURE =
            new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");

    public SlabScreen(SlabContainer screenContainer, PlayerInventory inv, ITextComponent title) {
        super(screenContainer, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        // Рисуем весь фон программно без нарисованных слотов на текстуре
        // Цвет фона как у ванильного GUI: #C6C6C6 (198, 198, 198)
        AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xFFC6C6C6);
        
        // Рисуем рамку всего GUI в стиле ванильного GUI
        // Внешняя темная рамка
        AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + 1, 0xFF000000);
        AbstractGui.fill(matrixStack, this.leftPos, this.topPos + this.imageHeight - 1, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xFF000000);
        AbstractGui.fill(matrixStack, this.leftPos, this.topPos, this.leftPos + 1, this.topPos + this.imageHeight, 0xFF000000);
        AbstractGui.fill(matrixStack, this.leftPos + this.imageWidth - 1, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xFF000000);
        
        // Внутренняя светлая рамка (верхняя и левая)
        AbstractGui.fill(matrixStack, this.leftPos + 1, this.topPos + 1, this.leftPos + this.imageWidth - 1, this.topPos + 2, 0xFF8B8B8B);
        AbstractGui.fill(matrixStack, this.leftPos + 1, this.topPos + 1, this.leftPos + 2, this.topPos + this.imageHeight - 1, 0xFF8B8B8B);
        
        // Внутренняя темная рамка (нижняя и правая)
        AbstractGui.fill(matrixStack, this.leftPos + 1, this.topPos + this.imageHeight - 2, this.leftPos + this.imageWidth - 1, this.topPos + this.imageHeight - 1, 0xFF373737);
        AbstractGui.fill(matrixStack, this.leftPos + this.imageWidth - 2, this.topPos + 1, this.leftPos + this.imageWidth - 1, this.topPos + this.imageHeight - 1, 0xFF373737);
        
        // Разделительная линия между контейнером и инвентарём игрока
        AbstractGui.fill(matrixStack, this.leftPos + 1, this.topPos + 54, this.leftPos + this.imageWidth - 1, this.topPos + 54, 0xFF373737);
        AbstractGui.fill(matrixStack, this.leftPos + 1, this.topPos + 55, this.leftPos + this.imageWidth - 1, this.topPos + 55, 0xFF8B8B8B);
        
        // Рисуем окоёмку для сетки 3x3 в стиле ванильного верстака
        // Используем текстуру верстака для окоёмки сетки
        // В ванильном верстаке окоёмка сетки находится на координатах (7, 17) размером 58x58
        // Наша сетка начинается с (62, 17), поэтому сдвигаем окоёмку на правильную позицию
        this.minecraft.getTextureManager().bind(CRAFTING_TABLE_TEXTURE);
        int frameX = this.leftPos + SlabContainer.GRID_START_X - 1;
        int frameY = this.topPos + SlabContainer.GRID_START_Y - 1;
        // Координаты окоёмки на текстуре верстака: (7, 17) размер 58x58 (включая рамку вокруг сетки 3x3)
        blit(matrixStack, frameX, frameY, 7, 17, 58, 58, 256, 256);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title, this.titleLabelX, this.titleLabelY, 0x404040);
        this.font.draw(matrixStack, this.inventory.getDisplayName(), this.inventoryLabelX, this.inventoryLabelY, 0x404040);
    }
}
