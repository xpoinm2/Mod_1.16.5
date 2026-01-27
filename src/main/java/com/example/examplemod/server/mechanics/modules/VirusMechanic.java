package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncAllStatsPacket;
import com.example.examplemod.server.mechanics.IMechanicModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashSet;
import java.util.Set;

public class VirusMechanic implements IMechanicModule {
    private static final Set<Item> COOKED_FOODS;

    static {
        Set<Item> cooked = new HashSet<>();
        cooked.add(Items.BAKED_POTATO);
        cooked.add(Items.COOKED_BEEF);
        cooked.add(Items.COOKED_CHICKEN);
        cooked.add(Items.COOKED_COD);
        cooked.add(Items.COOKED_MUTTON);
        cooked.add(Items.COOKED_PORKCHOP);
        cooked.add(Items.COOKED_RABBIT);
        cooked.add(Items.COOKED_SALMON);
        COOKED_FOODS = cooked;
    }

    @Override
    public String id() {
        return "virus";
    }

    @Override
    public boolean enableUseItemFinish() {
        return true;
    }

    @Override
    public boolean enableBlockBreak() {
        return true;
    }

    @Override
    public void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntityLiving() instanceof ServerPlayerEntity)) return;
        ItemStack stack = event.getItem();
        if (stack.getItem().isEdible() && !isCookedFood(stack)) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
            if (player.level.random.nextFloat() < 0.1f) {
                increase(player, 10);
            }
        }
    }

    @Override
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        World world = (World) event.getWorld();
        if (world.isClientSide()) return;
        PlayerEntity player = event.getPlayer();
        if (world.random.nextFloat() < (0.01f / 3f) && player instanceof ServerPlayerEntity) {
            increase((ServerPlayerEntity) player, 5);
        }
    }

    private static boolean isCookedFood(ItemStack stack) {
        return COOKED_FOODS.contains(stack.getItem());
    }

    private static void increase(ServerPlayerEntity player, int amount) {
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            int current = stats.getVirus();
            int value = Math.min(100, current + amount);
            if (value != current) {
                stats.setVirus(value);
                // Оптимизация: SyncAllStatsPacket вместо отдельного пакета
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncAllStatsPacket(stats)
                );
            }
        });
    }
}
