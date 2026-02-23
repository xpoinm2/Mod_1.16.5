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
        // Используем BLOCKS, чтобы громкость урагана из команды контролировалась тем же ползунком,
        // что и у звука блока урагана. Это убирает ситуацию, когда блок слышно, а командный
        // ураган "тихий" из-за отдельной категории WEATHER.
        super(ModSounds.HURRICANE_LOOP.get(), SoundCategory.BLOCKS);
        this.minecraft = minecraft;
        this.looping = true;
        this.delay = 0;
        this.relative = true;
        this.attenuation = AttenuationType.NONE;
        this.volume = 0.0F;
        this.pitch = 1.0F;
    }

    public boolean canStart() {
        return !isStopped() && minecraft.player != null && minecraft.level != null && HurricaneClientState.isActive();
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

        if (!HurricaneClientState.isActive()) {
            stop();
            return;
        }

        float intensity = HurricaneClientState.getIntensity();
        float targetVolume = MathHelper.clamp(intensity * MAX_VOLUME, MIN_ACTIVE_VOLUME, MAX_VOLUME);
        this.volume = targetVolume;
    }
}
