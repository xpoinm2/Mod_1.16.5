package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

/**
 * Key binding to toggle simple x-ray highlight for mod ores.
 */
public class XRayKeyHandler {
    public static final KeyBinding XRAY_KEY = new KeyBinding(
            "key.examplemod.xray",
            GLFW.GLFW_KEY_V,
            "key.categories.gameplay"
    );

    private static boolean enabled = false;

    @Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ClientRegistry.registerKeyBinding(XRAY_KEY);
        }
    }

    @Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Handler {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.KeyInputEvent event) {
            if (XRAY_KEY.consumeClick()) {
                enabled = !enabled;
            }
        }

        @SubscribeEvent
        public static void onRenderWorld(RenderWorldLastEvent event) {
            if (!enabled) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null) return;
            BlockPos playerPos = mc.player.blockPosition();
            int radius = 32;
            IRenderTypeBuffer.Impl buffer = mc.renderBuffers().bufferSource();
            for (BlockPos pos : BlockPos.betweenClosed(playerPos.offset(-radius, -radius, -radius),
                    playerPos.offset(radius, radius, radius))) {
                Block block = mc.level.getBlockState(pos).getBlock();
                if (block == ModBlocks.IMPURE_IRON_ORE.get() || block == ModBlocks.PYRITE.get()) {
                    AxisAlignedBB box = new AxisAlignedBB(pos);
                    WorldRenderer.renderLineBox(event.getMatrixStack(), buffer.getBuffer(RenderType.lines()), box,
                            1.0F, 1.0F, 0.0F, 1.0F);
                }
            }
            buffer.endBatch(RenderType.lines());
        }
    }
}