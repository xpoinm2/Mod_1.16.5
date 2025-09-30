package com.example.examplemod.container;

import com.example.examplemod.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

public class FirepitContainer extends Container {
    private final IInventory firepitInv;

    public FirepitContainer(int id, PlayerInventory playerInv) {
        this(id, new Inventory(14), playerInv, new IntArray(1));
    }

    public FirepitContainer(int id, IInventory inv, PlayerInventory playerInv, IIntArray data) {
        super(ModContainers.FIREPIT.get(), id);
        checkContainerSize(inv, 14);
        this.firepitInv = inv;
        inv.startOpen(playerInv.player);

        // Top 12 slots (3×4). Centered in 176 width:
        // startX = (176 - 4*18)/2 = 52; startY = 20
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 4; ++col) {
                this.addSlot(new Slot(inv, col + row * 4, 52 + col * 18, 20 + row * 18));
            }
        }

        // Reserved utility slots on the right
        // Top slot (index 12) currently does not accept any items
        this.addSlot(new Slot(inv, 12, 136, 38) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // Bottom slot (index 13) accepts furnace fuel items only
        this.addSlot(new Slot(inv, 13, 136, 56) {
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

    @Override
    public boolean stillValid(PlayerEntity player) {
        return firepitInv.stillValid(player);
    }
}