package com.example.examplemod.tileentity;

import com.example.examplemod.ModItems;
import com.example.examplemod.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SlabTileEntity extends TileEntity implements ITickableTileEntity {
    private static final int DRYING_TIME_TICKS = 500; // 25 секунд (20 тиков в секунду)
    
    // Храним время начала сушки для каждого слота
    private final long[] dryingStartTimes = new long[INV_SLOTS];
    public static final int INV_SLOTS = 9;

    private final ItemStackHandler inventory = new ItemStackHandler(INV_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            SlabTileEntity.this.setChanged();
            // Отправляем обновление клиенту для визуализации предметов
            if (SlabTileEntity.this.level != null && !SlabTileEntity.this.level.isClientSide) {
                SlabTileEntity.this.level.sendBlockUpdated(SlabTileEntity.this.worldPosition, 
                    SlabTileEntity.this.getBlockState(), 
                    SlabTileEntity.this.getBlockState(), 3);
            }
        }
    };

    private final LazyOptional<IItemHandler> inventoryCapability = LazyOptional.of(() -> inventory);

    public SlabTileEntity() {
        super(ModTileEntities.SLAB.get());
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        inventory.deserializeNBT(nbt.getCompound("Inventory"));
        // Загружаем время начала сушки для каждого слота
        if (nbt.contains("DryingStartTimes", 11)) { // 11 = long array
            long[] savedTimes = nbt.getLongArray("DryingStartTimes");
            System.arraycopy(savedTimes, 0, dryingStartTimes, 0, Math.min(savedTimes.length, INV_SLOTS));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.put("Inventory", inventory.serializeNBT());
        // Сохраняем время начала сушки для каждого слота
        nbt.putLongArray("DryingStartTimes", dryingStartTimes);
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

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        load(state, tag);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable net.minecraft.util.Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        inventoryCapability.invalidate();
    }

    public void dropInventoryContents() {
        if (level == null || level.isClientSide) {
            return;
        }
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                InventoryHelper.dropItemStack(level,
                        worldPosition.getX() + 0.5D,
                        worldPosition.getY() + 0.5D,
                        worldPosition.getZ() + 0.5D,
                        stack);
                inventory.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) {
            return;
        }

        // Получаем температуру биома в позиции полублока
        int temperature = getBiomeTemperature(level, worldPosition);
        
        // Если температура меньше нуля, сушка не происходит
        if (temperature <= 0) {
            // Сбрасываем таймеры сушки для всех слотов
            for (int i = 0; i < INV_SLOTS; i++) {
                if (dryingStartTimes[i] != 0) {
                    dryingStartTimes[i] = 0;
                    setChanged();
                }
            }
            return;
        }

        long currentTime = level.getGameTime();
        boolean changed = false;

        // Проверяем каждый слот на наличие сырого кирпича
        for (int slot = 0; slot < INV_SLOTS; slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            
            if (!stack.isEmpty() && stack.getItem() == ModItems.RAW_CLAY_BRICK.get()) {
                // Если сушка еще не началась, начинаем отсчет
                if (dryingStartTimes[slot] == 0) {
                    dryingStartTimes[slot] = currentTime;
                    changed = true;
                } else {
                    // Проверяем, прошло ли достаточно времени
                    long elapsedTime = currentTime - dryingStartTimes[slot];
                    if (elapsedTime >= DRYING_TIME_TICKS) {
                        // Превращаем сырой кирпич в сушеный
                        ItemStack driedBrick = new ItemStack(ModItems.DRIED_CLAY_BRICK.get(), stack.getCount());
                        inventory.setStackInSlot(slot, driedBrick);
                        dryingStartTimes[slot] = 0;
                        changed = true;
                    }
                }
            } else {
                // Если в слоте нет сырого кирпича, сбрасываем таймер
                if (dryingStartTimes[slot] != 0) {
                    dryingStartTimes[slot] = 0;
                    changed = true;
                }
            }
        }

        if (changed) {
            setChanged();
        }
    }

    /**
     * Получает температуру биома в указанной позиции.
     * Использует ту же логику, что и BiomeTemperatureCache для игроков.
     */
    private int getBiomeTemperature(World world, BlockPos pos) {
        // Специальные измерения
        if (world.dimension() == World.NETHER) return 666;
        if (world.dimension() == World.END) return -666;
        
        // Получаем биом (дорогой вызов!)
        Biome biome = world.getBiome(pos);
        Biome.Category cat = biome.getBiomeCategory();
        
        // Маппинг категорий на температуры (та же логика, что в BiomeTemperatureCache)
        switch (cat) {
            case PLAINS:
                return 23;
            case DESERT:
            case MESA:
                return 37;
            case SAVANNA:
                return 30;
            case FOREST:
                return 17;
            case JUNGLE:
                return 30;
            case SWAMP:
                return -13;
            case TAIGA:
                return -25;
            case EXTREME_HILLS:
                return -10;
            case ICY:
                return -40;
            case BEACH:
            case RIVER:
                return 10;
            case OCEAN:
                return 6;
            case MUSHROOM:
                return 0;
            case NETHER:
                return 666;
            case THEEND:
                return -666;
            default:
                return 0;
        }
    }
}