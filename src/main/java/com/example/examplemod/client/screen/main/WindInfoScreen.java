package com.example.examplemod.client.screen.main;

import com.example.examplemod.client.FramedButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WindInfoScreen extends Screen {
    private final Screen parent;

    public WindInfoScreen(Screen parent) {
        super(new StringTextComponent("О ветре"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int panelWidth = Math.max(220, this.width / 2);
        int panelHeight = Math.max(150, this.height / 2);
        int x0 = (this.width - panelWidth) / 2;
        int y0 = (this.height - panelHeight) / 2;

        this.addButton(new FramedButton(x0 + 10, y0 + 10, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(parent)));
        super.init();
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        this.renderBackground(ms);
        int panelWidth = Math.max(220, this.width / 2);
        int panelHeight = Math.max(150, this.height / 2);
        int x0 = (this.width - panelWidth) / 2;
        int y0 = (this.height - panelHeight) / 2;

        fill(ms, x0 - 1, y0 - 1, x0 + panelWidth + 1, y0 + panelHeight + 1, 0xFF00FF00);
        fill(ms, x0, y0, x0 + panelWidth, y0 + panelHeight, 0xFF000000);
        drawCenteredString(ms, this.font, this.title, x0 + panelWidth / 2, y0 + 14, 0xFF00FFFF);

        int textX = x0 + 10;
        int textY = y0 + 40;
        int line = 12;
        int color = 0xFFFFFF;

        this.font.draw(ms, "Скорость ветра = базовый ветер", textX, textY, color);
        textY += line;
        this.font.draw(ms, "+ шум чанка * коэффициент погоды.", textX, textY, color);
        textY += line + 4;

        this.font.draw(ms, "Далее умножается на множители:", textX, textY, color);
        textY += line;
        this.font.draw(ms, "- биом (горы/тайга сильнее, лес слабее)", textX, textY, color);
        textY += line;
        this.font.draw(ms, "- высота (после Y=64 рост до +25%)", textX, textY, color);
        textY += line;
        this.font.draw(ms, "- погода (ураган > гроза > дождь > ясно)", textX, textY, color);
        textY += line;
        this.font.draw(ms, "- ночь (небольшое снижение)", textX, textY, color);
        textY += line + 4;

        this.font.draw(ms, "В грозу/дождь, в горах/тайге и", textX, textY, color);
        textY += line;
        this.font.draw(ms, "на высоте ветер не ниже 15 м/с.", textX, textY, color);
        textY += line + 4;

        this.font.draw(ms, "Итог округляется и ограничен 0-30 м/с.", textX, textY, color);

        super.render(ms, mouseX, mouseY, pt);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
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
