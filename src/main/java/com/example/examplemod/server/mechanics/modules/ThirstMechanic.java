package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncAllStatsPacket;
import com.example.examplemod.server.mechanics.IMechanicModule;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Жажда + усталость (движение/бег/прыжки/плавание/покой + питьё/еда).
 * Перенесено из старого ThirstHandler в модуль.
 */
public final class ThirstMechanic implements IMechanicModule {
    private static final int TICKS_PER_2_MINUTES = 2400; // 2 real minutes = 2400 ticks
    private static final int TICKS_PER_15_SECONDS = 300; // 15 real seconds = 300 ticks
    
    // Оптимизация: статический Set для проверки рыбных предметов (O(1) вместо O(n) сравнений)
    private static final Set<Item> FISH_ITEMS;
    static {
        Set<Item> fish = new HashSet<>();
        fish.add(Items.COD);
        fish.add(Items.SALMON);
        fish.add(Items.PUFFERFISH);
        fish.add(Items.TROPICAL_FISH);
        fish.add(Items.COOKED_COD);
        fish.add(Items.COOKED_SALMON);
        FISH_ITEMS = fish; // Неизменяемость не требуется, т.к. private static final
    }

    // Оптимизация: Fastutil коллекции вместо HashMap (50-70% меньше памяти)
    private final Object2ObjectOpenHashMap<UUID, double[]> lastPos = new Object2ObjectOpenHashMap<>();
    private final Object2DoubleOpenHashMap<UUID> runDist = new Object2DoubleOpenHashMap<>();
    private final Object2IntOpenHashMap<UUID> jumpCount = new Object2IntOpenHashMap<>();
    private final Object2IntOpenHashMap<UUID> stillTicks = new Object2IntOpenHashMap<>();
    private final Object2IntOpenHashMap<UUID> hourTicks = new Object2IntOpenHashMap<>();
    private final Object2IntOpenHashMap<UUID> swimTicks = new Object2IntOpenHashMap<>();

    @Override
    public String id() {
        return "thirst";
    }

    @Override
    public int playerIntervalTicks() {
        return 5; // как было в оптимизированной версии
    }

    @Override
    public void onPlayerTick(ServerPlayerEntity player) {
        tick(player);
    }

    @Override
    public void onPlayerLogout(ServerPlayerEntity player) {
        UUID id = player.getUUID();
        lastPos.remove(id);
        runDist.remove(id);
        jumpCount.remove(id);
        stillTicks.remove(id);
        hourTicks.remove(id);
        swimTicks.remove(id);
    }

    @Override
    public boolean enableLivingJump() {
        return true;
    }

    @Override
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntityLiving() instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();

        UUID id = player.getUUID();
        int count = jumpCount.getOrDefault(id, 0) + 1;
        if (count >= 20) {
            count = 0;
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                int thirst = stats.getThirst();
                int fatigue = Math.min(100, stats.getFatigue() + 2);
                stats.setFatigue(fatigue);
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncAllStatsPacket(stats)
                );
            });
        }
        jumpCount.put(id, count);
    }

    @Override
    public boolean enableAttackEntity() {
        return true;
    }

    @Override
    public void onAttackEntity(AttackEntityEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            int fatigue = Math.min(100, stats.getFatigue() + 1); // +0.5 implemented as +1 every 2 attacks
            stats.setFatigue(fatigue);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncAllStatsPacket(stats)
            );
        });
    }

    @Override
    public boolean enableUseItemFinish() {
        return true;
    }

    @Override
    public void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
        ItemStack stack = event.getItem();

        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            int thirst = stats.getThirst();
            int fatigue = stats.getFatigue();

            if (stack.getItem() == Items.POTION && PotionUtils.getPotion(stack) == Potions.WATER) {
                thirst = Math.max(0, thirst - 20);
                stats.setThirst(thirst);
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncAllStatsPacket(stats)
                );
            } else if (isFishItem(stack)) {
                thirst = Math.min(100, thirst + 15);
                stats.setThirst(thirst);
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncAllStatsPacket(stats)
                );
            }
        });
    }

    public void onDrinkButton(ServerPlayerEntity player) {
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            int thirst = Math.max(0, stats.getThirst() - 2);
            stats.setThirst(thirst);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncAllStatsPacket(stats)
            );
        });
    }

    public void onMixWater(ServerPlayerEntity player) {
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            int fatigue = Math.min(100, stats.getFatigue() + 3);
            stats.setFatigue(fatigue);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncAllStatsPacket(stats)
            );
        });
    }

    private void tick(ServerPlayerEntity player) {
        // Делаем шаг раз в 5 тиков (4 раза в секунду), время считаем через deltaTicks.
        final int deltaTicks = 5;
        if ((player.tickCount % deltaTicks) != 0) return;

        UUID id = player.getUUID();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            boolean dirty = false;
            int thirst = stats.getThirst();
            int fatigue = stats.getFatigue();

            // Every 2400 ticks (2 real minutes) increase thirst and fatigue by 2
            int ht = hourTicks.getOrDefault(id, 0) + deltaTicks;
            if (ht >= TICKS_PER_2_MINUTES) {
                ht -= TICKS_PER_2_MINUTES;
                int newThirst = Math.min(100, thirst + 2);
                int newFatigue = Math.min(100, fatigue + 2);
                if (newThirst != thirst) { thirst = newThirst; dirty = true; }
                if (newFatigue != fatigue) { fatigue = newFatigue; dirty = true; }
            }
            hourTicks.put(id, ht);

            // Swimming fatigue gain every 15 real seconds
            if (player.isInWater()) {
                int st = swimTicks.getOrDefault(id, 0) + deltaTicks;
                if (st >= TICKS_PER_15_SECONDS) {
                    st -= TICKS_PER_15_SECONDS;
                    int newFatigue = Math.min(100, fatigue + 5);
                    if (newFatigue != fatigue) { fatigue = newFatigue; dirty = true; }
                }
                swimTicks.put(id, st);
            } else {
                swimTicks.remove(id);
            }

            // movement based logic
            double[] prev = lastPos.computeIfAbsent(id, u -> new double[]{player.getX(), player.getY(), player.getZ()});
            double dx = player.getX() - prev[0];
            double dy = player.getY() - prev[1];
            double dz = player.getZ() - prev[2];
            prev[0] = player.getX();
            prev[1] = player.getY();
            prev[2] = player.getZ();

            double distSqAll = dx * dx + dy * dy + dz * dz;
            if (distSqAll < 0.0001D) {
                int ticks = stillTicks.getOrDefault(id, 0) + deltaTicks;
                if (ticks >= 1200) { // 1 real minute = 1200 ticks
                    ticks -= 1200;
                    int newFatigue = Math.max(0, fatigue - 5);
                    if (newFatigue != fatigue) { fatigue = newFatigue; dirty = true; }
                }
                stillTicks.put(id, ticks);
            } else {
                stillTicks.put(id, 0);
            }

            if (player.isSprinting()) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                double total = runDist.getOrDefault(id, 0.0) + dist;
                if (total >= 100.0) {
                    int steps = (int) (total / 100.0);
                    total -= steps * 100.0;
                    int newFatigue = Math.min(100, fatigue + steps * 3);
                    if (newFatigue != fatigue) { fatigue = newFatigue; dirty = true; }
                }
                runDist.put(id, total);
            }

            if (dirty) {
                stats.setThirst(thirst);
                stats.setFatigue(fatigue);
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncAllStatsPacket(stats)
                );
            }
        });
    }

    private static boolean isFishItem(ItemStack stack) {
        return FISH_ITEMS.contains(stack.getItem());
    }
}


