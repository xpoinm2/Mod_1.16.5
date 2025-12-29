package com.example.examplemod.server;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PechugaStructureHandler {
    public static void onUse(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() != ModItems.BONE_HAMMER.get() && stack.getItem() != ModItems.STONE_HAMMER.get()) {
            return;
        }

        World world = event.getWorld();
        if (world.isClientSide) return;

        BlockPos clicked = event.getPos();
        // Ищем структуру вокруг кликнутого блока
        // Структура 6x6x3, кострище должно быть в центре на уровне y=1
        for (int dx = -5; dx <= 0; dx++) {
            for (int dz = -5; dz <= 0; dz++) {
                BlockPos start = clicked.offset(dx, 0, dz);
                if (isPechuga(world, start)) {
                    activate(world, start, event.getPlayer(), event.getHand());
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    private static boolean isBrickBlockWithLining(BlockState state) {
        return state.getBlock() == ModBlocks.BRICK_BLOCK_WITH_LINING.get();
    }

    private static boolean isCampfire(BlockState state) {
        return state.getBlock() == Blocks.CAMPFIRE;
    }

    /**
     * Проверяет, является ли структура валидной Печугой.
     * Структура: куб 6x6x3 из кирпичных блоков с футеровкой,
     * в центре на уровне y=1 должно быть кострище.
     * На одной стороне должно быть отверстие 2 блока высотой.
     * Сверху должно быть 1 отверстие для дыма.
     */
    private static boolean isPechuga(World world, BlockPos start) {
        // Проверяем, что в центре на y=1 есть кострище
        BlockPos center = start.offset(2, 1, 2); // Центр 6x6 структуры
        if (!isCampfire(world.getBlockState(center))) {
            return false;
        }

        // Проверяем структуру 6x6x3
        int airBlocksOnTop = 0;
        int sideOpenings = 0;
        int brickBlocks = 0;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 6; x++) {
                for (int z = 0; z < 6; z++) {
                    BlockPos pos = start.offset(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    // Центральная позиция на y=1 должна быть кострищем
                    if (x == 2 && z == 2 && y == 1) {
                        if (!isCampfire(state)) {
                            return false;
                        }
                        continue;
                    }

                    // На верхнем уровне (y=2) считаем воздушные блоки
                    if (y == 2) {
                        if (world.isEmptyBlock(pos)) {
                            airBlocksOnTop++;
                        } else if (isBrickBlockWithLining(state)) {
                            brickBlocks++;
                        }
                    } else {
                        // На уровнях y=0 и y=1 проверяем стены
                        boolean isWall = (x == 0 || x == 5 || z == 0 || z == 5);
                        if (isWall) {
                            // Проверяем наличие отверстия на стороне (2 блока высотой)
                            if (world.isEmptyBlock(pos)) {
                                // Проверяем, что это часть отверстия 2 блока высотой
                                if (y == 0) {
                                    // Проверяем, что сверху тоже воздух
                                    BlockPos above = pos.above();
                                    if (world.isEmptyBlock(above)) {
                                        sideOpenings++;
                                    }
                                } else if (y == 1) {
                                    // Проверяем, что снизу тоже воздух
                                    BlockPos below = pos.below();
                                    if (world.isEmptyBlock(below)) {
                                        sideOpenings++;
                                    }
                                }
                            } else if (isBrickBlockWithLining(state)) {
                                brickBlocks++;
                            } else {
                                // Стена должна быть кирпичной или воздухом
                                return false;
                            }
                        } else {
                            // Внутренние блоки должны быть кирпичными или воздухом
                            if (isBrickBlockWithLining(state)) {
                                brickBlocks++;
                            } else if (!world.isEmptyBlock(pos)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        // Проверяем условия: 1 воздушный блок сверху, хотя бы одно отверстие с одной стороны, достаточно кирпичных блоков
        return airBlocksOnTop == 1 && sideOpenings >= 2 && brickBlocks >= 20;
    }

    private static void activate(World world, BlockPos start, PlayerEntity player, Hand hand) {
        // Заменяем структуру на блоки Печуги
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 6; x++) {
                for (int z = 0; z < 6; z++) {
                    BlockPos pos = start.offset(x, y, z);
                    BlockState currentState = world.getBlockState(pos);

                    // Пропускаем кострище и воздушные блоки
                    if (isCampfire(currentState) || world.isEmptyBlock(pos)) {
                        continue;
                    }

                    // Заменяем кирпичные блоки на блоки Печуги
                    if (isBrickBlockWithLining(currentState)) {
                        world.setBlock(pos, ModBlocks.PECHUGA_BLOCK.get().defaultBlockState()
                                .setValue(ModBlocks.PechugaBlock.X, x)
                                .setValue(ModBlocks.PechugaBlock.Y, y)
                                .setValue(ModBlocks.PechugaBlock.Z, z), 3);
                    }
                }
            }
        }

        world.playSound(null, start.offset(3, 1, 3), SoundEvents.ANVIL_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.4F + 0.8F);
        if (!player.abilities.instabuild) {
            player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
        }
    }
}

