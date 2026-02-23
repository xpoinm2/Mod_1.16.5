package com.example.examplemod.client.sound;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class UraganBlockLoopSound extends TickableSound {
    private static final float DEFAULT_VOLUME = 0.85F;

    private final Minecraft minecraft;
    private final BlockPos sourcePos;

    public UraganBlockLoopSound(Minecraft minecraft, BlockPos sourcePos) {
        super(ModSounds.HURRICANE_LOOP.get(), SoundCategory.BLOCKS);
        this.minecraft = minecraft;
        this.sourcePos = sourcePos.immutable();
        this.looping = true;
        this.delay = 0;
        this.relative = false;
        this.attenuation = AttenuationType.LINEAR;
        this.pitch = 1.0F;
        this.volume = DEFAULT_VOLUME;
        this.x = sourcePos.getX() + 0.5D;
        this.y = sourcePos.getY() + 0.5D;
        this.z = sourcePos.getZ() + 0.5D;
    }

    public BlockPos getSourcePos() {
        return sourcePos;
    }

    public void stopLoop() {
        stop();
    }

    @Override
    public void tick() {
        if (minecraft.level == null || minecraft.player == null) {
            stop();
            return;
        }

        if (!minecraft.level.isLoaded(sourcePos) || minecraft.level.getBlockState(sourcePos).getBlock() != ModBlocks.URAGAN_BLOCK.get()) {
            stop();
            return;
        }

        this.x = sourcePos.getX() + 0.5D;
        this.y = sourcePos.getY() + 0.5D;
        this.z = sourcePos.getZ() + 0.5D;
    }
}
