package com.example.examplemod.container;

import com.example.examplemod.ModContainers;
import com.example.examplemod.item.BoneTongsCapabilityProvider;
import com.example.examplemod.tileentity.FirepitTileEntity;
import com.example.examplemod.tileentity.PechugaTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BoneTongsSelectionContainer extends Container {
    private final IItemHandler sourceHandler;
    private final IItemHandler tongsHandler;
    private final List<Integer> sourceSlots = new ArrayList<>();
    private final World world;
    private final BlockPos blockPos;

    public BoneTongsSelectionContainer(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
        super(ModContainers.BONE_TONGS_SELECTION.get(), windowId);

        this.world = playerInventory.player.level;
        this.blockPos = data.readBlockPos();
        ItemStack boneStack = data.readItem();

        // Получаем TileEntity источника
        TileEntity sourceTile = world.getBlockEntity(blockPos);
        this.sourceHandler = createSourceHandler(sourceTile);
        this.tongsHandler = boneStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .orElseGet(() -> new BoneTongsCapabilityProvider(boneStack).getHandler());

        // Добавляем слоты источника (печи/кострища)
        int sourceSlotIndex = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                if (sourceSlotIndex < sourceHandler.getSlots()) {
                    addSlot(new SelectableSlot(sourceHandler, sourceSlotIndex, 8 + col * 18, 18 + row * 18, this));
                    sourceSlots.add(sourceSlotIndex);
                    sourceSlotIndex++;
                }
            }
        }

        // Добавляем слоты щипцов (для показа, что в них уже есть)
        for (int i = 0; i < tongsHandler.getSlots(); i++) {
            addSlot(new SlotItemHandler(tongsHandler, i, 8 + i * 18, 84));
        }

        // Добавляем слоты инвентаря игрока
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 106 + row * 18));
            }
        }

        // Добавляем слоты хотбара
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 164));
        }
    }

    private IItemHandler createSourceHandler(TileEntity tileEntity) {
        if (tileEntity instanceof FirepitTileEntity) {
            FirepitTileEntity firepit = (FirepitTileEntity) tileEntity;
            return new FurnaceLikeItemHandler(firepit, FirepitTileEntity.GRID_SLOT_COUNT);
        } else if (tileEntity instanceof PechugaTileEntity) {
            PechugaTileEntity pechuga = (PechugaTileEntity) tileEntity;
            return new FurnaceLikeItemHandler(pechuga, PechugaTileEntity.GRID_SLOT_COUNT);
        } else if (tileEntity instanceof AbstractFurnaceTileEntity) {
            AbstractFurnaceTileEntity furnace = (AbstractFurnaceTileEntity) tileEntity;
            return new FurnaceLikeItemHandler(furnace, 3); // input, fuel, output
        }
        return new EmptyItemHandler();
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickType, PlayerEntity player) {
        if (slotId >= 0 && slotId < sourceSlots.size() && clickType == ClickType.PICKUP) {
            Slot slot = this.slots.get(slotId);
            if (slot instanceof SelectableSlot) {
                SelectableSlot selectableSlot = (SelectableSlot) slot;
                selectableSlot.toggleSelection();

                // Если предмет выбран и есть место в щипцах, переносим его
                if (selectableSlot.isSelected()) {
                    ItemStack stack = slot.getItem();
                    if (!stack.isEmpty()) {
                        // Ищем свободный слот в щипцах
                        for (int tongsSlot = sourceSlots.size(); tongsSlot < sourceSlots.size() + tongsHandler.getSlots(); tongsSlot++) {
                            Slot tongsSlotObj = this.slots.get(tongsSlot);
                            if (tongsSlotObj.getItem().isEmpty()) {
                                // Переносим предмет
                                ItemStack extracted = sourceHandler.extractItem(slot.getSlotIndex(), stack.getCount(), false);
                                if (!extracted.isEmpty()) {
                                    tongsHandler.insertItem(tongsSlot - sourceSlots.size(), extracted, false);
                                    if (sourceHandler instanceof FurnaceLikeItemHandler) {
                                        ((FurnaceLikeItemHandler) sourceHandler).markChanged();
                                    }
                                    break;
                                }
                            }
                        }
                        // Снимаем выделение после переноса
                        selectableSlot.setSelected(false);
                    }
                }
            }
        }
        return super.clicked(slotId, dragType, clickType, player);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    // Вспомогательный класс для слотов с возможностью выделения
    public static class SelectableSlot extends SlotItemHandler {
        private boolean selected = false;
        private final BoneTongsSelectionContainer container;

        public SelectableSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, BoneTongsSelectionContainer container) {
            super(itemHandler, index, xPosition, yPosition);
            this.container = container;
        }

        public void toggleSelection() {
            this.selected = !this.selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isSelected() {
            return selected;
        }

        @Override
        public boolean mayPickup(PlayerEntity playerIn) {
            return false; // Предметы нельзя брать напрямую, только через выделение
        }
    }

    // Обёртка для работы с различными типами печей
    private static class FurnaceLikeItemHandler implements IItemHandler {
        private final TileEntity tileEntity;
        private final int slotCount;

        public FurnaceLikeItemHandler(TileEntity tileEntity, int slotCount) {
            this.tileEntity = tileEntity;
            this.slotCount = slotCount;
        }

        @Override
        public int getSlots() {
            return slotCount;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (tileEntity instanceof FirepitTileEntity) {
                return ((FirepitTileEntity) tileEntity).getItem(slot);
            } else if (tileEntity instanceof PechugaTileEntity) {
                return ((PechugaTileEntity) tileEntity).getItem(slot);
            } else if (tileEntity instanceof AbstractFurnaceTileEntity) {
                return ((AbstractFurnaceTileEntity) tileEntity).getItem(slot);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack; // Не поддерживаем вставку через этот интерфейс
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (tileEntity instanceof FirepitTileEntity) {
                FirepitTileEntity firepit = (FirepitTileEntity) tileEntity;
                ItemStack stack = firepit.getItem(slot);
                if (!stack.isEmpty()) {
                    ItemStack extracted = firepit.removeItem(slot, amount);
                    if (!simulate && !extracted.isEmpty()) {
                        firepit.setChanged();
                    }
                    return extracted;
                }
            } else if (tileEntity instanceof PechugaTileEntity) {
                PechugaTileEntity pechuga = (PechugaTileEntity) tileEntity;
                ItemStack stack = pechuga.getItem(slot);
                if (!stack.isEmpty()) {
                    ItemStack extracted = pechuga.removeItem(slot, amount);
                    if (!simulate && !extracted.isEmpty()) {
                        pechuga.setChanged();
                    }
                    return extracted;
                }
            } else if (tileEntity instanceof AbstractFurnaceTileEntity) {
                AbstractFurnaceTileEntity furnace = (AbstractFurnaceTileEntity) tileEntity;
                ItemStack stack = furnace.getItem(slot);
                if (!stack.isEmpty()) {
                    ItemStack extracted = furnace.removeItem(slot, amount);
                    if (!simulate && !extracted.isEmpty()) {
                        furnace.setChanged();
                    }
                    return extracted;
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return true;
        }

        public void markChanged() {
            if (tileEntity != null) {
                tileEntity.setChanged();
            }
        }
    }

    // Пустой обработчик для случаев, когда TileEntity не найден
    private static class EmptyItemHandler implements IItemHandler {
        @Override
        public int getSlots() {
            return 0;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 0;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    }
}