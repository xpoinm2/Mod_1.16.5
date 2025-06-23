// === FILE src/main/java/com/example/examplemod/network/MixWaterPacket.java
package com.example.examplemod.network;

import com.example.examplemod.server.ThirstHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MixWaterPacket {
    // Конструктор без полей — просто «нажал – миксую»
    public MixWaterPacket() {}

    public static void encode(MixWaterPacket pkt, PacketBuffer buf) {
        // Ничего не пишем
    }

    public static MixWaterPacket decode(PacketBuffer buf) {
        return new MixWaterPacket();
    }

    public static void handle(MixWaterPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                // Вызываем ваш метод смешивания
                ThirstHandler.onMixWater(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
