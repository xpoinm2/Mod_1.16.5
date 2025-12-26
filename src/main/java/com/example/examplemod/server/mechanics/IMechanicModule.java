package com.example.examplemod.server.mechanics;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

/**
 * Базовый интерфейс "механики" для большого мода.
 *
 * Идея: вместо 100 классов с @SubscribeEvent onPlayerTick(), мы регистрируем модули здесь,
 * а один общий шедулер вызывает их с нужной частотой.
 *
 * Правило по производительности: любые "тяжёлые" вычисления делайте по интервалам (не каждый тик),
 * и держите ранние выходы.
 */
public interface IMechanicModule {
    /**
     * Уникальный id механики (для логов/профилирования).
     */
    String id();

    /**
     * Интервал вызова {@link #onPlayerTick(ServerPlayerEntity)} в тиках.
     * 0 или меньше — модуль не будет вызываться на player tick.
     */
    default int playerIntervalTicks() {
        return 0;
    }

    /**
     * Интервал вызова {@link #onServerTick(MinecraftServer)} в тиках.
     * 0 или меньше — модуль не будет вызываться на server tick.
     */
    default int serverIntervalTicks() {
        return 0;
    }

    /**
     * Вызывается шедулером раз в {@link #playerIntervalTicks()} тиков (END phase, server-side).
     */
    default void onPlayerTick(ServerPlayerEntity player) {
    }

    /**
     * Вызывается шедулером раз в {@link #serverIntervalTicks()} тиков (END phase, server-side).
     */
    default void onServerTick(MinecraftServer server) {
    }

    /**
     * Хуки жизненного цикла игрока (опционально).
     * Используй для очистки Map<UUID,...> и т.п.
     */
    default void onPlayerLogin(ServerPlayerEntity player) {
    }

    default void onPlayerLogout(ServerPlayerEntity player) {
    }
}


