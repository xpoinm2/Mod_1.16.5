package com.example.examplemod.network;

import com.example.examplemod.capability.PlayerStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncPoisonPacket {
    private final int value;

    public SyncPoisonPacket(int value) {
        this.value = value;
    }

    public static void encode(SyncPoisonPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.value);
    }

    public static SyncPoisonPacket decode(PacketBuffer buf) {
        return new SyncPoisonPacket(buf.readInt());
    }

    public static void handle(SyncPoisonPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                Minecraft.getInstance().player
                        .getCapability(PlayerStatsProvider.PLAYER_STATS_CAP)
                        .ifPresent(stats -> stats.setPoison(pkt.value))
        );
        ctx.get().setPacketHandled(true);
    }
}