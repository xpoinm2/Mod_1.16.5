package com.example.examplemod.server.mechanics;

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
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

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

    // ---- Optional Forge events (enable flags keep dispatch cheap for 100+ modules) ----

    default boolean enableWorldTick() { return false; }
    default void onWorldTick(TickEvent.WorldTickEvent event) { }

    default boolean enableBlockBreak() { return false; }
    default void onBlockBreak(BlockEvent.BreakEvent event) { }

    default boolean enablePlayerInteract() { return false; }
    default void onPlayerInteract(PlayerInteractEvent event) { }

    default boolean enableAttackEntity() { return false; }
    default void onAttackEntity(AttackEntityEvent event) { }

    default boolean enableLivingJump() { return false; }
    default void onLivingJump(LivingEvent.LivingJumpEvent event) { }

    default boolean enableUseItemFinish() { return false; }
    default void onUseItemFinish(LivingEntityUseItemEvent.Finish event) { }

    default boolean enableLivingDrops() { return false; }
    default void onLivingDrops(LivingDropsEvent event) { }

    default boolean enableChunkEvent() { return false; }
    default void onChunkEvent(ChunkEvent event) { }

    default boolean enablePlayerClone() { return false; }
    default void onPlayerClone(PlayerEvent.Clone event) { }

    default boolean enableRegisterCommands() { return false; }
    default void onRegisterCommands(RegisterCommandsEvent event) { }

    default boolean enableServerStopping() { return false; }
    default void onServerStopping(FMLServerStoppingEvent event) { }

    default boolean enableServerStarting() { return false; }
    default void onServerStarting(FMLServerStartingEvent event) { }

    default boolean enableItemCrafted() { return false; }
    default void onItemCrafted(PlayerEvent.ItemCraftedEvent event) { }
}


