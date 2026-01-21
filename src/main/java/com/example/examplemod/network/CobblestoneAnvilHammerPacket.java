package com.example.examplemod.network;

import com.example.examplemod.tileentity.CobblestoneAnvilTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class CobblestoneAnvilHammerPacket {
    private final BlockPos pos;

    public CobblestoneAnvilHammerPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(CobblestoneAnvilHammerPacket pkt, PacketBuffer buf) {
        buf.writeBlockPos(pkt.pos);
    }

    public static CobblestoneAnvilHammerPacket decode(PacketBuffer buf) {
        return new CobblestoneAnvilHammerPacket(buf.readBlockPos());
    }

    public static void handle(CobblestoneAnvilHammerPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                World world = player.level;
                TileEntity tileEntity = world.getBlockEntity(pkt.pos);

                if (tileEntity instanceof CobblestoneAnvilTileEntity) {
                    CobblestoneAnvilTileEntity anvil = (CobblestoneAnvilTileEntity) tileEntity;

                    // Увеличиваем прогресс
                    anvil.incrementProgress();

                    // Если достигли максимального прогресса, выполняем крафт
                    if (anvil.isComplete()) {
                        // Логика крафта - пока просто выводим сообщение
                        System.out.println("Crafting completed at anvil!");
                        anvil.resetProgress();
                    }

                    // Отправляем обновление прогресса клиенту
                    ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new CobblestoneAnvilProgressPacket(anvil.getProgress())
                    );
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}