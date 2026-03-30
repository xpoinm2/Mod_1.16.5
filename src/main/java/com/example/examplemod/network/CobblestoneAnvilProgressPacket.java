package com.example.examplemod.network;

import com.example.examplemod.client.network.ClientPacketHandlers;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CobblestoneAnvilProgressPacket {
    private final BlockPos pos;
    private final int progress;

    public CobblestoneAnvilProgressPacket(BlockPos pos, int progress) {
        this.pos = pos;
        this.progress = progress;
    }

    public static void encode(CobblestoneAnvilProgressPacket pkt, PacketBuffer buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeInt(pkt.progress);
    }

    public static CobblestoneAnvilProgressPacket decode(PacketBuffer buf) {
        return new CobblestoneAnvilProgressPacket(buf.readBlockPos(), buf.readInt());
    }

    public static void handle(CobblestoneAnvilProgressPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.updateCobblestoneAnvilProgress(pkt))
        );
        ctx.get().setPacketHandled(true);
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getProgress() {
        return progress;
    }
}