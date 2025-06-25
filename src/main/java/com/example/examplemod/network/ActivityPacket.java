package com.example.examplemod.network;

import com.example.examplemod.server.RestHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ActivityPacket {
    public ActivityPacket() {}

    public static void encode(ActivityPacket pkt, PacketBuffer buf) {
// packet has no payload
    }

    public static ActivityPacket decode(PacketBuffer buf) {
        return new ActivityPacket();
    }

    public static void handle(ActivityPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                RestHandler.startSitting(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}