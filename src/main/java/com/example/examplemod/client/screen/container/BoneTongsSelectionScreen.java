package com.example.examplemod.client.screen.container;

import com.example.examplemod.container.BoneTongsSelectionContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class BoneTongsSelectionScreen extends ContainerScreen<BoneTongsSelectionContainer> {

    public BoneTongsSelectionScreen(BoneTongsSelectionContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 188;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        // Простой серый фон вместо текстуры
        fill(matrixStack, leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFC6C6C6);
        // Рамка
        hLine(matrixStack, leftPos, leftPos + imageWidth - 1, topPos, 0xFF000000);
        hLine(matrixStack, leftPos, leftPos + imageWidth - 1, topPos + imageHeight - 1, 0xFF000000);
        vLine(matrixStack, leftPos, topPos + imageHeight - 1, topPos, 0xFF000000);
        vLine(matrixStack, leftPos + imageWidth - 1, topPos + imageHeight - 1, topPos, 0xFF000000);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);

        // Рисуем подсказки для выделенных слотов
        for (int i = 0; i < this.menu.slots.size(); i++) {
            if (this.menu.slots.get(i) instanceof BoneTongsSelectionContainer.SelectableSlot) {
                BoneTongsSelectionContainer.SelectableSlot slot = (BoneTongsSelectionContainer.SelectableSlot) this.menu.slots.get(i);
                if (slot.isSelected()) {
                    // Рисуем красную рамку вокруг выделенного слота
                    fill(matrixStack, leftPos + slot.x - 1, topPos + slot.y - 1,
                         leftPos + slot.x + 17, topPos + slot.y + 17, 0xFFFF0000);
                }
            }
        }
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        // Рисуем заголовок
        this.font.draw(matrixStack, this.title.getString(), 8, 6, 4210752);

        // Рисуем подписи секций
        this.font.draw(matrixStack, "Предметы в печи:", 8, 16, 4210752);
        this.font.draw(matrixStack, "Щипцы:", 8, 74, 4210752);
        this.font.draw(matrixStack, "Инвентарь:", 8, 96, 4210752);

        // Подсказка для выделения
        this.font.draw(matrixStack, "Кликните по предмету для переноса", 8, 175, 11184810);
    }
}