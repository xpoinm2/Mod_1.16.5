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
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.tags.ItemTags;

import javax.annotation.Nullable;

public class FirepitTileEntity extends LockableTileEntity implements ITickableTileEntity {
    public static final int COOK_TIME_TOTAL = 400;
    public static final int CLAY_OVERCOOK_TIME = 300; // 15 seconds for clay items to turn into shards
    public static final int GRID_SLOT_COUNT = 12;
    public static final int FUEL_SLOT = GRID_SLOT_COUNT;
    public static final int MAX_HEAT = 100;
    public static final int SPECIFIC_HEAT_PER_HEAT_UNIT = 80;
    public static final int CONSUMPTION_INTERVAL_TICKS = 40;
    public static final int MIN_HEAT_FOR_SMELTING = 80;
    public static final int COOLING_INTERVAL_TICKS = 200;
    public static final int COOLING_AMOUNT = 4;
    private final NonNullList<ItemStack> items = NonNullList.withSize(GRID_SLOT_COUNT + 1, ItemStack.EMPTY);

    private final int[] slotCookTimes = new int[GRID_SLOT_COUNT];
    private final int[] slotCookingStages = new int[GRID_SLOT_COUNT]; // 0: not cooking, 1: raw->finished, 2: finished->shards

    private int heat;
    private int heatingTicks;
    private int cookProgress;
    private int cookProgressTotal = COOK_TIME_TOTAL;
    private int coolingTicks;

    private final IIntArray dataAccess = new IIntArray() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return heat;
                case 1:
                    return heatingTicks;
                case 2:
                    return cookProgress;
                case 3:
                    return cookProgressTotal;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    heat = Math.max(0, Math.min(MAX_HEAT, value));
                    break;
                case 1:
                    heatingTicks = Math.max(0, Math.min(CONSUMPTION_INTERVAL_TICKS, value));
                    break;
                case 2:
                    cookProgress = value;
                    break;
                case 3:
                    cookProgressTotal = Math.max(1, value);
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getCount() {
            return 4;
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

        boolean changed = false;

        if (heat > 0) {
            coolingTicks++;
            if (coolingTicks >= COOLING_INTERVAL_TICKS) {
                coolingTicks = 0;
                int newHeat = Math.max(0, heat - COOLING_AMOUNT);
                if (newHeat != heat) {
                    heat = newHeat;
                    changed = true;
                }
            }
        } else if (coolingTicks != 0) {
            coolingTicks = 0;
        }


        ItemStack fuelStack = items.get(FUEL_SLOT);
        boolean hasInput = hasSmeltableInput();
        int specificHeat = getSpecificHeat(fuelStack);

        if (heat < MAX_HEAT && specificHeat > 0) {
            if (heatingTicks < CONSUMPTION_INTERVAL_TICKS) {
                heatingTicks++;
            }
            if (heatingTicks >= CONSUMPTION_INTERVAL_TICKS) {
                heatingTicks = 0;
                ItemStack containerItem = fuelStack.getContainerItem();
                fuelStack.shrink(1);
                if (fuelStack.isEmpty()) {
                    items.set(FUEL_SLOT, containerItem);
                }
                int heatGain = Math.max(1, specificHeat / SPECIFIC_HEAT_PER_HEAT_UNIT);
                int newHeat = Math.min(MAX_HEAT, heat + heatGain);
                if (newHeat != heat) {
                    heat = newHeat;
                    changed = true;
                }
                changed = true;
            }
        } else if (heatingTicks != 0) {
            heatingTicks = 0;
            changed = true;
        }

        if (heat >= MIN_HEAT_FOR_SMELTING && hasInput) {
            if (processCookingCycle()) {
                changed = true;
            }
        } else if (!hasInput) {
            resetCookingProgress();
        }

        updateCookProgress();

        if (changed) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private boolean hasSmeltableInput() {
        for (int i = 0; i < GRID_SLOT_COUNT; ++i) {
            if (isSmeltable(items.get(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean processCookingCycle() {
        boolean changed = false;
        for (int i = 0; i < GRID_SLOT_COUNT; ++i) {
            ItemStack stack = items.get(i);
            if (isSmeltable(stack)) {
                slotCookTimes[i]++;
                int requiredTime = getRequiredCookTime(stack, slotCookingStages[i]);
                if (slotCookTimes[i] >= requiredTime) {
                    ItemStack result = getCookingResult(stack, slotCookingStages[i]);
                    items.set(i, result);
                    slotCookTimes[i] = 0;

                    // If we just cooked raw clay to finished clay, start the overcooking stage
                    if (isRawClayItem(stack) && slotCookingStages[i] == 0) {
                        slotCookingStages[i] = 1; // Start overcooking stage
                    } else {
                        slotCookingStages[i] = 0; // Reset stage
                    }

                    changed = true;
                }
            } else if (slotCookTimes[i] != 0) {
                slotCookTimes[i] = 0;
                slotCookingStages[i] = 0;
            }
        }
        return changed;
    }

    private boolean isRawClayItem(ItemStack stack) {
        return stack.getItem() == ModItems.RAW_CLAY_CUP.get() || stack.getItem() == ModItems.RAW_CLAY_POT.get();
    }

    private boolean isFinishedClayItem(ItemStack stack) {
        return stack.getItem() == ModItems.CLAY_CUP.get() || stack.getItem() == ModItems.CLAY_POT.get();
    }

    private int getRequiredCookTime(ItemStack stack, int stage) {
        if (stack.getItem() == ModItems.PURE_IRON_ORE.get()) {
            return COOK_TIME_TOTAL;
        } else if (isRawClayItem(stack) && stage == 0) {
            return COOK_TIME_TOTAL;
        } else if (isFinishedClayItem(stack) && stage == 1) {
            return CLAY_OVERCOOK_TIME;
        }
        return COOK_TIME_TOTAL;
    }

    private ItemStack getCookingResult(ItemStack stack, int stage) {
        if (stack.getItem() == ModItems.PURE_IRON_ORE.get()) {
            return new ItemStack(ModItems.CALCINED_IRON_ORE.get());
        } else if (stack.getItem() == ModItems.RAW_CLAY_CUP.get() && stage == 0) {
            return new ItemStack(ModItems.CLAY_CUP.get());
        } else if (stack.getItem() == ModItems.CLAY_CUP.get() && stage == 1) {
            return new ItemStack(ModItems.CLAY_SHARDS.get());
        } else if (stack.getItem() == ModItems.RAW_CLAY_POT.get() && stage == 0) {
            return new ItemStack(ModItems.CLAY_POT.get());
        } else if (stack.getItem() == ModItems.CLAY_POT.get() && stage == 1) {
            return new ItemStack(ModItems.CLAY_SHARDS.get());
        }
        return stack; // fallback
    }

    private void resetCookingProgress() {
        for (int i = 0; i < GRID_SLOT_COUNT; ++i) {
            slotCookTimes[i] = 0;
            slotCookingStages[i] = 0;
        }
        cookProgress = 0;
        cookProgressTotal = COOK_TIME_TOTAL;
    }

    private void updateCookProgress() {
        int maxProgress = 0;
        boolean hasInput = false;
        for (int i = 0; i < GRID_SLOT_COUNT; ++i) {
            if (isSmeltable(items.get(i))) {
                hasInput = true;
                if (slotCookTimes[i] > maxProgress) {
                    maxProgress = slotCookTimes[i];
                }
            }
        }
        cookProgress = hasInput ? maxProgress : 0;
        cookProgressTotal = COOK_TIME_TOTAL;
    }

    private void enforceInputStackLimits() {
        for (int i = 0; i < GRID_SLOT_COUNT; ++i) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty() && stack.getCount() > 1) {
                stack.setCount(1);
            }
        }
    }

    private void onInventoryChanged() {
        enforceInputStackLimits();
        for (int i = 0; i < GRID_SLOT_COUNT; ++i) {
            if (!isSmeltable(items.get(i))) {
                slotCookTimes[i] = 0;
                slotCookingStages[i] = 0;
            }
        }
        updateCookProgress();
    }

    public boolean isHeating() {
        return heatingTicks > 0 || (heat < MAX_HEAT && getSpecificHeat(items.get(FUEL_SLOT)) > 0);
    }

    public int getHeat() {
        return heat;
    }

    public boolean isSmeltable(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.getItem() == ModItems.PURE_IRON_ORE.get()
                || stack.getItem() == ModItems.RAW_CLAY_CUP.get()
                || stack.getItem() == ModItems.CLAY_CUP.get()
                || stack.getItem() == ModItems.RAW_CLAY_POT.get()
                || stack.getItem() == ModItems.CLAY_POT.get();
    }


    public IIntArray getDataAccess() {
        return dataAccess;
    }
    // LockableTileEntity (official Mojang mappings) does not expose helper
    // hooks such as getItems/setItems, so inventory access is implemented via
    // the IInventory methods below.

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
        if (!stack.isEmpty()) {
            onInventoryChanged();
            setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = ItemStackHelper.takeItem(items, index);
        if (!stack.isEmpty()) {
            onInventoryChanged();
            setChanged();
        }
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        items.set(index, stack);
        int max = (index < GRID_SLOT_COUNT) ? 1 : getMaxStackSize();
        if (stack.getCount() > max) {
            stack.setCount(max);
        }

        onInventoryChanged();

        coolingTicks = 0;
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
        resetCookingProgress();
        heat = 0;
        heatingTicks = 0;
        coolingTicks = 0;
        setChanged();
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        for (int i = 0; i < items.size(); ++i) {
            items.set(i, ItemStack.EMPTY);
        }
        ItemStackHelper.loadAllItems(nbt, items);
        enforceInputStackLimits();
        heat = Math.max(0, Math.min(MAX_HEAT, nbt.getInt("Heat")));
        heatingTicks = Math.max(0,
                Math.min(CONSUMPTION_INTERVAL_TICKS, nbt.getInt("HeatingTicks")));
        coolingTicks = Math.max(0, Math.min(COOLING_INTERVAL_TICKS, nbt.getInt("CoolingTicks")));
        int[] savedCookTimes = nbt.getIntArray("SlotCookTimes");
        if (savedCookTimes.length == GRID_SLOT_COUNT) {
            System.arraycopy(savedCookTimes, 0, slotCookTimes, 0, GRID_SLOT_COUNT);
        } else {
            for (int i = 0; i < GRID_SLOT_COUNT; ++i) {
                slotCookTimes[i] = 0;
            }
        }
        int[] savedCookingStages = nbt.getIntArray("SlotCookingStages");
        if (savedCookingStages.length == GRID_SLOT_COUNT) {
            System.arraycopy(savedCookingStages, 0, slotCookingStages, 0, GRID_SLOT_COUNT);
        } else {
            for (int i = 0; i < GRID_SLOT_COUNT; ++i) {
                slotCookingStages[i] = 0;
            }
        }
        cookProgress = nbt.contains("CookProgress") ? nbt.getInt("CookProgress") : 0;
        cookProgressTotal = nbt.contains("CookProgressTotal")
                ? Math.max(1, nbt.getInt("CookProgressTotal"))
                : COOK_TIME_TOTAL;
        updateCookProgress();
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        ItemStackHelper.saveAllItems(nbt, items);
        nbt.putInt("Heat", heat);
        nbt.putInt("HeatingTicks", heatingTicks);
        nbt.putInt("CoolingTicks", coolingTicks);
        nbt.putIntArray("SlotCookTimes", slotCookTimes);
        nbt.putIntArray("SlotCookingStages", slotCookingStages);
        nbt.putInt("CookProgress", cookProgress);
        nbt.putInt("CookProgressTotal", cookProgressTotal);
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

    private int getSpecificHeat(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        if (stack.getItem() == Items.COAL || stack.getItem() == Items.CHARCOAL) {
            return 1600;
        }
        if (stack.getItem().is(ItemTags.PLANKS)) {
            return 160;
        }
        if (stack.getItem().is(ItemTags.LOGS)) {
            return 320;
        }
        if (stack.getItem() == Items.STICK) {
            return 80;
        }
        return 0;
    }
}