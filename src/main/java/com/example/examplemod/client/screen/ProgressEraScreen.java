package com.example.examplemod.client.screen;

import com.example.examplemod.client.FramedButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProgressEraScreen extends Screen {
    private final Screen parent;

    public ProgressEraScreen(Screen parent) {
        super(new StringTextComponent("Древний мир"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addButton(new FramedButton(5, 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(parent)));

        int tabW = 120;
        int tabH = 20;
        int x = 20;
        int y = 60;
        this.addButton(new FramedButton(x, y, tabW, tabH, "Собирательство", 0xFFFFFF00, 0xFFFF0000, b -> {}));
        this.addButton(new FramedButton(x, y + 25, tabW, tabH, "Металлургия", 0xFFFFFF00, 0xFFFF0000, b -> {}));
        this.addButton(new FramedButton(x, y + 50, tabW, tabH, "Производство", 0xFFFFFF00, 0xFFFF0000, b -> {}));
        super.init();
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        this.renderBackground(ms);
        AbstractGui.fill(ms, 0, 0, this.width, this.height, 0xCC000000);
        drawCenteredString(ms, this.font, this.title, this.width / 2, 30, 0xFF00FFFF);
        super.render(ms, mouseX, mouseY, pt);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}