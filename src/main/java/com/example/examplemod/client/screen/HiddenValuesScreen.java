package com.example.examplemod.client.screen;

import com.example.examplemod.client.FramedButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HiddenValuesScreen extends Screen {
    private static final int WIDTH = 150;
    private static final int HEIGHT = 150;
    private final Screen parent;
    private TextFieldWidget passwordField;
    private boolean unlocked = false;

    public HiddenValuesScreen(Screen parent) {
        super(new StringTextComponent("Скрытые значения"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x0 = 5;
        int y0 = 5;
        this.addButton(new FramedButton(x0 + 5, y0 + 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(parent)));
        if (!unlocked) {
            passwordField = new TextFieldWidget(this.font, x0 + 15, y0 + 40, 120, 20, new StringTextComponent(""));
            this.children.add(passwordField);
            this.setFocused(passwordField);
            this.addButton(new FramedButton(x0 + 15, y0 + 70, 120, 20, "OK", 0xFFFFFF00, 0xFFFF0000,
                    b -> checkPassword()));
        }
        super.init();
    }

    private void checkPassword() {
        if ("1234".equals(passwordField.getValue())) {
            unlocked = true;
            this.clearWidgets();
            this.init();
        }
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        this.renderBackground(ms);
        int x0 = 5;
        int y0 = 5;
        fill(ms, x0 - 1, y0 - 1, x0 + WIDTH + 1, y0 + HEIGHT + 1, 0xFF00FF00);
        fill(ms, x0, y0, x0 + WIDTH, y0 + HEIGHT, 0xFF000000);
        drawCenteredString(ms, this.font, this.title, x0 + WIDTH / 2, y0 + 10, 0xFF00FFFF);

        if (!unlocked) {
            passwordField.render(ms, mouseX, mouseY, pt);
        } else {
            long time = this.minecraft.level.getDayTime();
            String text = "Игровое время: " + time;
            this.font.draw(ms, text, x0 + 10, y0 + 40, 0xFFFFFF);
        }

        super.render(ms, mouseX, mouseY, pt);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}