// === FILE src/main/java/com/example/examplemod/server/HotOreTimerHandler.java
package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.item.HotRoastedOreItem;
import com.example.examplemod.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HotOreTimerHandler {
    private static final Map<UUID, Integer> TICK_COUNTERS = new HashMap<>();
    private static final int TICKS_PER_SECOND = 20; // 20 тиков в секунду
    private static int WORLD_TICK_COUNTER = 0;
    private static final int WORLD_CHECK_INTERVAL = 10 * TICKS_PER_SECOND; // Проверяем весь мир каждые 10 секунд
    private static boolean hasPlayersOnline = false; // Флаг наличия игроков онлайн

    public static boolean hasPlayersOnline() {
        return hasPlayersOnline;
    }

    @SubscribeEvent
    public static void onServerStarted(FMLServerStartedEvent event) {
        // При запуске сервера сбрасываем флаг
        hasPlayersOnline = false;
    }

    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppingEvent event) {
        // При остановке сервера сбрасываем флаг
        hasPlayersOnline = false;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayerEntity)) return;

        ServerPlayerEntity player = (ServerPlayerEntity) event.player;
        UUID playerId = player.getUUID();

        // Увеличиваем счетчик тиков для этого игрока
        int tickCount = TICK_COUNTERS.getOrDefault(playerId, 0) + 1;

        // Каждую секунду (каждые 20 тиков) проверяем таймеры
        if (tickCount >= TICKS_PER_SECOND) {
            tickCount = 0;
            checkAndUpdateHotOres(player);
        }

        TICK_COUNTERS.put(playerId, tickCount);
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.world.isClientSide()) return;

        // Обновляем флаг наличия игроков на сервере (проверяем только в overworld, чтобы не дублировать)
        if (event.world.dimension().equals(net.minecraft.world.Dimension.OVERWORLD)) {
            hasPlayersOnline = event.world.getServer() != null && !event.world.getServer().getPlayerList().getPlayers().isEmpty();
        }

        // Проверка руд работает только когда есть игроки на сервере
        if (!hasPlayersOnline) return;

        WORLD_TICK_COUNTER++;

        // Проверяем весь мир каждые 10 секунд
        if (WORLD_TICK_COUNTER >= WORLD_CHECK_INTERVAL) {
            WORLD_TICK_COUNTER = 0;
            checkAllWorldItems(event.world);
        }
    }

    private static void checkAndUpdateHotOres(ServerPlayerEntity player) {
        // Проверяем основной инвентарь
        checkInventory(player.inventory.items, player);

        // Проверяем armor
        checkInventory(player.inventory.armor, player);

        // Проверяем offhand
        checkInventory(player.inventory.offhand, player);
    }

    private static void checkInventory(NonNullList<ItemStack> inventory, ServerPlayerEntity player) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);

            if (!stack.isEmpty() && stack.getItem() instanceof HotRoastedOreItem) {
                if (HotRoastedOreItem.isTimerExpired(stack)) {
                    // Преобразуем горячую руду в обычную
                    ItemStack resultStack = HotRoastedOreItem.getResultItemStack(stack);
                    inventory.set(i, resultStack);

                    // Можно добавить эффект частиц или звук здесь, если нужно
                }
            }
        }
    }

    private static void checkGroundItems(ServerPlayerEntity player) {
        if (player.level == null) return;

        // Ищем все ItemEntity в радиусе 16 блоков от игрока
        AxisAlignedBB searchBox = new AxisAlignedBB(
            player.getX() - 16, player.getY() - 8, player.getZ() - 16,
            player.getX() + 16, player.getY() + 8, player.getZ() + 16
        );

        List<ItemEntity> itemEntities = player.level.getEntitiesOfClass(ItemEntity.class, searchBox);

        for (ItemEntity itemEntity : itemEntities) {
            ItemStack stack = itemEntity.getItem();

            if (!stack.isEmpty() && stack.getItem() instanceof HotRoastedOreItem) {
                // Проверяем, находится ли предмет в воде для ускоренного охлаждения
                float speedMultiplier = isItemInWater(itemEntity) ? 10.0f : 1.0f;

                if (HotRoastedOreItem.isTimerExpired(stack, speedMultiplier)) {
                    // Преобразуем горячую руду в обычную
                    ItemStack resultStack = HotRoastedOreItem.getResultItemStack(stack);
                    itemEntity.setItem(resultStack);

                    // Можно добавить эффект частиц или звук здесь, если нужно
                }
            }
        }
    }

    private static boolean isItemInWater(ItemEntity itemEntity) {
        World world = itemEntity.level;
        BlockPos pos = itemEntity.blockPosition();

        // Проверяем блок, в котором находится предмет
        return world.getBlockState(pos).getBlock() == Blocks.WATER;
    }

    private static void checkAllWorldItems(World world) {
        // Получаем все ItemEntity во всем мире
        List<ItemEntity> allItemEntities = world.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(
            Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
        ));

        for (ItemEntity itemEntity : allItemEntities) {
            ItemStack stack = itemEntity.getItem();

            if (!stack.isEmpty() && stack.getItem() instanceof HotRoastedOreItem) {
                // Проверяем, находится ли предмет в воде для ускоренного охлаждения
                float speedMultiplier = isItemInWater(itemEntity) ? 10.0f : 1.0f;

                if (HotRoastedOreItem.isTimerExpired(stack, speedMultiplier)) {
                    // Преобразуем горячую руду в обычную
                    ItemStack resultStack = HotRoastedOreItem.getResultItemStack(stack);
                    itemEntity.setItem(resultStack);

                    // Можно добавить эффект частиц или звук здесь, если нужно
                }
            }
        }
    }

    // Метод для принудительной проверки всех игроков (может пригодиться для админ команд)
    public static void forceUpdateAllPlayers() {
        // Эта функция может быть вызвана из команд или других обработчиков
        // Для реализации нужно получить всех онлайн игроков через MinecraftServer
    }
}