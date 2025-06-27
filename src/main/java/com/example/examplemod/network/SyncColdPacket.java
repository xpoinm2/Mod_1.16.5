package com.example.examplemod.network;

import com.example.examplemod.capability.PlayerStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncColdPacket {
    private final int cold;

    public SyncColdPacket(int cold) {
        this.cold = cold;
    }

    public static void encode(SyncColdPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.cold);
    }

    public static SyncColdPacket decode(PacketBuffer buf) {
        return new SyncColdPacket(buf.readInt());
    }

    public static void handle(SyncColdPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft.getInstance().player
                    .getCapability(PlayerStatsProvider.PLAYER_STATS_CAP)
                    .ifPresent(stats -> stats.setCold(pkt.cold));
        });
        ctx.get().setPacketHandled(true);
    }
}