package com.example.examplemod.client.screen;

import com.example.examplemod.capability.IPlayerStats;
import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.client.FramedButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
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
            drawBar(ms, bx, by + spacing, w, h, stats.getFatigue(), 0xFFFFAA55, 0xFFFF5500);
            drawBar(ms, bx, by + spacing * 2, w, h, stats.getDisease(), 0xFF88CC88, 0xFF00AA00);
        });

        super.render(ms, mouseX, mouseY, pt);
    }

    private void drawBar(MatrixStack ms, int x, int y, int w, int h, int value, int bg, int fg) {
        int filled = value * w / 100;
        AbstractGui.fill(ms, x - 1, y - 1, x + w + 1, y + h + 1, 0xFFFFFF00);
        AbstractGui.fill(ms, x, y, x + w, y + h, bg);
        AbstractGui.fill(ms, x, y, x + filled, y + h, fg);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}