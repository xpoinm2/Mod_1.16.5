package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Applies blindness if the player keeps staring at the sun.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SunBlindnessHandler {
    private static final int LOOK_THRESHOLD = 15 * 20;      // 15 seconds
    private static final int BLINDNESS_DURATION = 30 * 20;   // 30 seconds

    private static final Map<UUID, Integer> LOOK_TICKS = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayerEntity)) return;

        ServerPlayerEntity player = (ServerPlayerEntity) event.player;
        ServerWorld world = (ServerWorld) player.level;
        UUID id = player.getUUID();

        if (world.isDay() && isLookingAtSun(player, world)) {
            int ticks = LOOK_TICKS.getOrDefault(id, 0) + 1;
            if (ticks >= LOOK_THRESHOLD) {
                player.addEffect(new EffectInstance(Effects.BLINDNESS, BLINDNESS_DURATION));
                ticks = 0;
            }
            LOOK_TICKS.put(id, ticks);
        } else {
            LOOK_TICKS.remove(id);
        }
    }

    private static boolean isLookingAtSun(ServerPlayerEntity player, ServerWorld world) {
        float angle = world.getSunAngle(1.0F); // 0..1
        float sunYaw = angle * 360F - 90F;
        float sunPitch = -MathHelper.cos(angle * ((float)Math.PI * 2F)) * 90F;
        Vector3d sunDir = Vector3d.directionFromRotation(sunPitch, sunYaw);
        Vector3d look = player.getViewVector(1.0F);
        return look.normalize().dot(sunDir.normalize()) > 0.95D;
    }
}