package com.example.examplemod.network;

import com.example.examplemod.client.network.ClientPacketHandlers;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenCobblestoneDialogPacket {
    private BlockPos pos;

    public OpenCobblestoneDialogPacket(BlockPos pos) {
        this.pos = pos;
    }

    public OpenCobblestoneDialogPacket() {}

    public static void encode(OpenCobblestoneDialogPacket pkt, PacketBuffer buf) {
        buf.writeBlockPos(pkt.pos);
    }

    public static OpenCobblestoneDialogPacket decode(PacketBuffer buf) {
        return new OpenCobblestoneDialogPacket(buf.readBlockPos());
    }

    public static void handle(OpenCobblestoneDialogPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.openCobblestoneDialog(pkt.pos))
        );
        ctx.get().setPacketHandled(true);
    }
}