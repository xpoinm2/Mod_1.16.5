package com.example.examplemod.network;

import com.example.examplemod.container.ClayPotContainer;
import com.example.examplemod.tileentity.ClayPotTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class WashProgressPacket {
    private final BlockPos pos;

    public WashProgressPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(WashProgressPacket pkt, PacketBuffer buf) {
        buf.writeBlockPos(pkt.pos);
    }

    public static WashProgressPacket decode(PacketBuffer buf) {
        return new WashProgressPacket(buf.readBlockPos());
    }

    public static void handle(WashProgressPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null) {
                return;
            }
            TileEntity tile = player.level.getBlockEntity(pkt.pos);
            if (!(tile instanceof ClayPotTileEntity)) {
                return;
            }
            ClayPotTileEntity clayPot = (ClayPotTileEntity) tile;
            clayPot.incrementWashProgress();
        });
        ctx.get().setPacketHandled(true);
    }
}