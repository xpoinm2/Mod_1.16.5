package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModStructures;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Command that teleports the executing player to the nearest generated volcano structure.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VolcanoTeleportCommand {

    private static final int SEARCH_RADIUS = 512;

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("tpvolcano")
                        .requires(cs -> cs.hasPermission(2))
                        .executes(VolcanoTeleportCommand::teleportToVolcano)
        );
        event.getDispatcher().register(
                Commands.literal("tpminibiome")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.literal("volcano")
                                .executes(VolcanoTeleportCommand::teleportToVolcano))
        );
    }

    private static int teleportToVolcano(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        ServerWorld world = player.getLevel();
        Structure<?> volcano = ModStructures.VOLCANO.get();

        BlockPos origin = player.blockPosition();
        BlockPos found = world.getChunkSource().getGenerator()
                .findNearestMapFeature(world, volcano, origin, SEARCH_RADIUS, false);

        if (found == null) {
            ctx.getSource().sendFailure(new StringTextComponent("Volcano not found within search radius"));
            return 0;
        }

        world.getChunk(found.getX() >> 4, found.getZ() >> 4); // ensure the chunk is loaded
        BlockPos surface = world.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, found);
        player.teleportTo(world, surface.getX() + 0.5, surface.getY(), surface.getZ() + 0.5, player.yRot, player.xRot);
        ctx.getSource().sendSuccess(new StringTextComponent("Teleported to volcano at "
                + surface.getX() + ", " + surface.getY() + ", " + surface.getZ()), false);
        return 1;
    }
}