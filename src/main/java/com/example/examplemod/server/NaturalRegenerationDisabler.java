package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import net.minecraft.world.GameRules;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

/**
 * Disables natural health regeneration from hunger when the server starts.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NaturalRegenerationDisabler {
    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event) {
        event.getServer().getGameRules()
                .getRule(GameRules.RULE_NATURAL_REGENERATION)
                .set(false, event.getServer());
    }
}