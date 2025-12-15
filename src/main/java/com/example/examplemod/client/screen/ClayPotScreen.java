package com.example.examplemod.client.screen;

import com.example.examplemod.ModFluids;
import com.example.examplemod.container.ClayPotContainer;
import com.example.examplemod.network.ClayPotModePacket;
import com.example.examplemod.network.ModNetworkHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class ClayPotScreen extends ContainerScreen<ClayPotContainer> {
    private static final ResourceLocation BACKGROUND =
            new ResourceLocation("examplemod", "textures/gui/clay_pot.png");
    private static final int FLUID_GAUGE_WIDTH = 5;
    private static final int FLUID_GAUGE_HEIGHT = 63;
    private static final int FLUID_GAUGE_X = 176 - 4 - FLUID_GAUGE_WIDTH;
    private static final int FLUID_GAUGE_Y = 4;

    private ModeToggleButton modeButton;

    public ClayPotScreen(ClayPotContainer screenContainer, PlayerInventory inv, ITextComponent title) {
        super(screenContainer, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 4;
        this.titleLabelY = 4;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
    }

    @Override
    protected void init() {
        super.init();
        this.modeButton = this.addButton(new ModeToggleButton(this,
                this.leftPos + ClayPotContainer.MODE_BUTTON_X,
                this.topPos + ClayPotContainer.MODE_BUTTON_Y
        ));
        updateModeButtonText();
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight,
                this.imageWidth, this.imageHeight);
        renderFluidGauge(matrixStack);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        updateModeButtonText();
        if (modeButton != null && modeButton.isHovered()) {
            renderTooltip(matrixStack, getModeButtonTooltip(), mouseX, mouseY);
        }
        if (isMouseOverGauge(mouseX, mouseY)) {
            renderTooltip(matrixStack, getFluidTooltip(), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title, this.titleLabelX, this.titleLabelY, 0xFFD9AC71);
        this.font.draw(matrixStack, this.inventory.getDisplayName(), this.inventoryLabelX, this.inventoryLabelY, 0xFFD9AC71);
    }

    private void renderFluidGauge(MatrixStack matrixStack) {
        int capacity = menu.getFluidCapacity();
        if (capacity <= 0) {
            return;
        }
        int amount = menu.getFluidAmount();
        int fill = MathHelper.ceil((double) amount * FLUID_GAUGE_HEIGHT / capacity);
        fill = MathHelper.clamp(fill, 0, FLUID_GAUGE_HEIGHT);
        if (fill == 0) {
            return;
        }
        int left = this.leftPos + FLUID_GAUGE_X;
        int right = left + FLUID_GAUGE_WIDTH;
        int bottom = this.topPos + FLUID_GAUGE_Y + FLUID_GAUGE_HEIGHT;
        int top = bottom - fill;
        fill(matrixStack, left, top, right, bottom, getFluidColor(menu.getFluidType()));
    }

    private boolean isMouseOverGauge(double mouseX, double mouseY) {
        int left = this.leftPos + FLUID_GAUGE_X;
        int right = left + FLUID_GAUGE_WIDTH;
        int top = this.topPos + FLUID_GAUGE_Y;
        int bottom = top + FLUID_GAUGE_HEIGHT;
        return mouseX >= left && mouseX < right && mouseY >= top && mouseY < bottom;
    }

    private ITextComponent getFluidTooltip() {
        String amount = menu.getFluidAmount() + " / " + menu.getFluidCapacity();
        return new TranslationTextComponent("tooltip.examplemod.clay_pot.fluid",
                getFluidName(),
                new StringTextComponent(amount));
    }

    private ITextComponent getFluidName() {
        Fluid fluid = menu.getFluidType();
        if (fluid == null) {
            return new TranslationTextComponent("tooltip.examplemod.clay_pot.empty");
        }
        return new TranslationTextComponent(fluid.getAttributes().getTranslationKey());
    }

    private int getFluidColor(@Nullable Fluid fluid) {
        if (fluid == null || fluid.isSame(Fluids.WATER)) {
            return 0xFF3A7DFF;
        }
        if (fluid.isSame(ModFluids.DIRTY_WATER.get())) {
            return 0xFF7A4D2B;
        }
        return 0xFF6CC8F4;
    }

    private void updateModeButtonText() {
        if (modeButton != null) {
            modeButton.setMessage(StringTextComponent.EMPTY);
        }
    }

    private TranslationTextComponent getModeButtonTooltip() {
        return new TranslationTextComponent(menu.isDrainMode()
                ? "button.examplemod.clay_pot.mode.drain"
                : "button.examplemod.clay_pot.mode.fill");
    }

    private static final class ModeToggleButton extends Button {
        private final ClayPotScreen screen;

        private ModeToggleButton(ClayPotScreen screen, int x, int y) {
            super(x, y, ClayPotContainer.MODE_BUTTON_SIZE, ClayPotContainer.MODE_BUTTON_SIZE,
                    StringTextComponent.EMPTY,
                    button -> ModNetworkHandler.CHANNEL.sendToServer(
                            new ClayPotModePacket(screen.menu.getBlockPos()))
            );
            this.screen = screen;
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
            int background = this.isHovered() ? 0xFF3F2410 : 0xFF2B190E;
            int border = this.isHovered() ? 0xFF7B4F29 : 0xFF5A3520;
            screen.fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, background);
            screen.fill(matrices, this.x, this.y, this.x + this.width, this.y + 1, border);
            screen.fill(matrices, this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, border);
            screen.fill(matrices, this.x, this.y, this.x + 1, this.y + this.height, border);
            screen.fill(matrices, this.x + this.width - 1, this.y, this.x + this.width, this.y + this.height, border);

            int arrowColor = this.isHovered() ? 0xFFF7E3C5 : 0xFFD7C2A0;
            int centerX = this.x + this.width / 2;
            int centerY = this.y + this.height / 2;
            screen.fill(matrices, centerX - 3, centerY - 1, centerX + 3, centerY + 1, arrowColor);
            if (screen.menu.isDrainMode()) {
                screen.fill(matrices, centerX - 5, centerY - 3, centerX - 3, centerY + 3, arrowColor);
            } else {
                screen.fill(matrices, centerX + 3, centerY - 3, centerX + 5, centerY + 3, arrowColor);
            }
        }
    }
}
