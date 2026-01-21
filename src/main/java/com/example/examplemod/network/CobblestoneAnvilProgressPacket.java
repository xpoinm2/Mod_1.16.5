package com.example.examplemod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CobblestoneAnvilProgressPacket {
    private final int progress;

    public CobblestoneAnvilProgressPacket(int progress) {
        this.progress = progress;
    }

    public static void encode(CobblestoneAnvilProgressPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.progress);
    }

    public static CobblestoneAnvilProgressPacket decode(PacketBuffer buf) {
        return new CobblestoneAnvilProgressPacket(buf.readInt());
    }

    public static void handle(CobblestoneAnvilProgressPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Обработка на клиенте - обновление прогресса в GUI
            if (ctx.get().getSender() != null) {
                // Серверная сторона - ничего не делаем, прогресс уже обновлен в TileEntity
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public int getProgress() {
        return progress;
    }
}