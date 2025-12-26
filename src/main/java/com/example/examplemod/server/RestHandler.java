package com.example.examplemod.server;

import com.example.examplemod.server.mechanics.ModMechanics;
import com.example.examplemod.server.mechanics.modules.RestMechanic;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * ФАСАД для внешних вызовов (пакеты/GUI).
 *
 * Вся логика теперь живёт в {@link RestMechanic} и вызывается через менеджер механик.
 */
public final class RestHandler {
    private RestHandler() {
    }

    private static RestMechanic mechanic() {
        RestMechanic m = ModMechanics.get(RestMechanic.class);
        if (m == null) {
            ModMechanics.init();
            m = ModMechanics.get(RestMechanic.class);
        }
        return m;
    }

    public static void startSitting(ServerPlayerEntity player) {
        RestMechanic m = mechanic();
        if (m != null) m.startSitting(player);
    }
}