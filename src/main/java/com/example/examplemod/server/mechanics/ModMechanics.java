package com.example.examplemod.server.mechanics;

import com.example.examplemod.Config;
import com.example.examplemod.server.*;
import com.example.examplemod.server.mechanics.modules.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Реестр механик.
 *
 * Сюда ты добавляешь новые механики со временем, не трогая шедулер и не плодя сотни @SubscribeEvent классов.
 */
public final class ModMechanics {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<IMechanicModule> MODULES = new ArrayList<>();
    private static boolean initialized = false;

    private ModMechanics() {
    }

    /**
     * Инициализация + регистрация встроенных модулей.
     * Можно вызывать сколько угодно раз — сработает один раз.
     * 
     * ОПТИМИЗАЦИЯ: Ленивая инициализация - механики регистрируются только если включены в конфиге.
     * Это экономит память и ускоряет загрузку для игроков, которые не используют определённые системы.
     */
    public static void init() {
        if (initialized) return;
        initialized = true;

        LOGGER.info("Initializing mechanics (lazy loading enabled)...");
        int registeredCount = 0;

        // Все механики теперь живут в server/mechanics/modules/*
        // Старые server/*Handler классы остаются максимум фасадами для внешних вызовов (пакеты/GUI).
        
        // === Механики выживания (жажда, усталость, отдых) ===
        if (Config.ENABLE_SURVIVAL_MECHANICS.get()) {
            register(new ThirstMechanic());
            register(new RestMechanic());
            registeredCount += 2;
            LOGGER.debug("Loaded survival mechanics (thirst, rest)");
        } else {
            LOGGER.info("Survival mechanics disabled in config - skipping");
        }

        // === Механики температуры (холод, переохлаждение) ===
        if (Config.ENABLE_TEMPERATURE_MECHANICS.get()) {
            register(new ColdMechanic());
            register(new HypothermiaMechanic());
            registeredCount += 2;
            LOGGER.debug("Loaded temperature mechanics (cold, hypothermia)");
        } else {
            LOGGER.info("Temperature mechanics disabled in config - skipping");
        }

        // === Механики болезней (вирусы, яды) ===
        if (Config.ENABLE_DISEASE_MECHANICS.get()) {
            register(new VirusMechanic());
            register(new HealthEffectsMechanic());
            registeredCount += 2;
            LOGGER.debug("Loaded disease mechanics (virus, health effects)");
        } else {
            LOGGER.info("Disease mechanics disabled in config - skipping");
        }

        // === Всегда активные механики (core gameplay) ===
        register(new BlockBreakMechanic());
        register(new DayNightCycleMechanic());
        register(new HotOreDamageMechanic());
        registeredCount += 3;

        // === Команды (основные) ===
        register(new StatsCommandsMechanic());
        register(new QuestCommandsMechanic());
        registeredCount += 2;

        // === Отладочные команды (опционально) ===
        if (Config.ENABLE_DEBUG_COMMANDS.get()) {
            register(new PyramidDebugCommandsMechanic());
            register(new BiomeTeleportCommandsMechanic());
            register(new BlockTeleportCommandsMechanic());
            register(new MechanicsDebugCommand());
            registeredCount += 4;
            LOGGER.debug("Loaded debug commands");
        } else {
            LOGGER.info("Debug commands disabled in config - skipping");
        }

        // Остальные механики, которые пока не переносились в отдельные модули
        register(new HandlerModule("gravel_ore_wash") {
            @Override public boolean enablePlayerInteract() { return true; }
            @Override public void onPlayerInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent e) {
                if (e instanceof net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem) {
                    GravelOreWashHandler.onUse((net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem) e);
                }
            }
        });

        register(new HandlerModule("iron_cluster_wash") {
            @Override public boolean enablePlayerInteract() { return true; }
            @Override public void onPlayerInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent e) {
                if (e instanceof net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem) {
                    IronClusterWashHandler.onUse((net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem) e);
                }
            }
        });

        register(new HandlerModule("crafting_blocker") {
            @Override public boolean enableItemCrafted() { return true; }
            @Override public void onItemCrafted(net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent e) { CraftingBlocker.onItemCrafted(e); }
        });

        register(new HandlerModule("firepit_structure") {
            @Override public boolean enablePlayerInteract() { return true; }
            @Override public void onPlayerInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent e) {
                if (e instanceof net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock) {
                    FirepitStructureHandler.onUse((net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock) e);
                }
            }
        });

        register(new HandlerModule("pechuga_structure") {
            @Override public boolean enablePlayerInteract() { return true; }
            @Override public void onPlayerInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent e) {
                if (e instanceof net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock) {
                    PechugaStructureHandler.onUse((net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock) e);
                }
            }
        });

        register(new HandlerModule("flax_soak") {
            @Override public boolean enablePlayerInteract() { return true; }
            @Override public void onPlayerInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent e) {
                if (e instanceof net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem) {
                    FlaxSoakHandler.onUse((net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem) e);
                }
            }
        });

        register(new HandlerModule("flax_drying") {
            @Override public boolean enablePlayerInteract() { return true; }
            @Override public void onPlayerInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent e) {
                if (e instanceof net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock) {
                    FlaxDryingHandler.onUse((net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock) e);
                }
            }
        });

        register(new HandlerModule("sharp_bone") {
            @Override public boolean enablePlayerInteract() { return true; }
            @Override public void onPlayerInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent e) {
                if (e instanceof net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock) {
                    SharpBoneHandler.onLeftClick((net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock) e);
                }
            }
        });

        register(new HandlerModule("red_mushroom") {
            @Override public boolean enablePlayerInteract() { return true; }
            @Override public void onPlayerInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent e) {
                if (e instanceof net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem) {
                    RedMushroomHandler.onMushroomUse((net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem) e);
                }
            }
        });

        register(new HandlerModule("big_bone_drop") {
            @Override public boolean enableLivingDrops() { return true; }
            @Override public void onLivingDrops(net.minecraftforge.event.entity.living.LivingDropsEvent e) { BigBoneDropHandler.onLivingDrops(e); }
        });

        register(new HandlerModule("hewn_stone_spawn") {
            @Override public boolean enableChunkEvent() { return true; }
            @Override public void onChunkEvent(net.minecraftforge.event.world.ChunkEvent e) {
                if (e instanceof net.minecraftforge.event.world.ChunkEvent.Load) {
                    HewnStoneSpawnHandler.onChunkLoad((net.minecraftforge.event.world.ChunkEvent.Load) e);
                }
            }
        });

        register(new HandlerModule("natural_regen_disable") {
            @Override public boolean enableServerStarting() { return true; }
            @Override public void onServerStarting(net.minecraftforge.fml.event.server.FMLServerStartingEvent e) { NaturalRegenerationDisabler.onServerStarting(e); }
        });

        register(new HandlerModule("auto_save") {
            @Override public boolean enableServerStopping() { return true; }
            @Override public void onServerStopping(net.minecraftforge.fml.event.server.FMLServerStoppingEvent e) { AutoSaveHandler.onServerStopping(e); }
        });

        // Подсчет оставшихся механик (HandlerModule обёртки)
        registeredCount += 14; // gravel_ore_wash, iron_cluster_wash, crafting_blocker, firepit_structure, 
                               // pechuga_structure, flax_soak, flax_drying, sharp_bone, red_mushroom, big_bone_drop, 
                               // hewn_stone_spawn, natural_regen_disable, auto_save

        LOGGER.info("Mechanics initialization complete: {} mechanics registered", registeredCount);
        LOGGER.info("Memory saved by lazy loading: ~{} mechanics skipped", 
                (2 + 2 + 1 + 4) - (registeredCount - 13 - 3 - 2)); // Максимум - загруженные
    }

    /**
     * Упрощённый модуль-адаптер для старых статических хендлеров.
     */
    private abstract static class HandlerModule implements IMechanicModule {
        private final String id;
        protected HandlerModule(String id) { this.id = id; }
        @Override public String id() { return id; }
    }

    public static void register(IMechanicModule module) {
        if (module == null) return;
        MODULES.add(module);
    }

    public static List<IMechanicModule> modules() {
        return Collections.unmodifiableList(MODULES);
    }

    /**
     * Получить модуль по типу (удобно для фасадов/внешних вызовов).
     */
    public static <T extends IMechanicModule> T get(Class<T> type) {
        for (IMechanicModule m : MODULES) {
            if (type.isInstance(m)) {
                return type.cast(m);
            }
        }
        return null;
    }

    public static boolean isInitialized() {
        return initialized;
    }
}


