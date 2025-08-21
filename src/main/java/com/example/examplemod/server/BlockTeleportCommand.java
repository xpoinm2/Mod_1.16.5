package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Command to teleport the player to the nearest instance of a given block.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockTeleportCommand {

    private static final int SEARCH_RADIUS = 128;

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("tpblock")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.argument("block", StringArgumentType.string())
                                .executes(ctx -> teleport(ctx, StringArgumentType.getString(ctx, "block"))))
        );
    }

    private static int teleport(CommandContext<CommandSource> ctx, String blockName) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        ServerWorld world = player.getLevel();
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        if (block == null) {
            ctx.getSource().sendFailure(new StringTextComponent("Unknown block: " + blockName));
            return 0;
        }
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
        for (int r = 0; r <= SEARCH_RADIUS; r++) {
            for (int x = -r; x <= r; x++) {
                for (int y = -r; y <= r; y++) {
                    for (int z = -r; z <= r; z++) {
                        if (Math.abs(x) != r && Math.abs(y) != r && Math.abs(z) != r) continue;
                        int posY = origin.getY() + y;
                        if (posY < 0 || posY >= world.getMaxBuildHeight()) continue;
                        BlockPos pos = origin.offset(x, y, z);
                        if (world.getBlockState(pos).getBlock() == block) {
                            return pos.immutable();
                        }
                    }
                }
            }
        }
        return null;
    }
}