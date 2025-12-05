package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.server.HotOreTimerHandler;
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
        // Цикл день/ночь работает всегда, независимо от игроков

        ServerWorld world = (ServerWorld) event.world;
        long time = world.getDayTime();
        long dayTime = time % 24000L;

        // Treat the first 16 in-game hours (0-16000 ticks) as day time.
        if (dayTime < 16000L) {
            // Day: slow down to last 16 real minutes (5/8 speed)
            if (world.getGameTime() % 8L < 3L) {
                world.setDayTime(time - 1);
            }
        } else {
            // Night: speed up to last 8 real minutes (5/4 speed)
            if (world.getGameTime() % 4L == 0L) {
                world.setDayTime(time + 1);
            }
        }
    }
}