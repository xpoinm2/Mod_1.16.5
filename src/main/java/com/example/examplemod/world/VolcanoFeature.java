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
 * Ровно один вулкан на каждый "островок" (пятно) биома mountains.
 * Реализовано так: по сетке REG_SIZE (64 блоков) выбирается один якорь,
 * мы ищем ближайшую точку внутри этого пятна mountains (спиральный поиск),
 * и ставим вулкан. Для каждого региона 64x64 гарантируется не более одного вулкана.
 */
public class VolcanoFeature extends Feature<NoFeatureConfig> {

    // Ограничиваем генерацию максимум одним вулканом на регион REG_SIZE x REG_SIZE
    private static final int REG_SIZE = 64;
    private static final Set<String> GENERATED_REGIONS = ConcurrentHashMap.newKeySet();

    public VolcanoFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig cfg) {
        Biome biomeAtPos = world.getBiome(pos);
        ResourceLocation biomeName = ForgeRegistries.BIOMES.getKey(biomeAtPos);

        // Генерируем только в биомах с именем, содержащим "mountains"
        if (biomeName == null || !biomeName.getPath().contains("mountains")) {
            return false;
        }

        // Региональная привязка 64x64 (намного плотнее прежних 512x512)
        int regionX = Math.floorDiv(pos.getX(), REG_SIZE);
        int regionZ = Math.floorDiv(pos.getZ(), REG_SIZE);
        String regionKey = regionX + "," + regionZ;
        if (!GENERATED_REGIONS.add(regionKey)) {
            return false; // в этом регионе уже генерировали
        }

        // Начальная точка — центр региона
        int startX = regionX * REG_SIZE + REG_SIZE / 2;
        int startZ = regionZ * REG_SIZE + REG_SIZE / 2;

        // Ищем ближайшую точку внутри mountains (спираль), чтобы не зависеть от размера пятна
        BlockPos target = findNearestMountains(world, startX, pos.getY(), startZ, 96);
        if (target == null) {
            return false; // поблизости нет mountains — отменяем
        }

        int groundY = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, target.getX(), target.getZ());
        int maxHeight = world.getMaxBuildHeight();
        int height = Math.min(40 + rand.nextInt(20), maxHeight - groundY);
        if (height <= 0) {
            return false;
        }

        buildVolcano(world, new BlockPos(target.getX(), groundY, target.getZ()), height, rand);
        return true;
    }

    /** Спиральный поиск ближайшего блока, где биом содержит "mountains". */
    private BlockPos findNearestMountains(ISeedReader world, int x0, int y, int z0, int maxRadius) {
        int x = 0, z = 0;
        int dx = 0, dz = -1;
        for (int i = 0; i < (maxRadius * maxRadius); i++) {
            int cx = x0 + x;
            int cz = z0 + z;
            Biome b = world.getBiome(new BlockPos(cx, y, cz));
            ResourceLocation name = ForgeRegistries.BIOMES.getKey(b);
            if (name != null && name.getPath().contains("mountains")) {
                return new BlockPos(cx, y, cz);
            }
            if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
                int tmp = dx;
                dx = -dz;
                dz = tmp;
            }
            x += dx;
            z += dz;
            if (Math.max(Math.abs(x), Math.abs(z)) > maxRadius) break;
        }
        return null;
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