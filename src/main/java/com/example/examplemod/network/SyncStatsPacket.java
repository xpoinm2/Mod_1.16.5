// === FILE src\main\java\com\example\examplemod\network\SyncStatsPacket.java

package com.example.examplemod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.NetworkEvent;
import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.capability.IPlayerStats;

import java.util.function.Supplier;

public class SyncStatsPacket {
    private final int thirst;
    private final int fatigue;

    public SyncStatsPacket(int thirst, int fatigue) {
        this.thirst  = thirst;
        this.fatigue = fatigue;
    }

    public static void encode(SyncStatsPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.thirst);
        buf.writeInt(pkt.fatigue);
    }

    public static SyncStatsPacket decode(PacketBuffer buf) {
        return new SyncStatsPacket(buf.readInt(), buf.readInt());
    }

    public static void handle(SyncStatsPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // на клиенте записываем в capability
            Minecraft.getInstance().player
                    .getCapability(PlayerStatsProvider.PLAYER_STATS_CAP)
                    .ifPresent(stats -> {
                        stats.setThirst(pkt.thirst);
                        stats.setFatigue(pkt.fatigue);
                    });
        });
        ctx.get().setPacketHandled(true);
    }
}
