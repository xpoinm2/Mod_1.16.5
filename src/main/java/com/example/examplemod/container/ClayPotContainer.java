package com.example.examplemod.container;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModContainers;
import com.example.examplemod.ModFluids;
import com.example.examplemod.tileentity.ClayPotTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import javax.annotation.Nullable;

public class ClayPotContainer extends Container {
    public static final int GRID_SIZE = 3;
    public static final int GRID_START_X = 8;
    public static final int GRID_START_Y = 17;
    public static final int FLUID_INPUT_X = 140;
    public static final int FLUID_INPUT_Y = 17;
    public static final int FLUID_OUTPUT_X = FLUID_INPUT_X;
    public static final int FLUID_OUTPUT_Y = FLUID_INPUT_Y + 24;
    public static final int MODE_BUTTON_X = 120;
    public static final int MODE_BUTTON_Y = 80;

    private final ClayPotTileEntity tileEntity;
    private final IWorldPosCallable canInteract;
    private final ClayPotData data;
    private final IItemHandler inventory;

    public ClayPotContainer(int windowId, PlayerInventory playerInventory, PacketBuffer buffer) {
        this(windowId, playerInventory, getTileEntity(playerInventory, buffer));
    }

    public ClayPotContainer(int windowId, PlayerInventory playerInventory, ClayPotTileEntity tileEntity) {
        super(ModContainers.CLAY_POT.get(), windowId);
        this.tileEntity = tileEntity;
        this.inventory = tileEntity.getInventory();
        this.canInteract = IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos());
        this.data = new ClayPotData(tileEntity);
        this.addDataSlots(data);

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int index = col + row * GRID_SIZE;
                this.addSlot(new SlotItemHandler(inventory, index,
                        GRID_START_X + col * 18,
                        GRID_START_Y + row * 18));
            }
        }

        this.addSlot(new FluidInputSlot(inventory, ClayPotTileEntity.FLUID_INPUT_SLOT,
                FLUID_INPUT_X, FLUID_INPUT_Y));
        this.addSlot(new FluidOutputSlot(inventory, ClayPotTileEntity.FLUID_OUTPUT_SLOT,
                FLUID_OUTPUT_X, FLUID_OUTPUT_Y));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    private static ClayPotTileEntity getTileEntity(PlayerInventory playerInventory, PacketBuffer buffer) {
        BlockPos pos = buffer.readBlockPos();
        if (playerInventory.player.level != null && playerInventory.player.level.getBlockEntity(pos) instanceof ClayPotTileEntity) {
            return (ClayPotTileEntity) playerInventory.player.level.getBlockEntity(pos);
        }
        throw new IllegalStateException("Clay Pot tile entity not found at " + pos);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return canInteract.evaluate((world, pos) -> world.getBlockState(pos).is(ModBlocks.CLAY_POT.get()), true);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            int playerInvStart = ClayPotTileEntity.TOTAL_SLOTS;
            int playerInvEnd = playerInvStart + 27;
            int hotbarStart = playerInvEnd;
            int hotbarEnd = hotbarStart + 9;

            if (index < playerInvStart) {
                if (!this.moveItemStackTo(stack, playerInvStart, hotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (stackHasFluidCapability(stack)) {
                    if (!this.moveItemStackTo(stack, ClayPotTileEntity.FLUID_INPUT_SLOT, ClayPotTileEntity.FLUID_INPUT_SLOT + 1, false)) {
                        if (!this.moveItemStackTo(stack, 0, ClayPotTileEntity.INV_SLOTS, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else if (index >= playerInvStart && index < playerInvEnd) {
                    if (!this.moveItemStackTo(stack, hotbarStart, hotbarEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= hotbarStart && index < hotbarEnd) {
                    if (!this.moveItemStackTo(stack, playerInvStart, playerInvEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stack, 0, ClayPotTileEntity.INV_SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }
        return result;
    }

    public int getFluidAmount() {
        return data.get(0);
    }

    public int getFluidCapacity() {
        return ClayPotTileEntity.CAPACITY;
    }

    @Nullable
    public Fluid getFluidType() {
        int type = data.get(1);
        if (type == 2) {
            return ModFluids.DIRTY_WATER.get();
        }
        if (type == 1) {
            return Fluids.WATER;
        }
        return null;
    }

    public boolean isDrainMode() {
        return tileEntity.isDrainMode();
    }

    public BlockPos getBlockPos() {
        return tileEntity.getBlockPos();
    }

    public void toggleMode() {
        tileEntity.toggleDrainMode();
        this.broadcastChanges();
    }

    private boolean stackHasFluidCapability(ItemStack stack) {
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
    }

    private final class FluidInputSlot extends SlotItemHandler {
        private FluidInputSlot(IItemHandler handler, int index, int xPosition, int yPosition) {
            super(handler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stackHasFluidCapability(stack);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            tileEntity.tryProcessFluidSlots();
        }
    }

    private static final class FluidOutputSlot extends SlotItemHandler {
        private FluidOutputSlot(IItemHandler handler, int index, int xPosition, int yPosition) {
            super(handler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }

    private static final class ClayPotData implements IIntArray {
        private static final int MODE_INDEX = 2;
        private final ClayPotTileEntity tileEntity;

        private ClayPotData(ClayPotTileEntity tileEntity) {
            this.tileEntity = tileEntity;
        }

        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return tileEntity.getTank().getFluidAmount();
                case 1:
                    Fluid fluid = tileEntity.getTank().getFluid().getFluid();
                    if (fluid.isSame(ModFluids.DIRTY_WATER.get()) || fluid.isSame(ModFluids.DIRTY_WATER_FLOWING.get())) {
                        return 2;
                    }
                    if (fluid.isSame(Fluids.WATER)) {
                        return 1;
                    }
                    return 0;
                case MODE_INDEX:
                    return tileEntity.isDrainMode() ? 1 : 0;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            if (index == MODE_INDEX) {
                tileEntity.setDrainMode(value == 1);
            }
        }

        @Override
        public int getCount() {
            return MODE_INDEX + 1;
        }
    }
}

