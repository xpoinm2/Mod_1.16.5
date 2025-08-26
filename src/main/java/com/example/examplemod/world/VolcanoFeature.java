package com.example.examplemod.world;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Feature that generates exactly one hollow basalt volcano with an open crater per biome.
 */
public class VolcanoFeature extends Feature<NoFeatureConfig> {

    /** Tracks biomes that already contain a volcano. */
    private static final Set<ResourceLocation> GENERATED_BIOMES = ConcurrentHashMap.newKeySet();

    public VolcanoFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig cfg) {
        Biome biome = world.getBiome(pos);
        ResourceLocation biomeName = ForgeRegistries.BIOMES.getKey(biome);

        // Проверяем, не генерировали ли вулкан в этом биоме
        if (biomeName == null || !GENERATED_BIOMES.add(biomeName)) {
            return false;
        }

        // Находим "центр" области биома (по сетке 512x512 чанков)
        int biomeCenterX = (pos.getX() & ~511) + 256;
        int biomeCenterZ = (pos.getZ() & ~511) + 256;

        // Высота поверхности
        int groundY = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, biomeCenterX, biomeCenterZ);

        int maxHeight = world.getMaxBuildHeight();
        int height = Math.min(40 + rand.nextInt(20), maxHeight - groundY);
        if (height <= 0) {
            return false;
        }

        buildVolcano(world, new BlockPos(biomeCenterX, groundY, biomeCenterZ), height, rand);
        return true;
    }

    private void buildVolcano(ISeedReader world, BlockPos base, int height, Random rand) {
        int maxRadius = 16 + rand.nextInt(8);
        int craterRadius = maxRadius / 3;
        int buildLimit = world.getMaxBuildHeight();

        for (int y = 0; y < height && base.getY() + y < buildLimit; y++) {
            int radius = Math.max(craterRadius, (int) (maxRadius * (1.0 - (double) y / height)));
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    BlockPos p = base.offset(dx, y, dz);
                    if (dist <= radius) {
                        if (dist >= radius - 1) {
                            world.setBlock(p, Blocks.BASALT.defaultBlockState(), 2);
                        } else {
                            world.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }
    }
}
