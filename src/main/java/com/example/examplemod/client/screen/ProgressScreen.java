package com.example.examplemod.client.screen;

import com.example.examplemod.client.FramedButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProgressScreen extends Screen {
    private static final int WIDTH = 200;
    private static final int HEIGHT = 180;

    private Tab currentTab = Tab.MAIN;

    private enum Tab { MAIN, ERAS, ACHIEVEMENTS }

    public ProgressScreen() {
        super(new StringTextComponent("Технический прогресс"));
    }

    @Override
    protected void init() {
        int x0 = 5;
        int y0 = 5;
        this.addButton(new FramedButton(x0 + 5, y0 + 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(null)));
        this.addButton(new FramedButton(x0 + 30, y0 + 35, 70, 20, "Главная", 0xFFFFFF00, 0xFFFF0000,
                b -> { currentTab = Tab.MAIN; rebuild(); }));
        this.addButton(new FramedButton(x0 + 30, y0 + 60, 70, 20, "Эпохи", 0xFFFFFF00, 0xFFFF0000,
                b -> { currentTab = Tab.ERAS; rebuild(); }));
        this.addButton(new FramedButton(x0 + 30, y0 + 85, 70, 20, "Достижения", 0xFFFFFF00, 0xFFFF0000,
                b -> { currentTab = Tab.ACHIEVEMENTS; rebuild(); }));
        super.init();
    }

    private void rebuild() {
        this.buttons.clear();
        this.children.clear();
        this.init();
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        this.renderBackground(ms);
        int x0 = 5;
        int y0 = 5;
        AbstractGui.fill(ms, x0 - 1, y0 - 1, x0 + WIDTH + 1, y0 + HEIGHT + 1, 0xFF00FF00);
        AbstractGui.fill(ms, x0, y0, x0 + WIDTH, y0 + HEIGHT, 0xFF000000);
        drawCenteredString(ms, this.font, this.title, x0 + WIDTH / 2, y0 + 10, 0xFF00FFFF);

        switch (currentTab) {
            case MAIN:
                drawCenteredString(ms, this.font, "🧭 XVI–XVII века: Научная революция",
                        x0 + WIDTH / 2, y0 + 40, 0xFFFFFF);
                break;
            case ERAS:
                this.font.draw(ms, "Древний мир", x0 + 30, y0 + 40, 0xFFFFFF);
                this.font.draw(ms, "Средние века", x0 + 30, y0 + 55, 0xFFFFFF);
                this.font.draw(ms, "Возрождение", x0 + 30, y0 + 70, 0xFFFFFF);
                break;
            case ACHIEVEMENTS:
                this.font.draw(ms, "Достижения будут тут", x0 + 30, y0 + 40, 0xFFFFFF);
                break;
        }

        super.render(ms, mouseX, mouseY, pt);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(null);
    }
}