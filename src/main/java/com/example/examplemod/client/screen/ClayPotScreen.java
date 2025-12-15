package com.example.examplemod.client.screen;

import com.example.examplemod.container.ClayPotContainer;
import com.example.examplemod.network.ClayPotModePacket;
import com.example.examplemod.network.ModNetworkHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ClayPotScreen extends ContainerScreen<ClayPotContainer> {
    private static final ResourceLocation BACKGROUND =
            new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");
    private Button modeButton;

    public ClayPotScreen(ClayPotContainer screenContainer, PlayerInventory inv, ITextComponent title) {
        super(screenContainer, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.modeButton = this.addButton(new Button(
                this.leftPos + ClayPotContainer.MODE_BUTTON_X,
                this.topPos + ClayPotContainer.MODE_BUTTON_Y,
                60,
                20,
                getModeButtonText(),
                button -> ModNetworkHandler.CHANNEL.sendToServer(
                        new ClayPotModePacket(menu.getBlockPos()))
        ));
        updateModeButtonText();
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        updateModeButtonText();
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    private void updateModeButtonText() {
        if (modeButton != null) {
            modeButton.setMessage(getModeButtonText());
        }
    }

    private TranslationTextComponent getModeButtonText() {
        return new TranslationTextComponent(menu.isDrainMode()
                ? "button.examplemod.clay_pot.mode.drain"
                : "button.examplemod.clay_pot.mode.fill");
    }
}
