package com.example.examplemod;

import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.fml.RegistryObject;

public final class ModParticles {

    public static final RegistryObject<BasicParticleType> HURRICANE_WIND =
            ModRegistries.PARTICLES.register("hurricane_wind", () -> new BasicParticleType(false));

    private ModParticles() {
    }

    public static void register() {
        // no-op; вызов нужен, чтобы гарантировать загрузку класса и регистрацию ParticleType.
    }
}
