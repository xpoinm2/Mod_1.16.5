// === FILE src/main/java/com/example/examplemod/network/PacketHandler.java
package com.example.examplemod.network;

import com.example.examplemod.ExampleMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL = "1";
    public static SimpleChannel CHANNEL;
    private static int id = 0;

    private static int nextID() {
        return id++;
    }

    // Этот метод привязан в ExampleMod
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(ExampleMod.MODID, "main"),
                () -> PROTOCOL,
                PROTOCOL::equals,
                PROTOCOL::equals
        );

        CHANNEL.registerMessage(
                nextID(),
                MixWaterPacket.class,
                MixWaterPacket::encode,
                MixWaterPacket::decode,
                MixWaterPacket::handle
        );
        CHANNEL.registerMessage(
                nextID(),
                DrinkWaterPacket.class,
                DrinkWaterPacket::encode,
                DrinkWaterPacket::decode,
                DrinkWaterPacket::handle
        );
    }
}