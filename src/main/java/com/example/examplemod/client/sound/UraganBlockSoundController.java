package com.example.examplemod.client.sound;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class UraganBlockSoundController {
    private static final int SCAN_RADIUS_XZ = 16;
    private static final int SCAN_RADIUS_Y = 8;
    private static final int SCAN_INTERVAL_TICKS = 10;

    private static final Map<BlockPos, UraganBlockLoopSound> ACTIVE_SOUNDS = new HashMap<>();

    private UraganBlockSoundController() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            stopAll();
            return;
        }

        if (minecraft.level.getGameTime() % SCAN_INTERVAL_TICKS != 0) {
            return;
        }

        BlockPos center = minecraft.player.blockPosition();
        Set<BlockPos> detectedBlocks = new HashSet<>();

        BlockPos min = center.offset(-SCAN_RADIUS_XZ, -SCAN_RADIUS_Y, -SCAN_RADIUS_XZ);
        BlockPos max = center.offset(SCAN_RADIUS_XZ, SCAN_RADIUS_Y, SCAN_RADIUS_XZ);

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (minecraft.level.getBlockState(pos).getBlock() != ModBlocks.URAGAN_BLOCK.get()) {
                continue;
            }

            BlockPos immutablePos = pos.immutable();
            detectedBlocks.add(immutablePos);
            ensureSoundIsPlaying(minecraft, immutablePos);
        }

        stopMissingOrRemoved(detectedBlocks, minecraft);
    }

    private static void ensureSoundIsPlaying(Minecraft minecraft, BlockPos pos) {
        UraganBlockLoopSound existing = ACTIVE_SOUNDS.get(pos);
        if (existing != null && !existing.isStopped()) {
            return;
        }

        UraganBlockLoopSound sound = new UraganBlockLoopSound(minecraft, pos);
        ACTIVE_SOUNDS.put(pos, sound);
        minecraft.getSoundManager().play(sound);
    }

    private static void stopMissingOrRemoved(Set<BlockPos> detectedBlocks, Minecraft minecraft) {
        Iterator<Map.Entry<BlockPos, UraganBlockLoopSound>> iterator = ACTIVE_SOUNDS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, UraganBlockLoopSound> entry = iterator.next();
            BlockPos pos = entry.getKey();
            UraganBlockLoopSound sound = entry.getValue();

            boolean shouldStop = sound.isStopped()
                    || !detectedBlocks.contains(pos)
                    || !minecraft.level.isLoaded(pos)
                    || minecraft.level.getBlockState(pos).getBlock() != ModBlocks.URAGAN_BLOCK.get();

            if (shouldStop) {
                sound.stopLoop();
                iterator.remove();
            }
        }
    }

    private static void stopAll() {
        for (UraganBlockLoopSound sound : ACTIVE_SOUNDS.values()) {
            sound.stopLoop();
        }
        ACTIVE_SOUNDS.clear();
    }
}
