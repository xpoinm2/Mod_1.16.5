package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Simple commands to teleport player to various biomes for testing.
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BiomeTeleportCommands {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
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
        );
    }

    private static int teleport(CommandContext<CommandSource> ctx, RegistryKey<Biome> biomeKey) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        ServerWorld world = player.getLevel();
        BlockPos origin = player.blockPosition();
        BlockPos found = findBiome(world, biomeKey, origin);
        if (found == null) {
            ctx.getSource().sendFailure(new StringTextComponent("Biome not found"));
            return 0;
        }
        int y = world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, found.getX(), found.getZ());
        player.teleportTo(world, found.getX() + 0.5, y, found.getZ() + 0.5, player.yRot, player.xRot);
        ctx.getSource().sendSuccess(new StringTextComponent("Teleported to " + biomeKey.location()), false);
        return 1;
    }

    private static final int SEARCH_RADIUS = 6400;
    private static final int SEARCH_STEP = 64;

    private static BlockPos findBiome(ServerWorld world, RegistryKey<Biome> biomeKey, BlockPos origin) {
        for (int r = 0; r <= SEARCH_RADIUS; r += SEARCH_STEP) {
            for (int x = -r; x <= r; x += SEARCH_STEP) {
                for (int z = -r; z <= r; z += SEARCH_STEP) {
                    BlockPos pos = origin.offset(x, 0, z);
                    Biome biome = world.getBiome(pos);
                    if (biome.getRegistryName() != null && biome.getRegistryName().equals(biomeKey.location())) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }
}