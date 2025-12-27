package com.example.examplemod.item;

import com.example.examplemod.ModFluids;
import com.example.examplemod.tileentity.ClayPotTileEntity;
import com.example.examplemod.util.FluidTextUtil;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.List;

public class ClayPotBlockItem extends BlockItem {
    public ClayPotBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundNBT nbt) {
        return new ClayPotFluidHandler(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if (CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY != null) {
            stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
                FluidStack fluidStack = handler.getFluidInTank(0);
                if (fluidStack.isEmpty() || fluidStack.getAmount() <= 0) {
                    tooltip.add(new TranslationTextComponent("tooltip.examplemod.clay_pot.empty").withStyle(TextFormatting.GRAY));
                } else {
                    ITextComponent currentAmount = FluidTextUtil.formatAmount(fluidStack.getAmount());
                    ITextComponent capacity = FluidTextUtil.formatAmount(ClayPotTileEntity.CAPACITY);
                    tooltip.add(new TranslationTextComponent("tooltip.examplemod.clay_pot.water", currentAmount, capacity)
                            .withStyle(TextFormatting.BLUE));
                }
            });
        }
    }

    protected static class ClayPotFluidHandler extends FluidHandlerItemStackSimple {
        protected ClayPotFluidHandler(ItemStack container) {
            super(container, ClayPotTileEntity.CAPACITY);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return stack.getFluid().isSame(net.minecraft.fluid.Fluids.WATER)
                    || stack.getFluid().isSame(ModFluids.DIRTY_WATER.get())
                    || stack.getFluid().isSame(ModFluids.DIRTY_WATER_FLOWING.get());
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            if (container == null || CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY == null) {
                return LazyOptional.empty();
            }
            return super.getCapability(cap, side);
        }
    }
}