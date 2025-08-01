package com.example.examplemod.client.screen;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.container.FirepitContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.inventory.container.Slot;

public class FirepitScreen extends ContainerScreen<FirepitContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ExampleMod.MODID, "textures/gui/firepit.png");
    private static final ResourceLocation SLOT = new ResourceLocation(ExampleMod.MODID, "textures/gui/slot_border.png");

    public FirepitScreen(FirepitContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(MatrixStack ms, int mouseX, int mouseY) {
        // Draw the title in red and center it
        int titleX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.font.draw(ms, this.title, (float) titleX, 6.0F, 0xFF0000);
        this.font.draw(ms, this.playerInventoryTitle, 8.0F, (float) (this.imageHeight - 96 + 2), 4210752);
    }

    @Override
    protected void renderBg(MatrixStack ms, float partialTicks, int x, int y) {
        this.minecraft.getTextureManager().bind(TEXTURE);
        int i = this.leftPos;
        int j = this.topPos;
        blit(ms, i, j, 0, 0, this.imageWidth, this.imageHeight);

        // Draw grey slot borders for firepit inventory
        this.minecraft.getTextureManager().bind(SLOT);
        for (int idx = 0; idx < 12; idx++) {
            Slot slot = this.menu.getSlot(idx);
            blit(ms, i + slot.x - 1, j + slot.y - 1, 0, 0, 18, 18, 18, 18);
        }
    }
}