package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PebbleSpawnHandler {
    private static final double SPAWN_CHANCE = 256.0 / 400.0; // approx 1 per 20x20 area

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getWorld() instanceof ServerWorld)) return;
        if (!(event.getChunk() instanceof Chunk)) return;
        ServerWorld world = (ServerWorld) event.getWorld();
        Chunk chunk = (Chunk) event.getChunk();

        if (world.random.nextDouble() >= SPAWN_CHANCE) return;

        int x = chunk.getPos().getMinBlockX() + world.random.nextInt(16);
        int z = chunk.getPos().getMinBlockZ() + world.random.nextInt(16);
        int y = world.getHeight(Heightmap.Type.WORLD_SURFACE, x, z);
        BlockPos pos = new BlockPos(x, y, z);

        Biome biome = world.getBiome(pos);
        if (biome.getBiomeCategory() == Biome.Category.DESERT) return;

        BlockPos ground = pos.below();
        BlockState state = world.getBlockState(ground);
        if (state.getMaterial().isSolid() && world.isEmptyBlock(pos)) {
            world.setBlock(pos, ModBlocks.PEBBLE_BLOCK.get().defaultBlockState(), 3);
        }
    }
}