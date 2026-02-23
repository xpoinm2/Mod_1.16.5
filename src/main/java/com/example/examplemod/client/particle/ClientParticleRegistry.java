package com.example.examplemod.client.particle;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModParticles;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientParticleRegistry {

    private ClientParticleRegistry() {
    }

    @SubscribeEvent
    public static void onRegisterFactories(ParticleFactoryRegisterEvent event) {
        net.minecraft.client.Minecraft.getInstance().particleEngine
                .register(ModParticles.HURRICANE_WIND.get(), HurricaneWindParticle.Factory::new);
    }
}
