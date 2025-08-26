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
 * Генерирует ровно один полый базальтовый вулкан с открытым кратером в каждом регионе 512x512 блоков,
 * но только если этот регион содержит биомы, в названии которых есть "mountains".
 * Это устраняет проблему «один на весь тип биома» и гарантирует, что вулканы реально появляются.
 */
public class VolcanoFeature extends Feature<NoFeatureConfig> {

    // Вместо глобального "по типу биома" — отслеживаем уже сгенерированные регионы мира (по сетке 512x512).
    private static final Set<String> GENERATED_REGIONS = ConcurrentHashMap.newKeySet();

    public VolcanoFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig cfg) {
        Biome biomeAtPos = world.getBiome(pos);
        ResourceLocation biomeName = ForgeRegistries.BIOMES.getKey(biomeAtPos);

        // Генерируем только в биомах, имя которых содержит "mountains"
        if (biomeName == null || !biomeName.getPath().contains("mountains")) {
            return false;
        }

        // Привязываем по региону 512x512 (чтобы был максимум один вулкан на такой регион)
        final int REGION_SIZE = 512;
        int regionX = Math.floorDiv(pos.getX(), REGION_SIZE);
        int regionZ = Math.floorDiv(pos.getZ(), REGION_SIZE);
        String regionKey = regionX + "," + regionZ;
        if (!GENERATED_REGIONS.add(regionKey)) {
            return false; // В этом регионе уже есть вулкан
        }

        // Центр региона — фиксированная точка для вулкана
        int centerX = regionX * REGION_SIZE + REGION_SIZE / 2;
        int centerZ = regionZ * REGION_SIZE + REGION_SIZE / 2;

        // Проверяем, что в центре тоже горный биом — если нет, отменяем (вулкан не будет в "плоском" пятне)
        Biome biomeAtCenter = world.getBiome(new BlockPos(centerX, pos.getY(), centerZ));
        ResourceLocation centerName = ForgeRegistries.BIOMES.getKey(biomeAtCenter);
        if (centerName == null || !centerName.getPath().contains("mountains")) {
            return false;
        }

        int groundY = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, centerX, centerZ);
        int maxHeight = world.getMaxBuildHeight();
        int height = Math.min(40 + rand.nextInt(20), maxHeight - groundY);
        if (height <= 0) {
            return false;
        }

        buildVolcano(world, new BlockPos(centerX, groundY, centerZ), height, rand);
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