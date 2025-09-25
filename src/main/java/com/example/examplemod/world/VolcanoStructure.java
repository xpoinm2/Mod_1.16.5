package com.example.examplemod.world;

import com.example.examplemod.ModStructures;
import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructurePieceType;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

public class VolcanoStructure extends Structure<NoFeatureConfig> {
    private static final int SEARCH_RADIUS = 96;

    public VolcanoStructure(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return Start::new;
    }

    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    @Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeProvider biomeSource, long seed,
                                     SharedSeedRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos,
                                     NoFeatureConfig config) {
        return isMountainBiome(biome);
    }

    private static boolean isMountainBiome(Biome biome) {
        if (biome == null) return false;
        ResourceLocation rl = ForgeRegistries.BIOMES.getKey(biome);
        if (rl == null) return false;
        RegistryKey<Biome> key = RegistryKey.create(Registry.BIOME_REGISTRY, rl);
        return BiomeDictionary.hasType(key, BiomeDictionary.Type.MOUNTAIN);
    }

    private static BlockPos findNearestMountain(ChunkGenerator generator, int x0, int z0, int maxR) {
        BiomeProvider provider = generator.getBiomeSource();
        int seaLevel = generator.getSeaLevel();
        int x = 0, z = 0, dx = 0, dz = -1;
        for (int i = 0; i < maxR * maxR; i++) {
            int cx = x0 + x;
            int cz = z0 + z;
            Biome biome = provider.getNoiseBiome((cx >> 2), (seaLevel >> 2), (cz >> 2));
            if (isMountainBiome(biome)) {
                return new BlockPos(cx, 0, cz);
            }
            if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
                int tmp = dx;
                dx = -dz;
                dz = tmp;
            }
            x += dx;
            z += dz;
            if (Math.max(Math.abs(x), Math.abs(z)) > maxR) break;
        }
        return null;
    }

    private static void buildVolcano(ISeedReader world, BlockPos base, int height, Random rand, MutableBoundingBox box) {
        int maxRadius = 16 + rand.nextInt(8);
        int craterRadius = Math.max(3, maxRadius / 3);
        int buildLimit = world.getMaxBuildHeight();

        for (int y = 0; y < height && base.getY() + y < buildLimit; y++) {
            int radius = Math.max(craterRadius, (int) (maxRadius * (1.0 - (double) y / height)));
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    if (dist > radius) continue;
                    BlockPos pos = base.offset(dx, y, dz);
                    if (!box.isInside(pos)) continue;
                    if (dist >= radius - 1) {
                        world.setBlock(pos, Blocks.BASALT.defaultBlockState(), 2);
                    } else {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    public static class Start extends StructureStart<NoFeatureConfig> {
        public Start(Structure<NoFeatureConfig> structure, int chunkX, int chunkZ, MutableBoundingBox box, int references,
                     long seed) {
            super(structure, chunkX, chunkZ, box, references, seed);
        }

        @Override
        public void generatePieces(DynamicRegistries registries, ChunkGenerator generator, TemplateManager templateManager,
                                   int chunkX, int chunkZ, Biome biome, NoFeatureConfig config) {
            int centerX = chunkX * 16 + 8;
            int centerZ = chunkZ * 16 + 8;
            BlockPos target = findNearestMountain(generator, centerX, centerZ, SEARCH_RADIUS);
            if (target == null) return;

            int surfaceY = generator.getFirstFreeHeight(target.getX(), target.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
            int maxBuild = generator.getGenDepth();
            int height = Math.min(40 + this.random.nextInt(20), maxBuild - surfaceY);
            if (height <= 0) return;

            BlockPos base = new BlockPos(target.getX(), surfaceY, target.getZ());
            VolcanoPiece piece = new VolcanoPiece(base, height, this.random.nextLong());
            this.pieces.add(piece);
            this.calculateBoundingBox();
        }
    }

    public static class VolcanoPiece extends StructurePiece {
        private final BlockPos base;
        private final int height;
        private final long seed;

        public VolcanoPiece(BlockPos base, int height, long seed) {
            super(ModStructures.VOLCANO_PIECE.get(), 0);
            this.base = base;
            this.height = height;
            this.seed = seed;
            int radius = 64;
            this.boundingBox = new MutableBoundingBox(
                    base.getX() - radius, base.getY() - 4, base.getZ() - radius,
                    base.getX() + radius, base.getY() + height + 32, base.getZ() + radius
            );
        }

        public VolcanoPiece(TemplateManager templateManager, CompoundNBT nbt) {
            this(ModStructures.VOLCANO_PIECE.get(), nbt);
        }

        public VolcanoPiece(StructurePieceType type, CompoundNBT nbt) {
            super(type, nbt);
            this.base = new BlockPos(nbt.getInt("BaseX"), nbt.getInt("BaseY"), nbt.getInt("BaseZ"));
            this.height = nbt.getInt("Height");
            this.seed = nbt.getLong("Seed");
        }

        @Override
        protected void addAdditionalSaveData(CompoundNBT nbt) {
            nbt.putInt("BaseX", base.getX());
            nbt.putInt("BaseY", base.getY());
            nbt.putInt("BaseZ", base.getZ());
            nbt.putInt("Height", height);
            nbt.putLong("Seed", seed);
        }

        @Override
        public boolean postProcess(ISeedReader world, StructureManager manager, ChunkGenerator generator, Random rand,
                                   MutableBoundingBox box, ChunkPos chunkPos, BlockPos pos) {
            Random seeded = new Random(seed);
            buildVolcano(world, base, height, seeded, box);
            return true;
        }
    }
}