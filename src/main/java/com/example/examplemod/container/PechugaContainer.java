package com.example.examplemod.container;

import com.example.examplemod.ModContainers;
import com.example.examplemod.ModItems;
import com.example.examplemod.item.BoneTongsItem;
import com.example.examplemod.item.RoastedOreItem;
import com.example.examplemod.tileentity.PechugaTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class PechugaContainer extends Container {
    private final IInventory pechugaInv;
    private final PechugaTileEntity tileEntity;
    private final IIntArray dataAccess;
    private final IItemHandler tongsHandler;
    private final int tongsSlotStart;
    private final int tongsSlotEnd;

    public static final int TONGS_SLOT_X = 8;
    public static final int TONGS_SLOT_Y = 20;

    public PechugaContainer(int id, PlayerInventory playerInv, PacketBuffer buffer) {
        this(id, playerInv, getTileEntity(playerInv, buffer));
    }

    public PechugaContainer(int id, PlayerInventory playerInv, PechugaTileEntity tileEntity) {
        this(id, playerInv, tileEntity, true);
    }

    public PechugaContainer(int id, PlayerInventory playerInv, PechugaTileEntity tileEntity, boolean includeTongsSlots) {
        super(com.example.examplemod.ModContainers.PECHUGA.get(), id);
        this.tileEntity = tileEntity;
        this.pechugaInv = tileEntity;
        this.dataAccess = tileEntity.getDataAccess();
        this.tongsHandler = includeTongsSlots ? findTongsHandler(playerInv.player) : null;
        this.tongsSlotStart = 13;
        this.tongsSlotEnd = this.tongsSlotStart + (tongsHandler != null ? tongsHandler.getSlots() : 0);
        checkContainerSize(this.pechugaInv, 13);
        this.pechugaInv.startOpen(playerInv.player);
        this.addDataSlots(this.dataAccess);

        // Top 12 slots (3×4). Centered in 176 width:
        // startX = (176 - 4*18)/2 = 52; startY = 20
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 4; ++col) {
                final int slotIndex = col + row * 4;
                this.addSlot(new Slot(pechugaInv, slotIndex, 52 + col * 18, 20 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return tileEntity != null && tileEntity.isSmeltable(stack);
                    }

                    @Override
                    public int getMaxStackSize() {
                        return 1;
                    }
                });
            }
        }

        // Fuel slot (index 12)
        this.addSlot(new Slot(pechugaInv, 12, 136, 56) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return AbstractFurnaceTileEntity.isFuel(stack);
            }
        });

        if (tongsHandler != null) {
            for (int slot = 0; slot < tongsHandler.getSlots(); slot++) {
                this.addSlot(new SlotItemHandler(tongsHandler, slot, TONGS_SLOT_X, TONGS_SLOT_Y + slot * 18));
            }
        }

        // Player inventory (3×9)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Hotbar (1×9)
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    private static PechugaTileEntity getTileEntity(PlayerInventory playerInventory, PacketBuffer buffer) {
        BlockPos pos = buffer.readBlockPos();
        if (playerInventory.player.level != null) {
            if (playerInventory.player.level.getBlockEntity(pos) instanceof PechugaTileEntity) {
                return (PechugaTileEntity) playerInventory.player.level.getBlockEntity(pos);
            }
        }
        throw new IllegalStateException("Pechuga tile entity not found at " + pos);
    }

    public int getHeatScaled(int pixels) {
        int heat = MathHelper.clamp(this.dataAccess.get(0), 0, PechugaTileEntity.MAX_HEAT);
        return MathHelper.ceil((double) heat * pixels / PechugaTileEntity.MAX_HEAT);
    }

    public int getProcessingScaled(int pixels) {
        int cookTime = this.dataAccess.get(2);
        int cookTotal = this.dataAccess.get(3);
        if (cookTotal == 0) {
            cookTotal = 200;
        }
        cookTime = MathHelper.clamp(cookTime, 0, cookTotal);
        return MathHelper.ceil((double) cookTime * pixels / cookTotal);
    }

    public float getProcessingProgress() {
        int cookTime = this.dataAccess.get(2);
        int cookTotal = this.dataAccess.get(3);
        if (cookTotal <= 0) {
            return 0.0F;
        }
        cookTime = MathHelper.clamp(cookTime, 0, cookTotal);
        return (float) cookTime / (float) cookTotal;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return pechugaInv.stillValid(player);
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        this.pechugaInv.stopOpen(player);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            int pechugaSlots = 13;
            int playerInvStart = tongsSlotEnd;
            int playerInvEnd = playerInvStart + 27;
            int hotbarStart = playerInvEnd;
            int hotbarEnd = hotbarStart + 9;

            if (index < pechugaSlots) {
                if (!tryMoveToTongs(stack)) {
                    if (!this.moveItemStackTo(stack, playerInvStart, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (index >= pechugaSlots && index < tongsSlotEnd) {
                if (!this.moveItemStackTo(stack, 0, pechugaSlots, false)
                        && !this.moveItemStackTo(stack, playerInvStart, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (tileEntity != null && tileEntity.isSmeltable(stack)) {
                if (!this.moveItemStackTo(stack, 0, 12, false) && !tryMoveToTongs(stack)) {
                    return ItemStack.EMPTY;
                }
            } else if (AbstractFurnaceTileEntity.isFuel(stack)) {
                if (!this.moveItemStackTo(stack, 12, 13, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (tryMoveToTongs(stack)) {
                // Successfully moved to tongs.
            } else if (index >= playerInvStart && index < playerInvEnd) {
                if (!this.moveItemStackTo(stack, hotbarStart, hotbarEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= hotbarStart && index < hotbarEnd) {
                if (!this.moveItemStackTo(stack, playerInvStart, playerInvEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, playerInvStart, this.slots.size(), false)) {
                return ItemStack.EMPTY;
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
    
    private boolean tryMoveToTongs(ItemStack stack) {
        if (tongsHandler == null || !(stack.getItem() instanceof RoastedOreItem)) {
            return false;
        }
        return this.moveItemStackTo(stack, tongsSlotStart, tongsSlotEnd, false);
    }

    public boolean hasTongsSlots() {
        return tongsHandler != null;
    }

    private static IItemHandler findTongsHandler(PlayerEntity player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof BoneTongsItem) {
            return mainHand.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                    .orElse(null);
        }

        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof BoneTongsItem) {
            return offHand.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                    .orElse(null);
        }

        return null;
    }
}
