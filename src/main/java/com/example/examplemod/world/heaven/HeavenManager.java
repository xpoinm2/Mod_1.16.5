package com.example.examplemod.world.heaven;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralised manager for all logic related to the Heaven dimension.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HeavenManager {

    private HeavenManager() {
    }

    public static final ResourceLocation HEAVEN_ID = new ResourceLocation(ExampleMod.MODID, "heaven");
    public static final RegistryKey<World> HEAVEN_WORLD_KEY = RegistryKey.create(Registry.DIMENSION_REGISTRY, HEAVEN_ID);

    private static final BlockPos ARRIVAL_CENTER = new BlockPos(0, 3, 0);
    private static final int FENCE_HALF_SIZE = 200; // Creates a 400x400 square perimeter

    private static final Map<UUID, ReturnLocation> RETURN_LOCATIONS = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> PENDING_HEAVEN_RESPAWN = new ConcurrentHashMap<>();
    private static final Map<UUID, ArrivalReason> ARRIVAL_REASONS = new ConcurrentHashMap<>();

    public static void storeReturnLocation(ServerPlayerEntity player, Vector3d pos, RegistryKey<World> dimension) {
        RETURN_LOCATIONS.put(player.getUUID(), new ReturnLocation(pos, player.yRot, player.xRot, dimension));
    }

    public static void markForHeavenRespawn(ServerPlayerEntity player) {
        PENDING_HEAVEN_RESPAWN.put(player.getUUID(), Boolean.TRUE);
        ARRIVAL_REASONS.put(player.getUUID(), ArrivalReason.DEATH);
    }

    public static void teleportToHeaven(ServerPlayerEntity player) {
        ServerWorld heaven = player.server.getLevel(HEAVEN_WORLD_KEY);
        if (heaven == null) {
            player.sendMessage(new StringTextComponent("Heaven dimension is not available."), player.getUUID());
            return;
        }

        prepareArrivalArea(heaven, ARRIVAL_CENTER);

        player.fallDistance = 0.0f;
        player.teleportTo(heaven, ARRIVAL_CENTER.getX() + 0.5, ARRIVAL_CENTER.getY(), ARRIVAL_CENTER.getZ() + 0.5,
                player.yRot, player.xRot);
    }

    public static void handleDoorOpened(ServerPlayerEntity player, ServerWorld world, BlockPos doorPos) {
        if (!world.dimension().equals(HEAVEN_WORLD_KEY)) {
            return;
        }

        ReturnLocation target = RETURN_LOCATIONS.remove(player.getUUID());
        ArrivalReason reason = ARRIVAL_REASONS.remove(player.getUUID());
        ServerWorld destination = player.server.getLevel(World.OVERWORLD);
        if (destination == null) {
            destination = player.server.overworld();
        }

        double targetX = 0.0;
        double targetY = 0.0;
        double targetZ = 0.0;
        float targetYaw = player.yRot;
        float targetPitch = player.xRot;

        if (reason == ArrivalReason.DEATH && target != null && destination != null
                && World.OVERWORLD.equals(target.dimension)) {
            targetX = target.position.x;
            targetY = target.position.y;
            targetZ = target.position.z;
            targetYaw = target.yaw;
            targetPitch = target.pitch;
        } else if (reason == ArrivalReason.TICKET) {
            // Defaults already set to origin.
        } else if (destination != null) {
            BlockPos spawn = destination.getSharedSpawnPos();
            targetX = spawn.getX() + 0.5;
            targetY = spawn.getY() + 0.1;
            targetZ = spawn.getZ() + 0.5;
        }

        if (destination != null) {
            player.fallDistance = 0.0f;
            player.teleportTo(destination, targetX, targetY, targetZ, targetYaw, targetPitch);
        }

        BlockState state = world.getBlockState(doorPos);
        if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.OPEN)) {
            world.setBlock(doorPos, state.setValue(DoorBlock.OPEN, Boolean.FALSE), 2);
            BlockPos above = doorPos.above();
            BlockState aboveState = world.getBlockState(above);
            if (aboveState.getBlock() instanceof DoorBlock) {
                world.setBlock(above, aboveState.setValue(DoorBlock.OPEN, Boolean.FALSE), 2);
            }
        }
    }

    public static void rememberTicketLocation(ServerPlayerEntity player) {
        ARRIVAL_REASONS.put(player.getUUID(), ArrivalReason.TICKET);
        storeReturnLocation(player, player.position(), player.getLevel().dimension());
    }

    private static void prepareArrivalArea(ServerWorld world, BlockPos center) {
        world.getChunk(center.getX() >> 4, center.getZ() >> 4);
        BlockPos ground = center.below();
        BlockState paradise = ModBlocks.PARADISE_BLOCK.get().defaultBlockState();
        world.setBlock(ground, paradise, 3);

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos basePos = ground.offset(dx, 0, dz);
                world.setBlock(basePos, paradise, 3);
                for (int dy = 1; dy <= 5; dy++) {
                    BlockPos airPos = basePos.above(dy);
                    world.setBlock(airPos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        buildFencePerimeter(world, ground);
    }

    private static void buildFencePerimeter(ServerWorld world, BlockPos groundCenter) {
        BlockState fence = ModBlocks.PARADISE_FENCE.get().defaultBlockState();
        int minX = groundCenter.getX() - FENCE_HALF_SIZE;
        int maxX = groundCenter.getX() + FENCE_HALF_SIZE - 1;
        int minZ = groundCenter.getZ() - FENCE_HALF_SIZE;
        int maxZ = groundCenter.getZ() + FENCE_HALF_SIZE - 1;

        for (int x = minX; x <= maxX; x++) {
            placeFence(world, new BlockPos(x, groundCenter.getY() + 1, minZ), fence);
            placeFence(world, new BlockPos(x, groundCenter.getY() + 1, maxZ), fence);
        }

        for (int z = minZ; z <= maxZ; z++) {
            placeFence(world, new BlockPos(minX, groundCenter.getY() + 1, z), fence);
            placeFence(world, new BlockPos(maxX, groundCenter.getY() + 1, z), fence);
        }

        placeRandomDoor(world, groundCenter, minX, maxX, minZ, maxZ);
    }

    private static void placeFence(ServerWorld world, BlockPos pos, BlockState fence) {
        world.setBlock(pos, fence, 3);
        BlockState above = world.getBlockState(pos.above());
        if (!(above.getBlock() instanceof WallBlock)) {
            world.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private static void placeRandomDoor(ServerWorld world, BlockPos groundCenter, int minX, int maxX, int minZ, int maxZ) {
        Random random = world.random;
        int side = random.nextInt(4);
        int x = groundCenter.getX();
        int z = groundCenter.getZ();
        Direction facing;

        switch (side) {
            case 0:
                z = minZ;
                x = randomBetween(random, minX + 2, maxX - 2);
                facing = Direction.NORTH;
                break;
            case 1:
                z = maxZ;
                x = randomBetween(random, minX + 2, maxX - 2);
                facing = Direction.SOUTH;
                break;
            case 2:
                x = minX;
                z = randomBetween(random, minZ + 2, maxZ - 2);
                facing = Direction.WEST;
                break;
            default:
                x = maxX;
                z = randomBetween(random, minZ + 2, maxZ - 2);
                facing = Direction.EAST;
                break;
        }

        BlockPos doorBase = new BlockPos(x, groundCenter.getY() + 1, z);
        clearForDoor(world, doorBase, facing);

        BlockState lower = ModBlocks.PARADISE_DOOR.get().defaultBlockState()
                .setValue(DoorBlock.FACING, facing)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT)
                .setValue(DoorBlock.OPEN, Boolean.FALSE)
                .setValue(DoorBlock.POWERED, Boolean.FALSE);

        BlockState upper = lower.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);

        world.setBlock(doorBase, lower, 3);
        world.setBlock(doorBase.above(), upper, 3);
    }

    private static void clearForDoor(ServerWorld world, BlockPos doorBase, Direction facing) {
        world.setBlock(doorBase, Blocks.AIR.defaultBlockState(), 3);
        world.setBlock(doorBase.above(), Blocks.AIR.defaultBlockState(), 3);

        BlockPos left = doorBase.relative(facing.getClockWise());
        BlockPos right = doorBase.relative(facing.getCounterClockWise());
        BlockState fence = ModBlocks.PARADISE_FENCE.get().defaultBlockState();
        world.setBlock(left, fence, 3);
        world.setBlock(right, fence, 3);

        BlockPos outside = doorBase.relative(facing);
        BlockPos inside = doorBase.relative(facing.getOpposite());
        world.setBlock(outside, Blocks.AIR.defaultBlockState(), 3);
        world.setBlock(inside, Blocks.AIR.defaultBlockState(), 3);
    }

    private static int randomBetween(Random random, int min, int max) {
        if (min >= max) {
            return min;
        }
        return random.nextInt(max - min + 1) + min;
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof ServerPlayerEntity)) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
        storeReturnLocation(player, player.position(), player.getLevel().dimension());
        markForHeavenRespawn(player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayerEntity)) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        if (Boolean.TRUE.equals(PENDING_HEAVEN_RESPAWN.remove(player.getUUID()))) {
            teleportToHeaven(player);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getWorld() instanceof World)) {
            return;
        }
        World world = (World) event.getWorld();
        if (world.dimension().equals(HEAVEN_WORLD_KEY)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        if (!player.level.dimension().equals(HEAVEN_WORLD_KEY)) {
            return;
        }

        player.setDeltaMovement(player.getDeltaMovement().x, 0.0, player.getDeltaMovement().z);
        player.hasImpulse = true;
    }

    private static final class ReturnLocation {
        private final Vector3d position;
        private final float yaw;
        private final float pitch;
        private final RegistryKey<World> dimension;

        private ReturnLocation(Vector3d position, float yaw, float pitch, RegistryKey<World> dimension) {
            this.position = position;
            this.yaw = yaw;
            this.pitch = pitch;
            this.dimension = dimension;
        }
    }

    private enum ArrivalReason {
        DEATH,
        TICKET
    }
}
