package com.example.examplemod.container;

import com.example.examplemod.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

public class FirepitContainer extends Container {
    private final IInventory firepitInv;

    public FirepitContainer(int id, PlayerInventory playerInv) {
        this(id, new Inventory(12), playerInv, new IntArray(1));
    }

    public FirepitContainer(int id, IInventory inv, PlayerInventory playerInv, IIntArray data) {
        super(ModContainers.FIREPIT.get(), id);
        checkContainerSize(inv, 12);
        this.firepitInv = inv;
        inv.startOpen(playerInv.player);

        // Firepit slots 4x3 starting at x=12,y=17 (extra column on the left)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 4; ++col) {
                this.addSlot(new Slot(inv, col + row * 4, 12 + col * 18, 17 + row * 18));
            }
        }

        // Player inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return firepitInv.stillValid(player);
    }
}
