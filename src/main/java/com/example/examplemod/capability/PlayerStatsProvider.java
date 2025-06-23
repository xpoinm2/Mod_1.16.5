// === FILE src/main/java/com/example/examplemod/capability/PlayerStatsProvider.java
package com.example.examplemod.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Провайдер для IPlayerStats.
 * При сериализации/десериализации использует собственный PlayerStatsStorage,
 * не дергая null’овый PLAYER_STATS_CAP.
 */
public class PlayerStatsProvider implements ICapabilitySerializable<INBT> {
    @CapabilityInject(IPlayerStats.class)
    public static Capability<IPlayerStats> PLAYER_STATS_CAP = null;

    // собственный экземпляр данных
    private final IPlayerStats instance = new PlayerStats();
    private final LazyOptional<IPlayerStats> optional = LazyOptional.of(() -> instance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(
            @Nonnull Capability<T> cap,
            @Nullable Direction side
    ) {
        return cap == PLAYER_STATS_CAP ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        // используем прямо наш storage, не дергая PLAYER_STATS_CAP.getStorage()
        PlayerStatsStorage storage = new PlayerStatsStorage();
        return storage.writeNBT(
                PLAYER_STATS_CAP,
                instance,
                null
        );
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        PlayerStatsStorage storage = new PlayerStatsStorage();
        storage.readNBT(
                PLAYER_STATS_CAP,
                instance,
                null,
                nbt
        );
    }
}
