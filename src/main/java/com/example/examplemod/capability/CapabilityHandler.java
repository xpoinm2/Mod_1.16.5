package com.example.examplemod.capability;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncStatsPacket;
import com.example.examplemod.network.SyncColdPacket;
import com.example.examplemod.network.SyncHypothermiaPacket;
import com.example.examplemod.network.SyncVirusPacket;
import com.example.examplemod.network.SyncPoisonPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityHandler {

    @SubscribeEvent
    public static void onRegisterCapabilities(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(
                IPlayerStats.class,
                new PlayerStatsStorage(),
                PlayerStats::new
        );
    }

    @SubscribeEvent
    public static void onAttachCaps(AttachCapabilitiesEvent<Entity> ev) {
        if (ev.getObject() instanceof PlayerEntity) {
            ev.addCapability(
                    new ResourceLocation(ExampleMod.MODID, "player_stats"),
                    new PlayerStatsProvider()
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent ev) {
        if (!(ev.getPlayer() instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) ev.getPlayer();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            // Отправляем только thirst и fatigue, т.к. SyncStatsPacket принимает 2 аргумента
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncStatsPacket(stats.getThirst(), stats.getFatigue())
            );
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncColdPacket(stats.getCold())
            );
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncHypothermiaPacket(stats.getHypothermia())
            );
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncVirusPacket(stats.getVirus())
            );
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncPoisonPacket(stats.getPoison())
            );
        });
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone ev) {
        ev.getOriginal().getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(oldStats ->
                ev.getPlayer().getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(newStats -> {
                    newStats.setThirst(oldStats.getThirst());
                    newStats.setFatigue(oldStats.getFatigue());
                    newStats.setDisease(oldStats.getDisease());
                    newStats.setPoison(oldStats.getPoison());
                    newStats.setVirus(oldStats.getVirus());
                    newStats.setHypothermia(oldStats.getHypothermia());
                    newStats.setBlood(oldStats.getBlood());
                })
        );
    }
}
