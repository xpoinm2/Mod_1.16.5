package com.example.examplemod.network;

import com.example.examplemod.item.BoneTongsItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleTongsModePacket {
    public ToggleTongsModePacket() {}

    public static void encode(ToggleTongsModePacket pkt, PacketBuffer buf) {
        // no payload
    }

    public static ToggleTongsModePacket decode(PacketBuffer buf) {
        return new ToggleTongsModePacket();
    }

    public static void handle(ToggleTongsModePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                BoneTongsItem.toggleMode(player.getMainHandItem());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
