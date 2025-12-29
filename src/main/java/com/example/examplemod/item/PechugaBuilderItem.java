package com.example.examplemod.item;

import com.example.examplemod.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PechugaBuilderItem extends Item {
    public PechugaBuilderItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }

        PlayerEntity player = context.getPlayer();
        if (player == null) {
            return ActionResultType.FAIL;
        }

        BlockPos clickedPos = context.getClickedPos();
        BlockPos startPos = clickedPos.offset(-2, 0, -2); // Центрируем структуру относительно клика

        // Проверяем, что место свободно
        if (!canBuild(world, startPos)) {
            return ActionResultType.FAIL;
        }

        // Строим структуру
        buildPechuga(world, startPos);

        world.playSound(null, startPos.offset(3, 1, 3), SoundEvents.ANVIL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return ActionResultType.SUCCESS;
    }

    private boolean canBuild(World world, BlockPos start) {
        // Проверяем, что место свободно для постройки 6x6x3
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 6; x++) {
                for (int z = 0; z < 6; z++) {
                    BlockPos pos = start.offset(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    
                    // Пропускаем воздушные блоки и блоки, которые можно заменить
                    if (!world.isEmptyBlock(pos) && !state.getMaterial().isReplaceable()) {
                        // Исключение: центральный блок на y=1 должен быть воздухом для кострища
                        if (x == 2 && z == 2 && y == 1) {
                            if (!world.isEmptyBlock(pos)) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void buildPechuga(World world, BlockPos start) {
        // Строим структуру 6x6x3
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 6; x++) {
                for (int z = 0; z < 6; z++) {
                    BlockPos pos = start.offset(x, y, z);
                    
                    // Центральный блок на y=1 - кострище
                    if (x == 2 && z == 2 && y == 1) {
                        world.setBlock(pos, Blocks.CAMPFIRE.defaultBlockState(), 3);
                        continue;
                    }

                    // Стены (границы 6x6)
                    boolean isWall = (x == 0 || x == 5 || z == 0 || z == 5);
                    
                    if (isWall) {
                        // На одной стороне (z=0) делаем отверстие 2 блока высотой
                        if (z == 0 && y < 2 && (x == 2 || x == 3)) {
                            // Оставляем воздух для входа
                            continue;
                        }
                        // Строим стену
                        world.setBlock(pos, ModBlocks.BRICK_BLOCK_WITH_LINING.get().defaultBlockState(), 3);
                    } else {
                        // Внутренние блоки - кирпичные блоки
                        if (y < 2) {
                            world.setBlock(pos, ModBlocks.BRICK_BLOCK_WITH_LINING.get().defaultBlockState(), 3);
                        } else {
                            // На верхнем уровне делаем одно отверстие для дыма (в центре)
                            if (x == 3 && z == 3) {
                                // Оставляем воздух для дыма
                                continue;
                            }
                            world.setBlock(pos, ModBlocks.BRICK_BLOCK_WITH_LINING.get().defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }
}

