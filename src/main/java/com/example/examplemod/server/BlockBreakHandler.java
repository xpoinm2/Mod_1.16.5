// === FILE src/main/java/com/example/examplemod/BlockBreakHandler.java
package com.example.examplemod.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockBreakHandler {

    private static final String KEY_FATIGUE = "fatigue";
    private static final String KEY_THIRST = "thirst";
    private static final Set<Block> VANILLA_ORES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            Blocks.COAL_ORE,
            Blocks.IRON_ORE,
            Blocks.GOLD_ORE,
            Blocks.REDSTONE_ORE,
            Blocks.LAPIS_ORE,
            Blocks.DIAMOND_ORE,
            Blocks.EMERALD_ORE,
            Blocks.NETHER_QUARTZ_ORE,
            Blocks.NETHER_GOLD_ORE,
            Blocks.ANCIENT_DEBRIS
    )));

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
        if (player == null) {
            return;
        }

        // 2) Получаем состояние блока
        BlockState state = event.getState();

        if (isOreBlock(state.getBlock()) && !canMineOre(player)) {
            event.setCanceled(true);
            return;
        }

        // Replace iron ore drop with impure iron ore block
        if (state.getBlock() == Blocks.IRON_ORE) {
            event.setCanceled(true);
            world.destroyBlock(event.getPos(), false);
            Block.popResource(world, event.getPos(), new ItemStack(ModBlocks.IMPURE_IRON_ORE.get()));
            return;
        }

        // Replace gold ore drop with unrefined gold ore
        if (state.getBlock() == Blocks.GOLD_ORE) {
            event.setCanceled(true);
            world.destroyBlock(event.getPos(), false);
            Block.popResource(world, event.getPos(), new ItemStack(ModItems.UNREFINED_GOLD_ORE.get()));
            return;
        }

        // Replace tin ore drop with unrefined tin ore
        if (state.getBlock() == ModBlocks.TIN_ORE.get()) {
            event.setCanceled(true);
            world.destroyBlock(event.getPos(), false);
            Block.popResource(world, event.getPos(), new ItemStack(ModItems.UNREFINED_TIN_ORE.get()));
            return;
        }

        if (state.getBlock() == ModBlocks.IMPURE_IRON_ORE.get()) {
            if (tryCrushOreWithHammer(world, event, player, ModItems.IRON_ORE_GRAVEL.get())) {
                return;
            }
            event.setCanceled(true);
            world.destroyBlock(event.getPos(), false);
            Block.popResource(world, event.getPos(), new ItemStack(ModBlocks.IMPURE_IRON_ORE.get()));
            return;
        }

        if (state.getBlock() == ModBlocks.UNREFINED_TIN_ORE.get()) {
            if (tryCrushOreWithHammer(world, event, player, ModItems.TIN_ORE_GRAVEL.get())) {
                return;
            }
        }

        if (state.getBlock() == ModBlocks.UNREFINED_GOLD_ORE.get()) {
            if (tryCrushOreWithHammer(world, event, player, ModItems.GOLD_ORE_GRAVEL.get())) {
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

    private static boolean tryCrushOreWithHammer(World world, BreakEvent event, PlayerEntity player, Item gravelItem) {
        ItemStack held = player.getMainHandItem();
        if (held.getItem() != ModItems.STONE_HAMMER.get() && held.getItem() != ModItems.BONE_HAMMER.get()) {
            return false;
        }
        event.setCanceled(true);
        world.destroyBlock(event.getPos(), false);
        dropOreGravelAndSlag(world, event.getPos(), gravelItem);
        held.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(Hand.MAIN_HAND));
        return true;
    }

    private static void dropOreGravelAndSlag(World world, BlockPos pos, Item gravelItem) {
        int gravelCount = world.random.nextBoolean() ? 1 : 2;
        int slagCount = world.random.nextBoolean() ? 1 : 2;
        Block.popResource(world, pos, new ItemStack(gravelItem, gravelCount));
        Block.popResource(world, pos, new ItemStack(ModItems.SLAG.get(), slagCount));
    }

    private static boolean isOreBlock(Block block) {
        return VANILLA_ORES.contains(block) || isModOre(block);
    }

    private static boolean isModOre(Block block) {
        return block == ModBlocks.IMPURE_IRON_ORE.get()
                || block == ModBlocks.PYRITE.get()
                || block == ModBlocks.TIN_GRAVEL_ORE.get()
                || block == ModBlocks.TIN_ORE.get()
                || block == ModBlocks.GOLD_GRAVEL_ORE.get()
                || block == ModBlocks.UNREFINED_TIN_ORE.get()
                || block == ModBlocks.UNREFINED_GOLD_ORE.get();
    }

    private static boolean canMineOre(PlayerEntity player) {
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) {
            return false;
        }
        Item item = held.getItem();
        if (item == ModItems.STONE_HAMMER.get() || item == ModItems.BONE_HAMMER.get()) {
            return true;
        }
        if (item instanceof TieredItem) {
            TieredItem tiered = (TieredItem) item;
            return tiered.getTier().getLevel() >= 1;
        }
        return false;
    }
}
