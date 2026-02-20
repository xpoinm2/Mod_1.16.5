package com.example.examplemod.client.sound;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModSounds;
import com.example.examplemod.client.HurricaneClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HurricaneSoundController {
    private static int hurricaneSoundTime;

    private HurricaneSoundController() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            hurricaneSoundTime = 0;
            return;
        }

        boolean shouldPlay = HurricaneClientState.isActive() || HurricaneClientState.getIntensity() > 0.0F;
        if (!shouldPlay) {
            hurricaneSoundTime = 0;
            return;
        }

        playVanillaLikeHurricaneSound(minecraft, minecraft.player, HurricaneClientState.getIntensity());
    }

    private static void playVanillaLikeHurricaneSound(Minecraft minecraft, ClientPlayerEntity player, float intensity) {
        hurricaneSoundTime--;
        if (hurricaneSoundTime > 0) {
            return;
        }

        BlockPos.Mutable samplePos = new BlockPos.Mutable();
        int playerX = MathHelper.floor(player.getX());
        int playerY = MathHelper.floor(player.getY());
        int playerZ = MathHelper.floor(player.getZ());
        int offsetX = minecraft.level.random.nextInt(21) - 10;
        int offsetZ = minecraft.level.random.nextInt(21) - 10;

        samplePos.set(playerX + offsetX, playerY, playerZ + offsetZ);
        samplePos.setY(minecraft.level.getHeightmapPos(net.minecraft.world.gen.Heightmap.Type.MOTION_BLOCKING, samplePos).getY());

        float clampedIntensity = MathHelper.clamp(intensity, 0.2F, 1.0F);
        float volume = Math.min(1.0F, 0.25F + clampedIntensity * 0.75F);

        minecraft.level.playLocalSound(samplePos.getX(), samplePos.getY(), samplePos.getZ(),
                ModSounds.HURRICANE_LOOP.get(), SoundCategory.WEATHER, volume, 1.0F, false);

        hurricaneSoundTime = Math.max(2, (int) (8.0F / clampedIntensity));
    }
}
