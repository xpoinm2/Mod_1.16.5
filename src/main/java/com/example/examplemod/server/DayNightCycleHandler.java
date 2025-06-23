package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DayNightCycleHandler {
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.world instanceof ServerWorld)) return;

        ServerWorld world = (ServerWorld) event.world;
        long time = world.getDayTime();
        long dayTime = time % 24000L;
        if (dayTime < 12000L && time % 2L == 0L) {
            world.setDayTime(time - 1);
        }
    }
}