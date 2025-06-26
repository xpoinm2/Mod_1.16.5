package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Hand;
import net.minecraftforge.common.Tags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncStatsPacket;

/**
 * Gives the player a sharp pebble when they left click any stone block.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PebbleSharpenHandler {

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
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        PlayerEntity player = event.getPlayer();
        World world = player.level;
        if (world.isClientSide()) return;

        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        if (!isStone(state)) return;

        ItemStack held = player.getItemInHand(event.getHand());
        if (held.getItem() != ModItems.PEBBLE.get()) return;

        int dmg = held.getDamageValue() + 1;
        held.setDamageValue(dmg);


        if (dmg >= held.getMaxDamage()) {
            player.setItemInHand(event.getHand(), new ItemStack(ModItems.SHARP_PEBBLE.get()));

            int fatigue = Math.min(100, getStat(player, KEY_FATIGUE, 0) + 5);
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

    private static boolean isStone(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD) ||
                state.is(BlockTags.BASE_STONE_NETHER) ||
                state.is(Tags.Blocks.COBBLESTONE) ||
                state.is(Tags.Blocks.STONE);
    }
}