package com.example.examplemod.network;

import com.example.examplemod.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayUraganClickSoundPacket {
    public static void encode(PlayUraganClickSoundPacket pkt, PacketBuffer buf) {
    }

    public static PlayUraganClickSoundPacket decode(PacketBuffer buf) {
        return new PlayUraganClickSoundPacket();
    }

    public static void handle(PlayUraganClickSoundPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                playOnClient();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    @net.minecraftforge.api.distmarker.OnlyIn(Dist.CLIENT)
    private static void playOnClient() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getSoundManager() == null) {
            return;
        }

        minecraft.getSoundManager().play(SimpleSound.forUI(ModSounds.HURRICANE_LOOP.get(), 1.0F));
    }
}
