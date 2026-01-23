package com.example.examplemod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

/**
 * Кнопка молотка для булыжниковой наковальни с задержкой нажатия и правильной границей.
 */
public class HammerButton extends Button {
    private long lastPressTime = 0;
    private static final long PRESS_DELAY_MS = 200; // 0.2 секунды
    private static final int BORDER_COLOR = 0xFF808080; // Серый цвет границы
    private static final int FILL_COLOR = 0xFF404040; // Темно-серый цвет заполнения
    private static final int HOVER_COLOR = 0xFF606060; // Цвет при наведении

    public HammerButton(int x, int y, int width, int height, ITextComponent text, IPressable onPress) {
        super(x, y, width, height, text, onPress);
    }

    @Override
    public void onPress() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPressTime >= PRESS_DELAY_MS) {
            lastPressTime = currentTime;
            super.onPress();
        }
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // Рисуем границу (включая нижнюю)
        AbstractGui.fill(matrixStack, 
            this.x - 1, this.y - 1, 
            this.x + this.width + 1, this.y + this.height + 1, 
            BORDER_COLOR);
        
        // Рисуем заполнение
        int fillColor = this.isHovered() ? HOVER_COLOR : FILL_COLOR;
        AbstractGui.fill(matrixStack, 
            this.x, this.y, 
            this.x + this.width, this.y + this.height, 
            fillColor);
        
        // Рисуем текст (молоток)
        drawCenteredString(matrixStack, Minecraft.getInstance().font, this.getMessage(), 
            this.x + this.width / 2, 
            this.y + (this.height - 8) / 2, 
            0xFFFFFFFF); // Белый цвет текста
    }
}
