package com.example.examplemod.network;

import com.example.examplemod.container.ClayPotContainer;
import com.example.examplemod.tileentity.ClayPotTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClayPotModePacket {
    private final BlockPos pos;

    public ClayPotModePacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(ClayPotModePacket pkt, PacketBuffer buf) {
        buf.writeBlockPos(pkt.pos);
    }

    public static ClayPotModePacket decode(PacketBuffer buf) {
        return new ClayPotModePacket(buf.readBlockPos());
    }

    public static void handle(ClayPotModePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null) {
                return;
            }
            TileEntity tile = player.level.getBlockEntity(pkt.pos);
            if (!(tile instanceof ClayPotTileEntity)) {
                return;
            }
            if (player.containerMenu instanceof ClayPotContainer) {
                ((ClayPotContainer) player.containerMenu).toggleMode();
            } else {
                ((ClayPotTileEntity) tile).toggleDrainMode();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

