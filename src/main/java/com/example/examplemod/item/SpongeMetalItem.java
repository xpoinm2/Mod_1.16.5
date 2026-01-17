// === FILE src/main/java/com/example/examplemod/item/SpongeMetalItem.java
package com.example.examplemod.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SpongeMetalItem extends Item {
    // Константы для состояний
    public static final int STATE_HOT = 1;    // Горячий - наносит урон
    public static final int STATE_WARM = 2;   // Теплый - без эффектов
    public static final int STATE_COLD = 3;   // Холодный - без эффектов
    public static final int STATE_WET = 4;    // Влажный - портится, без эффектов

    // Константы для NBT тегов
    private static final String STATE_TAG = "SpongeState";
    private static final String CREATION_TIME_TAG = "CreationTime";
    private static final String TRANSITION_TIME_TAG = "TransitionTime";

    // Время переходов в секундах
    private static final int TRANSITION_TIME = 20; // 20 секунд

    public SpongeMetalItem(Properties properties) {
        super(properties);
    }

    /**
     * Получить текущее состояние предмета
     */
    public static int getState(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        int state = nbt.getInt(STATE_TAG);

        // Инициализируем состояние, если его нет
        if (state == 0) {
            setState(stack, STATE_HOT);
            return STATE_HOT;
        }

        return state;
    }

    /**
     * Установить состояние предмета
     */
    public static void setState(ItemStack stack, int state) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt(STATE_TAG, state);
        nbt.putLong(CREATION_TIME_TAG, System.currentTimeMillis() / 1000);
        nbt.putLong(TRANSITION_TIME_TAG, System.currentTimeMillis() / 1000);
        stack.setTag(nbt);
    }

    /**
     * Проверить, нужно ли перейти к следующему состоянию
     */
    private static void checkStateTransition(ItemStack stack) {
        int currentState = getState(stack);
        if (currentState == STATE_WET) {
            return; // Влажное состояние финальное
        }

        CompoundNBT nbt = stack.getOrCreateTag();
        long transitionTime = nbt.getLong(TRANSITION_TIME_TAG);
        long currentTime = System.currentTimeMillis() / 1000;

        if (currentTime - transitionTime >= TRANSITION_TIME) {
            int nextState = currentState + 1;
            if (nextState <= STATE_COLD) { // Не переходим в WET автоматически
                setState(stack, nextState);
            }
        }
    }

    /**
     * Проверить контакт с водой и перейти во влажное состояние
     */
    private static void checkWaterContact(ItemStack stack, World world, Entity entity) {
        // Проверяем только для ItemEntity (выброшенные предметы)
        if (!(entity instanceof ItemEntity)) {
            return;
        }

        ItemEntity itemEntity = (ItemEntity) entity;
        if (world.getFluidState(itemEntity.blockPosition()).is(FluidTags.WATER)) {
            setState(stack, STATE_WET);
        }
    }

    /**
     * Нанести урон игроку от горячего предмета
     */
    private static void applyHotDamage(ItemStack stack, PlayerEntity player) {
        if (getState(stack) == STATE_HOT) {
            // Наносим урон аналогично горячей руде
            player.hurt(DamageSource.HOT_FLOOR, 1.0F);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (world.isClientSide) return;
        if (!(entity instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) entity;

        // Проверяем переход состояний
        checkStateTransition(stack);

        // Эффект урона теперь обрабатывается в CommonModEvents.onPlayerTick
        // для корректной работы с перетаскиванием предметов
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.level.isClientSide) return false;

        // Проверяем переход состояний
        checkStateTransition(stack);

        // Проверяем контакт с водой
        checkWaterContact(stack, entity.level, entity);

        return false;
    }

    // Максимальный размер стака - 1 (чтобы состояния не смешивались)
    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    // Отображаем состояние в тултипе
    @Override
    public void appendHoverText(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        int state = getState(stack);
        String stateText = "";
        TextFormatting color = TextFormatting.GRAY;

        switch (state) {
            case STATE_HOT:
                stateText = "Горячий";
                color = TextFormatting.RED;
                break;
            case STATE_WARM:
                stateText = "Теплый";
                color = TextFormatting.YELLOW;
                break;
            case STATE_COLD:
                stateText = "Холодный";
                color = TextFormatting.BLUE;
                break;
            case STATE_WET:
                stateText = "Влажный (портится!)";
                color = TextFormatting.DARK_BLUE;
                break;
        }

        tooltip.add(new TranslationTextComponent("tooltip.examplemod.sponge_metal.state", stateText)
                .withStyle(color));
    }
}