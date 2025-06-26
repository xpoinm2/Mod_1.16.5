package com.example.examplemod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

/** Simple button with colored border and custom text color. */
public class FramedButton extends Button {
    private final int borderColor;
    private final int fillColor;
    private final int textColor;

    public FramedButton(int x, int y, int w, int h, String label, int borderColor, int textColor, IPressable press) {
        super(x, y, w, h, new StringTextComponent(label), press);
        this.borderColor = borderColor;
        this.fillColor = 0xFF333333;
        this.textColor = textColor;
    }

    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float pt) {
        AbstractGui.fill(ms, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, borderColor);
        AbstractGui.fill(ms, this.x, this.y, this.x + this.width, this.y + this.height, fillColor);
        drawCenteredString(ms, Minecraft.getInstance().font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, textColor);
    }
}