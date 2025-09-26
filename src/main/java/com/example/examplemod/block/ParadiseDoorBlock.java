package com.example.examplemod.block;

import com.example.examplemod.world.heaven.HeavenManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ParadiseDoorBlock extends DoorBlock {

    public ParadiseDoorBlock() {
        super(Properties.of(Material.METAL, MaterialColor.QUARTZ)
                .strength(-1.0F, 3600000.0F)
                .noDrops()
                .noOcclusion()
                .sound(SoundType.METAL)
                .lightLevel(state -> 12));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
                                BlockRayTraceResult hit) {
        ActionResultType result = super.use(state, world, pos, player, hand, hit);
        if (!world.isClientSide && result.consumesAction() && player instanceof ServerPlayerEntity) {
            BlockPos lowerPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
            HeavenManager.handleDoorOpened((ServerPlayerEntity) player, (ServerWorld) world, lowerPos);
        }
        return result;
    }
}