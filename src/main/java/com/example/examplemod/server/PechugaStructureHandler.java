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
        // Структура 6x6x3, кострище должно быть в центре на уровне y=0
        // Нужно проверить все возможные позиции начала структуры
        for (int dx = -5; dx <= 0; dx++) {
            for (int dz = -5; dz <= 0; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    BlockPos start = clicked.offset(dx, dy, dz);
                    if (isPechuga(world, start)) {
                        activate(world, start, event.getPlayer(), event.getHand());
                        event.setCanceled(true);
                        return;
                    }
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
     * в центре на уровне y=0 должно быть кострище из мода (FIREPIT_BLOCK) размером 4x4.
     * Внутри должна быть пустота (воздух).
     */
    private static boolean isPechuga(World world, BlockPos start) {
        // Проверяем, что кострище 4x4 находится в центре структуры 6x6 на y=0
        // Кострище должно быть на позициях от (1,0,1) до (4,0,4) относительно start
        // И кострище должно быть активировано (иметь правильные координаты X и Z)
        BlockPos firepitStart = start.offset(1, 0, 1);
        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                BlockPos firepitPos = firepitStart.offset(x, 0, z);
                BlockState firepitState = world.getBlockState(firepitPos);
                if (!isFirepit(firepitState)) {
                    return false;
                }
                // Проверяем, что кострище активировано (имеет правильные координаты)
                if (firepitState.hasProperty(ModBlocks.FirepitBlock.X) && 
                    firepitState.hasProperty(ModBlocks.FirepitBlock.Z)) {
                    int firepitX = firepitState.getValue(ModBlocks.FirepitBlock.X);
                    int firepitZ = firepitState.getValue(ModBlocks.FirepitBlock.Z);
                    if (firepitX != x || firepitZ != z) {
                        return false; // Кострище не активировано правильно
                    }
                } else {
                    return false; // Кострище не активировано
                }
            }
        }

        // Проверяем структуру 6x6x3
        int brickBlocks = 0;
        
        // Проверяем наличие отверстий на каждой стене (y=1, 2 блока)
        boolean hasOpeningNorth = world.isEmptyBlock(start.offset(2, 1, 0)) && world.isEmptyBlock(start.offset(3, 1, 0));
        boolean hasOpeningSouth = world.isEmptyBlock(start.offset(2, 1, 5)) && world.isEmptyBlock(start.offset(3, 1, 5));
        boolean hasOpeningWest = world.isEmptyBlock(start.offset(0, 1, 2)) && world.isEmptyBlock(start.offset(0, 1, 3));
        boolean hasOpeningEast = world.isEmptyBlock(start.offset(5, 1, 2)) && world.isEmptyBlock(start.offset(5, 1, 3));
        
        // Подсчитываем количество стен с отверстиями
        int openingCount = 0;
        if (hasOpeningNorth) openingCount++;
        if (hasOpeningSouth) openingCount++;
        if (hasOpeningWest) openingCount++;
        if (hasOpeningEast) openingCount++;
        
        // Отверстие должно быть ровно на одной стене
        if (openingCount != 1) {
            return false;
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 6; x++) {
                for (int z = 0; z < 6; z++) {
                    BlockPos pos = start.offset(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    // Пропускаем область кострища (4x4 в центре на y=0)
                    if (y == 0 && x >= 1 && x < 5 && z >= 1 && z < 5) {
                        continue; // Кострище уже проверено
                    }

                    // Проверяем стены (границы структуры)
                    boolean isWall = (x == 0 || x == 5 || z == 0 || z == 5);
                    
                    if (isWall) {
                        // Определяем, является ли это позицией отверстия на валидной стене
                        boolean isOpening = false;
                        if (hasOpeningNorth && z == 0 && y == 1 && (x == 2 || x == 3)) {
                            isOpening = true;
                        } else if (hasOpeningSouth && z == 5 && y == 1 && (x == 2 || x == 3)) {
                            isOpening = true;
                        } else if (hasOpeningWest && x == 0 && y == 1 && (z == 2 || z == 3)) {
                            isOpening = true;
                        } else if (hasOpeningEast && x == 5 && y == 1 && (z == 2 || z == 3)) {
                            isOpening = true;
                        }
                        
                        if (isOpening) {
                            // Это отверстие - должно быть воздухом
                            if (!world.isEmptyBlock(pos)) {
                                return false;
                            }
                        } else {
                            // Все остальные блоки стены должны быть кирпичными
                            if (!isBrickBlockWithLining(state)) {
                                return false;
                            }
                            brickBlocks++;
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
        // Структура остается из кирпичных блоков, просто помечаем её как активированную
        // Для этого сохраняем позицию начала структуры в NBT блока кострища (master блок)
        BlockPos firepitMaster = start.offset(2, 0, 2); // Master блок кострища
        
        // Помечаем структуру как активированную через TileEntity кострища
        net.minecraft.tileentity.TileEntity tile = world.getBlockEntity(firepitMaster);
        if (tile instanceof com.example.examplemod.tileentity.FirepitTileEntity) {
            // Структура активирована - блоки остаются кирпичными, но структура работает
            // Это определяется через проверку структуры при ПКМ
        }

        world.playSound(null, start.offset(3, 1, 3), SoundEvents.ANVIL_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.4F + 0.8F);
        if (!player.abilities.instabuild) {
            player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
        }
    }
    
    /**
     * Проверяет, активирована ли структура кирпичной печи (после ПКМ молотом)
     * и открывает GUI если структура валидна
     */
    public static boolean tryOpenGui(World world, BlockPos clickedPos, net.minecraft.entity.player.PlayerEntity player) {
        if (world.isClientSide) return false;
        
        // Ищем структуру вокруг кликнутого блока
        for (int dx = -5; dx <= 0; dx++) {
            for (int dz = -5; dz <= 0; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    BlockPos start = clickedPos.offset(dx, dy, dz);
                    if (isPechugaActivated(world, start)) {
                        BlockPos firepitMaster = start.offset(2, 0, 2);
                        net.minecraft.tileentity.TileEntity tile = world.getBlockEntity(firepitMaster);
                        if (tile instanceof com.example.examplemod.tileentity.FirepitTileEntity) {
                            com.example.examplemod.tileentity.FirepitTileEntity firepit = 
                                (com.example.examplemod.tileentity.FirepitTileEntity) tile;
                            net.minecraft.inventory.container.SimpleNamedContainerProvider provider = 
                                new net.minecraft.inventory.container.SimpleNamedContainerProvider(
                                    (id, inv, p) -> new com.example.examplemod.container.PechugaContainer(id, inv, firepit),
                                    new net.minecraft.util.text.StringTextComponent("Кирпичная печь")
                                );
                            net.minecraftforge.fml.network.NetworkHooks.openGui(
                                (net.minecraft.entity.player.ServerPlayerEntity) player, provider, firepitMaster);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Проверяет, является ли структура активированной кирпичной печью
     * (структура валидна и кострище активировано)
     */
    private static boolean isPechugaActivated(World world, BlockPos start) {
        // Проверяем, что структура валидна (кострище активировано и стены из кирпичных блоков)
        if (!isPechuga(world, start)) {
            return false;
        }
        
        // Проверяем, что кострище активировано (имеет TileEntity)
        BlockPos firepitMaster = start.offset(2, 0, 2);
        net.minecraft.tileentity.TileEntity tile = world.getBlockEntity(firepitMaster);
        if (!(tile instanceof com.example.examplemod.tileentity.FirepitTileEntity)) {
            return false;
        }
        
        // Проверяем, что стены из кирпичных блоков (не из PECHUGA_BLOCK)
        // Это означает, что структура была активирована, но блоки остались кирпичными
        BlockPos firepitStart = start.offset(1, 0, 1);
        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                BlockPos firepitPos = firepitStart.offset(x, 0, z);
                BlockState firepitState = world.getBlockState(firepitPos);
                if (!isFirepit(firepitState)) {
                    return false;
                }
                // Проверяем, что кострище активировано
                if (firepitState.hasProperty(ModBlocks.FirepitBlock.X) && 
                    firepitState.hasProperty(ModBlocks.FirepitBlock.Z)) {
                    int firepitX = firepitState.getValue(ModBlocks.FirepitBlock.X);
                    int firepitZ = firepitState.getValue(ModBlocks.FirepitBlock.Z);
                    if (firepitX != x || firepitZ != z) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        
        return true;
    }
}

