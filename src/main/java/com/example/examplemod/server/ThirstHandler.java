// src/main/java/com/example/examplemod/server/ThirstHandler.java
package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncStatsPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ThirstHandler {
    private static final Logger LOGGER     = LogManager.getLogger();
    private static final String KEY_THIRST = "thirst";
    private static final String KEY_FATIGUE= "fatigue";

    private static final Map<UUID, double[]> LAST_POS   = new HashMap<>();
    private static final Map<UUID, Double>   RUN_DIST   = new HashMap<>();
    private static final Map<UUID, Integer>  JUMP_COUNT = new HashMap<>();
    private static final Map<UUID, Integer>  STILL_TICKS= new HashMap<>();
    private static final Map<UUID, Integer>  HOUR_TICKS = new HashMap<>();
    private static final Map<UUID, Integer>  SWIM_TICKS = new HashMap<>();

    private static final int TICKS_PER_HOUR = 20 * 60; // 1 real minute
    private static final int TICKS_PER_15MIN = TICKS_PER_HOUR / 4;

    private static CompoundNBT getStatsTag(PlayerEntity player) {
        CompoundNBT root = player.getPersistentData();
        if (!root.contains(PlayerEntity.PERSISTED_NBT_TAG)) {
            root.put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
        }
        return root.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
    }

    private static int getStat(PlayerEntity player, String key, int def) {
        CompoundNBT stats = getStatsTag(player);
        if (!stats.contains(key)) {
            stats.putInt(key, def);
        }
        return stats.getInt(key);
    }

    private static void setStat(PlayerEntity player, String key, int value) {
        getStatsTag(player).putInt(key, value);
    }

    private static boolean isFishItem(ItemStack stack) {
        return stack.getItem() == Items.COD ||
                stack.getItem() == Items.SALMON ||
                stack.getItem() == Items.PUFFERFISH ||
                stack.getItem() == Items.TROPICAL_FISH ||
                stack.getItem() == Items.COOKED_COD ||
                stack.getItem() == Items.COOKED_SALMON;
    }

    @SubscribeEvent
    public static void onClone(Clone event) {
        LOGGER.info("onClone: копирование статистик с {} на {}",
                event.getOriginal().getName().getString(),
                event.getPlayer().getName().getString()
        );
        event.getOriginal().getCapability(
                com.example.examplemod.capability.PlayerStatsProvider.PLAYER_STATS_CAP
        ).ifPresent(oldStats ->
                event.getPlayer().getCapability(
                        com.example.examplemod.capability.PlayerStatsProvider.PLAYER_STATS_CAP
                ).ifPresent(newStats -> {
                    newStats.setThirst(oldStats.getThirst());
                    newStats.setFatigue(oldStats.getFatigue());
                    newStats.setDisease(oldStats.getDisease());
                })
        );
    }

    @SubscribeEvent
    public static void onLogin(PlayerLoggedInEvent event) {
        LOGGER.info("onLogin: отправка статистик игроку {}", event.getPlayer().getName().getString());
        if (!(event.getPlayer() instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        ModNetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncStatsPacket(
                        getStat(player, KEY_THIRST, 40),
                        getStat(player, KEY_FATIGUE, 0)
                )
        );
    }

    @SubscribeEvent
    public static void onLogout(PlayerLoggedOutEvent event) {
        LOGGER.info("onLogout: игрок {} вышел", event.getPlayer().getName().getString());
        UUID id = event.getPlayer().getUUID();
        LAST_POS.remove(id);
        RUN_DIST.remove(id);
        JUMP_COUNT.remove(id);
        STILL_TICKS.remove(id);
        HOUR_TICKS.remove(id);
        SWIM_TICKS.remove(id);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.player;
        UUID id = player.getUUID();
        // Every in-game hour increase thirst and fatigue by 2
        int ht = HOUR_TICKS.getOrDefault(id, 0) + 1;
        if (ht >= TICKS_PER_HOUR) {
            ht -= TICKS_PER_HOUR;
            int thirst = Math.min(100, getStat(player, KEY_THIRST, 40) + 2);
            int fatigue = Math.min(100, getStat(player, KEY_FATIGUE, 0) + 2);
            setStat(player, KEY_THIRST, thirst);
            setStat(player, KEY_FATIGUE, fatigue);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncStatsPacket(thirst, fatigue)
            );
        }
        HOUR_TICKS.put(id, ht);

        // Swimming fatigue gain
        if (player.isInWater()) {
            int st = SWIM_TICKS.getOrDefault(id, 0) + 1;
            if (st >= TICKS_PER_15MIN) {
                st -= TICKS_PER_15MIN;
                int fatigue = Math.min(100, getStat(player, KEY_FATIGUE, 0) + 5);
                setStat(player, KEY_FATIGUE, fatigue);
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncStatsPacket(getStat(player, KEY_THIRST, 40), fatigue)
                );
            }
            SWIM_TICKS.put(id, st);
        } else {
            SWIM_TICKS.remove(id);
        }


        double[] prev = LAST_POS.computeIfAbsent(id, u -> new double[]{player.getX(), player.getY(), player.getZ()});
        double dx = player.getX() - prev[0];
        double dy = player.getY() - prev[1];
        double dz = player.getZ() - prev[2];
        prev[0] = player.getX();
        prev[1] = player.getY();
        prev[2] = player.getZ();

        double distSqAll = dx * dx + dy * dy + dz * dz;
        if (distSqAll < 0.0001D) {
            int ticks = STILL_TICKS.getOrDefault(id, 0) + 1;
            if (ticks >= TICKS_PER_HOUR) {
                ticks -= TICKS_PER_HOUR;
                int fatigue = Math.max(0, getStat(player, KEY_FATIGUE, 0) - 5);
                setStat(player, KEY_FATIGUE, fatigue);
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncStatsPacket(getStat(player, KEY_THIRST, 40), fatigue)
                );
            }
            STILL_TICKS.put(id, ticks);
        } else {
            STILL_TICKS.put(id, 0);
        }

        if (!player.isSprinting()) return;

        double dist = Math.sqrt(dx * dx + dz * dz);
        double total = RUN_DIST.getOrDefault(id, 0.0) + dist;
        if (total >= 100.0) {
            int steps = (int) (total / 100.0);
            total -= steps * 100.0;
            int fatigue = Math.min(100, getStat(player, KEY_FATIGUE, 0) + steps * 5);
            setStat(player, KEY_FATIGUE, fatigue);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncStatsPacket(getStat(player, KEY_THIRST, 40), fatigue)
            );
        }
        RUN_DIST.put(id, total);
    }

    @SubscribeEvent
    public static void onPlayerJump(LivingJumpEvent event) {
        if (!(event.getEntityLiving() instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
        UUID id = player.getUUID();
        int count = JUMP_COUNT.getOrDefault(id, 0) + 1;
        if (count >= 20) {
            count = 0;
            int fatigue = Math.min(100, getStat(player, KEY_FATIGUE, 0) + 3);
            setStat(player, KEY_FATIGUE, fatigue);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncStatsPacket(getStat(player, KEY_THIRST, 40), fatigue)
            );
        }
        JUMP_COUNT.put(id, count);
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        int fatigue = Math.min(100, getStat(player, KEY_FATIGUE, 0) + 1);
        setStat(player, KEY_FATIGUE, fatigue);
        ModNetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncStatsPacket(getStat(player, KEY_THIRST, 40), fatigue)
        );
    }


    @SubscribeEvent
    public static void onDrinkFinish(LivingEntityUseItemEvent.Finish event) {
        LOGGER.info("onDrinkFinish: {} использует {}",
                event.getEntity().getName().getString(),
                event.getItem().getItem().getRegistryName()
        );
        if (!(event.getEntity() instanceof ServerPlayerEntity)) return;
        ItemStack stack = event.getItem();
        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
        if (stack.getItem() == Items.POTION && PotionUtils.getPotion(stack) == Potions.WATER) {

            int thirst = Math.max(0, getStat(player, KEY_THIRST, 40) - 20);
            setStat(player, KEY_THIRST, thirst);

            LOGGER.info("  -> новая жажда = {}", thirst);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncStatsPacket(thirst, getStat(player, KEY_FATIGUE, 0))
            );
        } else if (isFishItem(stack)) {
            int thirst = Math.min(100, getStat(player, KEY_THIRST, 40) + 15);
            setStat(player, KEY_THIRST, thirst);

            LOGGER.info("  -> новая жажда = {}", thirst);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncStatsPacket(thirst, getStat(player, KEY_FATIGUE, 0))
            );
        }
    }

    public static void onDrinkButton(ServerPlayerEntity player) {
        LOGGER.info("onDrinkButton: нажата кнопка «Попить» игроком {}", player.getName().getString());
        int thirst = Math.max(0, getStat(player, KEY_THIRST, 40) - 2);
        setStat(player, KEY_THIRST, thirst);
        ModNetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncStatsPacket(thirst, getStat(player, KEY_FATIGUE, 0))
        );
    }

    public static void onMixWater(ServerPlayerEntity player) {
        LOGGER.info("onMixWater: нажата кнопка «Перемешать» игроком {}", player.getName().getString());
        int fatigue = Math.min(100, getStat(player, KEY_FATIGUE, 0) + 3);
        setStat(player, KEY_FATIGUE, fatigue);
        ModNetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncStatsPacket(getStat(player, KEY_THIRST, 40), fatigue)
        );
    }
}
