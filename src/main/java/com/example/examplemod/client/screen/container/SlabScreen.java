package com.example.examplemod.client.screen.container;

import com.example.examplemod.container.SlabContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class SlabScreen extends ContainerScreen<SlabContainer> {

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
        
        // Рисуем темно-серую окоёмку вокруг сетки 3x3
        // Сетка начинается с координат GRID_START_X = 62, GRID_START_Y = 17
        // Размер сетки: 3x3 = 54x54 пикселей (3 * 18)
        int gridLeft = this.leftPos + SlabContainer.GRID_START_X - 1;
        int gridTop = this.topPos + SlabContainer.GRID_START_Y - 1;
        int gridRight = gridLeft + 54 + 2; // 54 + 2 пикселя для рамки
        int gridBottom = gridTop + 54 + 2;
        
        // Темно-серая рамка (цвет как у внутренней темной рамки GUI)
        AbstractGui.fill(matrixStack, gridLeft, gridTop, gridRight, gridTop + 1, 0xFF555555);
        AbstractGui.fill(matrixStack, gridLeft, gridBottom - 1, gridRight, gridBottom, 0xFF555555);
        AbstractGui.fill(matrixStack, gridLeft, gridTop, gridLeft + 1, gridBottom, 0xFF555555);
        AbstractGui.fill(matrixStack, gridRight - 1, gridTop, gridRight, gridBottom, 0xFF555555);
        
        // Рисуем темно-серые окоёмки для слотов инвентаря игрока (3x9)
        // Инвентарь начинается с координат x=8, y=84
        int invLeft = this.leftPos + 7; // 8 - 1 для рамки
        int invTop = this.topPos + 83; // 84 - 1 для рамки
        int invWidth = 9 * 18 + 2; // 9 слотов * 18 + 2 пикселя для рамки
        int invHeight = 3 * 18 + 2; // 3 ряда * 18 + 2 пикселя для рамки
        int invRight = invLeft + invWidth;
        int invBottom = invTop + invHeight;
        
        // Темно-серая рамка вокруг инвентаря
        AbstractGui.fill(matrixStack, invLeft, invTop, invRight, invTop + 1, 0xFF555555);
        AbstractGui.fill(matrixStack, invLeft, invBottom - 1, invRight, invBottom, 0xFF555555);
        AbstractGui.fill(matrixStack, invLeft, invTop, invLeft + 1, invBottom, 0xFF555555);
        AbstractGui.fill(matrixStack, invRight - 1, invTop, invRight, invBottom, 0xFF555555);
        
        // Рисуем темно-серые окоёмки для слотов хотбара (1x9)
        // Хотбар начинается с координат x=8, y=142
        int hotbarLeft = this.leftPos + 7; // 8 - 1 для рамки
        int hotbarTop = this.topPos + 141; // 142 - 1 для рамки
        int hotbarWidth = 9 * 18 + 2; // 9 слотов * 18 + 2 пикселя для рамки
        int hotbarHeight = 18 + 2; // 1 ряд * 18 + 2 пикселя для рамки
        int hotbarRight = hotbarLeft + hotbarWidth;
        int hotbarBottom = hotbarTop + hotbarHeight;
        
        // Темно-серая рамка вокруг хотбара
        AbstractGui.fill(matrixStack, hotbarLeft, hotbarTop, hotbarRight, hotbarTop + 1, 0xFF555555);
        AbstractGui.fill(matrixStack, hotbarLeft, hotbarBottom - 1, hotbarRight, hotbarBottom, 0xFF555555);
        AbstractGui.fill(matrixStack, hotbarLeft, hotbarTop, hotbarLeft + 1, hotbarBottom, 0xFF555555);
        AbstractGui.fill(matrixStack, hotbarRight - 1, hotbarTop, hotbarRight, hotbarBottom, 0xFF555555);
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
