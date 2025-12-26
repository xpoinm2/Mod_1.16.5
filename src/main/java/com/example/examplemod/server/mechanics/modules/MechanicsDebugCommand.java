package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.server.mechanics.IMechanicModule;
import com.example.examplemod.server.mechanics.MechanicScheduler;
import com.example.examplemod.server.mechanics.ModMechanics;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.RegisterCommandsEvent;

import java.util.Map;

/**
 * Команда для отладки и мониторинга производительности механик.
 */
public class MechanicsDebugCommand implements IMechanicModule {
    @Override
    public String id() {
        return "mechanics_debug";
    }

    @Override
    public boolean enableRegisterCommands() {
        return true;
    }

    @Override
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        
        dispatcher.register(Commands.literal("mechanics")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("list")
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(
                                    new StringTextComponent("=== Registered Mechanics ===")
                                            .withStyle(TextFormatting.GOLD, TextFormatting.BOLD), 
                                    false
                            );
                            
                            int count = 0;
                            for (IMechanicModule module : ModMechanics.modules()) {
                                count++;
                                String intervals = String.format("server:%d, player:%d", 
                                        module.serverIntervalTicks(), 
                                        module.playerIntervalTicks());
                                
                                ctx.getSource().sendSuccess(
                                        new StringTextComponent(String.format("%d. %s (%s)", 
                                                count, module.id(), intervals))
                                                .withStyle(TextFormatting.YELLOW), 
                                        false
                                );
                            }
                            
                            ctx.getSource().sendSuccess(
                                    new StringTextComponent("Total: " + count + " mechanics")
                                            .withStyle(TextFormatting.GREEN), 
                                    false
                            );
                            
                            return count;
                        })
                )
                .then(Commands.literal("perf")
                        .executes(ctx -> {
                            Map<String, String> snapshot = MechanicScheduler.getPerfSnapshot();
                            
                            if (snapshot.isEmpty()) {
                                ctx.getSource().sendSuccess(
                                        new StringTextComponent("No performance data available. Enable profiling in config.")
                                                .withStyle(TextFormatting.RED), 
                                        false
                                );
                                return 0;
                            }
                            
                            ctx.getSource().sendSuccess(
                                    new StringTextComponent("=== Mechanics Performance ===")
                                            .withStyle(TextFormatting.GOLD, TextFormatting.BOLD), 
                                    false
                            );
                            
                            snapshot.forEach((key, value) -> {
                                ctx.getSource().sendSuccess(
                                        new StringTextComponent(key + ": " + value)
                                                .withStyle(TextFormatting.YELLOW), 
                                        false
                                );
                            });
                            
                            ctx.getSource().sendSuccess(
                                    new StringTextComponent("Tip: Check server logs for detailed reports")
                                            .withStyle(TextFormatting.GRAY, TextFormatting.ITALIC), 
                                    false
                            );
                            
                            return snapshot.size();
                        })
                )
                .then(Commands.literal("help")
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(
                                    new StringTextComponent("=== Mechanics Debug Commands ===")
                                            .withStyle(TextFormatting.GOLD, TextFormatting.BOLD), 
                                    false
                            );
                            ctx.getSource().sendSuccess(
                                    new StringTextComponent("/mechanics list - Show all registered mechanics"), 
                                    false
                            );
                            ctx.getSource().sendSuccess(
                                    new StringTextComponent("/mechanics perf - Show performance snapshot"), 
                                    false
                            );
                            ctx.getSource().sendSuccess(
                                    new StringTextComponent("/mechanics help - Show this help"), 
                                    false
                            );
                            return 1;
                        })
                )
        );
    }
}

