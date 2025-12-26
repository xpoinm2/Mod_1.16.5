package com.example.examplemod.server.mechanics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Реестр механик.
 *
 * Сюда ты добавляешь новые механики со временем, не трогая шедулер и не плодя сотни @SubscribeEvent классов.
 */
public final class ModMechanics {
    private static final List<IMechanicModule> MODULES = new ArrayList<>();
    private static boolean initialized = false;

    private ModMechanics() {
    }

    /**
     * Инициализация + регистрация встроенных модулей.
     * Можно вызывать сколько угодно раз — сработает один раз.
     */
    public static void init() {
        if (initialized) return;
        initialized = true;

        // TODO: регистрируй модули здесь:
        // register(new MyCoolMechanic());
    }

    public static void register(IMechanicModule module) {
        if (module == null) return;
        MODULES.add(module);
    }

    public static List<IMechanicModule> modules() {
        return Collections.unmodifiableList(MODULES);
    }

    public static boolean isInitialized() {
        return initialized;
    }
}


