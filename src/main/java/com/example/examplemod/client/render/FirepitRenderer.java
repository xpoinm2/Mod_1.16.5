package com.example.examplemod.client.render;

import com.example.examplemod.tileentity.FirepitTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FirepitRenderer extends TileEntityRenderer<FirepitTileEntity> {
    private static final int SMOKE_THRESHOLD = FirepitTileEntity.MIN_HEAT_FOR_SMELTING;
    // Keep smoke synced with the GUI heat bar threshold.
    private static final float SMOKE_HEIGHT = 1.2f; // Height above the block to render smoke

    public FirepitRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void render(FirepitTileEntity tileEntity, float partialTicks, MatrixStack matrixStack,
                      IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

        // Only render smoke if the tile entity exists and has sufficient heat
        if (tileEntity == null || tileEntity.getHeat() < SMOKE_THRESHOLD) {
            return;
        }

        // Check if multiblock is intact
        if (!tileEntity.isMultiblockIntact()) {
            return;
        }

        // Only render on client side
        if (tileEntity.getLevel().isClientSide) {
            spawnSmokeParticles(tileEntity);
        }
    }

    private void spawnSmokeParticles(FirepitTileEntity tileEntity) {
        World world = tileEntity.getLevel();
        if (world == null) return;

        // Get the animation frame based on world time for timing
        long worldTime = world.getGameTime();

        // Spawn smoke particles over each of the 16 blocks in the multiblock
        BlockPos masterPos = tileEntity.getBlockPos();
        BlockPos startPos = masterPos.offset(-1, 0, -1); // Start of 4x4 structure

        // Only spawn particles occasionally to avoid performance issues
        if (worldTime % 4 != 0) return; // Spawn every 4 ticks

        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                BlockPos smokePos = startPos.offset(x, 0, z);

                // Add slight random variation
                double offsetX = (world.random.nextDouble() - 0.5) * 0.3;
                double offsetZ = (world.random.nextDouble() - 0.5) * 0.3;

                // Spawn smoke particle
                world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    smokePos.getX() + 0.5 + offsetX,
                    smokePos.getY() + SMOKE_HEIGHT,
                    smokePos.getZ() + 0.5 + offsetZ,
                    0.0, 0.05, 0.0); // Slight upward velocity
            }
        }
    }
}
