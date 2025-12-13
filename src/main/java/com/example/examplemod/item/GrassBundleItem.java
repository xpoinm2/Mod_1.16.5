package com.example.examplemod.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class GrassBundleItem extends BlockItem {
    private static final String STATE_TAG = "GrassState";

    public GrassBundleItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static ItemStack createWithState(GrassState state) {
        ItemStack stack = new ItemStack(com.example.examplemod.ModItems.BUNCH_OF_GRASS.get());
        setState(stack, state);
        return stack;
    }

    public static void setState(ItemStack stack, GrassState state) {
        stack.getOrCreateTag().putString(STATE_TAG, state.getSerializedName());
    }

    public static GrassState getState(ItemStack stack) {
        String id = stack.getOrCreateTag().getString(STATE_TAG);
        return GrassState.byName(id);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        return ActionResultType.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        GrassState state = getState(stack);
        tooltip.add(new TranslationTextComponent(state.getTranslationKey()).withStyle(TextFormatting.GRAY));
    }

    public enum GrassState {
        HEALING("healing"),
        DYE("dye"),
        FERTILIZER("fertilizer"),
        POISON("poison");

        private final String serializedName;

        GrassState(String serializedName) {
            this.serializedName = serializedName;
        }

        public String getSerializedName() {
            return serializedName;
        }

        public String getTranslationKey() {
            return "item.examplemod.bunch_of_grass.state." + serializedName;
        }

        public static GrassState byName(String name) {
            for (GrassState state : values()) {
                if (state.serializedName.equals(name)) {
                    return state;
                }
            }
            return HEALING;
        }
    }
}