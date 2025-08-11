package com.example.examplemod.client.screen;

import com.example.examplemod.client.FramedButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlanksQuestScreen extends Screen {
    private final Screen parent;

    public PlanksQuestScreen(Screen parent) {
        super(new StringTextComponent("Доски"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x0 = 10;
        int y0 = 10;
        this.addButton(new FramedButton(x0 + 5, y0 + 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(parent)));
        super.init();
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        this.renderBackground(ms);
        int x0 = 10;
        int y0 = 10;
        int width = this.width - 20;
        int height = this.height - 20;
        fill(ms, x0 - 1, y0 - 1, x0 + width + 1, y0 + height + 1, 0xFF000000);
        fill(ms, x0, y0, x0 + width, y0 + height, 0xFF000000);
        drawCenteredString(ms, this.font, this.title, x0 + width / 2, y0 + 15, 0xFFFFFFFF);

        int textX = x0 + 20;
        int currentY = y0 + 40;
        drawString(ms, this.font, "Описание", textX, currentY, 0xFFFFFFFF);
        currentY += 15;
        drawString(ms, this.font, "Люди работали топорами, чтобы", textX, currentY, 0xFFFFFFFF);
        currentY += 10;
        drawString(ms, this.font, "разделывать бревна.", textX, currentY, 0xFFFFFFFF);
        currentY += 15;

        drawString(ms, this.font, "Цель", textX, currentY, 0xFFFFFFFF);
        currentY += 15;
        drawString(ms, this.font, "Нужно получить 4 доски", textX, currentY, 0xFFFFFF00);
        currentY += 15;

        drawString(ms, this.font, "Инструкция", textX, currentY, 0xFFFFFFFF);
        currentY += 15;
        drawString(ms, this.font, "Крафт досок через топор", textX, currentY, 0xFFFFFF00);
        super.render(ms, mouseX, mouseY, pt);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}