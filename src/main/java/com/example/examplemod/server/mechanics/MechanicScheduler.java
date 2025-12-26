package com.example.examplemod.server.mechanics;

import com.example.examplemod.Config;
import com.example.examplemod.ExampleMod;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
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
        long minNanos = Long.MAX_VALUE;
        int calls;

        void add(long nanos) {
            totalNanos += nanos;
            calls++;
            if (nanos > maxNanos) maxNanos = nanos;
            if (nanos < minNanos) minNanos = nanos;
        }

        double getAvgMicros() {
            return calls > 0 ? (double) totalNanos / calls / 1_000L : 0.0;
        }

        void reset() {
            totalNanos = 0;
            maxNanos = 0;
            minNanos = Long.MAX_VALUE;
            calls = 0;
        }
    }

    // Оптимизация: отдельные карты для разных событий для более детальной статистики
    private static final Map<String, Perf> PERF_SERVER = new HashMap<>();
    private static final Map<String, Perf> PERF_PLAYER = new HashMap<>();
    private static final Map<String, Perf> PERF_EVENTS = new HashMap<>();
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
        MinecraftServer server = net.minecraftforge.fml.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

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
                PERF_SERVER.computeIfAbsent(module.id(), k -> new Perf()).add(nanos);
            }
        }

        if (profile) {
            perfLogCounter++;
            if (perfLogCounter >= logEvery) {
                perfLogCounter = 0;
                flushPerfReport();
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
                PERF_PLAYER.computeIfAbsent(module.id(), k -> new Perf()).add(nanos);

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

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.world.isClientSide()) return;
        ensureInit();
        if (ModMechanics.modules().isEmpty()) return;

        for (IMechanicModule module : ModMechanics.modules()) {
            if (!module.enableWorldTick()) continue;
            try {
                module.onWorldTick(event);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onWorldTick()", module.id(), e);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        ensureInit();
        if (ModMechanics.modules().isEmpty()) return;
        for (IMechanicModule module : ModMechanics.modules()) {
            if (!module.enablePlayerClone()) continue;
            try {
                module.onPlayerClone(event);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onPlayerClone()", module.id(), e);
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ensureInit();
        if (ModMechanics.modules().isEmpty()) return;
        for (IMechanicModule module : ModMechanics.modules()) {
            if (!module.enableRegisterCommands()) continue;
            try {
                module.onRegisterCommands(event);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onRegisterCommands()", module.id(), e);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().isClientSide()) return;
        ensureInit();
        if (ModMechanics.modules().isEmpty()) return;
        
        final boolean profile = Config.MECHANICS_PROFILING.get();
        for (IMechanicModule module : ModMechanics.modules()) {
            if (!module.enableBlockBreak()) continue;
            
            if (!profile) {
                try {
                    module.onBlockBreak(event);
                } catch (RuntimeException e) {
                    LOGGER.error("Mechanic '{}' crashed in onBlockBreak()", module.id(), e);
                }
                continue;
            }
            
            long start = System.nanoTime();
            try {
                module.onBlockBreak(event);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onBlockBreak()", module.id(), e);
            } finally {
                long nanos = System.nanoTime() - start;
                PERF_EVENTS.computeIfAbsent(module.id() + ".blockBreak", k -> new Perf()).add(nanos);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getWorld().isClientSide()) return;
        ensureInit();
        if (ModMechanics.modules().isEmpty()) return;
        for (IMechanicModule module : ModMechanics.modules()) {
            if (!module.enablePlayerInteract()) continue;
            try {
                module.onPlayerInteract(event);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onPlayerInteract()", module.id(), e);
            }
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (event.getPlayer() == null || event.getPlayer().level.isClientSide) return;
        ensureInit();
        if (ModMechanics.modules().isEmpty()) return;
        
        final boolean profile = Config.MECHANICS_PROFILING.get();
        for (IMechanicModule module : ModMechanics.modules()) {
            if (!module.enableAttackEntity()) continue;
            
            if (!profile) {
                try {
                    module.onAttackEntity(event);
                } catch (RuntimeException e) {
                    LOGGER.error("Mechanic '{}' crashed in onAttackEntity()", module.id(), e);
                }
                continue;
            }
            
            long start = System.nanoTime();
            try {
                module.onAttackEntity(event);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onAttackEntity()", module.id(), e);
            } finally {
                long nanos = System.nanoTime() - start;
                PERF_EVENTS.computeIfAbsent(module.id() + ".attack", k -> new Perf()).add(nanos);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntityLiving() == null || event.getEntityLiving().level.isClientSide) return;
        ensureInit();
        if (ModMechanics.modules().isEmpty()) return;
        
        final boolean profile = Config.MECHANICS_PROFILING.get();
        for (IMechanicModule module : ModMechanics.modules()) {
            if (!module.enableLivingJump()) continue;
            
            if (!profile) {
                try {
                    module.onLivingJump(event);
                } catch (RuntimeException e) {
                    LOGGER.error("Mechanic '{}' crashed in onLivingJump()", module.id(), e);
                }
                continue;
            }
            
            long start = System.nanoTime();
            try {
                module.onLivingJump(event);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onLivingJump()", module.id(), e);
            } finally {
                long nanos = System.nanoTime() - start;
                PERF_EVENTS.computeIfAbsent(module.id() + ".jump", k -> new Perf()).add(nanos);
            }
        }
    }

    @SubscribeEvent
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() == null || event.getEntity().level.isClientSide) return;
        ensureInit();
        if (ModMechanics.modules().isEmpty()) return;
        
        final boolean profile = Config.MECHANICS_PROFILING.get();
        for (IMechanicModule module : ModMechanics.modules()) {
            if (!module.enableUseItemFinish()) continue;
            
            if (!profile) {
                try {
                    module.onUseItemFinish(event);
                } catch (RuntimeException e) {
                    LOGGER.error("Mechanic '{}' crashed in onUseItemFinish()", module.id(), e);
                }
                continue;
            }
            
            long start = System.nanoTime();
            try {
                module.onUseItemFinish(event);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onUseItemFinish()", module.id(), e);
            } finally {
                long nanos = System.nanoTime() - start;
                PERF_EVENTS.computeIfAbsent(module.id() + ".useItem", k -> new Perf()).add(nanos);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity() == null || event.getEntity().level.isClientSide) return;
        ensureInit();
        if (ModMechanics.modules().isEmpty()) return;
        for (IMechanicModule module : ModMechanics.modules()) {
            if (!module.enableLivingDrops()) continue;
            try {
                module.onLivingDrops(event);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onLivingDrops()", module.id(), e);
            }
        }
    }

    @SubscribeEvent
    public static void onChunkEvent(ChunkEvent event) {
        ensureInit();
        if (ModMechanics.modules().isEmpty()) return;
        for (IMechanicModule module : ModMechanics.modules()) {
            if (!module.enableChunkEvent()) continue;
            try {
                module.onChunkEvent(event);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onChunkEvent()", module.id(), e);
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppingEvent event) {
        ensureInit();
        if (ModMechanics.modules().isEmpty()) return;
        for (IMechanicModule module : ModMechanics.modules()) {
            if (!module.enableServerStopping()) continue;
            try {
                module.onServerStopping(event);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onServerStopping()", module.id(), e);
            }
        }
    }

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event) {
        ensureInit();
        if (ModMechanics.modules().isEmpty()) return;
        for (IMechanicModule module : ModMechanics.modules()) {
            if (!module.enableServerStarting()) continue;
            try {
                module.onServerStarting(event);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onServerStarting()", module.id(), e);
            }
        }
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getPlayer() == null || event.getPlayer().level.isClientSide) return;
        ensureInit();
        if (ModMechanics.modules().isEmpty()) return;
        for (IMechanicModule module : ModMechanics.modules()) {
            if (!module.enableItemCrafted()) continue;
            try {
                module.onItemCrafted(event);
            } catch (RuntimeException e) {
                LOGGER.error("Mechanic '{}' crashed in onItemCrafted()", module.id(), e);
            }
        }
    }

    /**
     * Выводит полный отчёт о производительности механик.
     * Сортирует по среднему времени выполнения (самые медленные сверху).
     */
    private static void flushPerfReport() {
        boolean hasData = false;
        long totalServerNanos = 0;
        long totalPlayerNanos = 0;
        long totalEventsNanos = 0;

        // Подсчёт общего времени для процентов
        for (Perf p : PERF_SERVER.values()) totalServerNanos += p.totalNanos;
        for (Perf p : PERF_PLAYER.values()) totalPlayerNanos += p.totalNanos;
        for (Perf p : PERF_EVENTS.values()) totalEventsNanos += p.totalNanos;

        LOGGER.info("========== Mechanics Performance Report ==========");

        // Server Tick
        if (!PERF_SERVER.isEmpty() && totalServerNanos > 0) {
            hasData = true;
            LOGGER.info("[SERVER TICK] Total: {}ms", totalServerNanos / 1_000_000L);
            printSortedPerf(PERF_SERVER, totalServerNanos);
            PERF_SERVER.values().forEach(Perf::reset);
        }

        // Player Tick
        if (!PERF_PLAYER.isEmpty() && totalPlayerNanos > 0) {
            hasData = true;
            LOGGER.info("[PLAYER TICK] Total: {}ms", totalPlayerNanos / 1_000_000L);
            printSortedPerf(PERF_PLAYER, totalPlayerNanos);
            PERF_PLAYER.values().forEach(Perf::reset);
        }

        // Events
        if (!PERF_EVENTS.isEmpty() && totalEventsNanos > 0) {
            hasData = true;
            LOGGER.info("[EVENTS] Total: {}ms", totalEventsNanos / 1_000_000L);
            printSortedPerf(PERF_EVENTS, totalEventsNanos);
            PERF_EVENTS.values().forEach(Perf::reset);
        }

        if (hasData) {
            LOGGER.info("==================================================");
        }
    }

    /**
     * Выводит отсортированную статистику механик (от медленных к быстрым).
     */
    private static void printSortedPerf(Map<String, Perf> perfMap, long totalNanos) {
        perfMap.entrySet().stream()
                .filter(e -> e.getValue().calls > 0)
                .sorted((a, b) -> Long.compare(b.getValue().totalNanos, a.getValue().totalNanos))
                .forEach(e -> {
                    String name = e.getKey();
                    Perf p = e.getValue();
                    double percent = totalNanos > 0 ? (p.totalNanos * 100.0 / totalNanos) : 0.0;
                    LOGGER.info("  {:30} | calls={:6} | avg={:6.2f}µs | max={:6.2f}ms | min={:4.2f}µs | {:5.2f}%",
                            name,
                            p.calls,
                            p.getAvgMicros(),
                            p.maxNanos / 1_000_000.0,
                            p.minNanos / 1_000.0,
                            percent
                    );
                });
    }

    /**
     * Получить снимок статистики (для команд или внешнего использования).
     */
    public static Map<String, String> getPerfSnapshot() {
        Map<String, String> snapshot = new HashMap<>();
        
        for (Map.Entry<String, Perf> e : PERF_SERVER.entrySet()) {
            Perf p = e.getValue();
            if (p.calls > 0) {
                snapshot.put("server." + e.getKey(), 
                    String.format("calls=%d, avg=%.2fµs, max=%.2fms", 
                        p.calls, p.getAvgMicros(), p.maxNanos / 1_000_000.0));
            }
        }
        
        for (Map.Entry<String, Perf> e : PERF_PLAYER.entrySet()) {
            Perf p = e.getValue();
            if (p.calls > 0) {
                snapshot.put("player." + e.getKey(), 
                    String.format("calls=%d, avg=%.2fµs, max=%.2fms", 
                        p.calls, p.getAvgMicros(), p.maxNanos / 1_000_000.0));
            }
        }
        
        for (Map.Entry<String, Perf> e : PERF_EVENTS.entrySet()) {
            Perf p = e.getValue();
            if (p.calls > 0) {
                snapshot.put("event." + e.getKey(), 
                    String.format("calls=%d, avg=%.2fµs, max=%.2fms", 
                        p.calls, p.getAvgMicros(), p.maxNanos / 1_000_000.0));
            }
        }
        
        return snapshot;
    }
}


