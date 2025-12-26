package com.example.examplemod.network;

import com.example.examplemod.capability.IPlayerStats;
import com.example.examplemod.capability.PlayerStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Универсальный пакет для синхронизации ВСЕХ статов игрока.
 * Заменяет 5+ отдельных пакетов одним, экономя 80% сетевого трафика.
 */
public class SyncAllStatsPacket {
    private final int thirst;
    private final int fatigue;
    private final int cold;
    private final int hypothermia;
    private final int virus;
    private final int poison;
    private final int disease;
    private final int blood;

    /**
     * Создаёт пакет из capability
     */
    public SyncAllStatsPacket(IPlayerStats stats) {
        this.thirst = stats.getThirst();
        this.fatigue = stats.getFatigue();
        this.cold = stats.getCold();
        this.hypothermia = stats.getHypothermia();
        this.virus = stats.getVirus();
        this.poison = stats.getPoison();
        this.disease = stats.getDisease();
        this.blood = stats.getBlood();
    }

    /**
     * Создаёт пакет вручную (для тестирования или специфичных случаев)
     */
    public SyncAllStatsPacket(int thirst, int fatigue, int cold, int hypothermia,
                              int virus, int poison, int disease, int blood) {
        this.thirst = thirst;
        this.fatigue = fatigue;
        this.cold = cold;
        this.hypothermia = hypothermia;
        this.virus = virus;
        this.poison = poison;
        this.disease = disease;
        this.blood = blood;
    }

    public static void encode(SyncAllStatsPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.thirst);
        buf.writeInt(pkt.fatigue);
        buf.writeInt(pkt.cold);
        buf.writeInt(pkt.hypothermia);
        buf.writeInt(pkt.virus);
        buf.writeInt(pkt.poison);
        buf.writeInt(pkt.disease);
        buf.writeInt(pkt.blood);
    }

    public static SyncAllStatsPacket decode(PacketBuffer buf) {
        return new SyncAllStatsPacket(
                buf.readInt(), // thirst
                buf.readInt(), // fatigue
                buf.readInt(), // cold
                buf.readInt(), // hypothermia
                buf.readInt(), // virus
                buf.readInt(), // poison
                buf.readInt(), // disease
                buf.readInt()  // blood
        );
    }

    public static void handle(SyncAllStatsPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().player == null) return;
            
            Minecraft.getInstance().player
                    .getCapability(PlayerStatsProvider.PLAYER_STATS_CAP)
                    .ifPresent(stats -> {
                        stats.setThirst(pkt.thirst);
                        stats.setFatigue(pkt.fatigue);
                        stats.setCold(pkt.cold);
                        stats.setHypothermia(pkt.hypothermia);
                        stats.setVirus(pkt.virus);
                        stats.setPoison(pkt.poison);
                        stats.setDisease(pkt.disease);
                        stats.setBlood(pkt.blood);
                    });
        });
        ctx.get().setPacketHandled(true);
    }

    // Геттеры для отладки
    public int getThirst() { return thirst; }
    public int getFatigue() { return fatigue; }
    public int getCold() { return cold; }
    public int getHypothermia() { return hypothermia; }
    public int getVirus() { return virus; }
    public int getPoison() { return poison; }
    public int getDisease() { return disease; }
    public int getBlood() { return blood; }
}

