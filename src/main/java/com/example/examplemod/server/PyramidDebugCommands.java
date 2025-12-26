package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.world.feature.DesertPyramidFeature;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;

/**
 * Provides administrative commands related to custom world structures.
 */
public final class PyramidDebugCommands {
    private PyramidDebugCommands() {
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("pyramid")
                        .requires(cs -> cs.hasPermission(2))
                        .executes(ctx -> reportCount(ctx.getSource()))
                        .then(Commands.literal("count")
                                .executes(ctx -> reportCount(ctx.getSource())))
        );
    }

    private static int reportCount(CommandSource source) {
        int count = DesertPyramidFeature.getGeneratedCount();
        source.sendSuccess(new StringTextComponent("Desert pyramids generated: " + count), true);
        return count;
    }
}