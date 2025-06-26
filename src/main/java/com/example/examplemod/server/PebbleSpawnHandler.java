package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModItems;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
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
    // approx 1 pebble per 90x90 area
    private static final double SPAWN_CHANCE = 256.0 / 2700.0;

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getWorld() instanceof ServerWorld)) return;
        if (!(event.getChunk() instanceof Chunk)) return;
        ServerWorld world = (ServerWorld) event.getWorld();
        Chunk chunk = (Chunk) event.getChunk();

        if (world.random.nextDouble() >= SPAWN_CHANCE) return;

        int x = chunk.getPos().getMinBlockX() + world.random.nextInt(16);
        int z = chunk.getPos().getMinBlockZ() + world.random.nextInt(16);
        BlockPos pos = new BlockPos(x, 0, z);
        Biome.Category category = world.getBiome(pos).getBiomeCategory();
        // Spawn in all biomes except desert
        if (category != Biome.Category.DESERT) {
            int y = world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
            ItemEntity entity = new ItemEntity(world, x + 0.5, y + 0.0625, z + 0.5,
                    new ItemStack(ModItems.PEBBLE.get()));
            entity.setNoGravity(true);
            entity.setPickUpDelay(32767);
            entity.setExtendedLifetime();
            world.addFreshEntity(entity);
        }
    }
}