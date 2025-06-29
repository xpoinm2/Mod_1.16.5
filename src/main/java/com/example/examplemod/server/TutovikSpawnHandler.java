package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import com.example.examplemod.block.TutovikBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TutovikSpawnHandler {
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getWorld() instanceof ServerWorld)) return;
        if (!(event.getChunk() instanceof Chunk)) return;
        ServerWorld world = (ServerWorld) event.getWorld();
        Chunk chunk = (Chunk) event.getChunk();

        // Increase spawn chance from 1/20 to 1/2
        if (world.random.nextInt(2) != 0) return;

        for (int i = 0; i < 3; i++) {
            int x = chunk.getPos().getMinBlockX() + world.random.nextInt(16);
            int z = chunk.getPos().getMinBlockZ() + world.random.nextInt(16);
            int y = world.getHeight(Heightmap.Type.MOTION_BLOCKING, x, z);
            BlockPos pos = new BlockPos(x, y, z);
            while (pos.getY() > 0) {
                pos = pos.below();
                BlockState state = world.getBlockState(pos);
                if (state.is(BlockTags.LOGS)) {
                    Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(world.random);
                    BlockPos place = pos.relative(dir);
                    if (world.isEmptyBlock(place)) {
                        BlockState fungus = ModBlocks.TUTOVIK.get().defaultBlockState()
                                .setValue(TutovikBlock.FACING, dir.getOpposite());
                        world.setBlock(place, fungus, 2);
                    }
                    break;
                }
            }
        }
    }
}