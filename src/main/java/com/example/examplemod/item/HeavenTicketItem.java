package com.example.examplemod.item;

import com.example.examplemod.world.heaven.HeavenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class HeavenTicketItem extends Item {

    public HeavenTicketItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide && player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            HeavenManager.rememberTicketLocation(serverPlayer);
            HeavenManager.teleportToHeaven(serverPlayer);
            if (!serverPlayer.abilities.instabuild) {
                stack.shrink(1);
            }
        }
        return ActionResult.sidedSuccess(stack, world.isClientSide());
    }
}