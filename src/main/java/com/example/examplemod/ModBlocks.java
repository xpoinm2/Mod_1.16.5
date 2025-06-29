package com.example.examplemod;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.IBlockReader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.example.examplemod.block.TutovikBlock;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExampleMod.MODID);

    // Куст малины
    public static final RegistryObject<Block> RASPBERRY_BUSH = BLOCKS.register("raspberry_bush",
            RaspberryBushBlock::new);

    // Куст бузины чёрной
    public static final RegistryObject<Block> ELDERBERRY_BUSH = BLOCKS.register("elderberry_bush",
            ElderberryBushBlock::new);

    // Куст клюквы
    public static final RegistryObject<Block> CRANBERRY_BUSH = BLOCKS.register("cranberry_bush",
            CranberryBushBlock::new);

    // Дягель
    public static final RegistryObject<Block> ANGELICA = BLOCKS.register("angelica",
            AngelicaBlock::new);

    // Растение хрена
    public static final RegistryObject<Block> HORSERADISH_PLANT = BLOCKS.register("horseradish_plant",
            HorseradishPlantBlock::new);

    // Растение имбиря
    public static final RegistryObject<Block> GINGER_PLANT = BLOCKS.register("ginger_plant",
            GingerPlantBlock::new);

    // Мухомор
    public static final RegistryObject<Block> MUHOMOR = BLOCKS.register("muhomor",
            () -> new net.minecraft.block.BushBlock(AbstractBlock.Properties.copy(Blocks.RED_MUSHROOM)));

    // Тутовик - гриб на стволе
    public static final RegistryObject<Block> TUTOVIK = BLOCKS.register("tutovik",
            TutovikBlock::new);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }

    // === Класс блока куста малины ===
    public static class RaspberryBushBlock extends SweetBerryBushBlock {
        public static final IntegerProperty CLICKS = IntegerProperty.create("clicks", 0, 10);

        private static final int REGROW_TICKS = 24000 * 5; // 5 in-game days

        public RaspberryBushBlock() {
            super(AbstractBlock.Properties.copy(Blocks.SWEET_BERRY_BUSH));
            this.registerDefaultState(this.stateDefinition.any().setValue(CLICKS, 0).setValue(AGE, 3));
        }

        @Override
        protected void createBlockStateDefinition(StateContainer.Builder<Block, net.minecraft.block.BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(CLICKS);
        }

        @Override
        public ActionResultType use(net.minecraft.block.BlockState state, World world, BlockPos pos,
                                    PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
            if (!world.isClientSide) {
                int clicks = state.getValue(CLICKS);
                if (clicks < 10) {
                    Block.popResource(world, pos, new ItemStack(ModItems.RASPBERRY.get(), 3));
                    if (clicks + 1 >= 10) {
                        world.setBlock(pos, state.setValue(CLICKS, clicks + 1).setValue(AGE, 1), 2);
                        world.getBlockTicks().scheduleTick(pos, this, REGROW_TICKS);
                    } else {
                        world.setBlock(pos, state.setValue(CLICKS, clicks + 1), 2);
                    }
                }
            }
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
        @Override
        public void tick(net.minecraft.block.BlockState state, net.minecraft.world.server.ServerWorld world, BlockPos pos, java.util.Random random) {
            if (state.getValue(AGE) == 1 && state.getValue(CLICKS) >= 10) {
                world.setBlock(pos, state.setValue(AGE, 3).setValue(CLICKS, 0), 2);
            }
        }
        @Override
        public boolean isRandomlyTicking(net.minecraft.block.BlockState state) {
            return false; // не растет
        }

        @Override
        public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, net.minecraft.block.BlockState state) {
            return new ItemStack(ModItems.RASPBERRY_BUSH.get());
        }
    }

    // === Класс блока куста бузины чёрной ===
    public static class ElderberryBushBlock extends SweetBerryBushBlock {
        public static final IntegerProperty CLICKS = RaspberryBushBlock.CLICKS;
        private static final int REGROW_TICKS = 24000 * 5;

        public ElderberryBushBlock() {
            super(AbstractBlock.Properties.copy(Blocks.SWEET_BERRY_BUSH));
            this.registerDefaultState(this.stateDefinition.any().setValue(CLICKS, 0).setValue(AGE, 3));
        }

        @Override
        protected void createBlockStateDefinition(StateContainer.Builder<Block, net.minecraft.block.BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(CLICKS);
        }

        @Override
        public ActionResultType use(net.minecraft.block.BlockState state, World world, BlockPos pos,
                                    PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
            if (!world.isClientSide) {
                int clicks = state.getValue(CLICKS);
                if (clicks < 10) {
                    Block.popResource(world, pos, new ItemStack(ModItems.ELDERBERRY.get(), 3));

                    if (clicks + 1 >= 10) {
                        world.setBlock(pos, state.setValue(CLICKS, clicks + 1).setValue(AGE, 1), 2);
                        world.getBlockTicks().scheduleTick(pos, this, REGROW_TICKS);
                    } else {
                        world.setBlock(pos, state.setValue(CLICKS, clicks + 1), 2);
                    }
                }
            }
            return ActionResultType.sidedSuccess(world.isClientSide);
        }

        @Override
        public boolean isRandomlyTicking(net.minecraft.block.BlockState state) {
            return false;
        }
        @Override
        public void tick(net.minecraft.block.BlockState state, net.minecraft.world.server.ServerWorld world, BlockPos pos, java.util.Random random) {
            if (state.getValue(AGE) == 1 && state.getValue(CLICKS) >= 10) {
                world.setBlock(pos, state.setValue(AGE, 3).setValue(CLICKS, 0), 2);
            }
        }

        @Override
        public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, net.minecraft.block.BlockState state) {
            return new ItemStack(ModItems.ELDERBERRY_BUSH.get());
        }
    }

    // === Класс блока куста клюквы ===
    public static class CranberryBushBlock extends SweetBerryBushBlock {
        public static final IntegerProperty CLICKS = RaspberryBushBlock.CLICKS;
        private static final int REGROW_TICKS = 24000 * 5;

        public CranberryBushBlock() {
            super(AbstractBlock.Properties.copy(Blocks.SWEET_BERRY_BUSH));
            this.registerDefaultState(this.stateDefinition.any().setValue(CLICKS, 0).setValue(AGE, 3));
        }

        @Override
        protected void createBlockStateDefinition(StateContainer.Builder<Block, net.minecraft.block.BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(CLICKS);
        }

        @Override
        public ActionResultType use(net.minecraft.block.BlockState state, World world, BlockPos pos,
                                    PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
            if (!world.isClientSide) {
                int clicks = state.getValue(CLICKS);
                if (clicks < 10) {
                    Block.popResource(world, pos, new ItemStack(ModItems.CRANBERRY.get(), 3));
                    if (clicks + 1 >= 10) {
                        world.setBlock(pos, state.setValue(CLICKS, clicks + 1).setValue(AGE, 1), 2);
                        world.getBlockTicks().scheduleTick(pos, this, REGROW_TICKS);
                    } else {
                        world.setBlock(pos, state.setValue(CLICKS, clicks + 1), 2);
                    }
                }
            }
            return ActionResultType.sidedSuccess(world.isClientSide);
        }

        @Override
        public boolean isRandomlyTicking(net.minecraft.block.BlockState state) {
            return false;
        }
        @Override
        public void tick(net.minecraft.block.BlockState state, net.minecraft.world.server.ServerWorld world, BlockPos pos, java.util.Random random) {
            if (state.getValue(AGE) == 1 && state.getValue(CLICKS) >= 10) {
                world.setBlock(pos, state.setValue(AGE, 3).setValue(CLICKS, 0), 2);
            }
        }
        @Override
        public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, net.minecraft.block.BlockState state) {
            return new ItemStack(ModItems.CRANBERRY_BUSH.get());
        }
    }

    // === Класс блока дягеля ===
    public static class AngelicaBlock extends net.minecraft.block.BushBlock {
        public AngelicaBlock() {
            super(AbstractBlock.Properties.copy(Blocks.FERN));
        }

        @Override
        public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, net.minecraft.block.BlockState state) {
            return new ItemStack(ModItems.ANGELICA_ROOT.get());
        }
    }

    // === Класс растения хрена ===
    public static class HorseradishPlantBlock extends net.minecraft.block.BushBlock {
        public HorseradishPlantBlock() {
            super(AbstractBlock.Properties.copy(Blocks.FERN));
        }

        @Override
        public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, net.minecraft.block.BlockState state) {
            return new ItemStack(ModItems.HORSERADISH.get());
        }
    }
    // === Класс растения имбиря ===
    public static class GingerPlantBlock extends net.minecraft.block.BushBlock {
        public GingerPlantBlock() {
            super(AbstractBlock.Properties.copy(Blocks.FERN));
        }

        @Override
        public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, net.minecraft.block.BlockState state) {
            return new ItemStack(ModItems.GINGER.get());
        }
    }
}