package com.example.examplemod.client.screen;

import com.example.examplemod.client.FramedButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SystemInfoScreen extends Screen {
    private static final int WIDTH = 170;
    private static final int HEIGHT = 200;
    private final Screen parent;

    public SystemInfoScreen(Screen parent) {
        super(new StringTextComponent("Системное"));
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

        this.font.draw(ms, "Каждый игровой час: +2 жажды и усталости", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "15 минут плавания: +5 усталости", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "Час без движения: -5 усталости", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "100м бега: +5 усталости", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "20 прыжков: +3 усталости", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "Атака существа: +1 усталости", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "Бутылка воды: -20 жажды", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "Сырая/варёная рыба: +15 жажды", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "Кнопка 'Попить': -2 жажды", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "Кнопка 'Перемешать': +3 усталости", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "Заточка камня: +5 усталости", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "Ломание блоков руками: +4 усталости", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "Отдых сидя 15 мин: -5 усталости", tx, ty, 0xFFFFFF);
        ty += lh;
        this.font.draw(ms, "Полный сон: усталость = 0", tx, ty, 0xFFFFFF);

        super.render(ms, mouseX, mouseY, pt);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}