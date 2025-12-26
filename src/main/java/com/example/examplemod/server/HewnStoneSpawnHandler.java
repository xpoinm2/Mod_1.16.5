package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModItems;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.ChunkEvent;

/**
 Spawns hewn stone items in river biomes when chunks load.
 */
public class HewnStoneSpawnHandler {

    private static final int CHANCE = 1;

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getWorld() instanceof ServerWorld)) return;
        ServerWorld world = (ServerWorld) event.getWorld();
        Chunk chunk = (Chunk) event.getChunk();

        if (world.random.nextInt(CHANCE) != 0) return;

        int x = chunk.getPos().getMinBlockX() + world.random.nextInt(16);
        int z = chunk.getPos().getMinBlockZ() + world.random.nextInt(16);
        BlockPos ground = world.getHeightmapPos(Heightmap.Type.OCEAN_FLOOR, new BlockPos(x, 0, z));
        Biome biome = world.getBiome(ground);
        if (biome.getBiomeCategory() != Biome.Category.RIVER) return;

        BlockPos waterPos = ground.above();
        if (!world.getFluidState(waterPos).is(FluidTags.WATER)) return;

        ItemEntity entity = new ItemEntity(world, waterPos.getX() + 0.5, waterPos.getY() + 0.5,
                waterPos.getZ() + 0.5, new ItemStack(ModItems.HEWN_STONE.get()));
        world.addFreshEntity(entity);
    }
}