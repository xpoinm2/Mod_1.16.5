package com.example.examplemod.block;

import com.example.examplemod.ModSounds;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.PlayUraganClickSoundPacket;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;

public class UraganBlock extends Block {
    public UraganBlock() {
        super(AbstractBlock.Properties.copy(Blocks.DIAMOND_BLOCK));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos,
                                PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            world.playSound(null, pos, ModSounds.HURRICANE_LOOP.get(),
                    SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (player instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                ModNetworkHandler.CHANNEL.sendTo(
                        new PlayUraganClickSoundPacket(),
                        serverPlayer.connection.connection,
                        NetworkDirection.PLAY_TO_CLIENT
                );
            }
        }
        return ActionResultType.sidedSuccess(world.isClientSide);
    }
}
