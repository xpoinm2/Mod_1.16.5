package com.example.examplemod.network;

import com.example.examplemod.server.RestHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ActivityPacket {
    public static final int TYPE_SIT  = 0;
    public static final int TYPE_LIE  = 1;
    public static final int TYPE_SLEEP = 2;

    private final int type;
    private final int hours;

    public ActivityPacket(int type, int hours) {
        this.type = type;
        this.hours = hours;
    }

    public static void encode(ActivityPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.type);
        buf.writeInt(pkt.hours);
    }

    public static ActivityPacket decode(PacketBuffer buf) {
        return new ActivityPacket(buf.readInt(), buf.readInt());
    }

    public static void handle(ActivityPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null) return;
            switch (pkt.type) {
                case TYPE_SIT:
                    RestHandler.startSitting(player);
                    break;
                case TYPE_LIE:
                    RestHandler.startLying(player);
                    break;
                case TYPE_SLEEP:
                    RestHandler.startSleeping(player, pkt.hours);
                    break;
            }
        });
        ctx.get().setPacketHandled(true);
    }
}