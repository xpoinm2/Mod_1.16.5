package com.example.examplemod.server;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PechugaStructureHandler {
    public static void onUse(PlayerInteractEvent.RightClickBlock event) {
        net.minecraft.item.ItemStack stack = event.getItemStack();
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

    private static boolean isFirepit(BlockState state) {
        return state.getBlock() == ModBlocks.FIREPIT_BLOCK.get();
    }

    /**
     * Проверяет, является ли структура валидной Печугой.
     * Структура: полый куб 6x6x3 из кирпичных блоков с футеровкой,
     * в центре на уровне y=1 должно быть кострище из мода (FIREPIT_BLOCK) размером 4x4.
     * Внутри должна быть пустота (воздух).
     */
    private static boolean isPechuga(World world, BlockPos start) {
        // Проверяем, что кострище 4x4 находится в центре структуры 6x6 на y=1
        // Кострище должно быть на позициях от (1,1,1) до (4,1,4) относительно start
        BlockPos firepitStart = start.offset(1, 1, 1);
        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                BlockPos firepitPos = firepitStart.offset(x, 0, z);
                if (!isFirepit(world.getBlockState(firepitPos))) {
                    return false;
                }
            }
        }

        // Проверяем структуру 6x6x3
        int brickBlocks = 0;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 6; x++) {
                for (int z = 0; z < 6; z++) {
                    BlockPos pos = start.offset(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    // Пропускаем область кострища (4x4 в центре на y=1)
                    if (y == 1 && x >= 1 && x < 5 && z >= 1 && z < 5) {
                        continue; // Кострище уже проверено
                    }

                    // Проверяем стены (границы структуры)
                    boolean isWall = (x == 0 || x == 5 || z == 0 || z == 5);
                    
                    if (isWall) {
                        // Стены должны быть из кирпичных блоков или воздухом (отверстия)
                        if (isBrickBlockWithLining(state)) {
                            brickBlocks++;
                        } else if (!world.isEmptyBlock(pos)) {
                            // Стена должна быть кирпичной или воздухом
                            return false;
                        }
                    } else {
                        // Внутренние блоки должны быть воздухом (полая структура)
                        if (!world.isEmptyBlock(pos)) {
                            return false;
                        }
                    }
                }
            }
        }

        // Проверяем условия: достаточно кирпичных блоков для стен
        // Стены 6x6x3 = периметр * высота = (6*4 - 4 угла) * 3 = 20 * 3 = 60 блоков максимум
        // Но с отверстиями будет меньше, поэтому проверяем минимум
        return brickBlocks >= 30;
    }

    private static void activate(World world, BlockPos start, PlayerEntity player, Hand hand) {
        // Заменяем структуру на блоки Печуги
        // Заменяем только стены (границы), внутреннее пространство остается пустым
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 6; x++) {
                for (int z = 0; z < 6; z++) {
                    BlockPos pos = start.offset(x, y, z);
                    BlockState currentState = world.getBlockState(pos);

                    // Пропускаем кострище и воздушные блоки
                    if (isFirepit(currentState) || world.isEmptyBlock(pos)) {
                        continue;
                    }

                    // Заменяем только стены (границы) из кирпичных блоков на блоки Печуги
                    boolean isWall = (x == 0 || x == 5 || z == 0 || z == 5);
                    if (isWall && isBrickBlockWithLining(currentState)) {
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

