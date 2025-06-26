package com.example.examplemod.client.screen;

import com.example.examplemod.client.FramedButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DiseasesScreen extends Screen {
    private static final int WIDTH = 150;
    private static final int HEIGHT = 150;
    private final Screen parent;

    public DiseasesScreen(Screen parent) {
        super(new StringTextComponent("Болезни"));
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

        float scale = 0.75f;
        float inv = 1f / scale;
        ms.pushPose();
        ms.scale(scale, scale, 1f);

        float tx = (x0 + 10) * inv;
        float ty = (y0 + 30) * inv;
        float lh = (this.font.lineHeight + 2) * inv;

        this.font.draw(ms, "Простуда", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "Грипп", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "Отравление", tx, ty, 0xFFFFFF);

        ms.popPose();

        super.render(ms, mouseX, mouseY, pt);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}
