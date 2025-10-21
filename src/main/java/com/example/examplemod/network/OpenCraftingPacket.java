package com.example.examplemod.network;

import com.example.examplemod.container.PortableWorkbenchContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.function.Supplier;

public class OpenCraftingPacket {
    public OpenCraftingPacket() {}

    public static void encode(OpenCraftingPacket pkt, PacketBuffer buf) {}

    public static OpenCraftingPacket decode(PacketBuffer buf) {
        return new OpenCraftingPacket();
    }

    public static void handle(OpenCraftingPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                INamedContainerProvider provider = new SimpleNamedContainerProvider(
                        (id, inv, plyr) -> new PortableWorkbenchContainer(id, inv, plyr),
                        new StringTextComponent("\u0421\u043e\u0437\u0434\u0430\u043d\u0438\u0435")
                );
                NetworkHooks.openGui(player, provider);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}