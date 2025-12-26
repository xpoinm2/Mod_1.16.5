package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.server.mechanics.IMechanicModule;
import com.example.examplemod.world.feature.DesertPyramidFeature;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;

public final class PyramidDebugCommandsMechanic implements IMechanicModule {
    @Override
    public String id() {
        return "pyramid_debug_commands";
    }

    @Override
    public boolean enableRegisterCommands() {
        return true;
    }

    @Override
    public void onRegisterCommands(RegisterCommandsEvent event) {
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


