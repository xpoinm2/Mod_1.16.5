package com.example.examplemod.world;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * «Один вулкан на каждый островок гор»: для каждого малого региона 48x48 блоков
 * выбираем единственную точку-якорь; если в окрестности (радиус 96) есть MOUNTAIN,
 * ставим вулкан в ближайшем месте mountains. Так вулкан появляется даже на маленьких пятнах.
 */
public class VolcanoFeature extends Feature<NoFeatureConfig> {

    private static final int REGION = 48;
    private static final int SEARCH_RADIUS = 96;
    private static final Set<String> DONE = ConcurrentHashMap.newKeySet();

    public VolcanoFeature(Codec<NoFeatureConfig> codec) { super(codec); }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig cfg) {
        if (!isMountainBiome(world.getBiome(pos))) return false;

        int rx = Math.floorDiv(pos.getX(), REGION);
        int rz = Math.floorDiv(pos.getZ(), REGION);
        String key = rx + "," + rz;
        if (!DONE.add(key)) return false;

        int cx = rx * REGION + REGION / 2;
        int cz = rz * REGION + REGION / 2;

        BlockPos target = findNearestMountain(world, cx, pos.getY(), cz, SEARCH_RADIUS);
        if (target == null) return false;

        int groundY = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, target.getX(), target.getZ());
        int maxH = world.getMaxBuildHeight();
        int height = Math.min(40 + rand.nextInt(20), maxH - groundY);
        if (height <= 0) return false;

        buildVolcano(world, new BlockPos(target.getX(), groundY, target.getZ()), height, rand);
        return true;
    }

    private boolean isMountainBiome(Biome biome) {
        ResourceLocation rl = ForgeRegistries.BIOMES.getKey(biome);
        if (rl == null) return false;
        RegistryKey<Biome> key = RegistryKey.create(Registry.BIOME_REGISTRY, rl);
        return BiomeDictionary.hasType(key, BiomeDictionary.Type.MOUNTAIN);
    }

    private BlockPos findNearestMountain(ISeedReader world, int x0, int y, int z0, int maxR) {
        int x = 0, z = 0, dx = 0, dz = -1;
        for (int i = 0; i < maxR * maxR; i++) {
            int cx = x0 + x, cz = z0 + z;
            if (isMountainBiome(world.getBiome(new BlockPos(cx, y, cz)))) {
                return new BlockPos(cx, y, cz);
            }
            if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) { int t = dx; dx = -dz; dz = t; }
            x += dx; z += dz;
            if (Math.max(Math.abs(x), Math.abs(z)) > maxR) break;
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