package com.example.examplemod.client.sound;

import com.example.examplemod.ModSounds;
import com.example.examplemod.client.HurricaneClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

public class HurricaneLoopSound extends TickableSound {
    private static final float MAX_VOLUME = 0.9F;
    private static final float MIN_ACTIVE_VOLUME = 0.2F;

    private final Minecraft minecraft;

    public HurricaneLoopSound(Minecraft minecraft) {
        super(ModSounds.HURRICANE_LOOP.get(), SoundCategory.WEATHER);
        this.minecraft = minecraft;
        this.looping = true;
        this.delay = 0;
        this.relative = true;
        this.attenuation = AttenuationType.NONE;
        this.volume = 0.0F;
        this.pitch = 1.0F;
    }

    public boolean canStart() {
        return !isStopped() && minecraft.player != null && minecraft.level != null;
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
        float targetVolume = MathHelper.clamp(intensity * MAX_VOLUME, 0.0F, MAX_VOLUME);
        if (HurricaneClientState.isActive()) {
            targetVolume = Math.max(MIN_ACTIVE_VOLUME, targetVolume);
        }
        this.volume = targetVolume;

        if (!HurricaneClientState.isActive() && intensity <= 0.0F) {
            stop();
        }
    }
}
