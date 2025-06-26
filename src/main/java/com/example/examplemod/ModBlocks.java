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

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExampleMod.MODID);

    // Куст малины
    public static final RegistryObject<Block> RASPBERRY_BUSH = BLOCKS.register("raspberry_bush",
            RaspberryBushBlock::new);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }

    // === Класс блока куста малины ===
    public static class RaspberryBushBlock extends SweetBerryBushBlock {
        public static final IntegerProperty CLICKS = IntegerProperty.create("clicks", 0, 10);

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
                    world.setBlock(pos, state.setValue(CLICKS, clicks + 1), 2);
                    if (clicks + 1 >= 10) {
                        world.removeBlock(pos, false);
                    }
                }
            }
            return ActionResultType.sidedSuccess(world.isClientSide);
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
}