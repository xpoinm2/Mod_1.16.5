package com.example.examplemod.network;

import com.example.examplemod.quest.QuestManager;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CompleteCobblestoneAnvilQuestPacket {
    public CompleteCobblestoneAnvilQuestPacket() {}

    public static void encode(CompleteCobblestoneAnvilQuestPacket pkt, PacketBuffer buf) {}

    public static CompleteCobblestoneAnvilQuestPacket decode(PacketBuffer buf) {
        return new CompleteCobblestoneAnvilQuestPacket();
    }

    public static void handle(CompleteCobblestoneAnvilQuestPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            QuestManager.setCobblestoneAnvilCompleted(true);
        });
        ctx.get().setPacketHandled(true);
    }
}