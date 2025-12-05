// === FILE src/main/java/com/example/examplemod/server/HotOreTimerHandler.java
package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.item.HotRoastedOreItem;
import com.example.examplemod.ModItems;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HotOreTimerHandler {
    private static final Map<UUID, Integer> TICK_COUNTERS = new HashMap<>();
    private static final int TICKS_PER_SECOND = 20; // 20 тиков в секунду

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

    private static void checkAndUpdateHotOres(ServerPlayerEntity player) {
        // Проверяем основной инвентарь
        checkInventory(player.inventory.items, player);

        // Проверяем armor
        checkInventory(player.inventory.armor, player);

        // Проверяем offhand
        checkInventory(player.inventory.offhand, player);

        // Проверяем предметы на земле вокруг игрока
        checkGroundItems(player);
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

        var itemEntities = player.level.getEntitiesOfClass(ItemEntity.class, searchBox);

        for (ItemEntity itemEntity : itemEntities) {
            ItemStack stack = itemEntity.getItem();

            if (!stack.isEmpty() && stack.getItem() instanceof HotRoastedOreItem) {
                if (HotRoastedOreItem.isTimerExpired(stack)) {
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