package com.example.examplemod.tileentity;

import com.example.examplemod.ModTileEntities;
import com.example.examplemod.ModFluids;
import com.example.examplemod.ModItems;
import com.example.examplemod.block.ClayPotBlock;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.inventory.InventoryHelper;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClayPotTileEntity extends TileEntity {
    public static final int CAPACITY = 8000;
    private static final int CONTAMINATION_PER_ITEM = 250;
    private int contaminatedAmount = 0;
    private Fluid lastKnownFluid = Fluids.EMPTY;

       // Добавлено: getter для TESR (строки 45-48)
               public int getWaterLevel() {
               return MathHelper.clamp((tank.getFluidAmount() * 8) / CAPACITY, 0, 8);
           }

    public static final int INV_SLOTS = 9;
    public static final int FLUID_INPUT_SLOT = INV_SLOTS;
    public static final int FLUID_OUTPUT_SLOT = INV_SLOTS + 1;
    public static final int TOTAL_SLOTS = FLUID_OUTPUT_SLOT + 1;
    private static final String NBT_DRAIN_MODE = "DrainMode";
    private static final String NBT_WASH_PROGRESS = "WashProgress";
    private static final String NBT_LAST_WASH_TIME = "LastWashTime";

    private boolean drainMode = false;
    private int washProgress = 0; // 0-8, где 8 = полный прогресс
    private long lastWashTime = 0; // timestamp последнего клика

    private final FluidTank tank = new FluidTank(CAPACITY) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            ClayPotTileEntity.this.handleTankContentsChanged();
            ClayPotTileEntity.this.setChanged();
            if (level != null && !level.isClientSide) {
                BlockState previous = getBlockState();
                updateFillLevel();
                level.sendBlockUpdated(worldPosition, previous, getBlockState(), 3);
                // Добавлено: extra update для TESR (строки 60-61)
                level.blockUpdated(worldPosition, getBlockState().getBlock());
            }
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid().isSame(Fluids.WATER) || stack.getFluid().isSame(ModFluids.DIRTY_WATER.get());
        }
    };

    private final LazyOptional<IFluidHandler> fluidCapability = LazyOptional.of(() -> tank);

    private final ItemStackHandler inventory = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            ClayPotTileEntity.this.setChanged();
        }
    };

    private final LazyOptional<IItemHandler> inventoryCapability = LazyOptional.of(() -> inventory);

    public ClayPotTileEntity() {
        super(ModTileEntities.CLAY_POT.get());
    }

    public FluidTank getTank() {
        return tank;
    }

    public void clear() {
        tank.setFluid(FluidStack.EMPTY);
        setChanged();
    }

    public boolean isDrainMode() {
        return drainMode;
    }

    public void setDrainMode(boolean drain) {
        if (drainMode == drain) {
            return;
        }
        drainMode = drain;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void toggleDrainMode() {
        setDrainMode(!drainMode);
    }

    public int getWashProgress() {
        return washProgress;
    }

    public void incrementWashProgress() {
        long currentTime = System.currentTimeMillis();
        // Проверка задержки в 0.2 секунды (200 мс)
        if (currentTime - lastWashTime < 200) {
            return; // Слишком рано для следующего клика
        }

        lastWashTime = currentTime;
        washProgress++;

        if (washProgress >= 8) {
            washProgress = 0;
            // Здесь можно добавить логику завершения помывки и крафта
            tryCompleteWashing();
        }

        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState previous = getBlockState();
            level.sendBlockUpdated(worldPosition, previous, getBlockState(), 3);
        }
    }

    private void tryCompleteWashing() {
        // Сначала проверяем рецепт глиняной массы
        if (tryCraftClayMass()) {
            resetWashProgress();
            return;
        }

        // Рассчитываем максимальное количество предметов для промывки
        int maxItemsToWash = tank.getFluidAmount() / CONTAMINATION_PER_ITEM;
        if (maxItemsToWash <= 0) {
            return;
        }

        // Промываем предметы по слотам
        int itemsWashed = 0;
        for (int slot = 0; slot < INV_SLOTS; slot++) {
            if (itemsWashed >= maxItemsToWash) break;

            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            ItemStack result = getWashingResult(stack);
            if (!result.isEmpty()) {
                // Определяем сколько предметов можем помыть из этого слота
                int canWashFromSlot = Math.min(stack.getCount(), maxItemsToWash - itemsWashed);

                if (canWashFromSlot > 0) {
                    // Создаем результат для промытых предметов
                    ItemStack cleanResult = result.copy();
                    cleanResult.setCount(canWashFromSlot);

                    // Пытаемся вставить чистые предметы в свободные слоты
                    int inserted = insertStackIntoFreeSlots(cleanResult);

                    if (inserted > 0) {
                        // Уменьшаем количество грязных предметов
                        stack.shrink(inserted);

                        // Добавляем глину за каждый промытый предмет
                        ItemStack clay = new ItemStack(net.minecraft.item.Items.CLAY_BALL, inserted);
                        insertStackIntoFreeSlots(clay);

                        // Записываем загрязнение воды
                        for (int i = 0; i < inserted; i++) {
                            recordOreWashForItem();
                        }

                        itemsWashed += inserted;
                    }
                }
            }
        }

        // Сбрасываем прогресс после успешной промывки
        resetWashProgress();
    }

    private boolean tryCraftClayMass() {
        // Проверяем, есть ли вода в горшке
        FluidStack fluid = tank.getFluid();
        if (fluid.isEmpty() || !fluid.getFluid().isSame(Fluids.WATER) || fluid.getAmount() < 500) {
            return false;
        }

        // Подсчитываем ингредиенты
        int clayBallCount = 0;
        int sandCount = 0;
        int gravelCount = 0;

        for (int slot = 0; slot < INV_SLOTS; slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                if (stack.getItem() == net.minecraft.item.Items.CLAY_BALL) {
                    clayBallCount += stack.getCount();
                } else if (stack.getItem() == ModItems.HANDFUL_OF_SAND.get()) {
                    sandCount += stack.getCount();
                } else if (stack.getItem() == net.minecraft.item.Items.GRAVEL) {
                    gravelCount += stack.getCount();
                } else if (!getWashingResult(stack).isEmpty()) {
                    // Это промывочный предмет (рудный гравий), игнорируем для подсчета глиняной массы
                    continue;
                } else {
                    // Есть посторонний предмет, рецепт не подходит
                    return false;
                }
            }
        }

        // Проверяем, есть ли нужное количество ингредиентов
        if (clayBallCount >= 4 && sandCount >= 1 && gravelCount >= 1) {
            // Вычисляем, сколько глиняной массы можем создать
            int maxClayMass = Math.min(clayBallCount / 4, Math.min(sandCount, gravelCount));
            int clayMassToCreate = Math.min(maxClayMass, fluid.getAmount() / 500);

            if (clayMassToCreate > 0) {
                // Создаем глиняную массу
                if (ModItems.CLAY_MASS.isPresent()) {
                    ItemStack clayMassResult = new ItemStack(ModItems.CLAY_MASS.get(), clayMassToCreate);
                    int inserted = insertStackIntoFreeSlots(clayMassResult);

                    if (inserted > 0) {
                        // Удаляем использованные ингредиенты
                        int clayToConsume = inserted * 4;
                        int sandToConsume = inserted;
                        int gravelToConsume = inserted;

                        // Удаляем глину
                        int remainingClay = clayToConsume;
                        for (int slot = 0; slot < INV_SLOTS && remainingClay > 0; slot++) {
                            ItemStack stack = inventory.getStackInSlot(slot);
                            if (!stack.isEmpty() && stack.getItem() == net.minecraft.item.Items.CLAY_BALL) {
                                int toRemove = Math.min(stack.getCount(), remainingClay);
                                stack.shrink(toRemove);
                                remainingClay -= toRemove;
                            }
                        }

                        // Удаляем песок
                        int remainingSand = sandToConsume;
                        for (int slot = 0; slot < INV_SLOTS && remainingSand > 0; slot++) {
                            ItemStack stack = inventory.getStackInSlot(slot);
                            if (!stack.isEmpty() && stack.getItem() == ModItems.HANDFUL_OF_SAND.get()) {
                                int toRemove = Math.min(stack.getCount(), remainingSand);
                                stack.shrink(toRemove);
                                remainingSand -= toRemove;
                            }
                        }

                        // Удаляем гравий
                        int remainingGravel = gravelToConsume;
                        for (int slot = 0; slot < INV_SLOTS && remainingGravel > 0; slot++) {
                            ItemStack stack = inventory.getStackInSlot(slot);
                            if (!stack.isEmpty() && stack.getItem() == net.minecraft.item.Items.GRAVEL) {
                                int toRemove = Math.min(stack.getCount(), remainingGravel);
                                stack.shrink(toRemove);
                                remainingGravel -= toRemove;
                            }
                        }

                        // Загрязняем воду (500 мл на единицу глиняной массы)
                        for (int i = 0; i < inserted; i++) {
                            recordOreWashForItem();
                        }

                        // Проверяем, что предмет создался в инвентаре
                        boolean foundClayMass = false;
                        for (int slot = 0; slot < INV_SLOTS; slot++) {
                            ItemStack stack = inventory.getStackInSlot(slot);
                            if (!stack.isEmpty() && stack.getItem() == ModItems.CLAY_MASS.get()) {
                                foundClayMass = true;
                                break;
                            }
                        }

                        if (foundClayMass) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private int insertStackIntoFreeSlots(ItemStack stack) {
        int remaining = stack.getCount();
        int originalCount = remaining;

        // Сначала пытаемся добавить к существующим стакам того же типа
        for (int slot = 0; slot < INV_SLOTS && remaining > 0; slot++) {
            ItemStack existing = inventory.getStackInSlot(slot);
            if (!existing.isEmpty() && existing.getItem() == stack.getItem() &&
                existing.getCount() < existing.getMaxStackSize()) {
                int canAdd = Math.min(remaining, existing.getMaxStackSize() - existing.getCount());
                existing.grow(canAdd);
                remaining -= canAdd;
            }
        }

        // Затем кладем в пустые слоты
        for (int slot = 0; slot < INV_SLOTS && remaining > 0; slot++) {
            ItemStack existing = inventory.getStackInSlot(slot);
            if (existing.isEmpty()) {
                int canAdd = Math.min(remaining, stack.getMaxStackSize());
                ItemStack toInsert = stack.copy();
                toInsert.setCount(canAdd);
                inventory.setStackInSlot(slot, toInsert);
                remaining -= canAdd;
            }
        }

        return originalCount - remaining;
    }


    private void recordOreWashForItem() {
        FluidStack fluid = tank.getFluid();
        if (fluid.isEmpty() || !fluid.getFluid().isSame(Fluids.WATER)) {
            return;
        }
        contaminatedAmount += CONTAMINATION_PER_ITEM;
        if (contaminatedAmount >= tank.getFluidAmount()) {
            int amount = tank.getFluidAmount();
            if (amount > 0) {
                tank.setFluid(new FluidStack(ModFluids.DIRTY_WATER.get(), amount));
                notifyFluidTypeChanged();
            }
            contaminatedAmount = 0;
        }
    }

    private ItemStack getWashingResult(ItemStack input) {
        if (input.getItem() == ModItems.TIN_ORE_GRAVEL.get()) {
            return new ItemStack(ModItems.CLEANED_GRAVEL_TIN_ORE.get(), input.getCount());
        } else if (input.getItem() == ModItems.GOLD_ORE_GRAVEL.get()) {
            return new ItemStack(ModItems.CLEANED_GRAVEL_GOLD_ORE.get(), input.getCount());
        } else if (input.getItem() == ModItems.IRON_ORE_GRAVEL.get()) {
            return new ItemStack(ModItems.PURE_IRON_ORE.get(), input.getCount());
        }
        return ItemStack.EMPTY;
    }

    public void resetWashProgress() {
        washProgress = 0;
        lastWashTime = 0;
        setChanged();
    }

    private void updateFillLevel() {
        if (level == null) {
            return;
        }

        BlockState state = getBlockState();
        if (!state.hasProperty(ClayPotBlock.FILL_LEVEL)) {
            return;
        }

        int fill = getWaterLevel();  // Добавлено: через getter (строка 86)
        if (state.getValue(ClayPotBlock.FILL_LEVEL) != fill) {
            level.setBlock(worldPosition, state.setValue(ClayPotBlock.FILL_LEVEL, fill), 3);
        }
    }

    private void handleTankContentsChanged() {
        Fluid current = tank.getFluid().getFluid();
        if (!current.isSame(lastKnownFluid)) {
            lastKnownFluid = current;
            // Сбрасываем загрязнение только когда переходим от грязной воды к чистой
            if (current.isSame(Fluids.WATER) && lastKnownFluid.isSame(ModFluids.DIRTY_WATER.get())) {
                contaminatedAmount = 0;
            }
        }

        // Если объем воды уменьшился и загрязнение превышает текущий объем, превращаем воду в грязную
        if (current.isSame(Fluids.WATER) && contaminatedAmount >= tank.getFluidAmount() && tank.getFluidAmount() > 0) {
            tank.setFluid(new FluidStack(ModFluids.DIRTY_WATER.get(), tank.getFluidAmount()));
            notifyFluidTypeChanged();
            contaminatedAmount = 0;
        }
    }

    public boolean canWashOre() {
        FluidStack fluid = tank.getFluid();
        return fluid.getAmount() >= CAPACITY && fluid.getFluid().isSame(Fluids.WATER);
    }

    public boolean canWashOreUI() {
        FluidStack fluid = tank.getFluid();
        return fluid.getAmount() >= 250 && fluid.getFluid().isSame(Fluids.WATER);
    }

    public boolean hasWashableItems() {
        for (int slot = 0; slot < INV_SLOTS; slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty() && !getWashingResult(stack).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean canWashNow() {
        return canWashOreUI() && (hasWashableItems() || canCraftClayMass());
    }

    private boolean canCraftClayMass() {
        // Проверяем, есть ли вода в горшке
        FluidStack fluid = tank.getFluid();
        if (fluid.isEmpty() || !fluid.getFluid().isSame(Fluids.WATER) || fluid.getAmount() < 500) {
            return false;
        }

        // Подсчитываем ингредиенты
        int clayBallCount = 0;
        int sandCount = 0;
        int gravelCount = 0;

        for (int slot = 0; slot < INV_SLOTS; slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                if (stack.getItem() == net.minecraft.item.Items.CLAY_BALL) {
                    clayBallCount += stack.getCount();
                } else if (stack.getItem() == ModItems.HANDFUL_OF_SAND.get()) {
                    sandCount += stack.getCount();
                } else if (stack.getItem() == net.minecraft.item.Items.GRAVEL) {
                    gravelCount += stack.getCount();
                } else if (!getWashingResult(stack).isEmpty()) {
                    // Это промывочный предмет (рудный гравий), игнорируем для проверки глиняной массы
                    continue;
                } else {
                    // Есть посторонний предмет, который не подходит ни для промывки, ни для крафта глиняной массы
                    return false;
                }
            }
        }

        // Проверяем, есть ли нужное количество ингредиентов
        return clayBallCount >= 4 && sandCount >= 1 && gravelCount >= 1;
    }

    public void recordOreWash() {
        FluidStack fluid = tank.getFluid();
        if (fluid.isEmpty() || !fluid.getFluid().isSame(Fluids.WATER)) {
            System.out.println("Cannot wash ore - no water or not water");
            return;
        }
        contaminatedAmount += CONTAMINATION_PER_ITEM;
        System.out.println("Contaminated amount: " + contaminatedAmount + "/" + tank.getFluidAmount());
        if (contaminatedAmount >= tank.getFluidAmount()) {
            int amount = tank.getFluidAmount();
            if (amount > 0) {
                System.out.println("Converting water to dirty water");
                tank.setFluid(new FluidStack(ModFluids.DIRTY_WATER.get(), amount));
                notifyFluidTypeChanged();
            }
            contaminatedAmount = 0; // Сбрасываем после превращения в грязную воду
        }
    }

    private void notifyFluidTypeChanged() {
        if (level == null || level.isClientSide) {
            return;
        }
        setChanged();
        BlockState previous = getBlockState();
        updateFillLevel();
        level.sendBlockUpdated(worldPosition, previous, getBlockState(), 3);
        level.blockUpdated(worldPosition, getBlockState().getBlock());
        broadcastFluidUpdate();
    }

    private void broadcastFluidUpdate() {
        if (!(level instanceof ServerWorld)) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld) level;
        SUpdateTileEntityPacket packet = getUpdatePacket();
        if (packet == null) {
            return;
        }
        for (ServerPlayerEntity player : serverWorld.getPlayers(pred -> true)) {
            if (player.distanceToSqr(worldPosition.getX() + 0.5D,
                    worldPosition.getY() + 0.5D,
                    worldPosition.getZ() + 0.5D) <= 4096D) {
                player.connection.send(packet);
            }
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        inventory.deserializeNBT(getInventoryTagWithMinimumSize(nbt.getCompound("Inventory")));
        tank.readFromNBT(nbt.getCompound("Tank"));
        contaminatedAmount = nbt.getInt("ContaminatedAmount");
        lastKnownFluid = tank.getFluid().getFluid();
        if (nbt.contains(NBT_DRAIN_MODE)) {
            drainMode = nbt.getBoolean(NBT_DRAIN_MODE);
        }
        washProgress = nbt.getInt(NBT_WASH_PROGRESS);
        lastWashTime = nbt.getLong(NBT_LAST_WASH_TIME);
    }

    private CompoundNBT getInventoryTagWithMinimumSize(CompoundNBT tag) {
        CompoundNBT copy = tag.copy();
        int savedSize = copy.getInt("Size");
        if (savedSize < TOTAL_SLOTS) {
            copy.putInt("Size", TOTAL_SLOTS);
        }
        return copy;
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.put("Inventory", inventory.serializeNBT());
        nbt.put("Tank", tank.writeToNBT(new CompoundNBT()));
        nbt.putInt("ContaminatedAmount", contaminatedAmount);
        nbt.putBoolean(NBT_DRAIN_MODE, drainMode);
        nbt.putInt(NBT_WASH_PROGRESS, washProgress);
        nbt.putLong(NBT_LAST_WASH_TIME, lastWashTime);
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
        if (level != null && level.isClientSide) {
            updateFillLevel();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        load(state, tag);
        if (level != null) {
            updateFillLevel();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            updateFillLevel();
        }
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable net.minecraft.util.Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidCapability.cast();
        }
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        fluidCapability.invalidate();
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

    public boolean tryProcessFluidSlots() {
        if (level == null || level.isClientSide) {
            return false;
        }
        ItemStack input = inventory.getStackInSlot(FLUID_INPUT_SLOT);
        ItemStack output = inventory.getStackInSlot(FLUID_OUTPUT_SLOT);
        if (input.isEmpty() || !output.isEmpty()) {
            return false;
        }
        LazyOptional<IFluidHandlerItem> containerCap = input.getCapability(
                CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
        IFluidHandlerItem containerHandler = containerCap.orElse(null);
        if (containerHandler == null) {
            return false;
        }

        boolean processed = false;
        if (drainMode) {
            FluidStack simulated = containerHandler.drain(CAPACITY, FluidAction.SIMULATE);
            if (!simulated.isEmpty()) {
                int accepted = tank.fill(simulated, FluidAction.SIMULATE);
                if (accepted > 0) {
                    FluidStack drained = containerHandler.drain(accepted, FluidAction.EXECUTE);
                    tank.fill(drained, FluidAction.EXECUTE);
                    processed = drained.getAmount() > 0;
                }
            }
        } else {
            FluidStack available = tank.getFluid();
            if (!available.isEmpty()) {
                FluidStack toFill = available.copy();
                int filled = containerHandler.fill(toFill, FluidAction.SIMULATE);
                if (filled > 0) {
                    toFill.setAmount(filled);
                    containerHandler.fill(toFill, FluidAction.EXECUTE);
                    tank.drain(filled, FluidAction.EXECUTE);
                    processed = true;
                }
            }
        }

        if (!processed) {
            return false;
        }

        ItemStack moved = inventory.extractItem(FLUID_INPUT_SLOT, 1, false);
        if (!moved.isEmpty()) {
            inventory.setStackInSlot(FLUID_OUTPUT_SLOT, moved);
        }
        return true;
    }
}