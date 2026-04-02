package com.example.examplemod;

import com.example.examplemod.item.HotRoastedOreItem;
import com.example.examplemod.item.MetalChunkItem;
import com.example.examplemod.item.RoastedOreItem;
import com.example.examplemod.item.SpongeMetalItem;
import com.example.examplemod.item.WetItemData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommonModEvents {
    private static final Map<World, Map<BlockPos, Long>> WET_PLACED_BLOCKS = new HashMap<>();

    private CommonModEvents() {
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        // Временно убрали регистрацию генерации мира для диагностики
        event.enqueueWork(() -> {
            // WorldGenRegistry.register();
            // ModBiomes.setupBiomes();
        });
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level.isClientSide) return;

        PlayerEntity player = event.player;

        // Проверяем горячие предметы в курсоре и инвентаре,
        // чтобы урон шёл даже при перетаскивании.
        if (applyHotItemDamage(player, player.inventory.getCarried())) {
            return;
        }

        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
            ItemStack stack = player.inventory.getItem(i);
            if (applyHotItemDamage(player, stack)) {
                break; // Наносим урон только один раз за тик, даже если несколько горячих предметов
            }
        }

        applyWetState(player);
        applyLowHealthStarvationDamage(player);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getWorld().isClientSide) {
            return;
        }

        PlayerEntity player = event.getPlayer();
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }

        double reach = player.getAttribute(ForgeMod.REACH_DISTANCE.get()) != null
                ? player.getAttributeValue(ForgeMod.REACH_DISTANCE.get())
                : 5.0D;

        RayTraceResult rayTraceResult = player.pick(reach, 0.0F, true);
        if (rayTraceResult.getType() != RayTraceResult.Type.BLOCK) {
            return;
        }

        BlockPos pos = ((BlockRayTraceResult) rayTraceResult).getBlockPos();
        if (!event.getWorld().getFluidState(pos).is(FluidTags.WATER)) {
            return;
        }

        WetItemData.markWet(stack, event.getWorld().getGameTime());
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isClientSide) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }

        if (event.getWorld().getFluidState(event.getPos()).is(FluidTags.WATER)) {
            WetItemData.markWet(stack, event.getWorld().getGameTime());
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.getPlayer().getMainHandItem().isEmpty() && event.getState().is(BlockTags.LOGS)) {
            event.setNewSpeed(0.0F);
        }
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.getWorld().isClientSide()) {
            return;
        }
        if (!(event.getWorld() instanceof ServerWorld)) {
            return;
        }
        if (!(event.getEntity() instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) event.getEntity();
        BlockState placedState = event.getPlacedBlock();
        Block placedBlock = placedState.getBlock();
        long gameTime = player.level.getGameTime();

        for (Hand hand : Hand.values()) {
            ItemStack stackInHand = player.getItemInHand(hand);
            if (stackInHand.isEmpty() || !(stackInHand.getItem() instanceof BlockItem)) {
                continue;
            }

            BlockItem blockItem = (BlockItem) stackInHand.getItem();
            if (blockItem.getBlock() != placedBlock) {
                continue;
            }

            long wetUntil = WetItemData.getWetUntil(stackInHand);
            if (wetUntil > gameTime) {
                markPlacedBlockWet((ServerWorld) event.getWorld(), event.getPos(), wetUntil);
            }
            return;
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isClientSide()) {
            return;
        }
        if (!(event.world instanceof ServerWorld)) {
            return;
        }

        ServerWorld world = (ServerWorld) event.world;
        Map<BlockPos, Long> wetBlocks = WET_PLACED_BLOCKS.get(world);
        if (wetBlocks == null || wetBlocks.isEmpty()) {
            return;
        }

        long gameTime = world.getGameTime();
        Iterator<Map.Entry<BlockPos, Long>> iterator = wetBlocks.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<BlockPos, Long> entry = iterator.next();
            BlockPos pos = entry.getKey();
            long wetUntil = entry.getValue();

            if (wetUntil <= gameTime || world.isEmptyBlock(pos)) {
                iterator.remove();
                continue;
            }

            if (gameTime % 5L == 0L && world.random.nextFloat() < 0.65F) {
                spawnWetDrips(world, pos);
            }
        }

        if (wetBlocks.isEmpty()) {
            WET_PLACED_BLOCKS.remove(world);
        }
    }

    @SubscribeEvent
    public static void onServerStopping(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        MinecraftServer server = net.minecraftforge.fml.server.ServerLifecycleHooks.getCurrentServer();
        if (server != null && !server.isRunning()) {
            WET_PLACED_BLOCKS.clear();
        }
    }

    private static void applyWetState(PlayerEntity player) {
        long gameTime = player.level.getGameTime();

        if (gameTime % 20 == 0 && (player.isInWaterRainOrBubble() || player.isInWater())) {
            WetItemData.markWet(player.getMainHandItem(), gameTime);
            WetItemData.markWet(player.getOffhandItem(), gameTime);
        }

        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
            WetItemData.isWet(player.inventory.getItem(i), gameTime);
        }

        WetItemData.isWet(player.inventory.getCarried(), gameTime);
    }

    private static void applyLowHealthStarvationDamage(PlayerEntity player) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        if (player.level.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        if (player.getFoodData().getFoodLevel() > 0) {
            return;
        }

        // В обычном Minecraft на NORMAL starvation-урон останавливается на 5 сердцах (10 HP).
        // Доприменяем starvation-урон ниже этого порога, чтобы голод продолжал наносить урон.
        if (player.getHealth() <= 10.0F && player.tickCount % 80 == 0) {
            player.hurt(DamageSource.STARVE, 1.0F);
        }
    }

    private static boolean applyHotItemDamage(PlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() instanceof SpongeMetalItem) {
            if (SpongeMetalItem.getState(stack) == SpongeMetalItem.STATE_HOT) {
                player.hurt(DamageSource.HOT_FLOOR, 1.0F);
                return true;
            }
            return false;
        }
        if (stack.getItem() instanceof RoastedOreItem) {
            if (RoastedOreItem.getState(stack) == RoastedOreItem.STATE_HOT) {
                player.hurt(DamageSource.ON_FIRE, 2.0F);
                return true;
            }
            return false;
        }
        if (stack.getItem() instanceof HotRoastedOreItem) {
            if (HotRoastedOreItem.getState(stack) == HotRoastedOreItem.STATE_HOT) {
                player.hurt(DamageSource.ON_FIRE, 2.0F);
                return true;
            }
            return false;
        }
        if (stack.getItem() instanceof MetalChunkItem) {
            if (MetalChunkItem.getTemperature(stack) == MetalChunkItem.TEMP_HOT) {
                player.hurt(DamageSource.HOT_FLOOR, 1.0F);
                return true;
            }
            return false;
        }
        return false;
    }

    private static void markPlacedBlockWet(ServerWorld world, BlockPos pos, long wetUntil) {
        Map<BlockPos, Long> wetBlocks = WET_PLACED_BLOCKS.computeIfAbsent(world, ignored -> new HashMap<>());
        wetBlocks.put(pos.immutable(), wetUntil);
        spawnWetDrips(world, pos);
    }

    private static void spawnWetDrips(ServerWorld world, BlockPos pos) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.98D;
        double z = pos.getZ() + 0.5D;

        world.sendParticles(ParticleTypes.FALLING_WATER, x, y, z, 1, 0.25D, 0.05D, 0.25D, 0.0D);
        world.sendParticles(ParticleTypes.DRIPPING_WATER, x, y, z, 2, 0.35D, 0.0D, 0.35D, 0.0D);
    }
}
