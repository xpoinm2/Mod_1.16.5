package com.example.examplemod.client.screen;

import com.example.examplemod.client.FramedButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.example.examplemod.client.GuiUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProgressScreen extends Screen {
    private final Screen parent;

    public ProgressScreen() {
        this(null);
    }

    public ProgressScreen(Screen parent) {
        super(new StringTextComponent("Эпохи"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int btnW = 120;
        int btnH = 20;
        int x = (this.width - btnW) / 2;
        int y = 60;

        this.addButton(new FramedButton(5, 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(parent)));
        this.addButton(new FramedButton(x, y, btnW, btnH, "Древний мир", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressEraScreen(this))));
        this.addButton(new FramedButton(x, y + 35, btnW, btnH, "Древняя металлургия", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new AncientMetallurgyEraScreen(this))));
        super.init();
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        this.renderBackground(ms);
        GuiUtil.drawPanel(ms, 10, 10, this.width - 20, this.height - 20);
        drawCenteredString(ms, this.font, this.title, this.width / 2, 30, 0xFF00FFFF);

        super.render(ms, mouseX, mouseY, pt);
    }
}