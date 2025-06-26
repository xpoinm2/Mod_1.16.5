package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/** Inventory button with an icon texture. */
public class GreenManButton extends Button {
    private static final ResourceLocation ICON = new ResourceLocation(ExampleMod.MODID, "textures/item/example_icon.png");

    public GreenManButton(int x, int y, int w, int h, ITextComponent t, IPressable p) {
        super(x, y, w, h, t, p);
    }

    @Override
    public void renderButton(MatrixStack ms, int mx, int my, float pt) {
        Minecraft.getInstance().getTextureManager().bind(ICON);
        AbstractGui.blit(ms, this.x, this.y, 0, 0, this.width, this.height, this.width, this.height);
    }
}
