package com.example.examplemod.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class HurricaneWindParticle extends SpriteTexturedParticle {
    private final IAnimatedSprite sprites;
    private final float swirlAmplitude;
    private final float swirlSpeed;
    private final float swirlPhase;

    protected HurricaneWindParticle(ClientWorld world,
                                    double x,
                                    double y,
                                    double z,
                                    double xSpeed,
                                    double ySpeed,
                                    double zSpeed,
                                    IAnimatedSprite sprites) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        Random random = world.random;

        this.quadSize = 0.24F + random.nextFloat() * 0.34F;
        this.lifetime = 14 + random.nextInt(10);
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.friction = 0.96F;

        this.rCol = this.gCol = this.bCol = 1.0F;
        this.alpha = 0.1F + random.nextFloat() * 0.2F;

        this.swirlAmplitude = 0.008F + random.nextFloat() * 0.02F;
        this.swirlSpeed = 0.18F + random.nextFloat() * 0.3F;
        this.swirlPhase = random.nextFloat() * ((float) Math.PI * 2.0F);

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        float progress = (float) this.age / (float) this.lifetime;
        float fadeIn = Math.min(1.0F, progress * 4.5F);
        float fadeOut = Math.min(1.0F, (1.0F - progress) * 3.2F);
        this.alpha = 0.35F * fadeIn * fadeOut;

        float wave = this.age * this.swirlSpeed + this.swirlPhase;
        this.xd += MathHelper.cos(wave) * this.swirlAmplitude;
        this.zd += MathHelper.sin(wave) * this.swirlAmplitude;
        this.yd *= 0.98D;

        this.move(this.xd, this.yd, this.zd);
        this.xd *= this.friction;
        this.yd *= this.friction;
        this.zd *= this.friction;

        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprites;

        public Factory(IAnimatedSprite sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(BasicParticleType type,
                                       ClientWorld world,
                                       double x,
                                       double y,
                                       double z,
                                       double xSpeed,
                                       double ySpeed,
                                       double zSpeed) {
            return new HurricaneWindParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
