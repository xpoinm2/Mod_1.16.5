package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import com.example.examplemod.network.HarvestGrassPacket;
import com.example.examplemod.network.ModNetworkHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.block.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GrassHarvestingHandler extends AbstractGui {
    private static final int HARVEST_TICKS_REQUIRED = 20 * 10; // 10 seconds

    private static BlockPos trackedPos;
    private static int progressTicks;
    private static int idleTicks;
    private static boolean packetSent;

    private GrassHarvestingHandler() {
        // no instances needed
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null || mc.level == null || event.player != mc.player) {
            resetProgress();
            return;
        }

        if (!mc.options.keyUse.isDown()) {
            idleTicks++;
            if (idleTicks >= 5) {
                resetProgress();
            }
            return;
        }

        if (!(mc.hitResult instanceof BlockRayTraceResult)) {
            idleTicks++;
            if (idleTicks >= 5) {
                resetProgress();
            }
            return;
        }

        BlockRayTraceResult blockHit = (BlockRayTraceResult) mc.hitResult;
        BlockPos lookedPos = blockHit.getBlockPos();
        BlockState state = mc.level.getBlockState(lookedPos);
        if (!state.is(ModBlocks.BUNCH_OF_GRASS.get())) {
            idleTicks++;
            if (idleTicks >= 5) {
                resetProgress();
            }
            return;
        }

        idleTicks = 0;

        if (trackedPos == null || !trackedPos.equals(lookedPos)) {
            trackedPos = lookedPos;
            progressTicks = 0;
            packetSent = false;
        }

        if (progressTicks < HARVEST_TICKS_REQUIRED) {
            progressTicks++;
        }

        if (progressTicks >= HARVEST_TICKS_REQUIRED && !packetSent) {
            ModNetworkHandler.CHANNEL.sendToServer(new HarvestGrassPacket(trackedPos));
            packetSent = true;
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        if (progressTicks <= 0) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        MatrixStack matrixStack = event.getMatrixStack();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        int barWidth = 150;
        int barHeight = 6;
        int x = (width - barWidth) / 2;
        int y = height - 32;

        fill(matrixStack, x - 2, y - 2, x + barWidth + 2, y + barHeight + 2, 0x88000000);
        fill(matrixStack, x, y, x + barWidth, y + barHeight, 0x66000000);

        int progressWidth = Math.round(barWidth * (progressTicks / (float) HARVEST_TICKS_REQUIRED));
        if (progressWidth > 0) {
            fill(matrixStack, x, y, x + progressWidth, y + barHeight, 0xFF6EE4FF);
        }

        String label = "Сбор";
        float labelX = (width - mc.font.width(label)) / 2f;
        mc.font.draw(matrixStack, label, labelX, y - 12, 0xFFFFFFFF);
    }

    private static void resetProgress() {
        trackedPos = null;
        progressTicks = 0;
        packetSent = false;
        idleTicks = 0;
    }
}
