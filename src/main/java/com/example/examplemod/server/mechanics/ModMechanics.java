package com.example.examplemod.server.mechanics;

import com.example.examplemod.server.*;

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

        // Регистрируем существующие механики как модули-адаптеры.
        // Важно: соответствующие *Handler/*Commands классы переведены в режим "без подписки"
        // (без @EventBusSubscriber/@SubscribeEvent), чтобы избежать двойных срабатываний.
        register(new HandlerModule("stats_commands") {
            @Override public boolean enableRegisterCommands() { return true; }
            @Override public void onRegisterCommands(net.minecraftforge.event.RegisterCommandsEvent e) { StatsCommands.onRegisterCommands(e); }
        });
        register(new HandlerModule("pyramid_debug_commands") {
            @Override public boolean enableRegisterCommands() { return true; }
            @Override public void onRegisterCommands(net.minecraftforge.event.RegisterCommandsEvent e) { PyramidDebugCommands.onRegisterCommands(e); }
        });
        register(new HandlerModule("biome_teleport_commands") {
            @Override public boolean enableRegisterCommands() { return true; }
            @Override public void onRegisterCommands(net.minecraftforge.event.RegisterCommandsEvent e) { BiomeTeleportCommands.onRegisterCommands(e); }
        });
        register(new HandlerModule("block_teleport_commands") {
            @Override public boolean enableRegisterCommands() { return true; }
            @Override public void onRegisterCommands(net.minecraftforge.event.RegisterCommandsEvent e) { BlockTeleportCommand.onRegisterCommands(e); }
        });
        register(new HandlerModule("quest_commands") {
            @Override public boolean enableRegisterCommands() { return true; }
            @Override public void onRegisterCommands(net.minecraftforge.event.RegisterCommandsEvent e) { QuestCommands.onRegisterCommands(e); }
        });

        register(new HandlerModule("block_break") {
            @Override public boolean enableBlockBreak() { return true; }
            @Override public void onBlockBreak(net.minecraftforge.event.world.BlockEvent.BreakEvent e) { BlockBreakHandler.onBlockBreak(e); }
        });

        register(new HandlerModule("thirst") {
            @Override public int playerIntervalTicks() { return 5; } // как в ThirstHandler
            @Override public void onPlayerTick(net.minecraft.entity.player.ServerPlayerEntity p) { ThirstHandler.tick(p); }
            @Override public boolean enableAttackEntity() { return true; }
            @Override public void onAttackEntity(net.minecraftforge.event.entity.player.AttackEntityEvent e) { ThirstHandler.onAttackEntity(e); }
            @Override public boolean enableLivingJump() { return true; }
            @Override public void onLivingJump(net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent e) { ThirstHandler.onPlayerJump(e); }
            @Override public boolean enableUseItemFinish() { return true; }
            @Override public void onUseItemFinish(net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish e) { ThirstHandler.onDrinkFinish(e); }
            @Override public void onPlayerLogout(net.minecraft.entity.player.ServerPlayerEntity p) { ThirstHandler.logout(p); }
        });

        register(new HandlerModule("rest") {
            @Override public int playerIntervalTicks() { return 1; }
            @Override public void onPlayerTick(net.minecraft.entity.player.ServerPlayerEntity p) { RestHandler.tick(p); }
        });

        register(new HandlerModule("cold") {
            @Override public int playerIntervalTicks() { return 20; }
            @Override public void onPlayerTick(net.minecraft.entity.player.ServerPlayerEntity p) { ColdHandler.tick(p); }
        });

        register(new HandlerModule("hypothermia") {
            @Override public int playerIntervalTicks() { return 20; }
            @Override public void onPlayerTick(net.minecraft.entity.player.ServerPlayerEntity p) { HypothermiaHandler.tick(p); }
        });

        register(new HandlerModule("day_night_cycle") {
            @Override public boolean enableWorldTick() { return true; }
            @Override public void onWorldTick(net.minecraftforge.event.TickEvent.WorldTickEvent e) { DayNightCycleHandler.onWorldTick(e); }
        });

        register(new HandlerModule("hot_ore_damage") {
            @Override public int playerIntervalTicks() { return 20; }
            @Override public void onPlayerTick(net.minecraft.entity.player.ServerPlayerEntity p) { HotOreDamageHandler.tick(p); }
        });

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

        register(new HandlerModule("virus") {
            @Override public boolean enableUseItemFinish() { return true; }
            @Override public void onUseItemFinish(net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish e) { VirusHandler.onFoodEaten(e); }
            @Override public boolean enableBlockBreak() { return true; }
            @Override public void onBlockBreak(net.minecraftforge.event.world.BlockEvent.BreakEvent e) { VirusHandler.onBlockBreak(e); }
        });

        register(new HandlerModule("natural_regen_disable") {
            @Override public boolean enableServerStarting() { return true; }
            @Override public void onServerStarting(net.minecraftforge.fml.event.server.FMLServerStartingEvent e) { NaturalRegenerationDisabler.onServerStarting(e); }
        });

        register(new HandlerModule("auto_save") {
            @Override public boolean enableServerStopping() { return true; }
            @Override public void onServerStopping(net.minecraftforge.fml.event.server.FMLServerStoppingEvent e) { AutoSaveHandler.onServerStopping(e); }
        });
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

    public static boolean isInitialized() {
        return initialized;
    }
}


