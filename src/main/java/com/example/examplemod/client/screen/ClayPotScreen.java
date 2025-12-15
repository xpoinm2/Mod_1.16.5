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
    private static final int FLUID_GAUGE_X = 144;
    private static final int FLUID_GAUGE_Y = 26;
    private static final int FLUID_GAUGE_WIDTH = 18;
    private static final int FLUID_GAUGE_HEIGHT = 82;

    private Button modeButton;

    public ClayPotScreen(ClayPotContainer screenContainer, PlayerInventory inv, ITextComponent title) {
        super(screenContainer, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 12;
        this.titleLabelY = 8;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
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
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight,
                this.imageWidth, this.imageHeight);
        renderFluidGauge(matrixStack);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        updateModeButtonText();
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title, this.titleLabelX, this.titleLabelY, 0xFFD9AC71);
        this.font.draw(matrixStack, this.inventory.getDisplayName(), this.inventoryLabelX, this.inventoryLabelY, 0xFFD9AC71);
        renderFluidLabel(matrixStack);
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

    private void renderFluidLabel(MatrixStack matrixStack) {
        String amount = menu.getFluidAmount() + " / " + menu.getFluidCapacity();
        ITextComponent fluidName = getFluidName();
        ITextComponent label = new TranslationTextComponent("tooltip.examplemod.clay_pot.water",
                fluidName,
                new StringTextComponent(amount));
        int textX = FLUID_GAUGE_X - 12;
        int textY = FLUID_GAUGE_Y - 12;
        this.font.draw(matrixStack, label, textX, textY, 0xFFBFBFBF);
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
            modeButton.setMessage(getModeButtonText());
        }
    }

    private TranslationTextComponent getModeButtonText() {
        return new TranslationTextComponent(menu.isDrainMode()
                ? "button.examplemod.clay_pot.mode.drain"
                : "button.examplemod.clay_pot.mode.fill");
    }
}
