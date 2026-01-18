package com.example.examplemod.container;

import com.example.examplemod.ModContainers;
import com.example.examplemod.item.BoneTongsCapabilityProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EnhancedDualContainer extends Container {
    private final Container mainContainer; // Основной контейнер (печь/кострище)
    private final IItemHandler tongsHandler;
    private final List<Slot> originalMainSlots = new ArrayList<>();

    // Константы для позиционирования
    public static final int MAIN_GUI_OFFSET_X = 80; // Сдвиг основного GUI вправо
    public static final int MAIN_GUI_OFFSET_Y = 0;
    public static final int TONGS_GUI_X = 8; // Позиция GUI щипцов
    public static final int TONGS_GUI_Y = 18;

    public EnhancedDualContainer(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
        super(ModContainers.ENHANCED_DUAL_CONTAINER.get(), windowId);

        // Читаем данные
        boolean isFirepit = data.readBoolean();
        ItemStack tongsStack = data.readItem();
        BlockPos blockPos = data.readBlockPos();

        // Получаем FirepitTileEntity из мира
        com.example.examplemod.tileentity.FirepitTileEntity firepitTile = null;
        if (playerInventory.player.level instanceof net.minecraft.world.server.ServerWorld) {
            net.minecraft.world.server.ServerWorld serverWorld = (net.minecraft.world.server.ServerWorld) playerInventory.player.level;
            net.minecraft.tileentity.TileEntity tileEntity = serverWorld.getBlockEntity(blockPos);
            if (tileEntity instanceof com.example.examplemod.tileentity.FirepitTileEntity) {
                firepitTile = (com.example.examplemod.tileentity.FirepitTileEntity) tileEntity;
            }
        }

        // Получаем обработчик щипцов
        tongsHandler = tongsStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .orElseGet(() -> new BoneTongsCapabilityProvider(tongsStack).getHandler());

        // Создаем основной контейнер
        if (isFirepit && firepitTile != null) {
            mainContainer = new com.example.examplemod.container.FirepitContainer(windowId + 1, playerInventory, firepitTile);
        } else if (!isFirepit && firepitTile != null) {
            mainContainer = new com.example.examplemod.container.PechugaContainer(windowId + 1, playerInventory, firepitTile);
        } else {
            // Fallback - создаем контейнер без tile entity
            if (isFirepit) {
                mainContainer = new com.example.examplemod.container.FirepitContainer(windowId + 1, playerInventory, (com.example.examplemod.tileentity.FirepitTileEntity) null);
            } else {
                mainContainer = new com.example.examplemod.container.PechugaContainer(windowId + 1, playerInventory, (com.example.examplemod.tileentity.FirepitTileEntity) null);
            }
        }

        // Добавляем слоты щипцов слева
        for (int i = 0; i < tongsHandler.getSlots(); i++) {
            addSlot(new SlotItemHandler(tongsHandler, i, TONGS_GUI_X, TONGS_GUI_Y + i * 18));
        }

        // Копируем слоты основного контейнера со сдвигом
        if (mainContainer != null) {
            for (Slot slot : mainContainer.slots) {
                originalMainSlots.add(slot);
                // Сдвигаем слоты основного контейнера вправо
                Slot newSlot = createShiftedSlot(slot, MAIN_GUI_OFFSET_X, MAIN_GUI_OFFSET_Y);
                addSlot(newSlot);
            }
        }
    }

    private Slot createShiftedSlot(Slot originalSlot, int offsetX, int offsetY) {
        // Создаем новый слот со сдвигом, копируя свойства оригинального
        if (originalSlot instanceof net.minecraftforge.items.SlotItemHandler) {
            // Для SlotItemHandler создаем новый с правильными координатами
            net.minecraftforge.items.SlotItemHandler originalHandlerSlot = (net.minecraftforge.items.SlotItemHandler) originalSlot;
            return new net.minecraftforge.items.SlotItemHandler(originalHandlerSlot.getItemHandler(),
                                                              originalHandlerSlot.getSlotIndex(),
                                                              originalSlot.x + offsetX,
                                                              originalSlot.y + offsetY);
        } else {
            // Для обычных слотов - получаем inventory через reflection или создаем новый слот
            // Пока что просто создадим базовый слот
            return new Slot(originalSlot.container, originalSlot.getSlotIndex(),
                           originalSlot.x + offsetX, originalSlot.y + offsetY);
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return mainContainer.stillValid(player);
    }

    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickType, PlayerEntity player) {
        if (slotId >= 0 && slotId < slots.size()) {
            int mainSlotsCount = originalMainSlots.size();

            if (slotId < mainSlotsCount) {
                // Слот из основного контейнера - перенаправляем
                // Индекс в оригинальном контейнере соответствует индексу в нашем списке
                return mainContainer.clicked(slotId, dragType, clickType, player);
            } else {
                // Слот из щипцов - обрабатываем локально
                return super.clicked(slotId, dragType, clickType, player);
            }
        } else {
            return super.clicked(slotId, dragType, clickType, player);
        }
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        // Расширенная логика quick-move между контейнерами
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            int mainSlotsCount = originalMainSlots.size();
            int tongsSlotsStart = mainSlotsCount;

            if (index < mainSlotsCount) {
                // Из основного контейнера в щипцы
                if (!this.moveItemStackTo(itemstack1, tongsSlotsStart, slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Из щипцов в основной контейнер
                if (!this.moveItemStackTo(itemstack1, 0, mainSlotsCount, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        if (mainContainer != null) {
            mainContainer.removed(player);
        }
    }

    // Геттеры для доступа к контейнерам
    public Container getMainContainer() {
        return mainContainer;
    }

    public IItemHandler getTongsHandler() {
        return tongsHandler;
    }
}