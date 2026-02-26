package com.example.examplemod.tileentity;

import com.example.examplemod.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class BellowsTileEntity extends TileEntity implements ITickableTileEntity {
    private static final float STEP = 0.1F;

    private float progress;
    private boolean pumping;

    public BellowsTileEntity() {
        super(ModTileEntities.BELLOWS.get());
    }

    public float getProgress() {
        return progress;
    }

    public void startPump() {
        pumping = true;
        setChanged();
    }

    @Override
    public void tick() {
        if (level == null) {
            return;
        }

        float oldProgress = progress;
        boolean oldPumping = pumping;

        if (pumping) {
            progress = Math.min(1.0F, progress + STEP);
            if (progress >= 1.0F) {
                pumping = false;
            }
        } else {
            progress = Math.max(0.0F, progress - STEP);
        }

        if (oldProgress != progress || oldPumping != pumping) {
            setChanged();
            if (!level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        progress = nbt.getFloat("Progress");
        pumping = nbt.getBoolean("Pumping");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.putFloat("Progress", progress);
        nbt.putBoolean("Pumping", pumping);
        return nbt;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        load(getBlockState(), pkt.getTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        load(state, tag);
    }
}
