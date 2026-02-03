package com.example.examplemod.network;

import com.example.examplemod.client.HurricaneClientState;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class HurricaneStatePacket {
    private final boolean active;

    public HurricaneStatePacket(boolean active) {
        this.active = active;
    }

    public static void encode(HurricaneStatePacket pkt, PacketBuffer buf) {
        buf.writeBoolean(pkt.active);
    }

    public static HurricaneStatePacket decode(PacketBuffer buf) {
        return new HurricaneStatePacket(buf.readBoolean());
    }

    public static void handle(HurricaneStatePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                HurricaneClientState.setActive(pkt.active);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
