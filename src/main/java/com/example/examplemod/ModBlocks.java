package com.example.examplemod;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import com.example.examplemod.block.DanilkaBlock;
import com.example.examplemod.block.ParadiseDoorBlock;
import com.example.examplemod.block.ClayPotBlock;
import com.example.examplemod.block.ClayShardsBlock;
import com.example.examplemod.block.RawClayPotBlock;
import com.example.examplemod.block.DirtyWaterBlock;
import com.example.examplemod.block.BaseSlabBlock;
import com.example.examplemod.block.CobblestoneAnvilBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.IBlockReader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import com.example.examplemod.tileentity.FirepitTileEntity;
import com.example.examplemod.tileentity.SlabTileEntity;
import com.example.examplemod.container.SlabContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import javax.annotation.Nullable;


public class ModBlocks {
    // Используем унифицированный регистратор из ModRegistries

    // Костяной блок
    public static final RegistryObject<Block> BONE_BLOCK = ModRegistries.BLOCKS.register("bone_block",
            () -> new Block(AbstractBlock.Properties.copy(Blocks.BONE_BLOCK)));

    // Сырой глиняный горшок
    public static final RegistryObject<Block> RAW_CLAY_POT = ModRegistries.BLOCKS.register("raw_clay_pot",
            RawClayPotBlock::new);

    // Глиняный горшок
    public static final RegistryObject<Block> CLAY_POT = ModRegistries.BLOCKS.register("clay_pot",
            ClayPotBlock::new);

    // Глиняные осколки
    public static final RegistryObject<Block> CLAY_SHARDS = ModRegistries.BLOCKS.register("clay_shards",
            ClayShardsBlock::new);

    // Блок Данилка
    public static final RegistryObject<Block> DANILKA_BLOCK = ModRegistries.BLOCKS.register("danilka_block",
            DanilkaBlock::new);

    // Куст малины
    public static final RegistryObject<Block> RASPBERRY_BUSH = ModRegistries.BLOCKS.register("raspberry_bush",
            RaspberryBushBlock::new);

    // Куст бузины чёрной
    public static final RegistryObject<Block> ELDERBERRY_BUSH = ModRegistries.BLOCKS.register("elderberry_bush",
            ElderberryBushBlock::new);

    // Куст клюквы
    public static final RegistryObject<Block> CRANBERRY_BUSH = ModRegistries.BLOCKS.register("cranberry_bush",
            CranberryBushBlock::new);

    // Дягель
    public static final RegistryObject<Block> ANGELICA = ModRegistries.BLOCKS.register("angelica",
            AngelicaBlock::new);

    // Растение хрена
    public static final RegistryObject<Block> HORSERADISH_PLANT = ModRegistries.BLOCKS.register("horseradish_plant",
            HorseradishPlantBlock::new);

    // Растение имбиря
    public static final RegistryObject<Block> GINGER_PLANT = ModRegistries.BLOCKS.register("ginger_plant",
            GingerPlantBlock::new);

    // Растение льна
    public static final RegistryObject<Block> FLAX_PLANT = ModRegistries.BLOCKS.register("flax_plant",
            FlaxPlantBlock::new);

    // Пучок трав
    public static final RegistryObject<Block> BUNCH_OF_GRASS = ModRegistries.BLOCKS.register("bunch_of_grass",
            BunchOfGrassBlock::new);

    // Сушащийся лён (вешается под листвой)
    public static final RegistryObject<Block> HANGING_FLAX = ModRegistries.BLOCKS.register("hanging_flax",
            HangingFlaxBlock::new);

    // Высушенный лён
    public static final RegistryObject<Block> DRIED_FLAX = ModRegistries.BLOCKS.register("dried_flax",
            DriedFlaxBlock::new);

    // Полублок хвороста
    public static final RegistryObject<Block> BRUSHWOOD = ModRegistries.BLOCKS.register("brushwood",
            BrushwoodSlabBlock::new);

    // Полублок обгоревшего хвороста
    public static final RegistryObject<Block> BRUSHWOOD_SLAB_BURNT = ModRegistries.BLOCKS.register("brushwood_slab_burnt",
    BurnedBrushwoodSlabBlock::new);

    // Полублок дуба
    public static final RegistryObject<Block> OAK_SLAB = ModRegistries.BLOCKS.register("oak_slab",
            OakSlabBlock::new);

    // Полублок березы
    public static final RegistryObject<Block> BIRCH_SLAB = ModRegistries.BLOCKS.register("birch_slab",
            BirchSlabBlock::new);

    // Полублок ели
    public static final RegistryObject<Block> SPRUCE_SLAB = ModRegistries.BLOCKS.register("spruce_slab",
            SpruceSlabBlock::new);

    // Полублок тропического дерева
    public static final RegistryObject<Block> JUNGLE_SLAB = ModRegistries.BLOCKS.register("jungle_slab",
            JungleSlabBlock::new);

    // Полублок акации
    public static final RegistryObject<Block> ACACIA_SLAB = ModRegistries.BLOCKS.register("acacia_slab",
            AcaciaSlabBlock::new);

    // Полублок темного дуба
    public static final RegistryObject<Block> DARK_OAK_SLAB = ModRegistries.BLOCKS.register("dark_oak_slab",
            DarkOakSlabBlock::new);

    // Полублок багрового дерева
    public static final RegistryObject<Block> CRIMSON_SLAB = ModRegistries.BLOCKS.register("crimson_slab",
            CrimsonSlabBlock::new);

    // Полублок искаженного дерева
    public static final RegistryObject<Block> WARPED_SLAB = ModRegistries.BLOCKS.register("warped_slab",
            WarpedSlabBlock::new);

    // Полублок булыжника
    public static final RegistryObject<Block> COBBLESTONE_SLAB = ModRegistries.BLOCKS.register("cobblestone_slab",
            CobblestoneSlabBlock::new);

    // Блок кострища (часть мультиструктуры 4x4)
    public static final RegistryObject<Block> FIREPIT_BLOCK = ModRegistries.BLOCKS.register("firepit_block",
            FirepitBlock::new);

    // Блок Печуги (часть мультиструктуры 6x6x3)
    public static final RegistryObject<Block> PECHUGA_BLOCK = ModRegistries.BLOCKS.register("pechuga_block",
            PechugaBlock::new);

    // Железная руда с примесями
    public static final RegistryObject<Block> IMPURE_IRON_ORE = ModRegistries.BLOCKS.register("impure_iron_ore",
            () -> new Block(AbstractBlock.Properties.copy(Blocks.IRON_ORE)));

    // Пиритовая жила
    public static final RegistryObject<Block> PYRITE = ModRegistries.BLOCKS.register("pyrite",
            () -> new Block(AbstractBlock.Properties.copy(Blocks.COAL_ORE)));

    // Гравийная оловянная руда
    public static final RegistryObject<Block> TIN_GRAVEL_ORE = ModRegistries.BLOCKS.register("tin_gravel_ore",
            () -> new FallingBlock(AbstractBlock.Properties.copy(Blocks.GRAVEL)
                    .strength(0.6F)
                    .harvestTool(net.minecraftforge.common.ToolType.SHOVEL)
                    .harvestLevel(0)));

    // Оловянная руда
    public static final RegistryObject<Block> TIN_ORE = ModRegistries.BLOCKS.register("tin_ore",
            () -> new Block(AbstractBlock.Properties.copy(Blocks.IRON_ORE)
                    .strength(1.5F, 6.0F)
                    .harvestTool(net.minecraftforge.common.ToolType.PICKAXE)
                    .harvestLevel(1)));

    // Гравийная золотая руда
    public static final RegistryObject<Block> GOLD_GRAVEL_ORE = ModRegistries.BLOCKS.register("gold_gravel_ore",
            () -> new FallingBlock(AbstractBlock.Properties.copy(Blocks.GRAVEL)
                    .strength(0.6F)
                    .harvestTool(net.minecraftforge.common.ToolType.SHOVEL)
                    .harvestLevel(0)));

    // Неочищенная оловянная руда
    public static final RegistryObject<Block> UNREFINED_TIN_ORE = ModRegistries.BLOCKS.register("unrefined_tin_ore",
            () -> new Block(AbstractBlock.Properties.copy(Blocks.IRON_ORE)));

    // Неочищенная золотая руда
    public static final RegistryObject<Block> UNREFINED_GOLD_ORE = ModRegistries.BLOCKS.register("unrefined_gold_ore",
            () -> new Block(AbstractBlock.Properties.copy(Blocks.GOLD_ORE)));


    // Райский блок
    public static final RegistryObject<Block> PARADISE_BLOCK = ModRegistries.BLOCKS.register("paradise_block",
            () -> new Block(AbstractBlock.Properties.copy(Blocks.QUARTZ_BLOCK)
                    .strength(-1.0F, 3600000.0F)
                    .noDrops()
                    .lightLevel(state -> 12)));

    // Райская дверь
    public static final RegistryObject<Block> PARADISE_DOOR = ModRegistries.BLOCKS.register("paradise_door",
            ParadiseDoorBlock::new);

    // Райская ограда
    public static final RegistryObject<Block> PARADISE_FENCE = ModRegistries.BLOCKS.register("paradise_fence",
            () -> new WallBlock(AbstractBlock.Properties.copy(Blocks.COBBLESTONE_WALL)
                    .strength(-1.0F, 3600000.0F)
                    .noDrops()
                    .lightLevel(state -> 12)));

    // Блок грязной воды
    public static final RegistryObject<Block> DIRTY_WATER_BLOCK = ModRegistries.BLOCKS.register("dirty_water",
            DirtyWaterBlock::new);

    // Кирпичный блок с футеровкой
    public static final RegistryObject<Block> BRICK_BLOCK_WITH_LINING = ModRegistries.BLOCKS.register("brick_block_with_lining",
            BrickBlockWithLining::new);

    // Наковальня из булыжника
    public static final RegistryObject<Block> COBBLESTONE_ANVIL = ModRegistries.BLOCKS.register("cobblestone_anvil",
            CobblestoneAnvilBlock::new);


    public static void register(IEventBus bus) {
        ModRegistries.BLOCKS.register(bus);
    }

    /**
     * Форсирует загрузку класса (и, как следствие, добавление записей в DeferredRegister)
     * на стадии инициализации мода.
     */
    public static void init() {
        // no-op
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

    // === Класс растения льна ===
    public static class FlaxPlantBlock extends net.minecraft.block.BushBlock {
        public FlaxPlantBlock() {
            super(AbstractBlock.Properties.copy(Blocks.FERN));
        }

        @Override
        public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, net.minecraft.block.BlockState state) {
            return new ItemStack(ModItems.FLAX.get());
        }
    }

    // === Класс пучка трав ===
    public static class BunchOfGrassBlock extends net.minecraft.block.BushBlock {
        public BunchOfGrassBlock() {
            super(AbstractBlock.Properties.copy(Blocks.FERN));
        }

        @Override
        public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, net.minecraft.block.BlockState state) {
            return com.example.examplemod.item.GrassBundleItem.createWithState(com.example.examplemod.item.GrassBundleItem.GrassState.HEALING);
        }
    }
    // === Блок сушащегося льна ===
    public static class HangingFlaxBlock extends net.minecraft.block.BushBlock {
        public HangingFlaxBlock() {
            super(AbstractBlock.Properties.copy(Blocks.FERN).noCollission());
        }

        @Override
        public void onPlace(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
            super.onPlace(state, world, pos, oldState, isMoving);
            if (!world.isClientSide) {
                world.getBlockTicks().scheduleTick(pos, this, 2400);
            }
        }

        @Override
        public void tick(BlockState state, net.minecraft.world.server.ServerWorld world, BlockPos pos, java.util.Random random) {
            world.setBlock(pos, ModBlocks.DRIED_FLAX.get().defaultBlockState(), 2);
        }

        @Override
        public void playerDestroy(World world, PlayerEntity player, BlockPos pos, net.minecraft.block.BlockState state, @Nullable net.minecraft.tileentity.TileEntity te, ItemStack stack) {
            super.playerDestroy(world, player, pos, state, te, stack);
            if (!world.isClientSide) {
                Block.popResource(world, pos, new ItemStack(ModItems.SOAKED_FLAX.get()));
            }
        }

        @Override
        public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, net.minecraft.block.BlockState state) {
            return new ItemStack(ModItems.SOAKED_FLAX.get());
        }
    }

    // === Блок высушенного льна ===
    public static class DriedFlaxBlock extends net.minecraft.block.BushBlock {
        public DriedFlaxBlock() {
            super(AbstractBlock.Properties.copy(Blocks.FERN).noCollission());
        }

        @Override
        public void playerDestroy(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable net.minecraft.tileentity.TileEntity te, ItemStack stack) {
            super.playerDestroy(world, player, pos, state, te, stack);
            if (!world.isClientSide) {
                if (stack.getItem() == ModItems.WOODEN_COMB.get() || stack.getItem() == ModItems.BONE_COMB.get()) {
                    Block.popResource(world, pos, new ItemStack(ModItems.FLAX_FIBERS.get()));
                    Hand hand = player.getMainHandItem() == stack ? Hand.MAIN_HAND : Hand.OFF_HAND;
                    player.swing(hand, true);
                }
            }
        }

        @Override
        public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, BlockState state) {
            return new ItemStack(ModItems.FLAX_FIBERS.get());
        }
    }

    // === Класс полублока хвороста ===
    public static class BrushwoodSlabBlock extends SlabBlock {

        public BrushwoodSlabBlock() {
            super(AbstractBlock.Properties.copy(Blocks.OAK_SLAB));
        }

        @Override
        public boolean hasTileEntity(BlockState state) {
            return true;
        }

        @Nullable
        @Override
        public TileEntity createTileEntity(BlockState state, IBlockReader world) {
            return ModTileEntities.SLAB.get().create();
        }

        @Override
        public ActionResultType use(BlockState state, World world, BlockPos pos,
                                    PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
            if (world.isClientSide) {
                return ActionResultType.SUCCESS;
            }

            TileEntity tile = world.getBlockEntity(pos);
            if (tile instanceof SlabTileEntity) {
                INamedContainerProvider provider = new SimpleNamedContainerProvider(
                        (windowId, playerInventory, playerEntity) -> new SlabContainer(windowId, playerInventory, (SlabTileEntity) tile),
                        new TranslationTextComponent("container.examplemod.slab")
                );
                if (player instanceof ServerPlayerEntity) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, provider, pos);
                }
            }

            return ActionResultType.CONSUME;
        }
    }

    // === Класс обгоревшего полублока хвороста ===
    public static class BurnedBrushwoodSlabBlock extends BrushwoodSlabBlock {
        public BurnedBrushwoodSlabBlock() {
            super();
        }
    }


    // === Класс полублока дуба ===
    public static class OakSlabBlock extends BaseSlabBlock {
        public OakSlabBlock() {
            super(AbstractBlock.Properties.copy(Blocks.OAK_SLAB));
        }
    }

    // === Класс полублока березы ===
    public static class BirchSlabBlock extends BaseSlabBlock {
        public BirchSlabBlock() {
            super(AbstractBlock.Properties.copy(Blocks.BIRCH_SLAB));
        }
    }

    // === Класс полублока ели ===
    public static class SpruceSlabBlock extends BaseSlabBlock {
        public SpruceSlabBlock() {
            super(AbstractBlock.Properties.copy(Blocks.SPRUCE_SLAB));
        }
    }

    // === Класс полублока тропического дерева ===
    public static class JungleSlabBlock extends BaseSlabBlock {
        public JungleSlabBlock() {
            super(AbstractBlock.Properties.copy(Blocks.JUNGLE_SLAB));
        }
    }

    // === Класс полублока акации ===
    public static class AcaciaSlabBlock extends BaseSlabBlock {
        public AcaciaSlabBlock() {
            super(AbstractBlock.Properties.copy(Blocks.ACACIA_SLAB));
        }
    }

    // === Класс полублока темного дуба ===
    public static class DarkOakSlabBlock extends BaseSlabBlock {
        public DarkOakSlabBlock() {
            super(AbstractBlock.Properties.copy(Blocks.DARK_OAK_SLAB));
        }
    }

    // === Класс полублока багрового дерева ===
    public static class CrimsonSlabBlock extends BaseSlabBlock {
        public CrimsonSlabBlock() {
            super(AbstractBlock.Properties.copy(Blocks.CRIMSON_SLAB));
        }
    }

    // === Класс полублока искаженного дерева ===
    public static class WarpedSlabBlock extends BaseSlabBlock {
        public WarpedSlabBlock() {
            super(AbstractBlock.Properties.copy(Blocks.WARPED_SLAB));
        }
    }

    // === Класс полублока булыжника ===
    public static class CobblestoneSlabBlock extends BaseSlabBlock {
        public CobblestoneSlabBlock() {
            super(AbstractBlock.Properties.copy(Blocks.COBBLESTONE_SLAB));
        }
    }

    // === Блок кострища ===
    public static class FirepitBlock extends Block {
        public static final IntegerProperty X = IntegerProperty.create("x", 0, 3);
        public static final IntegerProperty Z = IntegerProperty.create("z", 0, 3);
        private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 8, 16);

        public FirepitBlock() {
            super(AbstractBlock.Properties.copy(Blocks.BRICKS));
            this.registerDefaultState(this.stateDefinition.any()
                    .setValue(X, 0)
                    .setValue(Z, 0));
        }

        @Override
        public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
            return SHAPE;
        }

        @Override
        protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
            builder.add(X, Z);
        }

        @Override
        public boolean hasTileEntity(BlockState state) {
            return isMaster(state);
        }

        @Nullable
        @Override
        public TileEntity createTileEntity(BlockState state, IBlockReader world) {
            return isMaster(state) ? ModTileEntities.FIREPIT.get().create() : null;
        }

        @Override
        public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
            if (!world.isClientSide && hand == Hand.MAIN_HAND) {
                BlockPos masterPos = getMasterPos(pos, state);
                TileEntity tile = world.getBlockEntity(masterPos);
                if (tile instanceof FirepitTileEntity) {
                    FirepitTileEntity firepit = (FirepitTileEntity) tile;
                    
                    // Сначала проверяем, находится ли кострище внутри структуры кирпичной печи
                    // Если да - открываем GUI кирпичной печи, если нет - GUI кострища
                    ItemStack mainHand = player.getMainHandItem();
                    ItemStack offHand = player.getOffhandItem();
                    boolean hasTongs = (mainHand.getItem() instanceof com.example.examplemod.item.BoneTongsItem) ||
                                     (offHand.getItem() instanceof com.example.examplemod.item.BoneTongsItem);

                    if (hasTongs) {
                        BlockPos pechugaMaster = com.example.examplemod.server.PechugaStructureHandler.findActivatedFirepitMaster(world, pos);
                        if (pechugaMaster != null) {
                            ItemStack tongs = mainHand.getItem() instanceof com.example.examplemod.item.BoneTongsItem ? mainHand : offHand;
                            com.example.examplemod.item.BoneTongsItem.openEnhancedDualGUI((ServerPlayerEntity) player, firepit, pechugaMaster, tongs, false);
                            return ActionResultType.SUCCESS;
                        }
                    }

                    if (com.example.examplemod.server.PechugaStructureHandler.tryOpenGui(world, pos, player)) {
                        return ActionResultType.SUCCESS;
                    }
                    
                    // Если не внутри кирпичной печи, открываем GUI кострища
                    // Check if the multiblock structure is still intact
                    if (firepit.isMultiblockIntact()) {
                        // Проверяем, есть ли щипцы у игрока
                        if (hasTongs) {
                            // Открываем enhanced dual GUI
                            ItemStack tongs = mainHand.getItem() instanceof com.example.examplemod.item.BoneTongsItem ? mainHand : offHand;
                            com.example.examplemod.item.BoneTongsItem.openEnhancedDualGUI((ServerPlayerEntity) player, firepit, masterPos, tongs, true);
                        } else {
                            // Открываем обычный GUI кострища
                            NetworkHooks.openGui((ServerPlayerEntity) player, firepit, masterPos);
                        }
                    } else {
                        // Structure is damaged, show message or just do nothing
                        return ActionResultType.FAIL;
                    }
                }
            }
            return ActionResultType.sidedSuccess(world.isClientSide);
        }

        @Override
        public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
            if (!state.is(newState.getBlock())) {
                if (isMaster(state)) {
                    TileEntity tile = world.getBlockEntity(pos);
                    if (tile instanceof FirepitTileEntity) {
                        InventoryHelper.dropContents(world, pos, (FirepitTileEntity) tile);
                        world.updateNeighbourForOutputSignal(pos, this);
                    }
                }
                super.onRemove(state, world, pos, newState, isMoving);
            } else {
                super.onRemove(state, world, pos, newState, isMoving);
            }
        }

        @Override
        public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
            super.entityInside(state, world, pos, entity);
            if (world.isClientSide) {
                return;
            }

            BlockPos masterPos = getMasterPos(pos, state);
            TileEntity tile = world.getBlockEntity(masterPos);
            if (tile instanceof FirepitTileEntity) {
                FirepitTileEntity firepit = (FirepitTileEntity) tile;
                if (firepit.getHeat() >= FirepitTileEntity.MIN_HEAT_FOR_SMELTING
                        && !entity.fireImmune()
                        && !entity.isSteppingCarefully()) {
                    entity.hurt(DamageSource.HOT_FLOOR, 1.0F);
                }
            }
        }

            private static boolean isMaster(BlockState state) {
                return state.getValue(X) == 1 && state.getValue(Z) == 1;
            }

            public static BlockPos getMasterPos(BlockPos pos, BlockState state) {
                int xOffset = state.getValue(X);
                int zOffset = state.getValue(Z);
                BlockPos start = pos.offset(-xOffset, 0, -zOffset);
                return start.offset(1, 0, 1);
            }
    }

    // === Блок Печуги ===
    public static class PechugaBlock extends Block {
        public static final IntegerProperty X = IntegerProperty.create("x", 0, 5);
        public static final IntegerProperty Y = IntegerProperty.create("y", 0, 2);
        public static final IntegerProperty Z = IntegerProperty.create("z", 0, 5);

        public PechugaBlock() {
            super(AbstractBlock.Properties.copy(Blocks.SANDSTONE));
            this.registerDefaultState(this.stateDefinition.any()
                    .setValue(X, 0)
                    .setValue(Y, 0)
                    .setValue(Z, 0));
        }

        @Override
        protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
            builder.add(X, Y, Z);
        }

        @Override
        public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
            if (!world.isClientSide && hand == Hand.MAIN_HAND) {
                // Находим позицию кострища в структуре
                int x = state.getValue(X);
                int y = state.getValue(Y);
                int z = state.getValue(Z);
                BlockPos structureStart = pos.offset(-x, -y, -z);
                
                // Master блок кострища на (2,0,2) относительно начала структуры
                // Кострище начинается с (1,0,1), master блок на (1,1) внутри кострища = (2,0,2) относительно начала
                BlockPos firepitMaster = structureStart.offset(2, 0, 2);

                ItemStack mainHand = player.getMainHandItem();
                ItemStack offHand = player.getOffhandItem();
                boolean hasTongs = (mainHand.getItem() instanceof com.example.examplemod.item.BoneTongsItem) ||
                                 (offHand.getItem() instanceof com.example.examplemod.item.BoneTongsItem);
                
                TileEntity tile = world.getBlockEntity(firepitMaster);
                if (tile instanceof FirepitTileEntity) {
                    FirepitTileEntity firepit = (FirepitTileEntity) tile;
                    // Проверяем, что структура кирпичной печи цела
                    if (isPechugaStructureIntact(world, structureStart)) {
                        if (hasTongs) {
                            ItemStack tongs = mainHand.getItem() instanceof com.example.examplemod.item.BoneTongsItem ? mainHand : offHand;
                            com.example.examplemod.item.BoneTongsItem.openEnhancedDualGUI((ServerPlayerEntity) player, firepit, firepitMaster, tongs, false);
                        } else {
                            // Открываем GUI кирпичной печи через специальный контейнер
                            net.minecraft.inventory.container.SimpleNamedContainerProvider provider =
                                new net.minecraft.inventory.container.SimpleNamedContainerProvider(
                                    (id, inv, p) -> new com.example.examplemod.container.PechugaContainer(id, inv, firepit),
                                    new net.minecraft.util.text.StringTextComponent("Кирпичная печь")
                                );
                            net.minecraftforge.fml.network.NetworkHooks.openGui((ServerPlayerEntity) player, provider, firepitMaster);
                        }
                    } else {
                        return ActionResultType.FAIL;
                    }
                }
            }
            return ActionResultType.sidedSuccess(world.isClientSide);
        }

        private static boolean isPechugaStructureIntact(World world, BlockPos start) {
            // Проверяем, что кострище 4x4 находится в центре структуры 6x6 на y=0
            BlockPos firepitStart = start.offset(1, 0, 1);
            for (int x = 0; x < 4; x++) {
                for (int z = 0; z < 4; z++) {
                    BlockPos firepitPos = firepitStart.offset(x, 0, z);
                    BlockState state = world.getBlockState(firepitPos);
                    if (state.getBlock() != ModBlocks.FIREPIT_BLOCK.get()) {
                        return false;
                    }
                    // Проверяем, что кострище активировано
                    if (state.hasProperty(ModBlocks.FirepitBlock.X) && 
                        state.hasProperty(ModBlocks.FirepitBlock.Z)) {
                        int firepitX = state.getValue(ModBlocks.FirepitBlock.X);
                        int firepitZ = state.getValue(ModBlocks.FirepitBlock.Z);
                        if (firepitX != x || firepitZ != z) {
                            return false; // Кострище не активировано правильно
                        }
                    } else {
                        return false; // Кострище не активировано
                    }
                }
            }
            
            // Проверяем стены структуры 6x6x3
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
                            continue;
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
                                if (!world.isEmptyBlock(pos)) {
                                    return false;
                                }
                            } else {
                                // Все остальные блоки стены должны быть кирпичными
                                if (state.getBlock() != ModBlocks.BRICK_BLOCK_WITH_LINING.get() && 
                                    state.getBlock() != ModBlocks.PECHUGA_BLOCK.get()) {
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
            
            return brickBlocks >= 30;
        }
    }

    // === Кирпичный блок с футеровкой ===
    public static class BrickBlockWithLining extends Block {
        public BrickBlockWithLining() {
            super(AbstractBlock.Properties.copy(Blocks.BRICKS));
        }

        @Override
        public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
            if (!world.isClientSide && hand == Hand.MAIN_HAND) {
                // Проверяем, является ли этот блок частью активированной структуры кирпичной печи
                ItemStack mainHand = player.getMainHandItem();
                ItemStack offHand = player.getOffhandItem();
                boolean hasTongs = (mainHand.getItem() instanceof com.example.examplemod.item.BoneTongsItem) ||
                                 (offHand.getItem() instanceof com.example.examplemod.item.BoneTongsItem);

                if (hasTongs) {
                    BlockPos firepitMaster = com.example.examplemod.server.PechugaStructureHandler.findActivatedFirepitMaster(world, pos);
                    if (firepitMaster != null && world.getBlockEntity(firepitMaster) instanceof FirepitTileEntity) {
                        ItemStack tongs = mainHand.getItem() instanceof com.example.examplemod.item.BoneTongsItem ? mainHand : offHand;
                        com.example.examplemod.item.BoneTongsItem.openEnhancedDualGUI((ServerPlayerEntity) player,
                                (FirepitTileEntity) world.getBlockEntity(firepitMaster), firepitMaster, tongs, false);
                        return ActionResultType.SUCCESS;
                    }
                }
                if (com.example.examplemod.server.PechugaStructureHandler.tryOpenGui(world, pos, player)) {
                    return ActionResultType.SUCCESS;
                }
            }
            return ActionResultType.PASS;
        }
    }
}
