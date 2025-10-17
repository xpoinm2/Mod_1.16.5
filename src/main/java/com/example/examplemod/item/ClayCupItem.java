package com.example.examplemod.item;

import com.example.examplemod.tileentity.ClayPotTileEntity;
import com.example.examplemod.util.FluidTextUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ClayCupItem extends Item {
    public static final int CAPACITY = 250;

    public ClayCupItem(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ClayCupFluidHandler(stack);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
        if (handler == null) {
            return ActionResult.pass(stack);
        }

        FluidStack contained = handler.getFluidInTank(0);
        int currentAmount = contained.getAmount();
        boolean needsFill = contained.isEmpty() || currentAmount < CAPACITY;
        RayTraceContext.FluidMode fluidMode = needsFill ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE;
        BlockRayTraceResult rayTraceResult = Item.getPlayerPOVHitResult(world, player, fluidMode);
        if (rayTraceResult.getType() == RayTraceResult.Type.MISS) {
            return ActionResult.pass(stack);
        }

        if (rayTraceResult.getType() != RayTraceResult.Type.BLOCK) {
            return ActionResult.pass(stack);
        }

        BlockPos hitPos = rayTraceResult.getBlockPos();
        Direction direction = rayTraceResult.getDirection();
        if (!world.mayInteract(player, hitPos)) {
            return ActionResult.pass(stack);
        }

        if (needsFill) {
            FluidState fluidState = world.getFluidState(hitPos);
            if (fluidState.is(FluidTags.WATER) && fluidState.isSource()) {
                FluidStack waterStack = new FluidStack(Fluids.WATER, CAPACITY);
                int canFill = handler.fill(waterStack, IFluidHandler.FluidAction.SIMULATE);
                if (canFill <= 0) {
                    return ActionResult.pass(stack);
                }
                if (!world.isClientSide) {
                    handler.fill(new FluidStack(Fluids.WATER, canFill), IFluidHandler.FluidAction.EXECUTE);
                    world.setBlock(hitPos, Blocks.AIR.defaultBlockState(), 11);
                }
                world.playSound(player, hitPos, SoundEvents.BUCKET_FILL, SoundCategory.PLAYERS, 1.0F, 1.0F);
                player.awardStat(Stats.ITEM_USED.get(this));
                return ActionResult.sidedSuccess(stack, world.isClientSide());
            }
            return ActionResult.pass(stack);
        }

        if (currentAmount <= 0) {
            return ActionResult.pass(stack);
        }

        TileEntity tile = world.getBlockEntity(hitPos);
        if (tile instanceof ClayPotTileEntity) {
            ClayPotTileEntity pot = (ClayPotTileEntity) tile;
            LazyOptional<IFluidHandler> potCap = pot.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction);
            if (potCap.isPresent()) {
                IFluidHandler potHandler = potCap.orElse(null);
                FluidStack transferable = new FluidStack(contained, Math.min(currentAmount, CAPACITY));
                int accepted = potHandler.fill(transferable, IFluidHandler.FluidAction.SIMULATE);
                if (accepted > 0) {
                    if (!world.isClientSide) {
                        potHandler.fill(new FluidStack(transferable, accepted), IFluidHandler.FluidAction.EXECUTE);
                        handler.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
                        world.playSound(null, hitPos, SoundEvents.BOTTLE_EMPTY, SoundCategory.PLAYERS, 0.6F, 1.0F);
                        player.awardStat(Stats.ITEM_USED.get(this));
                    }
                    return ActionResult.sidedSuccess(stack, world.isClientSide());
                }
            }
        }

        return ActionResult.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
            FluidStack fluidStack = handler.getFluidInTank(0);
            if (fluidStack.isEmpty() || fluidStack.getAmount() <= 0) {
                tooltip.add(new TranslationTextComponent("tooltip.examplemod.clay_cup.empty").withStyle(TextFormatting.GRAY));
            } else {
                ITextComponent currentAmount = FluidTextUtil.formatAmount(fluidStack.getAmount());
                ITextComponent capacity = FluidTextUtil.formatAmount(CAPACITY);
                tooltip.add(new TranslationTextComponent("tooltip.examplemod.clay_cup.water", currentAmount, capacity)
                        .withStyle(TextFormatting.BLUE));
            }
        });
    }

    private static class ClayCupFluidHandler extends FluidHandlerItemStackSimple {
        private ClayCupFluidHandler(ItemStack container) {
            super(container, CAPACITY);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return stack.getFluid().isSame(Fluids.WATER);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
            if (CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY == null) {
                return LazyOptional.empty();
            }
            return super.getCapability(capability, facing);
        }
    }
}