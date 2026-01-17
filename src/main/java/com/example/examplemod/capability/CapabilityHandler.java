package com.example.examplemod.capability;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncAllStatsPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityHandler {

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
            // ОПТИМИЗАЦИЯ: один пакет вместо 5 отдельных (80% меньше трафика при логине)
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncAllStatsPacket(stats)
            );
        });
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone ev) {
        ev.getOriginal().getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(oldStats ->
                ev.getPlayer().getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(newStats -> {
                    newStats.setThirst(oldStats.getThirst());
                    newStats.setFatigue(oldStats.getFatigue());
                    newStats.setPoison(oldStats.getPoison());
                    newStats.setVirus(oldStats.getVirus());
                    newStats.setHypothermia(oldStats.getHypothermia());
                    newStats.setBlood(oldStats.getBlood());
                })
        );
    }
}
