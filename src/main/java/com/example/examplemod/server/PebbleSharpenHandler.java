package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.Tags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Gives the player a sharp pebble when they left click any stone block.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PebbleSharpenHandler {


    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        PlayerEntity player = event.getPlayer();
        World world = player.level;
        if (world.isClientSide()) return;

        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        if (!isStone(state)) return;

        ItemStack stack = new ItemStack(ModItems.SHARP_PEBBLE.get());
        if (!player.addItem(stack)) {
            // drop at block position if inventory full
            BlockPos dropPos = pos.relative(event.getFace());
            net.minecraft.block.Block.popResource(world, dropPos, stack);
        }
    }

    private static boolean isStone(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD) ||
                state.is(BlockTags.BASE_STONE_NETHER) ||
                state.is(Tags.Blocks.COBBLESTONE) ||
                state.is(Tags.Blocks.STONE);
    }
}