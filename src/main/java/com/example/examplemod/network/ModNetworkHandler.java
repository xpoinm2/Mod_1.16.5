// === FILE src\main\java\com\example\examplemod\network\ModNetworkHandler.java

package com.example.examplemod.network;

import com.example.examplemod.ExampleMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import com.example.examplemod.network.ActivityPacket;

/**
 * Централизованная регистрация наших сетевых пакетов.
 */
public class ModNetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel CHANNEL;

    /**
     * Initialize the channel if it hasn't been created yet. This
     * should be called before the registration phase is closed.
     */
    private static void setupChannel() {
        if (CHANNEL == null) {
            CHANNEL = NetworkRegistry.newSimpleChannel(
                    new ResourceLocation(ExampleMod.MODID, "main"),
                    () -> PROTOCOL_VERSION,
                    PROTOCOL_VERSION::equals,
                    PROTOCOL_VERSION::equals
            );
        }
    }


    private static int packetId = 0;

    private static int nextId() {
        return packetId++;
    }

    public static void register() {
        // Ensure the channel exists before registering packets
        setupChannel();

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
        CHANNEL.registerMessage(
                nextId(),
                SyncStatsPacket.class,
                SyncStatsPacket::encode,
                SyncStatsPacket::decode,
                SyncStatsPacket::handle
        );
        CHANNEL.registerMessage(
                nextId(),
                ActivityPacket.class,
                ActivityPacket::encode,
                ActivityPacket::decode,
                ActivityPacket::handle
        );

    }
}
