package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DirtDropReplacementHandler {

    private DirtDropReplacementHandler() {
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getWorld() instanceof World)) {
            return;
        }

        World world = (World) event.getWorld();
        if (world.isClientSide()) {
            return;
        }

        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        PlayerEntity player = event.getPlayer();
        if (player == null || player.isCreative()) {
            return;
        }

        if (state.getBlock() == Blocks.DIRT) {
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            Block.popResource(world, pos, new ItemStack(ModItems.HANDFUL_OF_DIRT.get()));
            world.levelEvent(2001, pos, Block.getId(state));
            event.setCanceled(true);
        }
    }
}
