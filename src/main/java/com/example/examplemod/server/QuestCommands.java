package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.quest.QuestManager;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;

public class QuestCommands {
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("resetquests")
                        .requires(cs -> cs.hasPermission(2))
                        .executes(ctx -> {
                            QuestManager.resetAll();
                            ctx.getSource().sendSuccess(new StringTextComponent("All quests have been reset"), true);
                            return 1;
                        })
        );
    }
}