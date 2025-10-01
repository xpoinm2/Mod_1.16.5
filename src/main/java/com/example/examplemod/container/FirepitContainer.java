package com.example.examplemod.container;

import com.example.examplemod.ModContainers;
import com.example.examplemod.tileentity.FirepitTileEntity;
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

public class FirepitContainer extends Container {
    private final IInventory firepitInv;
    private final FirepitTileEntity tileEntity;
    private final IIntArray dataAccess;

    public FirepitContainer(int id, PlayerInventory playerInv, PacketBuffer buffer) {
        this(id, playerInv, getTileEntity(playerInv, buffer));
    }

    public FirepitContainer(int id, PlayerInventory playerInv, FirepitTileEntity tileEntity) {
        super(ModContainers.FIREPIT.get(), id);
        this.tileEntity = tileEntity;
        this.firepitInv = tileEntity;
        this.dataAccess = tileEntity.getDataAccess();
        checkContainerSize(this.firepitInv, 14);
        this.firepitInv.startOpen(playerInv.player);
        this.addDataSlots(this.dataAccess);

        // Top 12 slots (3×4). Centered in 176 width:
        // startX = (176 - 4*18)/2 = 52; startY = 20
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 4; ++col) {
                this.addSlot(new Slot(firepitInv, col + row * 4, 52 + col * 18, 20 + row * 18));
            }
        }

        // Input slot (index 12) - accepts only pure iron ore
        this.addSlot(new Slot(firepitInv, 12, 136, 38) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return tileEntity != null && tileEntity.isSmeltable(stack);
            }
        });

        // Fuel slot (index 13)
        this.addSlot(new Slot(firepitInv, 13, 136, 56) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return AbstractFurnaceTileEntity.isFuel(stack);
            }
        });

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

    private static FirepitTileEntity getTileEntity(PlayerInventory playerInventory, PacketBuffer buffer) {
        BlockPos pos = buffer.readBlockPos();
        if (playerInventory.player.level != null) {
            if (playerInventory.player.level.getBlockEntity(pos) instanceof FirepitTileEntity) {
                return (FirepitTileEntity) playerInventory.player.level.getBlockEntity(pos);
            }
        }
        throw new IllegalStateException("Firepit tile entity not found at " + pos);
    }

    public int getHeatScaled(int pixels) {
        int burnTime = this.dataAccess.get(0);
        int totalBurn = this.dataAccess.get(1);
        if (totalBurn == 0) {
            totalBurn = 200;
        }
        burnTime = MathHelper.clamp(burnTime, 0, totalBurn);
        return MathHelper.ceil((double) burnTime * pixels / totalBurn);
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

    @Override
    public boolean stillValid(PlayerEntity player) {
        return firepitInv.stillValid(player);
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        this.firepitInv.stopOpen(player);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            if (index < 14) {
                if (!this.moveItemStackTo(stack, 14, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (tileEntity != null && tileEntity.isSmeltable(stack)) {
                if (!this.moveItemStackTo(stack, 12, 13, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (AbstractFurnaceTileEntity.isFuel(stack)) {
                if (!this.moveItemStackTo(stack, 13, 14, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 14 && index < 41) {
                if (!this.moveItemStackTo(stack, 41, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 41 && index < this.slots.size()) {
                if (!this.moveItemStackTo(stack, 14, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 14, this.slots.size(), false)) {
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
}