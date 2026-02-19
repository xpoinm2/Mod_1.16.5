package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.ExampleMod;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.server.ServerWorld;

public final class HurricaneWeatherData extends WorldSavedData {
    private static final String DATA_NAME = ExampleMod.MODID + "_hurricane_weather";
    private static final String KEY_ACTIVE = "Active";
    private static final String KEY_END_TICK = "EndTick";
    private static final String KEY_TOTAL_BREAKS = "TotalBreaks";
    private static final String KEY_BREAKS_REMAINING = "BreaksRemaining";
    private static final String KEY_NEXT_BREAK_TICK = "NextBreakTick";
    private static final String KEY_ENABLED = "Enabled";

    private boolean active;
    private long endTick;
    private int totalBreaks;
    private int breaksRemaining;
    private long nextBreakTick;
    private boolean enabled = true;

    public HurricaneWeatherData() {
        super(DATA_NAME);
    }

    public static HurricaneWeatherData get(ServerWorld world) {
        return world.getDataStorage().computeIfAbsent(HurricaneWeatherData::new, DATA_NAME);
    }

    public boolean isActive() {
        return active;
    }

    public long getEndTick() {
        return endTick;
    }

    public int getTotalBreaks() {
        return totalBreaks;
    }

    public int getBreaksRemaining() {
        return breaksRemaining;
    }

    public long getNextBreakTick() {
        return nextBreakTick;
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

    public void start(long endTick, int totalBreaks, int breaksRemaining, long nextBreakTick) {
        this.active = true;
        this.endTick = endTick;
        this.totalBreaks = totalBreaks;
        this.breaksRemaining = breaksRemaining;
        this.nextBreakTick = nextBreakTick;
        setDirty();
    }

    public void updateProgress(int breaksRemaining, long nextBreakTick) {
        if (!active) {
            return;
        }
        this.breaksRemaining = breaksRemaining;
        this.nextBreakTick = nextBreakTick;
        setDirty();
    }

    public void clear() {
        if (!active) {
            return;
        }
        this.active = false;
        this.endTick = 0L;
        this.totalBreaks = 0;
        this.breaksRemaining = 0;
        this.nextBreakTick = 0L;
        setDirty();
    }

    @Override
    public void load(CompoundNBT nbt) {
        active = nbt.getBoolean(KEY_ACTIVE);
        endTick = nbt.getLong(KEY_END_TICK);
        totalBreaks = nbt.getInt(KEY_TOTAL_BREAKS);
        breaksRemaining = nbt.getInt(KEY_BREAKS_REMAINING);
        nextBreakTick = nbt.getLong(KEY_NEXT_BREAK_TICK);
        enabled = !nbt.contains(KEY_ENABLED) || nbt.getBoolean(KEY_ENABLED);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putBoolean(KEY_ACTIVE, active);
        compound.putLong(KEY_END_TICK, endTick);
        compound.putInt(KEY_TOTAL_BREAKS, totalBreaks);
        compound.putInt(KEY_BREAKS_REMAINING, breaksRemaining);
        compound.putLong(KEY_NEXT_BREAK_TICK, nextBreakTick);
        compound.putBoolean(KEY_ENABLED, enabled);
        return compound;
    }
}
