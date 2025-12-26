package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncVirusPacket;
import com.example.examplemod.server.mechanics.IMechanicModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class VirusMechanic implements IMechanicModule {
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
        if (stack.getItem().isEdible()) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
            if (player.level.random.nextFloat() < 0.02f) {
                increase(player, 10);
            }
        }
    }

    @Override
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        World world = (World) event.getWorld();
        if (world.isClientSide()) return;
        PlayerEntity player = event.getPlayer();
        if (world.random.nextFloat() < 0.01f && player instanceof ServerPlayerEntity) {
            increase((ServerPlayerEntity) player, 5);
        }
    }

    private static void increase(ServerPlayerEntity player, int amount) {
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            int current = stats.getVirus();
            int value = Math.min(100, current + amount);
            if (value != current) {
                stats.setVirus(value);
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncVirusPacket(value)
                );
            }
        });
    }
}

