// src/main/java/com/example/examplemod/server/ThirstHandler.java
package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncStatsPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThirstHandler {
    private static final Logger LOGGER     = LogManager.getLogger();

    private static final Map<UUID, double[]> LAST_POS   = new HashMap<>();
    private static final Map<UUID, Double>   RUN_DIST   = new HashMap<>();
    private static final Map<UUID, Integer>  JUMP_COUNT = new HashMap<>();
    private static final Map<UUID, Integer>  STILL_TICKS= new HashMap<>();
    private static final Map<UUID, Integer>  HOUR_TICKS = new HashMap<>();
    private static final Map<UUID, Integer>  SWIM_TICKS = new HashMap<>();

    private static final int TICKS_PER_HOUR = 20 * 60; // 1 real minute
    private static final int TICKS_PER_15MIN = TICKS_PER_HOUR / 4;

    private static boolean isFishItem(ItemStack stack) {
        return stack.getItem() == Items.COD ||
                stack.getItem() == Items.SALMON ||
                stack.getItem() == Items.PUFFERFISH ||
                stack.getItem() == Items.TROPICAL_FISH ||
                stack.getItem() == Items.COOKED_COD ||
                stack.getItem() == Items.COOKED_SALMON;
    }

    public static void onLogout(PlayerLoggedOutEvent event) {
        // На логине/клоне синхронизацией занимается CapabilityHandler.
        LOGGER.debug("onLogout: игрок {} вышел", event.getPlayer().getName().getString());
        logout((ServerPlayerEntity) event.getPlayer());
    }

    /**
     * Вынесено для менеджера механик: очистка пер-игрок state.
     */
    public static void logout(ServerPlayerEntity player) {
        UUID id = player.getUUID();
        LAST_POS.remove(id);
        RUN_DIST.remove(id);
        JUMP_COUNT.remove(id);
        STILL_TICKS.remove(id);
        HOUR_TICKS.remove(id);
        SWIM_TICKS.remove(id);
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayerEntity)) return;
        tick((ServerPlayerEntity) event.player);
    }

    /**
     * Вынесено для менеджера механик: можно вызывать напрямую без создания TickEvent.
     */
    public static void tick(ServerPlayerEntity player) {
        UUID id = player.getUUID();

        // Оптимизация: основная логика жажды/усталости не требует обработки каждый тик.
        // Делаем шаг раз в 5 тиков (4 раза в секунду), сохраняя шкалу "в тиках" через deltaTicks.
        final int deltaTicks = 5;
        if ((player.tickCount % deltaTicks) != 0) return;

        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            boolean dirty = false;
            int thirst = stats.getThirst();
            int fatigue = stats.getFatigue();

            // Every in-game hour increase thirst and fatigue by 2
            int ht = HOUR_TICKS.getOrDefault(id, 0) + deltaTicks;
            if (ht >= TICKS_PER_HOUR) {
                ht -= TICKS_PER_HOUR;
                int newThirst = Math.min(100, thirst + 2);
                int newFatigue = Math.min(100, fatigue + 2);
                if (newThirst != thirst) { thirst = newThirst; dirty = true; }
                if (newFatigue != fatigue) { fatigue = newFatigue; dirty = true; }
            }
            HOUR_TICKS.put(id, ht);

            // Swimming fatigue gain
            if (player.isInWater()) {
                int st = SWIM_TICKS.getOrDefault(id, 0) + deltaTicks;
                if (st >= TICKS_PER_15MIN) {
                    st -= TICKS_PER_15MIN;
                    int newFatigue = Math.min(100, fatigue + 5);
                    if (newFatigue != fatigue) { fatigue = newFatigue; dirty = true; }
                }
                SWIM_TICKS.put(id, st);
            } else {
                SWIM_TICKS.remove(id);
            }

            // movement based logic
            double[] prev = LAST_POS.computeIfAbsent(id, u -> new double[]{player.getX(), player.getY(), player.getZ()});
            double dx = player.getX() - prev[0];
            double dy = player.getY() - prev[1];
            double dz = player.getZ() - prev[2];
            prev[0] = player.getX();
            prev[1] = player.getY();
            prev[2] = player.getZ();

            double distSqAll = dx * dx + dy * dy + dz * dz;
            if (distSqAll < 0.0001D) {
                int ticks = STILL_TICKS.getOrDefault(id, 0) + deltaTicks;
                if (ticks >= TICKS_PER_HOUR) {
                    ticks -= TICKS_PER_HOUR;
                    int newFatigue = Math.max(0, fatigue - 5);
                    if (newFatigue != fatigue) { fatigue = newFatigue; dirty = true; }
                }
                STILL_TICKS.put(id, ticks);
            } else {
                STILL_TICKS.put(id, 0);
            }

            if (player.isSprinting()) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                double total = RUN_DIST.getOrDefault(id, 0.0) + dist;
                if (total >= 100.0) {
                    int steps = (int) (total / 100.0);
                    total -= steps * 100.0;
                    int newFatigue = Math.min(100, fatigue + steps * 5);
                    if (newFatigue != fatigue) { fatigue = newFatigue; dirty = true; }
                }
                RUN_DIST.put(id, total);
            }

            if (dirty) {
                stats.setThirst(thirst);
                stats.setFatigue(fatigue);
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncStatsPacket(thirst, fatigue)
                );
            }
        });
    }

    public static void onPlayerJump(LivingJumpEvent event) {
        if (!(event.getEntityLiving() instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
        UUID id = player.getUUID();
        int count = JUMP_COUNT.getOrDefault(id, 0) + 1;
        if (count >= 20) {
            count = 0;
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                int thirst = stats.getThirst();
                int fatigue = Math.min(100, stats.getFatigue() + 3);
                stats.setFatigue(fatigue);
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncStatsPacket(thirst, fatigue)
                );
            });
        }
        JUMP_COUNT.put(id, count);
    }

    public static void onAttackEntity(AttackEntityEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            int thirst = stats.getThirst();
            int fatigue = Math.min(100, stats.getFatigue() + 1);
            stats.setFatigue(fatigue);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncStatsPacket(thirst, fatigue)
            );
        });
    }


    public static void onDrinkFinish(LivingEntityUseItemEvent.Finish event) {
        LOGGER.debug("onDrinkFinish: {} использует {}",
                event.getEntity().getName().getString(),
                event.getItem().getItem().getRegistryName()
        );
        if (!(event.getEntity() instanceof ServerPlayerEntity)) return;
        ItemStack stack = event.getItem();
        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            int thirst = stats.getThirst();
            int fatigue = stats.getFatigue();

            if (stack.getItem() == Items.POTION && PotionUtils.getPotion(stack) == Potions.WATER) {
                thirst = Math.max(0, thirst - 20);
                stats.setThirst(thirst);
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncStatsPacket(thirst, fatigue)
                );
            } else if (isFishItem(stack)) {
                thirst = Math.min(100, thirst + 15);
                stats.setThirst(thirst);
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncStatsPacket(thirst, fatigue)
                );
            }
        });
    }

    public static void onDrinkButton(ServerPlayerEntity player) {
        LOGGER.debug("onDrinkButton: нажата кнопка «Попить» игроком {}", player.getName().getString());
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            int thirst = Math.max(0, stats.getThirst() - 2);
            stats.setThirst(thirst);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncStatsPacket(thirst, stats.getFatigue())
            );
        });
    }

    public static void onMixWater(ServerPlayerEntity player) {
        LOGGER.debug("onMixWater: нажата кнопка «Перемешать» игроком {}", player.getName().getString());
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            int thirst = stats.getThirst();
            int fatigue = Math.min(100, stats.getFatigue() + 3);
            stats.setFatigue(fatigue);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncStatsPacket(thirst, fatigue)
            );
        });
    }
}
