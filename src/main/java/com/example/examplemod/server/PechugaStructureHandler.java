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

    private static boolean isPechugaWallBlock(BlockState state) {
        return state.getBlock() == ModBlocks.BRICK_BLOCK_WITH_LINING.get()
                || state.getBlock() == ModBlocks.PECHUGA_BLOCK.get();
    }

    private static boolean isFirepit(BlockState state) {
        return state.getBlock() == ModBlocks.FIREPIT_BLOCK.get();
    }

    private static boolean isPechugaCore(BlockState state) {
        return state.getBlock() == ModBlocks.PECHUGA_CORE_BLOCK.get();
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
                            // Все остальные блоки стены должны быть кирпичными или печугой
                            if (!isPechugaWallBlock(state)) {
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
        // Заменяем кострище на ядро кирпичной печи
        BlockPos coreStart = start.offset(1, 0, 1);
        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                BlockPos corePos = coreStart.offset(x, 0, z);
                world.setBlock(corePos, ModBlocks.PECHUGA_CORE_BLOCK.get().defaultBlockState()
                        .setValue(ModBlocks.PechugaCoreBlock.X, x)
                        .setValue(ModBlocks.PechugaCoreBlock.Z, z), 3);
            }
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
        
        BlockPos pechugaMaster = findActivatedPechugaMaster(world, clickedPos);
        if (pechugaMaster == null) {
            return false;
        }
        net.minecraft.tileentity.TileEntity tile = world.getBlockEntity(pechugaMaster);
        if (!(tile instanceof com.example.examplemod.tileentity.PechugaTileEntity)) {
            return false;
        }
        com.example.examplemod.tileentity.PechugaTileEntity pechuga =
            (com.example.examplemod.tileentity.PechugaTileEntity) tile;
        net.minecraft.inventory.container.SimpleNamedContainerProvider provider =
            new net.minecraft.inventory.container.SimpleNamedContainerProvider(
                (id, inv, p) -> new com.example.examplemod.container.PechugaContainer(id, inv, pechuga),
                new net.minecraft.util.text.StringTextComponent("Кирпичная печь")
            );
        net.minecraftforge.fml.network.NetworkHooks.openGui(
            (net.minecraft.entity.player.ServerPlayerEntity) player, provider, pechugaMaster);
        return true;
    }

    public static BlockPos findActivatedPechugaMaster(World world, BlockPos clickedPos) {
        if (world.isClientSide) {
            return null;
        }
        // Ищем структуру вокруг кликнутого блока
        for (int dx = -5; dx <= 0; dx++) {
            for (int dz = -5; dz <= 0; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    BlockPos start = clickedPos.offset(dx, dy, dz);
                    if (isPechugaActivated(world, start)) {
                        return start.offset(2, 0, 2);
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Проверяет, является ли структура активированной кирпичной печью
     * (структура валидна и кострище активировано)
     */
    private static boolean isPechugaActivated(World world, BlockPos start) {
        // Проверяем, что ядро активировано (имеет TileEntity)
        BlockPos pechugaMaster = start.offset(2, 0, 2);
        net.minecraft.tileentity.TileEntity tile = world.getBlockEntity(pechugaMaster);
        if (!(tile instanceof com.example.examplemod.tileentity.PechugaTileEntity)) {
            return false;
        }

        // Проверяем центр 4x4 на ядро
        BlockPos coreStart = start.offset(1, 0, 1);
        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                BlockPos corePos = coreStart.offset(x, 0, z);
                BlockState coreState = world.getBlockState(corePos);
                if (!isPechugaCore(coreState)) {
                    return false;
                }
                if (coreState.getValue(ModBlocks.PechugaCoreBlock.X) != x ||
                    coreState.getValue(ModBlocks.PechugaCoreBlock.Z) != z) {
                    return false;
                }
            }
        }

        // Проверяем оболочку 6x6x3
        int brickBlocks = 0;
        boolean hasOpeningNorth = world.isEmptyBlock(start.offset(2, 1, 0)) && world.isEmptyBlock(start.offset(3, 1, 0));
        boolean hasOpeningSouth = world.isEmptyBlock(start.offset(2, 1, 5)) && world.isEmptyBlock(start.offset(3, 1, 5));
        boolean hasOpeningWest = world.isEmptyBlock(start.offset(0, 1, 2)) && world.isEmptyBlock(start.offset(0, 1, 3));
        boolean hasOpeningEast = world.isEmptyBlock(start.offset(5, 1, 2)) && world.isEmptyBlock(start.offset(5, 1, 3));

        int openingCount = 0;
        if (hasOpeningNorth) openingCount++;
        if (hasOpeningSouth) openingCount++;
        if (hasOpeningWest) openingCount++;
        if (hasOpeningEast) openingCount++;

        if (openingCount != 1) {
            return false;
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 6; x++) {
                for (int z = 0; z < 6; z++) {
                    BlockPos pos = start.offset(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    if (y == 0 && x >= 1 && x < 5 && z >= 1 && z < 5) {
                        continue; // Ядро уже проверено
                    }

                    boolean isWall = (x == 0 || x == 5 || z == 0 || z == 5);
                    if (isWall) {
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
                            if (!world.isEmptyBlock(pos)) {
                                return false;
                            }
                        } else {
                            if (!isPechugaWallBlock(state)) {
                                return false;
                            }
                            brickBlocks++;
                        }
                    } else {
                        if (!world.isEmptyBlock(pos)) {
                            return false;
                        }
                    }
                }
            }
        }

        return brickBlocks >= 30;
    }
}
