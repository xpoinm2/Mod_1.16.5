// === FILE src\main\java\com\example\examplemod\network\ModNetworkHandler.java

package com.example.examplemod.network;

import com.example.examplemod.ExampleMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import com.example.examplemod.network.ActivityPacket;
import com.example.examplemod.network.ClayPotModePacket;
import com.example.examplemod.network.HarvestGrassPacket;
import com.example.examplemod.network.OpenBoneTongsItemPacket;
import com.example.examplemod.network.OpenCraftingPacket;
import com.example.examplemod.network.SyncColdPacket;
import com.example.examplemod.network.SyncHypothermiaPacket;
import com.example.examplemod.network.SyncVirusPacket;
import com.example.examplemod.network.SyncPoisonPacket;
import com.example.examplemod.network.WashProgressPacket;

/**
 * Централизованная регистрация наших сетевых пакетов.
 */
public class ModNetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ExampleMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private static int nextId() {
        return packetId++;
    }

    public static void register() {


        // сюда вписываем все пакеты, которые у вас есть
        CHANNEL.registerMessage(
                nextId(),
                MixWaterPacket.class,
                MixWaterPacket::encode,
                MixWaterPacket::decode,
                MixWaterPacket::handle
        );
        CHANNEL.registerMessage(
                nextId(),
                DrinkWaterPacket.class,
                DrinkWaterPacket::encode,
                DrinkWaterPacket::decode,
                DrinkWaterPacket::handle
        );
        // НОВЫЙ: универсальный пакет для всех статов (заменяет 5 отдельных пакетов)
        CHANNEL.registerMessage(
                nextId(),
                SyncAllStatsPacket.class,
                SyncAllStatsPacket::encode,
                SyncAllStatsPacket::decode,
                SyncAllStatsPacket::handle
        );
        
        // УСТАРЕВШИЕ: оставлены для обратной совместимости, но рекомендуется использовать SyncAllStatsPacket
        CHANNEL.registerMessage(
                nextId(),
                SyncStatsPacket.class,
                SyncStatsPacket::encode,
                SyncStatsPacket::decode,
                SyncStatsPacket::handle
        );
        CHANNEL.registerMessage(
                nextId(),
                SyncHypothermiaPacket.class,
                SyncHypothermiaPacket::encode,
                SyncHypothermiaPacket::decode,
                SyncHypothermiaPacket::handle
        );
        CHANNEL.registerMessage(
                nextId(),
                SyncColdPacket.class,
                SyncColdPacket::encode,
                SyncColdPacket::decode,
                SyncColdPacket::handle
        );
        CHANNEL.registerMessage(
                nextId(),
                SyncVirusPacket.class,
                SyncVirusPacket::encode,
                SyncVirusPacket::decode,
                SyncVirusPacket::handle
        );
        CHANNEL.registerMessage(
                nextId(),
                SyncPoisonPacket.class,
                SyncPoisonPacket::encode,
                SyncPoisonPacket::decode,
                SyncPoisonPacket::handle
        );
        CHANNEL.registerMessage(
                nextId(),
                HarvestGrassPacket.class,
                HarvestGrassPacket::encode,
                HarvestGrassPacket::decode,
                HarvestGrassPacket::handle
        );
        CHANNEL.registerMessage(
                nextId(),
                ActivityPacket.class,
                ActivityPacket::encode,
                ActivityPacket::decode,
                ActivityPacket::handle
        );
        CHANNEL.registerMessage(
                nextId(),
                OpenCraftingPacket.class,
                OpenCraftingPacket::encode,
                OpenCraftingPacket::decode,
                OpenCraftingPacket::handle
        );
        CHANNEL.registerMessage(
                nextId(),
                OpenBoneTongsItemPacket.class,
                OpenBoneTongsItemPacket::encode,
                OpenBoneTongsItemPacket::decode,
                OpenBoneTongsItemPacket::handle
        );
        CHANNEL.registerMessage(
                nextId(),
                ClayPotModePacket.class,
                ClayPotModePacket::encode,
                ClayPotModePacket::decode,
                ClayPotModePacket::handle
        );
        CHANNEL.registerMessage(
                nextId(),
                WashProgressPacket.class,
                WashProgressPacket::encode,
                WashProgressPacket::decode,
                WashProgressPacket::handle
        );
    }
}
