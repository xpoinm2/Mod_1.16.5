package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.ExampleMod;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public final class FogWeatherData extends WorldSavedData {
    private static final String DATA_NAME = ExampleMod.MODID + "_fog_weather";
    private static final String KEY_ENABLED = "Enabled";
    private static final String KEY_FORCED_ACTIVE = "ForcedActive";

    private boolean enabled = true;
    private boolean forcedActive;

    public FogWeatherData() {
        super(DATA_NAME);
    }

    public static FogWeatherData get(ServerWorld world) {
        return world.getDataStorage().computeIfAbsent(FogWeatherData::new, DATA_NAME);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        setDirty();
    }

    public boolean isForcedActive() {
        return forcedActive;
    }

    public void setForcedActive(boolean forcedActive) {
        if (this.forcedActive == forcedActive) {
            return;
        }
        this.forcedActive = forcedActive;
        setDirty();
    }

    @Override
    public void load(CompoundNBT nbt) {
        enabled = !nbt.contains(KEY_ENABLED) || nbt.getBoolean(KEY_ENABLED);
        forcedActive = nbt.getBoolean(KEY_FORCED_ACTIVE);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putBoolean(KEY_ENABLED, enabled);
        compound.putBoolean(KEY_FORCED_ACTIVE, forcedActive);
        return compound;
    }
}
