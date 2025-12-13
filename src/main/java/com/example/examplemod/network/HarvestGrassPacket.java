package com.example.examplemod.network;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.item.GrassBundleItem;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class HarvestGrassPacket {
    private static final double MAX_DISTANCE_SQ = 64.0D;

    private final BlockPos target;

    public HarvestGrassPacket(BlockPos target) {
        this.target = target;
    }

    public static void encode(HarvestGrassPacket pkt, PacketBuffer buf) {
        buf.writeBlockPos(pkt.target);
    }

    public static HarvestGrassPacket decode(PacketBuffer buf) {
        return new HarvestGrassPacket(buf.readBlockPos());
    }

    public static void handle(HarvestGrassPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null) {
                return;
            }

            if (player.isSpectator()) {
                return;
            }

            ServerWorld world = player.getLevel();
            if (!world.isLoaded(pkt.target)) {
                return;
            }

            double distanceSq = player.distanceToSqr(
                    pkt.target.getX() + 0.5D,
                    pkt.target.getY() + 0.5D,
                    pkt.target.getZ() + 0.5D
            );
            if (distanceSq > MAX_DISTANCE_SQ) {
                return;
            }

            if (!world.getBlockState(pkt.target).is(ModBlocks.BUNCH_OF_GRASS.get())) {
                return;
            }

            GrassBundleItem.GrassState state = getRandomGrassState(world.getRandom());

            world.destroyBlock(pkt.target, false);
            Block.popResource(world, pkt.target, GrassBundleItem.createWithState(state));
            player.swing(Hand.MAIN_HAND);
        });
        ctx.get().setPacketHandled(true);
    }
    
    private static GrassBundleItem.GrassState getRandomGrassState(java.util.Random random) {
        int roll = random.nextInt(100);
        if (roll < 10) {
            return GrassBundleItem.GrassState.POISON;
        } else if (roll < 40) {
            return GrassBundleItem.GrassState.HEALING;
        } else if (roll < 70) {
            return GrassBundleItem.GrassState.DYE;
        }
        return GrassBundleItem.GrassState.FERTILIZER;
    }
}
