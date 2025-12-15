package com.example.examplemod.client.screen;

import com.example.examplemod.ModFluids;
import com.example.examplemod.container.ClayPotContainer;
import com.example.examplemod.util.FluidTextUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ClayPotScreen extends ContainerScreen<ClayPotContainer> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");

    public ClayPotScreen(ClayPotContainer screenContainer, PlayerInventory inv, ITextComponent title) {
        super(screenContainer, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        renderFluidBar(matrixStack);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderHoveredTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderHoveredTooltip(matrixStack, mouseX, mouseY);
        if (isHoveringFluidBar(mouseX, mouseY)) {
            List<ITextComponent> tooltip = new ArrayList<>();
            Fluid fluid = menu.getFluidType();
            int amount = menu.getFluidAmount();
            if (fluid == null || amount <= 0) {
                tooltip.add(new TranslationTextComponent("tooltip.examplemod.clay_pot.empty"));
            } else {
                FluidStack stack = new FluidStack(fluid, amount);
                tooltip.add(new TranslationTextComponent(
                        "tooltip.examplemod.clay_pot.fluid",
                        stack.getDisplayName(),
                        FluidTextUtil.formatAmount(amount),
                        FluidTextUtil.formatAmount(menu.getFluidCapacity())));
            }
            this.renderTooltip(matrixStack, tooltip, mouseX, mouseY);
        }
    }

    private void renderFluidBar(MatrixStack matrixStack) {
        int x = this.leftPos + ClayPotContainer.FLUID_BAR_X;
        int y = this.topPos + ClayPotContainer.FLUID_BAR_Y;
        int width = ClayPotContainer.FLUID_BAR_WIDTH;
        int height = ClayPotContainer.FLUID_BAR_HEIGHT;

        fill(matrixStack, x - 1, y - 1, x + width + 1, y + height + 1, 0xFF7F7F7F);
        fill(matrixStack, x, y, x + width, y + height, 0xFF000000);

        int amount = menu.getFluidAmount();
        int capacity = menu.getFluidCapacity();
        if (capacity > 0 && amount > 0) {
            int filled = Math.round((float) amount / capacity * height);
            int color = getFluidColor(menu.getFluidType());
            fill(matrixStack, x, y + height - filled, x + width, y + height, color);
        }
    }

    private boolean isHoveringFluidBar(int mouseX, int mouseY) {
        int x = this.leftPos + ClayPotContainer.FLUID_BAR_X;
        int y = this.topPos + ClayPotContainer.FLUID_BAR_Y;
        int width = ClayPotContainer.FLUID_BAR_WIDTH;
        int height = ClayPotContainer.FLUID_BAR_HEIGHT;
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private int getFluidColor(@Nullable Fluid fluid) {
        if (fluid == null) {
            return 0x00000000;
        }
        if (fluid.isSame(ModFluids.DIRTY_WATER.get()) || fluid.isSame(ModFluids.DIRTY_WATER_FLOWING.get())) {
            return 0xFF8B8B8B;
        }
        if (fluid.isSame(Fluids.WATER)) {
            return 0xFF3F76E4;
        }
        return 0xFF3F76E4;
    }

