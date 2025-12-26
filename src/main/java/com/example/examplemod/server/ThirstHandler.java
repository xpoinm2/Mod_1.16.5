package com.example.examplemod.server;

import com.example.examplemod.server.mechanics.ModMechanics;
import com.example.examplemod.server.mechanics.modules.ThirstMechanic;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * ФАСАД для внешних вызовов (пакеты/GUI).
 *
 * Вся логика теперь живёт в {@link ThirstMechanic} и вызывается через менеджер механик.
 */
public final class ThirstHandler {
    private ThirstHandler() {
    }

    private static ThirstMechanic mechanic() {
        ThirstMechanic m = ModMechanics.get(ThirstMechanic.class);
        if (m == null) {
            // На всякий случай: если вызвали слишком рано.
            ModMechanics.init();
            m = ModMechanics.get(ThirstMechanic.class);
        }
        return m;
    }

    public static void onDrinkButton(ServerPlayerEntity player) {
        ThirstMechanic m = mechanic();
        if (m != null) m.onDrinkButton(player);
    }

    public static void onMixWater(ServerPlayerEntity player) {
        ThirstMechanic m = mechanic();
        if (m != null) m.onMixWater(player);
    }
}
