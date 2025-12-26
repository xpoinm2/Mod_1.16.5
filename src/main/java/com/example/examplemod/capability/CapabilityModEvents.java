package com.example.examplemod.capability;

import com.example.examplemod.ExampleMod;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * MOD-bus события для регистрации capability.
 * Важно: FMLCommonSetupEvent приходит на MOD event bus, а не на Forge bus.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CapabilityModEvents {
    private CapabilityModEvents() {
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(
                IPlayerStats.class,
                new PlayerStatsStorage(),
                PlayerStats::new
        );
    }
}


