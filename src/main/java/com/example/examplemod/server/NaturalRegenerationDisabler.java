package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import net.minecraft.world.GameRules;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

/**
 * Disables natural health regeneration from hunger when the server starts.
 */
public class NaturalRegenerationDisabler {
    public static void onServerStarting(FMLServerStartingEvent event) {
        event.getServer().getGameRules()
                .getRule(GameRules.RULE_NATURAL_REGENERATION)
                .set(false, event.getServer());
    }
}