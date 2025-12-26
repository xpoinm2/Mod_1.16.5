package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.server.mechanics.IMechanicModule;
import com.example.examplemod.world.ModBiomes;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;

import java.util.Objects;

public final class BiomeTeleportCommandsMechanic implements IMechanicModule {
    @Override
    public String id() {
        return "biome_teleport_commands";
    }

    @Override
    public boolean enableRegisterCommands() {
        return true;
    }

    @Override
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("tpbiome")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.literal("plains").executes(ctx -> teleport(ctx, Biomes.PLAINS)))
                        .then(Commands.literal("forest").executes(ctx -> teleport(ctx, Biomes.FOREST)))
                        .then(Commands.literal("taiga").executes(ctx -> teleport(ctx, Biomes.TAIGA)))
                        .then(Commands.literal("swamp").executes(ctx -> teleport(ctx, Biomes.SWAMP)))
                        .then(Commands.literal("jungle").executes(ctx -> teleport(ctx, Biomes.JUNGLE)))
                        .then(Commands.literal("savanna").executes(ctx -> teleport(ctx, Biomes.SAVANNA)))
                        .then(Commands.literal("mountains").executes(ctx -> teleport(ctx, Biomes.MOUNTAINS)))
                        .then(Commands.literal("desert").executes(ctx -> teleport(ctx, Biomes.DESERT)))
                        .then(Commands.literal("basalt_mountains").executes(ctx -> teleport(ctx,
                                RegistryKey.create(Registry.BIOME_REGISTRY,
                                        Objects.requireNonNull(ModBiomes.BASALT_MOUNTAINS.getId(),
                                                "Basalt Mountains biome has not been registered yet")))))
        );
    }

    private static int teleport(CommandContext<CommandSource> ctx, RegistryKey<Biome> biomeKey) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        ServerWorld world = player.getLevel();
        BlockPos origin = player.blockPosition();
        SearchResult result = findBiome(world, biomeKey, origin);
        if (result.position == null) {
            if (result.timedOut) {
                ctx.getSource().sendFailure(new StringTextComponent("Biome search timed out; try again after exploring more of the world"));
            } else {
                ctx.getSource().sendFailure(new StringTextComponent("Biome not found"));
            }
            return 0;
        }
        BlockPos found = result.position;
        world.getChunk(found.getX() >> 4, found.getZ() >> 4); // ensure chunk is loaded
        BlockPos top = world.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, found);
        player.teleportTo(world, top.getX() + 0.5, top.getY(), top.getZ() + 0.5, player.yRot, player.xRot);
        ctx.getSource().sendSuccess(new StringTextComponent("Teleported to " + biomeKey.location()), false);
        return 1;
    }

    private static final int SEARCH_RADIUS = 6400;
    private static final int SEARCH_STEP = 64;
    private static final long SEARCH_TIMEOUT_NANOS = java.util.concurrent.TimeUnit.SECONDS.toNanos(2);

    private static SearchResult findBiome(ServerWorld world, RegistryKey<Biome> biomeKey, BlockPos origin) {
        long deadline = System.nanoTime() + SEARCH_TIMEOUT_NANOS;
        ResourceLocation targetName = biomeKey.location();

        SearchResult initial = tryPosition(world, origin, 0, 0, targetName, deadline);
        if (initial != null) {
            return initial;
        }

        for (int radius = SEARCH_STEP; radius <= SEARCH_RADIUS; radius += SEARCH_STEP) {
            for (int x = -radius; x <= radius; x += SEARCH_STEP) {
                SearchResult result = tryPosition(world, origin, x, radius, targetName, deadline);
                if (result != null) {
                    return result;
                }
                result = tryPosition(world, origin, x, -radius, targetName, deadline);
                if (result != null) {
                    return result;
                }
            }

            for (int z = -radius + SEARCH_STEP; z <= radius - SEARCH_STEP; z += SEARCH_STEP) {
                SearchResult result = tryPosition(world, origin, radius, z, targetName, deadline);
                if (result != null) {
                    return result;
                }
                result = tryPosition(world, origin, -radius, z, targetName, deadline);
                if (result != null) {
                    return result;
                }
            }
        }
        return new SearchResult(null, false);
    }

    private static SearchResult tryPosition(ServerWorld world, BlockPos origin, int dx, int dz,
                                            ResourceLocation targetName, long deadline) {
        if (System.nanoTime() > deadline) {
            return new SearchResult(null, true);
        }

        BlockPos pos = origin.offset(dx, 0, dz);
        Biome biome = world.getBiome(pos);
        if (biome.getRegistryName() != null && biome.getRegistryName().equals(targetName)) {
            return new SearchResult(pos, false);
        }
        return null;
    }

    private static final class SearchResult {
        private final BlockPos position;
        private final boolean timedOut;

        private SearchResult(BlockPos position, boolean timedOut) {
            this.position = position;
            this.timedOut = timedOut;
        }
    }
}


