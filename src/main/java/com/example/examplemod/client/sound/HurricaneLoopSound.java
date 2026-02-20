package com.example.examplemod.client.sound;

import com.example.examplemod.ModSounds;
import com.example.examplemod.client.HurricaneClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;

public class HurricaneLoopSound extends TickableSound {
    private static final float MAX_VOLUME = 0.9F;

    private final Minecraft minecraft;

    public HurricaneLoopSound(Minecraft minecraft) {
        super(ModSounds.HURRICANE_LOOP.get(), SoundCategory.WEATHER);
        this.minecraft = minecraft;
        this.looping = true;
        this.delay = 0;
        this.relative = true;
        this.volume = 0.0F;
        this.pitch = 1.0F;
    }

    public void stopLoop() {
        stop();
    }

    @Override
    public void tick() {
        if (minecraft.player == null || minecraft.level == null) {
            stop();
            return;
        }

        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 0.0F;

        float intensity = HurricaneClientState.getIntensity();
        this.volume = Math.max(0.0F, Math.min(MAX_VOLUME, intensity * MAX_VOLUME));

        if (!HurricaneClientState.isActive() && intensity <= 0.0F) {
            stop();
        }
    }
}
