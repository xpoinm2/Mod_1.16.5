package com.example.examplemod.network;

import com.example.examplemod.tileentity.ClayPotTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class WashProgressPacket {
    private final BlockPos pos;

    public WashProgressPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(WashProgressPacket msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static WashProgressPacket decode(PacketBuffer buf) {
        return new WashProgressPacket(buf.readBlockPos());
    }

    public static void handle(WashProgressPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerWorld world = ctx.get().getSender().getLevel();
            TileEntity tile = world.getBlockEntity(msg.pos);
            if (tile instanceof ClayPotTileEntity) {
                ((ClayPotTileEntity) tile).incrementWashProgress();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}