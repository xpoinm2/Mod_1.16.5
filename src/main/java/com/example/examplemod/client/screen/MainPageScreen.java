package com.example.examplemod.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MainPageScreen extends Screen {
    private static final int WIDTH = 150;
    private static final int HEIGHT = 100;

    public MainPageScreen() {
        super(new StringTextComponent("Главная страница"));
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        this.renderBackground(ms);
        int x0 = (this.width - WIDTH) / 2;
        int y0 = (this.height - HEIGHT) / 2;
        fill(ms, x0 - 1, y0 - 1, x0 + WIDTH + 1, y0 + HEIGHT + 1, 0xFF00FF00);
        fill(ms, x0, y0, x0 + WIDTH, y0 + HEIGHT, 0xFF000000);
        drawCenteredString(ms, this.font, this.title, x0 + WIDTH / 2, y0 + 10, 0xFF00FFFF);
        super.render(ms, mouseX, mouseY, pt);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(null);
    }
}