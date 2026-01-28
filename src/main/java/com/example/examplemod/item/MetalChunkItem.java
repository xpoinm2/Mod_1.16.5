package com.example.examplemod.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MetalChunkItem extends Item {
    public static final int STATE_GOOD = 1;
    public static final int STATE_MEDIUM = 2;
    public static final int STATE_BAD = 3;
    public static final int TEMP_HOT = 1;
    public static final int TEMP_COLD = 2;

    private static final String STATE_TAG = "MetalChunkState";
    private static final String TEMPERATURE_TAG = "MetalChunkTemperature";
    private static final String HOT_START_TICK_TAG = "MetalChunkHotStartTick";
    private static final int HOT_DURATION_TICKS = 800;

    public MetalChunkItem(Properties properties) {
        super(properties);
    }

    public static int getState(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        int state = nbt.getInt(STATE_TAG);
        if (state == 0) {
            setState(stack, STATE_MEDIUM);
            return STATE_MEDIUM;
        }
        return state;
    }

    public static void setState(ItemStack stack, int state) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt(STATE_TAG, state);
        stack.setTag(nbt);
    }

    public static int getTemperature(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        int temperature = nbt.getInt(TEMPERATURE_TAG);
        if (temperature == 0) {
            setTemperature(stack, TEMP_COLD);
            return TEMP_COLD;
        }
        return temperature;
    }

    public static void setTemperature(ItemStack stack, int temperature) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt(TEMPERATURE_TAG, temperature);
        if (temperature != TEMP_HOT) {
            nbt.remove(HOT_START_TICK_TAG);
        }
        stack.setTag(nbt);
    }

    public static void setHotStartTick(ItemStack stack, long gameTime) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putLong(HOT_START_TICK_TAG, gameTime);
        stack.setTag(nbt);
    }

    private static void checkTemperatureTransition(ItemStack stack, World world) {
        if (getTemperature(stack) != TEMP_HOT) {
            return;
        }
        CompoundNBT nbt = stack.getOrCreateTag();
        long startTick = nbt.getLong(HOT_START_TICK_TAG);
        long gameTime = world.getGameTime();
        if (startTick == 0L) {
            setHotStartTick(stack, gameTime);
            return;
        }
        if (gameTime - startTick >= HOT_DURATION_TICKS) {
            setTemperature(stack, TEMP_COLD);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world.isClientSide) {
            return;
        }
        checkTemperatureTransition(stack, world);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.level.isClientSide) {
            return false;
        }
        checkTemperatureTransition(stack, entity.level);
        return false;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        int state = getState(stack);
        TextFormatting color = TextFormatting.GRAY;
        ITextComponent stateText = new TranslationTextComponent("tooltip.examplemod.metal_chunk.state_medium");

        switch (state) {
            case STATE_GOOD:
                color = TextFormatting.GREEN;
                stateText = new TranslationTextComponent("tooltip.examplemod.metal_chunk.state_good");
                break;
            case STATE_MEDIUM:
                color = TextFormatting.YELLOW;
                stateText = new TranslationTextComponent("tooltip.examplemod.metal_chunk.state_medium");
                break;
            case STATE_BAD:
                color = TextFormatting.RED;
                stateText = new TranslationTextComponent("tooltip.examplemod.metal_chunk.state_bad");
                break;
            default:
                break;
        }

        tooltip.add(new TranslationTextComponent("tooltip.examplemod.metal_chunk.state", stateText).withStyle(color));

        int temperature = getTemperature(stack);
        TextFormatting temperatureColor = TextFormatting.GRAY;
        ITextComponent temperatureText = new TranslationTextComponent("tooltip.examplemod.metal_chunk.temperature_cold");
        switch (temperature) {
            case TEMP_HOT:
                temperatureColor = TextFormatting.RED;
                temperatureText = new TranslationTextComponent("tooltip.examplemod.metal_chunk.temperature_hot");
                break;
            case TEMP_COLD:
                temperatureColor = TextFormatting.BLUE;
                temperatureText = new TranslationTextComponent("tooltip.examplemod.metal_chunk.temperature_cold");
                break;
            default:
                break;
        }
        tooltip.add(new TranslationTextComponent("tooltip.examplemod.metal_chunk.temperature", temperatureText)
                .withStyle(temperatureColor));
    }
}
