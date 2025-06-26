package com.example.examplemod.client.screen;

import com.example.examplemod.client.FramedButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SharpPebbleInfoScreen extends Screen {
    private static final int WIDTH = 150;
    private static final int HEIGHT = 100;
    private final Screen parent;

    public SharpPebbleInfoScreen(Screen parent) {
        super(new StringTextComponent("Острый камешек"));
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

        int tx = x0 + 10;
        int ty = y0 + 30;
        int lh = this.font.lineHeight + 2;

        this.font.draw(ms, "1. Подберите камешек", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "2. Ударяйте им по камню", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "   пока он не станет острым", tx, ty, 0xFFFFFF);

        super.render(ms, mouseX, mouseY, pt);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}