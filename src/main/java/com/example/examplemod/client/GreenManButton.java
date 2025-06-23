package com.example.examplemod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class GreenManButton extends Button {
    // Теперь кнопка рисует небольшое красное сердечко
    private static final int[][] SHAPE = {
            {0,1,0,0,0,1,0},
            {1,1,1,0,1,1,1},
            {1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1},
            {0,1,1,1,1,1,0},
            {0,0,1,1,1,0,0},
            {0,0,0,1,0,0,0}
    };

    public GreenManButton(int x, int y, int w, int h, ITextComponent t, IPressable p) {
        super(x, y, w, h, t, p);
    }

    @Override
    public void renderButton(MatrixStack ms, int mx, int my, float pt) {
        super.renderButton(ms, mx, my, pt);
        drawShape(ms, this.x + (this.width - SHAPE[0].length*2)/2,
                this.y + (this.height - SHAPE.length*2)/2);
    }

    // Вынесли отрисовку самой фигуры в отдельный приватный метод
    private static void drawShape(MatrixStack ms, int startX, int startY) {
        int ps    = 2;
        int color = 0xFFFF0000; // красный цвет сердечка
        for (int r = 0; r < SHAPE.length; r++) {
            for (int c = 0; c < SHAPE[r].length; c++) {
                if (SHAPE[r][c] == 1) {
                    int x1 = startX + c*ps;
                    int y1 = startY + r*ps;
                    AbstractGui.fill(ms, x1, y1, x1+ps, y1+ps, color);
                }
            }
        }
    }

    /** Статический метод — рисует сердечко в произвольной точке */
    public static void drawHeart(MatrixStack ms, int x, int y) {
        // x,y — верхний левый угол формы
        drawShape(ms, x, y);
    }
}
