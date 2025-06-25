// === FILE src/main/java/com/example/examplemod/BlockBreakHandler.java
package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncStatsPacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockBreakHandler {

    private static final String KEY_FATIGUE = "fatigue";
    private static final String KEY_THIRST = "thirst";

    private static CompoundNBT getStatsTag(PlayerEntity player) {
        CompoundNBT root = player.getPersistentData();
        if (!root.contains(PlayerEntity.PERSISTED_NBT_TAG)) {
            root.put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
        }
        return root.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
    }

    private static int getStat(PlayerEntity player, String key, int def) {
        CompoundNBT stats = getStatsTag(player);
        if (!stats.contains(key)) {
            stats.putInt(key, def);
        }
        return stats.getInt(key);
    }

    private static void setStat(PlayerEntity player, String key, int value) {
        getStatsTag(player).putInt(key, value);
    }


    @SubscribeEvent
    public static void onBlockBreak(BreakEvent event) {
        // 1) Получаем мир и проверяем — это сервер?
        World world = (World) event.getWorld();
        if (world.isClientSide()) {
            return;
        }

        PlayerEntity player = (PlayerEntity) event.getPlayer();

        // 2) Получаем состояние блока
        BlockState state = event.getState();
        // 3) Если это лог и в руке ничего нет — отменяем ломание
        if (state.is(BlockTags.LOGS)) {
            ItemStack held = player.getItemInHand(Hand.MAIN_HAND);
            if (held.isEmpty()) {
                event.setCanceled(true);
            }
        }

        // 3.1) Если это камень/булыжник и в руке ничего нет — отменяем ломание
        if (player.getMainHandItem().isEmpty() && (
                state.is(BlockTags.BASE_STONE_OVERWORLD) ||
                        state.is(BlockTags.BASE_STONE_NETHER) ||
                        state.is(Tags.Blocks.COBBLESTONE) ||
                        state.is(Tags.Blocks.STONE)
        )) {
            event.setCanceled(true);
        }

        // 3.2) Если это листва — с шансом 50% дропаем палку
        if (state.is(BlockTags.LEAVES)) {
            if (world.random.nextFloat() < 0.5f) {
                Block.popResource(world, event.getPos(), new ItemStack(Items.STICK));
            }
        }


        // Fatigue when digging with bare hands
        if (player.getMainHandItem().isEmpty()) {
            int fatigue = Math.min(100, getStat(player, KEY_FATIGUE, 0) + 4);
            setStat(player, KEY_FATIGUE, fatigue);
            if (player instanceof ServerPlayerEntity) {
                ServerPlayerEntity sp = (ServerPlayerEntity) player;
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> sp),
                        new SyncStatsPacket(getStat(sp, KEY_THIRST, 40), fatigue)
                );
            }
        }
    }
}
