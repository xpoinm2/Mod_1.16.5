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
    private static final float STOP_VOLUME_THRESHOLD = 0.001F;

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
        // Важно: в 1.16.5 звук может не стартовать, если первая громкость равна 0.
        // Это давало "тихий" баг, когда событие активируется, но луп фактически не
        // запускается SoundManager'ом. Стартуем с минимальной слышимой громкости,
        // а дальше tick() уже плавно ведёт уровень по intensity.
        this.volume = MIN_ACTIVE_VOLUME;
        this.pitch = 1.0F;
    }

    @Override
    public boolean canStartSilent() {
        // Разрешаем безопасный старт даже при очень низкой громкости (например,
        // когда интенсивность только начала расти после активации урагана).
        return true;
    }

    public boolean canStart() {
        return !isStopped()
                && minecraft.player != null
                && minecraft.level != null
                && HurricaneClientState.shouldRenderEffects();
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

        if (!HurricaneClientState.shouldRenderEffects()) {
            stop();
            return;
        }

        float intensity = HurricaneClientState.getIntensity();
        float targetVolume = MathHelper.clamp(intensity * MAX_VOLUME, 0.0F, MAX_VOLUME);

        // Когда ураган уже выключен, но fade-out ещё идёт, позволяем громкости
        // опускаться до нуля. Иначе минимальный порог «держит» звук и создаёт
        // резкий обрыв вместо плавного затухания.
        if (HurricaneClientState.isActive()) {
            targetVolume = Math.max(targetVolume, MIN_ACTIVE_VOLUME);
        }

        if (!HurricaneClientState.isActive() && targetVolume <= STOP_VOLUME_THRESHOLD) {
            this.volume = 0.0F;
            stop();
            return;
        }

        this.volume = targetVolume;
    }
}
