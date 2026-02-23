package com.example.examplemod.network;

import com.example.examplemod.client.FogClientState;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class FogStatePacket {
    private final boolean active;

    public FogStatePacket(boolean active) {
        this.active = active;
    }

    public static void encode(FogStatePacket pkt, PacketBuffer buf) {
        buf.writeBoolean(pkt.active);
    }

    public static FogStatePacket decode(PacketBuffer buf) {
        return new FogStatePacket(buf.readBoolean());
    }

    public static void handle(FogStatePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                FogClientState.setActive(pkt.active);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
