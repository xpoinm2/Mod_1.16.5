package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class FirepitStructureHandler {
    public static void onUse(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() != ModItems.PYRITE_FLINT.get()) return;

        World world = event.getWorld();
        if (world.isClientSide) return;

        BlockPos clicked = event.getPos();
        for (int dx = -3; dx <= 0; dx++) {
            for (int dz = -3; dz <= 0; dz++) {
                BlockPos start = clicked.offset(dx, 0, dz);
                if (isFirepit(world, start)) {
                    activate(world, start, event.getPlayer(), event.getHand());
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    private static boolean isBrushwood(BlockState state) {
        return state.getBlock() == ModBlocks.BRUSHWOOD_SLAB.get()
                || state.getBlock() == ModBlocks.BURNED_BRUSHWOOD_SLAB.get();
    }

    private static boolean isWoodSlab(BlockState state) {
        return state.getBlock() instanceof SlabBlock && state.getMaterial() == Material.WOOD;
    }

    /**
     * Checks if the block state represents the cobblestone slab for the
     * firepit centre. The multiblock structure requires the cobblestone slab
     * specifically (not the stone slab).
     */
    private static boolean isCobblestoneSlab(BlockState state) {
        return state.getBlock() == ModBlocks.COBBLESTONE_SLAB.get();
    }

    private static boolean isFirepit(World world, BlockPos start) {
        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                BlockState st = world.getBlockState(start.offset(x, 0, z));
                boolean corner = (x == 0 || x == 3) && (z == 0 || z == 3);
                if (corner) {
                    if (!isBrushwood(st)) return false;
                } else if (x == 0 || x == 3 || z == 0 || z == 3) {
                    if (!isWoodSlab(st)) return false;
                } else {
                    if (!isCobblestoneSlab(st)) return false;
                }
            }
        }
        return true;
    }

    private static void activate(World world, BlockPos start, PlayerEntity player, Hand hand) {
        BlockPos base = start.offset(1, 0, 1);
        // Заменяем область 4x4 на блоки кострища с указанием позиции
        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                BlockPos pos = start.offset(x, 0, z);
                world.setBlock(pos, ModBlocks.FIREPIT_BLOCK.get().defaultBlockState()
                        .setValue(ModBlocks.FirepitBlock.X, x)
                        .setValue(ModBlocks.FirepitBlock.Z, z), 3);
            }
        }

        // Визуальный эффект огня в центре
        for (int x = 0; x < 2; x++) {
            for (int z = 0; z < 2; z++) {
                BlockPos firePos = base.offset(x, 1, z);
                if (world.isEmptyBlock(firePos)) {
                    world.addParticle(net.minecraft.particles.ParticleTypes.FLAME,
                            firePos.getX() + 0.5, firePos.getY() + 0.1,
                            firePos.getZ() + 0.5, 0.0D, 0.05D, 0.0D);
                }
            }
        }

        world.playSound(null, base, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.4F + 0.8F);
        if (!player.abilities.instabuild) {
            player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
        }
    }
}