// === FILE src/main/java/com/example/examplemod/BlockBreakHandler.java
package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncStatsPacket;
import com.example.examplemod.ModItems;
import com.example.examplemod.ModBlocks;
import net.minecraft.util.Hand;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
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

        // Replace iron ore drop with impure iron ore block
        if (state.getBlock() == Blocks.IRON_ORE) {
            event.setCanceled(true);
            world.destroyBlock(event.getPos(), false);
            Block.popResource(world, event.getPos(), new ItemStack(ModBlocks.IMPURE_IRON_ORE.get()));
            return;
        }

        // Crushing impure iron ore with hammers
        if (state.getBlock() == ModBlocks.IMPURE_IRON_ORE.get()) {
            ItemStack held = player.getMainHandItem();
            if (held.getItem() == ModItems.STONE_HAMMER.get() || held.getItem() == ModItems.BONE_HAMMER.get()) {
                event.setCanceled(true);
                world.destroyBlock(event.getPos(), false);
                Block.popResource(world, event.getPos(), new ItemStack(ModItems.IRON_CLUSTER.get()));
                held.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(Hand.MAIN_HAND));
                return;
            }
        }

        // 3.2) Если это листва — дропаем 3 листочка и с шансом 50% ветку
        if (state.is(BlockTags.LEAVES)) {
            Block.popResource(world, event.getPos(), new ItemStack(ModItems.LEAF.get(), 3));
            if (world.random.nextFloat() < 0.5f) {
                Block.popResource(world, event.getPos(), new ItemStack(ModItems.BRANCH.get()));
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
