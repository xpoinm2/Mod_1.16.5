// === FILE src/main/java/com/example/examplemod/server/mechanics/modules/HealthEffectsMechanic.java
package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.server.mechanics.IMechanicModule;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;

/**
 * Механика эффектов здоровья - применяет дебафы в зависимости от уровней показателей здоровья
 * с учетом складывания эффектов "по убывающей полезности"
 */
public class HealthEffectsMechanic implements IMechanicModule {
    @Override
    public String id() {
        return "health_effects";
    }

    @Override
    public int playerIntervalTicks() {
        return 20; // каждую секунду
    }

    @Override
    public void onPlayerTick(ServerPlayerEntity player) {
        // Не применяем механики, если игрок вышел из мира (в главном меню)
        if (player.connection == null || player.hasDisconnected()) {
            return;
        }
        applyHealthEffects(player);
    }

    private void applyHealthEffects(ServerPlayerEntity player) {
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            int cold = stats.getCold();
            int hypothermia = stats.getHypothermia();
            int virus = stats.getVirus();
            int poison = stats.getPoison();
            int thirst = stats.getThirst();
            int fatigue = stats.getFatigue();

            // Рассчитываем максимальные стадии для каждого типа эффекта
            int maxMovementSlowdown = 0;
            int maxDigSlowdown = 0;
            boolean hasPeriodicDamage = false;
            boolean hasNausea = false;
            int nauseaInterval = Integer.MAX_VALUE;

            // Проверяем все источники эффектов и берем максимальные значения
            maxMovementSlowdown = Math.max(maxMovementSlowdown, getMovementSlowdownStage(cold));
            maxMovementSlowdown = Math.max(maxMovementSlowdown, getMovementSlowdownStage(hypothermia));
            maxMovementSlowdown = Math.max(maxMovementSlowdown, getMovementSlowdownStage(virus));
            maxMovementSlowdown = Math.max(maxMovementSlowdown, getMovementSlowdownStage(poison));
            maxMovementSlowdown = Math.max(maxMovementSlowdown, getMovementSlowdownStage(thirst));
            maxMovementSlowdown = Math.max(maxMovementSlowdown, getMovementSlowdownStage(fatigue));

            maxDigSlowdown = Math.max(maxDigSlowdown, getDigSlowdownStage(cold, false));
            maxDigSlowdown = Math.max(maxDigSlowdown, getDigSlowdownStage(hypothermia, false));
            maxDigSlowdown = Math.max(maxDigSlowdown, getDigSlowdownStage(virus, false));
            maxDigSlowdown = Math.max(maxDigSlowdown, getDigSlowdownStage(poison, false));
            maxDigSlowdown = Math.max(maxDigSlowdown, getDigSlowdownStage(thirst, false));
            maxDigSlowdown = Math.max(maxDigSlowdown, getDigSlowdownStage(fatigue, true)); // Усталость может дать 100% замедление

            hasPeriodicDamage = hasPeriodicDamage || hasStage4Damage(cold) || hasStage4Damage(hypothermia) ||
                               hasStage4Damage(virus) || hasStage4Damage(poison) || hasStage4Damage(thirst) || hasStage4Damage(fatigue);

            // Проверяем тошноту от отравления
            if (poison >= 25) {
                hasNausea = true;
                nauseaInterval = Math.min(nauseaInterval, getNauseaInterval(poison));
            }

            // Применяем рассчитанные эффекты
            applyMovementEffect(player, maxMovementSlowdown);
            applyDigEffect(player, maxDigSlowdown);
            applyPeriodicDamage(player, hasPeriodicDamage);
            applyNausea(player, hasNausea, nauseaInterval);
        });
    }

    private int getMovementSlowdownStage(int value) {
        if (value >= 75) return 2; // 50% slowdown
        if (value >= 50) return 1; // 25% slowdown
        if (value >= 25) return 1; // 25% slowdown
        return 0;
    }

    private int getDigSlowdownStage(int value, boolean isFatigue) {
        if (value >= 100 && isFatigue) return 5; // 100% для усталости стадии 4
        if (value >= 75) return 1; // 40% slowdown
        if (value >= 50) return 1; // 25% slowdown
        if (value >= 25) return 1; // 25% slowdown
        return 0;
    }

    private boolean hasStage4Damage(int value) {
        return value >= 100;
    }

    private int getNauseaInterval(int poison) {
        if (poison >= 100) return 0; // постоянная
        if (poison >= 75) return 1200; // каждые 1200 тиков
        if (poison >= 50) return 2400; // каждые 2400 тиков
        if (poison >= 25) return 3600; // каждые 3600 тиков
        return Integer.MAX_VALUE;
    }

    private void applyMovementEffect(ServerPlayerEntity player, int stage) {
        player.removeEffect(Effects.MOVEMENT_SLOWDOWN);
        if (stage > 0) {
            int amplifier = stage - 1; // stage 1 = amplifier 0 (25%), stage 2 = amplifier 1 (50%)
            player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 40, amplifier, false, false, true));
        }
    }

    private void applyDigEffect(ServerPlayerEntity player, int stage) {
        player.removeEffect(Effects.DIG_SLOWDOWN);
        if (stage > 0) {
            int amplifier = Math.min(stage - 1, 4); // Ограничиваем до 4 для 100% замедления
            player.addEffect(new EffectInstance(Effects.DIG_SLOWDOWN, 40, amplifier, false, false, true));
        }
    }

    private void applyPeriodicDamage(ServerPlayerEntity player, boolean hasDamage) {
        if (hasDamage && player.tickCount % 40 == 0) { // Каждые 2 секунды
            player.hurt(DamageSource.MAGIC, 1.0F); // Полсердца урона
        }
    }

    private void applyNausea(ServerPlayerEntity player, boolean hasNausea, int interval) {
        if (!hasNausea) return;

        if (interval == 0) {
            // Постоянная тошнота
            player.addEffect(new EffectInstance(Effects.CONFUSION, 40, 0, false, false, true));
        } else if (player.tickCount % interval == 0) {
            // Периодическая тошнота
            player.addEffect(new EffectInstance(Effects.CONFUSION, 300, 0, false, false, true));
        }
    }
}