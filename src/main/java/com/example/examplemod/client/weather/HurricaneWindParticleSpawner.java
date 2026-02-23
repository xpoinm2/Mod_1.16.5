package com.example.examplemod.client.weather;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModParticles;
import com.example.examplemod.client.HurricaneClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HurricaneWindParticleSpawner {
    private HurricaneWindParticleSpawner() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.isPaused()) {
            return;
        }

        ClientWorld world = minecraft.level;
        ClientPlayerEntity player = minecraft.player;
        if (world == null || player == null || !HurricaneClientState.shouldRenderEffects()) {
            return;
        }

        float intensity = HurricaneClientState.getIntensity();
        int particlesPerTick = Math.max(1, (int) (2 + intensity * 8.0F));

        Vector3d look = player.getLookAngle();
        Vector3d side = new Vector3d(-look.z, 0.0D, look.x).normalize();
        if (side.lengthSqr() < 1.0E-4D) {
            side = new Vector3d(1.0D, 0.0D, 0.0D);
        }

        Vector3d windDir = side.scale(0.75D).add(look.scale(0.25D)).normalize();

        for (int i = 0; i < particlesPerTick; i++) {
            double spawnRadius = 5.0D + world.random.nextDouble() * 10.0D;
            double offsetX = (world.random.nextDouble() - 0.5D) * spawnRadius * 2.0D;
            double offsetY = -0.8D + world.random.nextDouble() * 3.0D;
            double offsetZ = (world.random.nextDouble() - 0.5D) * spawnRadius * 2.0D;

            double x = player.getX() + offsetX;
            double y = player.getY() + 1.0D + offsetY;
            double z = player.getZ() + offsetZ;

            double speed = 0.1D + world.random.nextDouble() * 0.18D + intensity * 0.24D;
            double sway = (world.random.nextDouble() - 0.5D) * 0.08D;

            double xSpeed = windDir.x * speed + sway;
            double ySpeed = (world.random.nextDouble() - 0.5D) * 0.03D;
            double zSpeed = windDir.z * speed - sway;

            world.addParticle(ModParticles.HURRICANE_WIND.get(), x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
