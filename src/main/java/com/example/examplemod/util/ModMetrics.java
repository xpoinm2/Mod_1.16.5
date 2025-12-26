package com.example.examplemod.util;

import com.example.examplemod.Config;
import com.example.examplemod.ModRegistries;
import com.example.examplemod.server.mechanics.MechanicScheduler;
import com.example.examplemod.server.mechanics.ModMechanics;
import com.example.examplemod.server.mechanics.util.BiomeTemperatureCache;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Система метрик и телеметрии для мониторинга состояния мода.
 * 
 * Помогает отслеживать:
 * - Использование памяти
 * - Количество зарегистрированных объектов
 * - Размеры кэшей
 * - TPS сервера
 * 
 * ИСПОЛЬЗОВАНИЕ:
 * - Автоматически логирует метрики каждые 5 минут (если включено профилирование)
 * - Команда /mechanics metrics для просмотра в игре
 */
public class ModMetrics {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Для расчёта среднего TPS
    private static long lastTickTime = 0;
    private static int ticksSinceLastCheck = 0;
    private static double averageTPS = 20.0;
    
    /**
     * Обновить счётчики TPS (вызывается каждый серверный тик из MechanicScheduler).
     */
    public static void updateTPSMetrics() {
        ticksSinceLastCheck++;
        
        // Обновляем средний TPS раз в секунду
        if (ticksSinceLastCheck >= 20) {
            long now = System.currentTimeMillis();
            if (lastTickTime > 0) {
                long elapsed = now - lastTickTime;
                double actualTPS = (ticksSinceLastCheck * 1000.0) / elapsed;
                // Экспоненциальное сглаживание
                averageTPS = averageTPS * 0.8 + actualTPS * 0.2;
            }
            lastTickTime = now;
            ticksSinceLastCheck = 0;
        }
    }
    
    /**
     * Получить средний TPS.
     */
    public static double getAverageTPS() {
        return Math.min(20.0, averageTPS); // Ограничено 20 TPS
    }
    
    /**
     * Логировать полный отчёт о метриках.
     */
    public static void logFullReport() {
        if (!Config.MECHANICS_PROFILING.get()) return;
        
        LOGGER.info("==================== MOD METRICS ====================");
        logMemoryMetrics();
        logRegistryMetrics();
        logCacheMetrics();
        logPerformanceMetrics();
        LOGGER.info("=====================================================");
    }
    
    /**
     * Метрики использования памяти.
     */
    private static void logMemoryMetrics() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024; // MB
        long totalMemory = runtime.totalMemory() / 1024 / 1024; // MB
        long freeMemory = runtime.freeMemory() / 1024 / 1024; // MB
        long usedMemory = totalMemory - freeMemory;
        
        double usagePercent = (usedMemory * 100.0) / maxMemory;
        
        LOGGER.info("[MEMORY] Used: {} MB / {} MB ({:.1f}%)", usedMemory, maxMemory, usagePercent);
        LOGGER.info("[MEMORY] Allocated: {} MB, Free in allocated: {} MB", totalMemory, freeMemory);
        
        if (usagePercent > 80) {
            LOGGER.warn("[MEMORY] Memory usage is HIGH (>80%)! Consider increasing heap size.");
        }
    }
    
    /**
     * Метрики регистраций (предметы, блоки, механики).
     */
    private static void logRegistryMetrics() {
        int itemCount = ModRegistries.ITEMS.getEntries().size();
        int blockCount = ModRegistries.BLOCKS.getEntries().size();
        int mechanicCount = ModMechanics.modules().size();
        
        LOGGER.info("[REGISTRY] Items: {}, Blocks: {}, Mechanics: {}", 
                itemCount, blockCount, mechanicCount);
        
        // Прогноз: оценка памяти на основе количества объектов
        long estimatedItemMemory = itemCount * 1; // ~1 KB на предмет (примерно)
        long estimatedMechanicMemory = mechanicCount * 5; // ~5 KB на механику (примерно)
        
        LOGGER.info("[REGISTRY] Est. memory: Items ~{} KB, Mechanics ~{} KB", 
                estimatedItemMemory, estimatedMechanicMemory);
    }
    
    /**
     * Метрики кэшей.
     */
    private static void logCacheMetrics() {
        int biomeCacheSize = BiomeTemperatureCache.getCacheSize();
        
        LOGGER.info("[CACHE] Biome temperature cache: {} entries", biomeCacheSize);
        
        if (biomeCacheSize > 100) {
            LOGGER.warn("[CACHE] Biome cache is large (>100 entries). Consider reducing TTL.");
        }
    }
    
    /**
     * Метрики производительности.
     */
    private static void logPerformanceMetrics() {
        double tps = getAverageTPS();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        
        LOGGER.info("[PERFORMANCE] Average TPS: {:.2f} / 20.0", tps);
        
        if (server != null) {
            int playerCount = server.getPlayerCount();
            LOGGER.info("[PERFORMANCE] Online players: {}", playerCount);
        }
        
        // Получаем снимок производительности механик
        Map<String, String> perfSnapshot = MechanicScheduler.getPerfSnapshot();
        if (!perfSnapshot.isEmpty()) {
            LOGGER.info("[PERFORMANCE] Mechanic stats available: {} entries", perfSnapshot.size());
        }
        
        if (tps < 18.0) {
            LOGGER.warn("[PERFORMANCE] TPS is LOW (<18)! Server is lagging.");
        } else if (tps < 19.5) {
            LOGGER.warn("[PERFORMANCE] TPS is slightly low (<19.5). Monitor closely.");
        }
    }
    
    /**
     * Краткий отчёт для команды (без полного лога).
     */
    public static String getQuickSummary() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        
        int itemCount = ModRegistries.ITEMS.getEntries().size();
        int mechanicCount = ModMechanics.modules().size();
        int cacheSize = BiomeTemperatureCache.getCacheSize();
        double tps = getAverageTPS();
        
        return String.format(
            "Memory: %d/%d MB | Items: %d | Mechanics: %d | Cache: %d | TPS: %.2f",
            usedMemory, maxMemory, itemCount, mechanicCount, cacheSize, tps
        );
    }
    
    /**
     * Проверить, нужно ли провести GC (если память заканчивается).
     * Возвращает true если рекомендуется запустить gc.
     */
    public static boolean shouldRunGC() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        
        return (usedMemory * 100.0 / maxMemory) > 85; // Если использовано >85%
    }
    
    /**
     * Предложить GC если нужно.
     */
    public static void suggestGCIfNeeded() {
        if (shouldRunGC()) {
            LOGGER.warn("[MEMORY] Memory usage critical (>85%). Suggesting GC...");
            System.gc(); // Только предложение, JVM решает сама
            LOGGER.info("[MEMORY] GC suggestion sent.");
        }
    }
}

