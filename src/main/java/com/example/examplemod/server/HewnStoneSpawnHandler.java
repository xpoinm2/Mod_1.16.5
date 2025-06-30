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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Spawns a hewn stone item in every river biome chunk when it loads.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HewnStoneSpawnHandler {

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getWorld() instanceof ServerWorld)) return;
        ServerWorld world = (ServerWorld) event.getWorld();
        Chunk chunk = (Chunk) event.getChunk();

        int x = chunk.getPos().getMinBlockX() + world.random.nextInt(16);
        int z = chunk.getPos().getMinBlockZ() + world.random.nextInt(16);
        BlockPos pos = world.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, new BlockPos(x, 0, z));

        Biome biome = world.getBiome(pos);
        if (biome.getBiomeCategory() != Biome.Category.RIVER) return;

        if (!world.getFluidState(pos).is(FluidTags.WATER)) return;

        ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                new ItemStack(ModItems.HEWN_STONE.get()));
        world.addFreshEntity(entity);
    }
}