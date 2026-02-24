package com.example.examplemod.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
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
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (getState(stack) != GrassState.HEALING) {
            return ActionResult.fail(stack);
        }

        player.startUsingItem(hand);
        return ActionResult.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
        if (getState(stack) == GrassState.HEALING) {
            entity.heal(2.0F);
            entity.playSound(SoundEvents.GENERIC_EAT, 0.5F, 1.0F);
            if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                if (!player.abilities.instabuild) {
                    stack.shrink(1);
                }
            } else {
                stack.shrink(1);
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return getState(stack) == GrassState.HEALING ? UseAction.EAT : UseAction.NONE;
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
