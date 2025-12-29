package com.example.examplemod.item;

import com.example.examplemod.ModBlocks;
import net.minecraft.block.BlockState;
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
                        // Исключение: область кострища на y=0 должна быть воздухом
                        if (y == 0 && x >= 1 && x < 5 && z >= 1 && z < 5) {
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
        // Строим полую структуру 6x6x3 вокруг кострища
        // Кострище - это мультиблок 4x4, размещаем его в центре структуры 6x6
        // Кострище должно быть на позициях от (1,0,1) до (4,0,4) относительно start
        BlockPos firepitStart = start.offset(1, 0, 1);
        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                BlockPos firepitPos = firepitStart.offset(x, 0, z);
                // Устанавливаем активированные блоки кострища
                world.setBlock(firepitPos, ModBlocks.FIREPIT_BLOCK.get().defaultBlockState()
                        .setValue(ModBlocks.FirepitBlock.X, x)
                        .setValue(ModBlocks.FirepitBlock.Z, z), 3);
            }
        }
        
        // Теперь строим стены Печуги вокруг кострища
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 6; x++) {
                for (int z = 0; z < 6; z++) {
                    BlockPos pos = start.offset(x, y, z);
                    
                    // Пропускаем область кострища (4x4 в центре на y=0)
                    if (y == 0 && x >= 1 && x < 5 && z >= 1 && z < 5) {
                        continue; // Кострище уже построено
                    }

                    // Стены (границы 6x6) - только границы структуры
                    boolean isWall = (x == 0 || x == 5 || z == 0 || z == 5);
                    
                    if (isWall) {
                        // На одной стороне (z=0) делаем отверстие 2 блока шириной (x=2 и x=3) и 1 блок высотой (y=1) для входа
                        if (z == 0 && y == 1 && (x == 2 || x == 3)) {
                            // Оставляем воздух для входа (ширина 2, высота 1, на уровне y=1)
                            continue;
                        }
                        // На верхнем уровне делаем одно отверстие для дыма
                        if (y == 2 && (x == 2 || x == 3) && (z == 2 || z == 3)) {
                            // Оставляем воздух для дыма (но только одно отверстие)
                            if (x == 2 && z == 2) {
                                continue;
                            }
                        }
                        // Строим стену из обычных кирпичных блоков (активация будет по ПКМ молотом)
                        world.setBlock(pos, ModBlocks.BRICK_BLOCK_WITH_LINING.get().defaultBlockState(), 3);
                    } else {
                        // Внутренние блоки остаются воздухом (полая структура)
                        // Ничего не делаем
                    }
                }
            }
        }
    }
}

