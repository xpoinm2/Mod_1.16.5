package com.example.examplemod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

/** Button that displays an item icon instead of text. */
public class ItemIconButton extends Button {
    private final int borderColor;
    private final int fillColor;
    private final ItemStack stack;

    public ItemIconButton(int x, int y, ItemStack stack, IPressable press) {
        super(x, y, 20, 20, StringTextComponent.EMPTY, press);
        this.borderColor = 0xFFFFFF00;
        this.fillColor = 0xFF333333;
        this.stack = stack;
    }

    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float pt) {
        AbstractGui.fill(ms, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, borderColor);
        AbstractGui.fill(ms, this.x, this.y, this.x + this.width, this.y + this.height, fillColor);
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, this.x + 2, this.y + 2);
    }
}