package com.example.examplemod.client.screen;

import com.example.examplemod.client.FramedButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlanksQuestScreen extends Screen {
    private static final int WIDTH = 150;
    private static final int HEIGHT = 100;
    private final Screen parent;

    public PlanksQuestScreen(Screen parent) {
        super(new StringTextComponent("доски"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x0 = 5;
        int y0 = 5;
        this.addButton(new FramedButton(x0 + 5, y0 + 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(parent)));
        super.init();
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        this.renderBackground(ms);
        int x0 = 5;
        int y0 = 5;
        fill(ms, x0 - 1, y0 - 1, x0 + WIDTH + 1, y0 + HEIGHT + 1, 0xFF00FF00);
        fill(ms, x0, y0, x0 + WIDTH, y0 + HEIGHT, 0xFF000000);
        drawCenteredString(ms, this.font, this.title, x0 + WIDTH / 2, y0 + 10, 0xFF00FFFF);
        drawString(ms, this.font, "Люди работали топорами, чтобы", x0 + 10, y0 + 30, 0xFFFFFFFF);
        drawString(ms, this.font, "разделывать бревна.", x0 + 10, y0 + 40, 0xFFFFFFFF);
        drawString(ms, this.font, "Нужно получить 4 доски", x0 + 10, y0 + 60, 0xFFFFFF00);
        drawString(ms, this.font, "Крафт досок через топор", x0 + 10, y0 + 75, 0xFFFFFF00);
        super.render(ms, mouseX, mouseY, pt);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}