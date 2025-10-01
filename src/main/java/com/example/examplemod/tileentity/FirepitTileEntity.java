package com.example.examplemod.tileentity;

import com.example.examplemod.ModItems;
import com.example.examplemod.ModTileEntities;
import com.example.examplemod.container.FirepitContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;

public class FirepitTileEntity extends LockableTileEntity implements ITickableTileEntity {
    private static final double MAX_HEAT = 10.0D;
    private static final double HEAT_PER_STAGE = MAX_HEAT / 4.0D;
    private static final double FUEL_UNITS_FOR_MAX_HEAT = 3.0D;
    private static final int COAL_BURN_TIME = 1600;
    private static final int MAX_HOLD_TICKS = 2400;
    private static final int COOLDOWN_STEP_TICKS = 200;
    private static final int PROCESS_TICKS = 1200;

    private final NonNullList<ItemStack> items = NonNullList.withSize(14, ItemStack.EMPTY);

    private int burnTime;
    private int currentItemBurnTime;
    private double heatLevel;
    private int holdTicks;
    private int cooldownTicks;
    private int processingTicks;

    private final IIntArray dataAccess = new IIntArray() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return (int) Math.round(heatLevel * 1000.0D);
                case 1:
                    return burnTime;
                case 2:
                    return currentItemBurnTime;
                case 3:
                    return processingTicks;
                case 4:
                    return holdTicks;
                case 5:
                    return cooldownTicks;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    heatLevel = value / 1000.0D;
                    break;
                case 1:
                    burnTime = value;
                    break;
                case 2:
                    currentItemBurnTime = value;
                    break;
                case 3:
                    processingTicks = value;
                    break;
                case 4:
                    holdTicks = value;
                    break;
                case 5:
                    cooldownTicks = value;
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public FirepitTileEntity() {
        super(ModTileEntities.FIREPIT.get());
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) {
            return;
        }

        boolean wasBurning = burnTime > 0;
        double prevHeat = heatLevel;
        int prevHold = holdTicks;
        int prevCooldown = cooldownTicks;
        int prevProcessing = processingTicks;
        boolean changed = false;
        boolean notifyBlock = false;

        if (burnTime > 0) {
            burnTime--;
            cooldownTicks = 0;
        }

        ItemStack fuelStack = items.get(13);
        if (burnTime <= 0 && canConsumeFuel(fuelStack)) {
            int fuelBurnTime = ForgeHooks.getBurnTime(fuelStack, IRecipeType.SMELTING);
            if (fuelBurnTime > 0) {
                burnTime = currentItemBurnTime = fuelBurnTime;
                double normalizedBurn = (double) fuelBurnTime / COAL_BURN_TIME;
                double heatIncrement = normalizedBurn * (MAX_HEAT / FUEL_UNITS_FOR_MAX_HEAT);
                heatLevel = Math.min(MAX_HEAT, heatLevel + heatIncrement);
                ItemStack containerItem = fuelStack.getContainerItem();
                fuelStack.shrink(1);
                if (fuelStack.isEmpty()) {
                    items.set(13, containerItem);
                }
                changed = true;
                notifyBlock = true;
            }
        }

        if (heatLevel >= MAX_HEAT) {
            heatLevel = MAX_HEAT;
            holdTicks = MAX_HOLD_TICKS;
            cooldownTicks = 0;
        } else if (holdTicks > 0) {
            holdTicks--;
            heatLevel = MAX_HEAT;
        } else if (heatLevel > 0.0D) {
            if (burnTime <= 0) {
                cooldownTicks++;
                if (cooldownTicks >= COOLDOWN_STEP_TICKS) {
                    cooldownTicks = 0;
                    heatLevel = Math.max(0.0D, heatLevel - HEAT_PER_STAGE);
                }
            } else {
                cooldownTicks = 0;
            }
        } else {
            cooldownTicks = 0;
        }

        ItemStack oreStack = items.get(12);
        if (oreStack.isEmpty() || oreStack.getItem() != ModItems.PURE_IRON_ORE.get()) {
            if (processingTicks != 0) {
                processingTicks = 0;
                changed = true;
            }
        } else if (isHeatAtMaximum()) {
            processingTicks++;
            if (processingTicks >= PROCESS_TICKS) {
                ItemStack result = new ItemStack(ModItems.CALCINED_IRON_ORE.get(), oreStack.getCount());
                items.set(12, result);
                processingTicks = 0;
                changed = true;
                notifyBlock = true;
            }
        }

        if (wasBurning != (burnTime > 0)) {
            changed = true;
            notifyBlock = true;
        }

        if (!changed) {
            if (Math.abs(prevHeat - heatLevel) > 1.0E-5D || prevHold != holdTicks
                    || prevCooldown != cooldownTicks || prevProcessing != processingTicks) {
                changed = true;
            }
        }

        if (changed) {
            setChanged();
            if (notifyBlock) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    private boolean canConsumeFuel(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (isHeatAtMaximum() && holdTicks > 0) {
            return false;
        }
        return ForgeHooks.getBurnTime(stack, IRecipeType.SMELTING) > 0;
    }

    public boolean canAcceptOre(ItemStack stack) {
        return stack.getItem() == ModItems.PURE_IRON_ORE.get() && isHeatAtMaximum();
    }

    public boolean isHeatAtMaximum() {
        return heatLevel >= MAX_HEAT - 1.0E-4D;
    }

    public IIntArray getDataAccess() {
        return dataAccess;
    }

    public double getHeatLevel() {
        return heatLevel;
    }

    public int getProcessingTicks() {
        return processingTicks;
    }


    protected NonNullList<ItemStack> getItems() {
        return items;
    }


    protected void setItems(NonNullList<ItemStack> stacks) {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY);
        }
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new StringTextComponent("Кострище");
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack stack = ItemStackHelper.removeItem(items, index, count);
        if (index == 12 && !stack.isEmpty()) {
            processingTicks = 0;
        }
        if (!stack.isEmpty()) {
            setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = ItemStackHelper.takeItem(items, index);
        if (index == 12 && !stack.isEmpty()) {
            processingTicks = 0;
        }
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        items.set(index, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        if (index == 12) {
            processingTicks = 0;
        }
        setChanged();
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        if (level == null) {
            return false;
        }
        if (level.getBlockEntity(worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(
                (double) worldPosition.getX() + 0.5D,
                (double) worldPosition.getY() + 0.5D,
                (double) worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < items.size(); ++i) {
            items.set(i, ItemStack.EMPTY);
        }
        processingTicks = 0;
        setChanged();
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        for (int i = 0; i < items.size(); ++i) {
            items.set(i, ItemStack.EMPTY);
        }
        ItemStackHelper.loadAllItems(nbt, items);
        burnTime = nbt.getInt("BurnTime");
        currentItemBurnTime = nbt.getInt("CurrentItemBurnTime");
        heatLevel = nbt.getDouble("HeatLevel");
        holdTicks = nbt.getInt("HoldTicks");
        cooldownTicks = nbt.getInt("CooldownTicks");
        processingTicks = nbt.getInt("ProcessingTicks");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        ItemStackHelper.saveAllItems(nbt, items);
        nbt.putInt("BurnTime", burnTime);
        nbt.putInt("CurrentItemBurnTime", currentItemBurnTime);
        nbt.putDouble("HeatLevel", heatLevel);
        nbt.putInt("HoldTicks", holdTicks);
        nbt.putInt("CooldownTicks", cooldownTicks);
        nbt.putInt("ProcessingTicks", processingTicks);
        return nbt;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        load(getBlockState(), pkt.getTag());
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory) {
        return new FirepitContainer(id, playerInventory, this);
    }

    public static int getProcessTicks() {
        return PROCESS_TICKS;
    }

    public static double getMaxHeat() {
        return MAX_HEAT;
    }
}