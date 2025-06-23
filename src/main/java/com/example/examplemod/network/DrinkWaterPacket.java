package com.example.examplemod.network;

import com.example.examplemod.server.ThirstHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class DrinkWaterPacket {
    public static void encode(DrinkWaterPacket pkt, PacketBuffer buf) { }

    public static DrinkWaterPacket decode(PacketBuffer buf) {
        return new DrinkWaterPacket();
    }

    public static void handle(DrinkWaterPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                // Используем кнопку «Попить» вместо допития бутылки
                ThirstHandler.onDrinkButton(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
