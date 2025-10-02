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
    private static final int COOK_TIME_TOTAL = 200;
    private static final int GRID_SLOT_COUNT = 12;
    private static final int FUEL_SLOT = GRID_SLOT_COUNT;
    private final NonNullList<ItemStack> items = NonNullList.withSize(GRID_SLOT_COUNT + 1, ItemStack.EMPTY);

    private int burnTime;
    private int burnTimeTotal;
    private int cookTime;
    private int cookTimeTotal = COOK_TIME_TOTAL;
    /** Number of smelts completed during the current aggregate cooking cycle. */
    private int completedSmelts;

    private final IIntArray dataAccess = new IIntArray() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return burnTime;
                case 1:
                    return burnTimeTotal;
                case 2:
                    return cookTime;
                case 3:
                    return cookTimeTotal;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    burnTime = value;
                    break;
                case 1:
                    burnTimeTotal = value;
                    break;
                case 2:
                    cookTime = value;
                    break;
                case 3:
                    cookTimeTotal = Math.max(1, value);
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

        boolean wasBurning = isBurning();
        boolean changed = false;

        if (burnTime > 0) {
            burnTime--;
        }

        ItemStack fuelStack = items.get(FUEL_SLOT);
        boolean hasInput = canSmelt();

        if (isBurning() || (!fuelStack.isEmpty() && hasInput)) {
            if (!isBurning() && hasInput) {
                burnTime = getFuelBurnTime(fuelStack);
                burnTimeTotal = burnTime;
                if (burnTime > 0) {
                    changed = true;
                    ItemStack containerItem = fuelStack.getContainerItem();
                    fuelStack.shrink(1);
                    if (fuelStack.isEmpty()) {
                        items.set(FUEL_SLOT, containerItem);
                    }
                }
                hasInput = canSmelt();
            }

            if (isBurning() && hasInput) {
                cookTime++;
                clampCompletedSmelts();
                if (handleCookingProgress()) {
                    changed = true;
                }
                hasInput = canSmelt();
            }

            if (!hasInput) {
                if (completedSmelts > 0 && cookTime >= cookTimeTotal) {
                    resetCookingProgress();
                }
            }
        } else if (!isBurning() && cookTime > 0) {
            cookTime = Math.max(cookTime - 2, 0);
            clampCompletedSmelts();
            if (cookTime == 0) {
                completedSmelts = 0;
            }
        }

        updateCookTimeTotal(countOreInputs());

        if (wasBurning != isBurning()) {
            changed = true;
        }

        if (changed) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private int getFuelBurnTime(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        int burn = ForgeHooks.getBurnTime(stack, IRecipeType.SMELTING);
        if (burn <= 0) {
            return 0;
        }
        burn /= 4; // Половинное потребление: 1 уголь переплавляет 2 руды
        return Math.max(burn, 0);
    }

    private boolean canSmelt() {
        return countOreInputs() > 0;
    }

    private boolean handleCookingProgress() {
        boolean changed = false;
        int oreCount = countOreInputs();
        updateCookTimeTotal(oreCount);

        int expectedSmelts = cookTime / COOK_TIME_TOTAL;
        while (expectedSmelts > completedSmelts) {
            if (smeltItem()) {
                completedSmelts++;
                changed = true;
            } else {
                cookTime = completedSmelts * COOK_TIME_TOTAL;
                break;
            }
            oreCount = countOreInputs();
            updateCookTimeTotal(oreCount);
            expectedSmelts = cookTime / COOK_TIME_TOTAL;
        }

        if (countOreInputs() == 0 && completedSmelts > 0 && cookTime >= cookTimeTotal) {
            resetCookingProgress();
            changed = true;
        }

        return changed;
    }

    private boolean smeltItem() {
        if (!canSmelt()) {
            return false;
        }

        int slot = findInputSlot();
        if (slot < 0) {
            return false;
        }

        ItemStack stack = items.get(slot);
        if (stack.getCount() > 1) {
            stack.shrink(1);
            ItemStack result = new ItemStack(ModItems.CALCINED_IRON_ORE.get());
            if (!storeOutput(result)) {
                stack.grow(1);
                return false;
            }
        } else {
            items.set(slot, new ItemStack(ModItems.CALCINED_IRON_ORE.get()));
        }
        return true;
    }

    private void clampCompletedSmelts() {
        int maxSmelts = cookTime / COOK_TIME_TOTAL;
        if (completedSmelts > maxSmelts) {
            completedSmelts = maxSmelts;
        }
        if (completedSmelts < 0) {
            completedSmelts = 0;
        }
    }

    private void resetCookingProgress() {
        cookTime = 0;
        cookTimeTotal = COOK_TIME_TOTAL;
        completedSmelts = 0;
    }

    private void updateCookTimeTotal(int oreCount) {
        clampCompletedSmelts();
        int totalWork = oreCount + completedSmelts;
        if (totalWork <= 0) {
            cookTimeTotal = COOK_TIME_TOTAL;
        } else {
            cookTimeTotal = totalWork * COOK_TIME_TOTAL;
        }
        if (cookTime > cookTimeTotal) {
            cookTime = cookTimeTotal;
            clampCompletedSmelts();
        }
    }

    private int countOreInputs() {
        int count = 0;
        for (int i = 0; i < GRID_SLOT_COUNT; ++i) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty() && stack.getItem() == ModItems.PURE_IRON_ORE.get()) {
                count += stack.getCount();
            }
        }
        return count;
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
        clampCompletedSmelts();
        int oreCount = countOreInputs();
        updateCookTimeTotal(oreCount);
        if (oreCount == 0 && completedSmelts == 0 && cookTime == 0) {
            cookTimeTotal = COOK_TIME_TOTAL;
        }
    }

    private boolean storeOutput(ItemStack result) {
        for (int i = 0; i < GRID_SLOT_COUNT && !result.isEmpty(); ++i) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) {
                ItemStack copy = result.copy();
                copy.setCount(Math.min(copy.getCount(), 1));
                items.set(i, copy);
                result.setCount(0);
                break;
            }

            if (ItemStack.isSame(stack, result) && ItemStack.tagMatches(stack, result)) {
                int slotLimit = 1;
                int transferable = Math.min(result.getCount(), slotLimit - stack.getCount());
                if (transferable > 0) {
                    stack.grow(transferable);
                    result.shrink(transferable);
                }
            }
        }

        return result.isEmpty();
    }

    private int findInputSlot() {
        for (int i = 0; i < GRID_SLOT_COUNT; ++i) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty() && stack.getItem() == ModItems.PURE_IRON_ORE.get()) {
                return i;
            }
        }
        return -1;
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    public boolean isSmeltable(ItemStack stack) {
        return stack.getItem() == ModItems.PURE_IRON_ORE.get();
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
        burnTime = 0;
        burnTimeTotal = 0;
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
        burnTime = nbt.getInt("BurnTime");
        burnTimeTotal = nbt.getInt("BurnTimeTotal");
        cookTime = nbt.getInt("CookTime");
        cookTimeTotal = Math.max(1, nbt.getInt("CookTimeTotal"));
        completedSmelts = Math.max(0, nbt.getInt("CompletedSmelts"));
        clampCompletedSmelts();
        updateCookTimeTotal(countOreInputs());
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        ItemStackHelper.saveAllItems(nbt, items);
        nbt.putInt("BurnTime", burnTime);
        nbt.putInt("BurnTimeTotal", burnTimeTotal);
        nbt.putInt("CookTime", cookTime);
        nbt.putInt("CookTimeTotal", cookTimeTotal);
        nbt.putInt("CompletedSmelts", completedSmelts);
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
}