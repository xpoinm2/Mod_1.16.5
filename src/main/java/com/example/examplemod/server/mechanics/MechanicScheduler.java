package com.example.examplemod.server.mechanics;

import com.example.examplemod.Config;
import com.example.examplemod.ExampleMod;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Единая точка тиков для механик.
 *
 * Идея: модулей может быть 100+, но вход в событие — один,
 * и каждый модуль сам говорит, как часто ему нужно выполняться.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MechanicScheduler {
    private static final Logger LOGGER = LogManager.getLogger();

    private static int serverTickCounter = 0;

    private static final class Perf {
        long totalNanos;
        long maxNanos;
        int calls;

        void add(long nanos) {
            totalNanos += nanos;
            calls++;
            if (nanos > maxNanos) maxNanos = nanos;
        }

        void reset() {
            totalNanos = 0;
            maxNanos = 0;
            calls = 0;
        }
    }

    // простая статистика (только серверный поток)
    private static final Map<String, Perf> PERF = new HashMap<>();
    private static int perfLogCounter = 0;

    private MechanicScheduler() {
    }

    private static void ensureInit() {
        if (!ModMechanics.isInitialized()) {
            ModMechanics.init();
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        ensureInit();

        if (ModMechanics.modules().isEmpty()) return;

        serverTickCounter++;
        MinecraftServer server = event.getServer();

        final boolean profile = Config.MECHANICS_PROFILING.get();
        final int logEvery = Math.max(20, Config.MECHANICS_PROFILE_LOG_EVERY_TICKS.get());

        for (IMechanicModule module : ModMechanics.modules()) {
            int interval = module.serverIntervalTicks();
            if (interval <= 0) continue;
            if ((serverTickCounter % interval) != 0) continue;

            if (!profile) {
                module.onServerTick(server);
                continue;
            }

            long start = System.nanoTime();
            try {
                module.onServerTick(server);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onServerTick()", module.id(), e);
            } finally {
                long nanos = System.nanoTime() - start;
                PERF.computeIfAbsent(module.id(), k -> new Perf()).add(nanos);
            }
        }

        if (profile) {
            perfLogCounter++;
            if (perfLogCounter >= logEvery) {
                perfLogCounter = 0;
                flushPerf("server");
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayerEntity)) return;
        ensureInit();
        if (ModMechanics.modules().isEmpty()) return;

        ServerPlayerEntity player = (ServerPlayerEntity) event.player;

        final boolean profile = Config.MECHANICS_PROFILING.get();
        final int thresholdMs = Math.max(0, Config.MECHANICS_SLOW_CALL_THRESHOLD_MS.get());

        int tick = player.tickCount;
        for (IMechanicModule module : ModMechanics.modules()) {
            int interval = module.playerIntervalTicks();
            if (interval <= 0) continue;
            if ((tick % interval) != 0) continue;

            if (!profile) {
                module.onPlayerTick(player);
                continue;
            }

            long start = System.nanoTime();
            try {
                module.onPlayerTick(player);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onPlayerTick() for {}", module.id(), player.getName().getString(), e);
            } finally {
                long nanos = System.nanoTime() - start;
                PERF.computeIfAbsent(module.id(), k -> new Perf()).add(nanos);

                if (thresholdMs > 0) {
                    long ms = nanos / 1_000_000L;
                    if (ms >= thresholdMs) {
                        LOGGER.warn("Slow mechanic call: id='{}' took {}ms (player={})",
                                module.id(), ms, player.getName().getString());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayerEntity)) return;
        ensureInit();
        if (ModMechanics.modules().isEmpty()) return;

        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        for (IMechanicModule module : ModMechanics.modules()) {
            try {
                module.onPlayerLogin(player);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onPlayerLogin()", module.id(), e);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayerEntity)) return;
        ensureInit();
        if (ModMechanics.modules().isEmpty()) return;

        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        for (IMechanicModule module : ModMechanics.modules()) {
            try {
                module.onPlayerLogout(player);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onPlayerLogout()", module.id(), e);
            }
        }
    }

    private static void flushPerf(String scope) {
        if (PERF.isEmpty()) return;

        // Логируем только действительно "тяжёлые" суммарно.
        // Это не профайлер JVM, но позволяет быстро увидеть, какая механика начала жрать время.
        LOGGER.info("[mechanics][{}] perf summary:", scope);
        for (Map.Entry<String, Perf> e : PERF.entrySet()) {
            Perf p = e.getValue();
            if (p.calls <= 0) continue;
            long avg = p.totalNanos / p.calls;
            LOGGER.info("  - {}: calls={}, avg={}us, max={}us",
                    e.getKey(),
                    p.calls,
                    avg / 1_000L,
                    p.maxNanos / 1_000L
            );
            p.reset();
        }
    }
}


