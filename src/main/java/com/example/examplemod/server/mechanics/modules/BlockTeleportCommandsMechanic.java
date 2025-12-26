package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.server.mechanics.IMechanicModule;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.registries.ForgeRegistries;

public final class BlockTeleportCommandsMechanic implements IMechanicModule {
    @Override
    public String id() {
        return "block_teleport_commands";
    }

    @Override
    public boolean enableRegisterCommands() {
        return true;
    }

    @Override
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("tpblock")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.argument("block", StringArgumentType.string())
                                .executes(ctx -> teleport(ctx, StringArgumentType.getString(ctx, "block"))))
        );
    }

    private static final int SEARCH_RADIUS = 128;

    private static int teleport(CommandContext<CommandSource> ctx, String blockName) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        ServerWorld world = player.getLevel();
        ResourceLocation id = blockName.contains(":")
                ? new ResourceLocation(blockName)
                : new ResourceLocation(ExampleMod.MODID, blockName);
        if (!ForgeRegistries.BLOCKS.containsKey(id)) {
            ctx.getSource().sendFailure(new StringTextComponent("Unknown block: " + blockName));
            return 0;
        }
        Block block = ForgeRegistries.BLOCKS.getValue(id);
        BlockPos origin = player.blockPosition();
        BlockPos found = findBlock(world, block, origin);
        if (found == null) {
            ctx.getSource().sendFailure(new StringTextComponent("Block not found"));
            return 0;
        }
        world.getChunk(found.getX() >> 4, found.getZ() >> 4); // ensure chunk is loaded
        player.teleportTo(world, found.getX() + 0.5, found.getY() + 1, found.getZ() + 0.5, player.yRot, player.xRot);
        ctx.getSource().sendSuccess(new StringTextComponent("Teleported to " + blockName), false);
        return 1;
    }

    private static BlockPos findBlock(ServerWorld world, Block block, BlockPos origin) {
        ChunkPos originChunk = new ChunkPos(origin);
        int chunkRadius = Math.max(0, (SEARCH_RADIUS + 15) >> 4);
        int minY = Math.max(0, origin.getY() - SEARCH_RADIUS);
        int maxY = Math.min(world.getMaxBuildHeight() - 1, origin.getY() + SEARCH_RADIUS);
        Mutable mutablePos = new Mutable();
        BlockPos bestPos = null;
        double bestDistance = Double.MAX_VALUE;

        for (int r = 0; r <= chunkRadius; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.abs(dx) != r && Math.abs(dz) != r) continue;

                    int chunkX = originChunk.x + dx;
                    int chunkZ = originChunk.z + dz;

                    IChunk chunk = world.getChunkSource().getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
                    if (chunk == null) {
                        continue;
                    }

                    int chunkMinX = chunkX << 4;
                    int chunkMinZ = chunkZ << 4;
                    int chunkMaxX = chunkMinX + 15;
                    int chunkMaxZ = chunkMinZ + 15;

                    int searchMinX = Math.max(chunkMinX, origin.getX() - SEARCH_RADIUS);
                    int searchMaxX = Math.min(chunkMaxX, origin.getX() + SEARCH_RADIUS);
                    int searchMinZ = Math.max(chunkMinZ, origin.getZ() - SEARCH_RADIUS);
                    int searchMaxZ = Math.min(chunkMaxZ, origin.getZ() + SEARCH_RADIUS);

                    if (searchMinX > searchMaxX || searchMinZ > searchMaxZ) {
                        continue;
                    }

                    for (int x = searchMinX; x <= searchMaxX; x++) {
                        for (int z = searchMinZ; z <= searchMaxZ; z++) {
                            for (int y = minY; y <= maxY; y++) {
                                mutablePos.set(x, y, z);
                                if (chunk.getBlockState(mutablePos).getBlock() == block) {
                                    double distance = mutablePos.distSqr(origin.getX(), origin.getY(), origin.getZ(), true);
                                    if (distance < bestDistance) {
                                        bestDistance = distance;
                                        bestPos = mutablePos.immutable();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (bestPos != null) {
                return bestPos;
            }
        }
        return null;
    }
}


