package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.server.mechanics.IMechanicModule;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;

public class DayNightCycleMechanic implements IMechanicModule {
    @Override
    public String id() {
        return "day_night_cycle";
    }

    @Override
    public boolean enableWorldTick() {
        return true;
    }

    @Override
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.world instanceof ServerWorld)) return;

        ServerWorld world = (ServerWorld) event.world;
        // Не тратим тики на ускорение/замедление времени, если в мире нет игроков
        if (world.getServer() == null || world.getServer().getPlayerList().getPlayers().isEmpty()) return;

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

