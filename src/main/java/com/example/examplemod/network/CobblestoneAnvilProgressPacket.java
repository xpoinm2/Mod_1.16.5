package com.example.examplemod.network;

import com.example.examplemod.tileentity.CobblestoneAnvilTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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
        ctx.get().enqueueWork(() -> {
            // Обработка на клиенте - обновление прогресса в TileEntity
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                Minecraft.getInstance().execute(() -> {
                    if (Minecraft.getInstance().level != null) {
                        TileEntity tileEntity = Minecraft.getInstance().level.getBlockEntity(pkt.pos);
                        if (tileEntity instanceof CobblestoneAnvilTileEntity) {
                            ((CobblestoneAnvilTileEntity) tileEntity).setProgress(pkt.progress);
                        }
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public int getProgress() {
        return progress;
    }
}