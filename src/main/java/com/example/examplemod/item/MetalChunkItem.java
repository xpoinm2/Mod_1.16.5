package com.example.examplemod.item;

import net.minecraft.client.util.ITooltipFlag;
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

    private static final String STATE_TAG = "MetalChunkState";

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
    }
}