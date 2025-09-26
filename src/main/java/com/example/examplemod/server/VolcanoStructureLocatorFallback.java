package com.example.examplemod.server;

import com.example.examplemod.world.ModConfiguredStructures;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

/**
 * Fallback locator that queries already generated structure data in the world
 * when a direct generator search fails. This mirrors what /locate does
 * internally and allows the teleport command to work even when the volcano is
 * outside the initial search radius but its start has already been discovered
 * by the world.
 */
final class VolcanoStructureLocatorFallback {

    private VolcanoStructureLocatorFallback() {
    }

    @Nullable
    static BlockPos locate(ServerWorld world, BlockPos origin) {
        StructureFeature<?, ?> configured = ModConfiguredStructures.CONFIGURED_VOLCANO;
        if (configured == null) {
            return null;
        }

        StructureManager manager = world.structureFeatureManager();
        StructureStart<?> start = manager.getStructureWithPieceAt(origin, configured);
        if (start == null || start == StructureStart.INVALID_START) {
            return null;
        }

        return start.getLocatePos();
    }
}