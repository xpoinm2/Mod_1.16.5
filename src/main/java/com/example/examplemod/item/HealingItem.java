// === FILE src/main/java/com/example/examplemod/item/HealingItem.java
package com.example.examplemod.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class HealingItem extends Item {
    public HealingItem(Properties properties) {
        super(properties);
    }


    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }


    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }


    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        player.startUsingItem(hand);

        return ActionResult.success(player.getItemInHand(hand));
    }


    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClientSide() && user instanceof PlayerEntity) {  // isRemote → isClientSide
            PlayerEntity player = (PlayerEntity) user;

            player.heal(4.0F);

            world.playSound(
                    /*player=*/null,
                    /*x=*/player.getX(), /*y=*/player.getY(), /*z=*/player.getZ(),
                    SoundEvents.GENERIC_DRINK,
                    SoundCategory.PLAYERS,
                    /*volume=*/1.0F, /*pitch=*/1.0F
            );

            stack.shrink(1);
        }
        return stack;
    }
}