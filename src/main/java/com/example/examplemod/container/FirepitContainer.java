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

        // Firepit slots: 3 rows x 4 columns, start at x=44, y=20
        // (центрируем относительно текстуры 256x256)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 4; ++col) {
                this.addSlot(new Slot(inv, col + row * 4, 44 + col * 18, 20 + row * 18));
            }
        }

        // Player inventory 3x9
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return firepitInv.stillValid(player);
    }
}
