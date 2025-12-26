package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.quest.QuestManager;
import com.example.examplemod.server.mechanics.IMechanicModule;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;

public final class QuestCommandsMechanic implements IMechanicModule {
    @Override
    public String id() {
        return "quest_commands";
    }

    @Override
    public boolean enableRegisterCommands() {
        return true;
    }

    @Override
    public void onRegisterCommands(RegisterCommandsEvent event) {
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


