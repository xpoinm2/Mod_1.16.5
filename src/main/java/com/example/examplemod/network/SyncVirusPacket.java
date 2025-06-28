package com.example.examplemod.network;

import com.example.examplemod.capability.PlayerStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncVirusPacket {
    private final int value;

    public SyncVirusPacket(int value) {
        this.value = value;
    }

    public static void encode(SyncVirusPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.value);
    }

    public static SyncVirusPacket decode(PacketBuffer buf) {
        return new SyncVirusPacket(buf.readInt());
    }

    public static void handle(SyncVirusPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                Minecraft.getInstance().player
                        .getCapability(PlayerStatsProvider.PLAYER_STATS_CAP)
                        .ifPresent(stats -> stats.setVirus(pkt.value))
        );
        ctx.get().setPacketHandled(true);
    }
}