package com.example.examplemod.client.screen;

import com.example.examplemod.client.FramedButton;
import com.example.examplemod.capability.IPlayerStats;
import com.example.examplemod.capability.PlayerStatsProvider;
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

        this.minecraft.player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent((IPlayerStats stats) -> {
            float scale = 0.75f;
            float inv = 1f / scale;
            ms.pushPose();
            ms.scale(scale, scale, 1f);
            String txt = String.format("простуда - %d%%", stats.getCold());
            this.font.draw(ms, txt, (x0 + 10) * inv, (y0 + 40) * inv, 0xFFFFFF);
            String txt2 = String.format("переохлаждение - %d%%", stats.getHypothermia());
            this.font.draw(ms, txt2, (x0 + 10) * inv, (y0 + 55) * inv, 0xFFFFFF);
            String txt3 = String.format("вирусы - %d%%", stats.getVirus());
            this.font.draw(ms, txt3, (x0 + 10) * inv, (y0 + 70) * inv, 0xFFFFFF);
            String txt4 = String.format("отравление - %d%%", stats.getPoison());
            this.font.draw(ms, txt4, (x0 + 10) * inv, (y0 + 85) * inv, 0xFFFFFF);
            ms.popPose();
        });

        super.render(ms, mouseX, mouseY, pt);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null && this.minecraft.player != null && this.minecraft.level != null && parent != null) {
            this.minecraft.setScreen(parent);
        } else {
            super.onClose();
        }
    }
}
