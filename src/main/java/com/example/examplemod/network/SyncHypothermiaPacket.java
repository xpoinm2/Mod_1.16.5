package com.example.examplemod.network;

import com.example.examplemod.capability.PlayerStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncHypothermiaPacket {
    private final int value;

    public SyncHypothermiaPacket(int value) {
        this.value = value;
    }

    public static void encode(SyncHypothermiaPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.value);
    }

    public static SyncHypothermiaPacket decode(PacketBuffer buf) {
        return new SyncHypothermiaPacket(buf.readInt());
    }

    public static void handle(SyncHypothermiaPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                Minecraft.getInstance().player
                        .getCapability(PlayerStatsProvider.PLAYER_STATS_CAP)
                        .ifPresent(stats -> stats.setHypothermia(pkt.value))
        );
        ctx.get().setPacketHandled(true);
    }
}