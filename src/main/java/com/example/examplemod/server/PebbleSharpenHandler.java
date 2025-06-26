package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks when a player tries to mine stone with a pebble and converts it
 * into a sharp pebble if the mining lasts at least 3 seconds.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PebbleSharpenHandler {
    private static class Info {
        BlockPos pos;
        long start;
    }

    private static final Map<UUID, Info> START = new HashMap<>();
    private static final long DURATION_TICKS = 60; // 3 seconds

    @SubscribeEvent
    public static void onStartBreak(PlayerInteractEvent.LeftClickBlock event) {
        PlayerEntity player = event.getPlayer();
        if (player.level.isClientSide()) return;

        ItemStack held = player.getMainHandItem();
        if (held.getItem() != ModItems.PEBBLE.get()) return;

        BlockState state = player.level.getBlockState(event.getPos());
        if (!isStone(state)) return;

        Info info = new Info();
        info.pos = event.getPos().immutable();
        info.start = player.level.getGameTime();
        START.put(player.getUUID(), info);
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player.level.isClientSide()) return;

        ItemStack held = player.getMainHandItem();
        if (held.getItem() != ModItems.PEBBLE.get()) return;

        BlockState state = event.getState();
        if (!isStone(state)) return;

        Info info = START.get(player.getUUID());
        if (info != null && info.pos.equals(event.getPos())) {
            long ticks = player.level.getGameTime() - info.start;
            if (ticks >= DURATION_TICKS) {
                player.setItemInHand(Hand.MAIN_HAND, new ItemStack(ModItems.SHARP_PEBBLE.get()));
            }
        }
        START.remove(player.getUUID());
    }

    private static boolean isStone(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD) ||
                state.is(BlockTags.BASE_STONE_NETHER) ||
                state.is(Tags.Blocks.COBBLESTONE) ||
                state.is(Tags.Blocks.STONE);
    }
}