package com.example.examplemod.client.screen;

import com.example.examplemod.capability.IPlayerStats;
import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.client.FramedButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import com.example.examplemod.client.screen.DiseasesScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HealthScreen extends Screen {
    private static final int WIDTH = 150;
    private static final int HEIGHT = 150;
    private final Screen parent;

    public HealthScreen(Screen parent) {
        super(new StringTextComponent("Здоровье"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x0 = 5;
        int y0 = 5;
        this.addButton(new FramedButton(x0 + 5, y0 + 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF, b -> this.minecraft.setScreen(parent)));
        this.addButton(new FramedButton(x0 + 15, y0 + 100, 120, 20, "Болезни", 0xFFFFFF00, 0xFFFF0000,
                b -> this.minecraft.setScreen(new DiseasesScreen(this))));
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

        int bx = x0 + 10;
        int by = y0 + 40;
        int w = 120;
        int h = 10;
        int spacing = 15;

        Minecraft.getInstance().player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent((IPlayerStats stats) -> {
            drawBar(ms, bx, by, w, h, stats.getThirst(), 0xFF5555FF, 0xFF0000FF);
            if (mouseX >= bx && mouseX <= bx + w && mouseY >= by && mouseY <= by + h) {
                drawValue(ms, "Жажда: " + stats.getThirst() + "/100", bx, by, w, h);
            }

            int y1 = by + spacing;
            drawBar(ms, bx, y1, w, h, stats.getFatigue(), 0xFFFFAA55, 0xFFFF5500);
            if (mouseX >= bx && mouseX <= bx + w && mouseY >= y1 && mouseY <= y1 + h) {
                drawValue(ms, "Усталость: " + stats.getFatigue() + "/100", bx, y1, w, h);
            }

            int y2 = by + spacing * 2;
            drawBar(ms, bx, y2, w, h, stats.getDisease(), 0xFF88CC88, 0xFF00AA00);
            if (mouseX >= bx && mouseX <= bx + w && mouseY >= y2 && mouseY <= y2 + h) {
                drawValue(ms, "Болезнь: " + stats.getDisease() + "/100", bx, y2, w, h);
            }

            int y3 = by + spacing * 3;
            float health = Minecraft.getInstance().player.getHealth();
            float maxHealth = Minecraft.getInstance().player.getMaxHealth();
            int bloodVal = (int)(health / maxHealth * 100);
            drawBar(ms, bx, y3, w, h, bloodVal, 0xFFCC5555, 0xFF880000);
            if (mouseX >= bx && mouseX <= bx + w && mouseY >= y3 && mouseY <= y3 + h) {
                float liters = 5f * health / maxHealth;
                String text = String.format("Кровь: %.2f/5 л", liters);
                drawValue(ms, text, bx, y3, w, h);
            }
        });

        super.render(ms, mouseX, mouseY, pt);
    }

    private void drawBar(MatrixStack ms, int x, int y, int w, int h, int value, int bg, int fg) {
        int filled = value * w / 100;
        AbstractGui.fill(ms, x - 1, y - 1, x + w + 1, y + h + 1, 0xFFFFFF00);
        AbstractGui.fill(ms, x, y, x + w, y + h, bg);
        AbstractGui.fill(ms, x, y, x + filled, y + h, fg);
    }

    private void drawValue(MatrixStack ms, String text, int x, int y, int w, int h) {
        ms.pushPose();
        float scale = 0.75f; // 1.5x larger than before
        ms.scale(scale, scale, 1f);
        float inv = 1f / scale;
        float tx = (x + (w - this.font.width(text) * scale) / 2f) * inv;
        float ty = (y + (h - this.font.lineHeight * scale) / 2f) * inv;
        this.font.draw(ms, text, tx, ty, 0xFFFFFF);
        ms.popPose();
    }


    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}