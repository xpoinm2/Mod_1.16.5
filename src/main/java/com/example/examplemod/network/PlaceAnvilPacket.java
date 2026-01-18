package com.example.examplemod.network;

import com.example.examplemod.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PlaceAnvilPacket {
    private BlockPos pos;

    public PlaceAnvilPacket(BlockPos pos) {
        this.pos = pos;
    }

    public PlaceAnvilPacket() {}

    public static void encode(PlaceAnvilPacket pkt, PacketBuffer buf) {
        buf.writeBlockPos(pkt.pos);
    }

    public static PlaceAnvilPacket decode(PacketBuffer buf) {
        return new PlaceAnvilPacket(buf.readBlockPos());
    }

    public static void handle(PlaceAnvilPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                // Проверяем, что на позиции все еще булыжник
                if (player.level.getBlockState(pkt.pos).getBlock() == Blocks.COBBLESTONE) {
                    // Заменяем булыжник на наковальню
                    player.level.setBlock(pkt.pos, ModBlocks.COBBLESTONE_ANVIL.get().defaultBlockState(), 3);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}