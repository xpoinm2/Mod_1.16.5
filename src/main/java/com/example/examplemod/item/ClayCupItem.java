package com.example.examplemod.item;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModFluids;
import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.tileentity.ClayPotTileEntity;
import com.example.examplemod.util.FluidTextUtil;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncAllStatsPacket;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.fml.network.PacketDistributor;

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
        ItemStack cupForFluidOps = stack;
        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
        if (handler == null) {
            return ActionResult.pass(stack);
        }

        FluidStack contained = handler.getFluidInTank(0);
        int currentAmount = contained.getAmount();
        boolean needsFill = currentAmount < CAPACITY;
        boolean stackIsEmpty = contained.isEmpty();
        boolean bucketEligible = needsFill && stackIsEmpty && stack.getCount() == stack.getMaxStackSize();

        ItemStack drainCupForPot = stack;
        IFluidHandler drainHandler = handler;
        boolean usingSeparateCupForDrain = false;
        if (!needsFill && stack.getCount() > 1 && currentAmount > 0) {
            drainCupForPot = stack.copy();
            drainCupForPot.setCount(1);
            drainHandler = drainCupForPot.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
            if (drainHandler == null) {
                return ActionResult.pass(stack);
            }
            usingSeparateCupForDrain = true;
        }

        RayTraceContext.FluidMode fluidMode = needsFill ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE;
        BlockRayTraceResult rayTraceResult = Item.getPlayerPOVHitResult(world, player, fluidMode);
        if (rayTraceResult.getType() == RayTraceResult.Type.MISS) {
            if (currentAmount > 0) {
                player.startUsingItem(hand);
                return ActionResult.sidedSuccess(stack, world.isClientSide());
            }
            return ActionResult.pass(stack);
        }

        if (rayTraceResult.getType() != RayTraceResult.Type.BLOCK) {
            if (currentAmount > 0) {
                player.startUsingItem(hand);
                return ActionResult.sidedSuccess(stack, world.isClientSide());
            }
            return ActionResult.pass(stack);
        }

        BlockPos hitPos = rayTraceResult.getBlockPos();
        Direction direction = rayTraceResult.getDirection();
        if (!world.mayInteract(player, hitPos)) {
            if (currentAmount > 0) {
                player.startUsingItem(hand);
                return ActionResult.sidedSuccess(stack, world.isClientSide());
            }
            return ActionResult.pass(stack);
        }

        boolean usingSeparateCup = false;
        if (needsFill && stack.getCount() > 1 && stackIsEmpty && !bucketEligible) {
            cupForFluidOps = stack.copy();
            cupForFluidOps.setCount(1);
            handler = cupForFluidOps.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
            if (handler == null) {
                return ActionResult.pass(stack);
            }
            usingSeparateCup = true;
        }

        if (needsFill) {
            FluidState fluidState = world.getFluidState(hitPos);
            Fluid sourceFluid = null;
            if (fluidState.isSource()) {
                Fluid fluid = fluidState.getType();
                if (fluid.isSame(Fluids.WATER)) {
                    sourceFluid = Fluids.WATER;
                } else if (fluid.isSame(ModFluids.DIRTY_WATER.get()) || fluid.isSame(ModFluids.DIRTY_WATER_FLOWING.get())) {
                    sourceFluid = ModFluids.DIRTY_WATER.get();
                }
            }
            if (sourceFluid != null) {
                if (bucketEligible && sourceFluid.isSame(Fluids.WATER)) {
                    if (!world.isClientSide) {
                        stack.shrink(1);
                        ItemStack waterBucket = new ItemStack(Items.WATER_BUCKET);
                        if (!player.addItem(waterBucket)) {
                            player.drop(waterBucket, false);
                        }
                        world.playSound(player, hitPos, SoundEvents.BUCKET_FILL, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        player.awardStat(Stats.ITEM_USED.get(this));
                    } else {
                        stack.shrink(1);
                    }
                    return ActionResult.sidedSuccess(stack, world.isClientSide());
                }

                FluidStack fillStack = new FluidStack(sourceFluid, CAPACITY);
                int canFill = handler.fill(fillStack, IFluidHandler.FluidAction.SIMULATE);
                if (canFill > 0) {
                    if (!world.isClientSide) {
                        handler.fill(new FluidStack(sourceFluid, canFill), IFluidHandler.FluidAction.EXECUTE);
                        world.setBlock(hitPos, Blocks.AIR.defaultBlockState(), 11);
                    }
                    giveOperatedCupToPlayer(world, player, hand, stack, cupForFluidOps, usingSeparateCup);
                    if (!world.isClientSide) {
                        world.playSound(player, hitPos, SoundEvents.BUCKET_FILL, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        player.awardStat(Stats.ITEM_USED.get(this));
                    }
                    return ActionResult.sidedSuccess(stack, world.isClientSide());
                }
            }
            return ActionResult.pass(stack);
        }

        TileEntity tile = world.getBlockEntity(hitPos);
        if (tile instanceof ClayPotTileEntity) {
            ClayPotTileEntity pot = (ClayPotTileEntity) tile;
            LazyOptional<IFluidHandler> potCap = pot.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction);
            if (potCap.isPresent()) {
                IFluidHandler potHandler = potCap.orElse(null);
                if (potHandler != null) {
                    if (needsFill) {
                        FluidStack available = potHandler.drain(CAPACITY, IFluidHandler.FluidAction.SIMULATE);
                        if (!available.isEmpty()) {
                            int canFill = handler.fill(available, IFluidHandler.FluidAction.SIMULATE);
                            if (canFill > 0) {
                                if (!world.isClientSide) {
                                    FluidStack drained = potHandler.drain(canFill, IFluidHandler.FluidAction.EXECUTE);
                                    handler.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                                    world.playSound(null, hitPos, SoundEvents.BOTTLE_FILL, SoundCategory.PLAYERS, 1.0F, 1.0F);
                                    player.awardStat(Stats.ITEM_USED.get(this));
                                }
                                giveOperatedCupToPlayer(world, player, hand, stack, cupForFluidOps, usingSeparateCup);
                                return ActionResult.sidedSuccess(stack, world.isClientSide());
                            }
                        }
                    }
                    if (!needsFill && currentAmount > 0) {
                        FluidStack available = drainHandler.drain(CAPACITY, IFluidHandler.FluidAction.SIMULATE);
                        int accepted = potHandler.fill(available, IFluidHandler.FluidAction.SIMULATE);
                        if (accepted > 0) {
                            if (!world.isClientSide) {
                                FluidStack drained = drainHandler.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
                                potHandler.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                                world.playSound(null, hitPos, SoundEvents.BOTTLE_EMPTY, SoundCategory.PLAYERS, 0.6F, 1.0F);
                                player.awardStat(Stats.ITEM_USED.get(this));
                            }
                            giveOperatedCupToPlayer(world, player, hand, stack, drainCupForPot, usingSeparateCupForDrain);
                            return ActionResult.sidedSuccess(stack, world.isClientSide());
                        }
                    }
                }
            }
        }

        if (!needsFill && currentAmount > 0 && tryPourFluid(world, player, drainHandler, rayTraceResult)) {
            giveOperatedCupToPlayer(world, player, hand, stack, drainCupForPot, usingSeparateCupForDrain);
            return ActionResult.sidedSuccess(stack, world.isClientSide());
        }

        if (currentAmount > 0) {
            player.startUsingItem(hand);
            return ActionResult.sidedSuccess(stack, world.isClientSide());
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
                Fluid fluid = fluidStack.getFluid();
                if (fluid.isSame(ModFluids.DIRTY_WATER.get())
                        || fluid.isSame(ModFluids.DIRTY_WATER_FLOWING.get())) {
                    tooltip.add(new TranslationTextComponent("tooltip.examplemod.clay_cup.dirty_water", currentAmount, capacity)
                            .withStyle(TextFormatting.GOLD));
                } else if (fluid.isSame(Fluids.WATER)) {
                    tooltip.add(new TranslationTextComponent("tooltip.examplemod.clay_cup.water", currentAmount, capacity)
                            .withStyle(TextFormatting.BLUE));
                } else {
                    tooltip.add(new TranslationTextComponent("tooltip.examplemod.clay_cup.unknown", currentAmount, capacity)
                            .withStyle(TextFormatting.GRAY));
                }
            }
        });
    }

    protected static class ClayCupFluidHandler extends FluidHandlerItemStackSimple {
        private ClayCupFluidHandler(ItemStack container) {
            super(container, CAPACITY);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return stack.getFluid().isSame(Fluids.WATER)
                    || stack.getFluid().isSame(ModFluids.DIRTY_WATER.get())
                    || stack.getFluid().isSame(ModFluids.DIRTY_WATER_FLOWING.get());
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

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity livingEntity) {
        if (!(livingEntity instanceof PlayerEntity)) {
            return stack;
        }
        PlayerEntity player = (PlayerEntity) livingEntity;
        if (world.isClientSide()) {
            return stack;
        }
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
            FluidStack drained = handler.drain(CAPACITY, IFluidHandler.FluidAction.EXECUTE);
            if (drained.isEmpty() || !(player instanceof ServerPlayerEntity)) {
                return;
            }
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            applyDrinkEffects(serverPlayer, drained);
            player.awardStat(Stats.ITEM_USED.get(this));
            world.playSound(null, player.blockPosition(), SoundEvents.GENERIC_DRINK, SoundCategory.PLAYERS, 1.0F, 1.0F);
        });
        return stack;
    }

    private boolean tryPourFluid(World world, PlayerEntity player, IFluidHandler handler, BlockRayTraceResult traceResult) {
        FluidStack fluidStack = handler.getFluidInTank(0);
        if (fluidStack.isEmpty()) {
            return false;
        }
        BlockState pourState = getBlockStateForFluid(fluidStack.getFluid());
        if (pourState == null) {
            return false;
        }
        BlockPos placePos = traceResult.getBlockPos().relative(traceResult.getDirection());
        BlockState targetState = world.getBlockState(placePos);
        if (!targetState.isAir() && !targetState.getMaterial().isReplaceable()) {
            return false;
        }
        if (!world.isClientSide) {
            int amount = Math.min(fluidStack.getAmount(), CAPACITY);
            handler.drain(amount, IFluidHandler.FluidAction.EXECUTE);
            world.setBlock(placePos, pourState, 11);
            world.playSound(null, placePos, SoundEvents.BUCKET_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.0F);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return true;
    }

    private void giveOperatedCupToPlayer(World world, PlayerEntity player, Hand hand, ItemStack handStack, ItemStack filledCup, boolean usedCopy) {
        if (!usedCopy) {
            return;
        }
        handStack.shrink(1);
        if (world.isClientSide) {
            return;
        }
        if (handStack.isEmpty()) {
            player.setItemInHand(hand, ItemStack.EMPTY);
        }
        if (!player.addItem(filledCup)) {
            player.drop(filledCup, false);
        }
    }

    @Nullable
    private BlockState getBlockStateForFluid(Fluid fluid) {
        if (fluid.isSame(ModFluids.DIRTY_WATER.get())) {
            return ModBlocks.DIRTY_WATER_BLOCK.get().defaultBlockState();
        }
        if (fluid.isSame(Fluids.WATER)) {
            return Blocks.WATER.defaultBlockState();
        }
        return null;
    }

    private void applyDrinkEffects(ServerPlayerEntity player, FluidStack fluid) {
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            int thirst = stats.getThirst();
            int disease = stats.getDisease();
            Fluid drinkFluid = fluid.getFluid();
            if (drinkFluid.isSame(Fluids.WATER)) {
                thirst = Math.max(0, thirst - 20);
            } else if (drinkFluid.isSame(ModFluids.DIRTY_WATER.get())) {
                thirst = Math.max(0, thirst - 15);
                disease = Math.min(100, disease + 5);
                player.addEffect(new EffectInstance(Effects.CONFUSION, 100, 0));
            } else {
                return;
            }
            stats.setThirst(thirst);
            stats.setDisease(disease);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncAllStatsPacket(stats)
            );
        });
    }
}