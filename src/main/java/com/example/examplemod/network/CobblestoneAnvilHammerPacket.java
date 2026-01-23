package com.example.examplemod.network;

import com.example.examplemod.ModItems;
import com.example.examplemod.item.MetalChunkItem;
import com.example.examplemod.item.SpongeMetalItem;
import com.example.examplemod.tileentity.CobblestoneAnvilTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Random;
import java.util.function.Supplier;

public class CobblestoneAnvilHammerPacket {
    private final BlockPos pos;

    public CobblestoneAnvilHammerPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(CobblestoneAnvilHammerPacket pkt, PacketBuffer buf) {
        buf.writeBlockPos(pkt.pos);
    }

    public static CobblestoneAnvilHammerPacket decode(PacketBuffer buf) {
        return new CobblestoneAnvilHammerPacket(buf.readBlockPos());
    }

    public static void handle(CobblestoneAnvilHammerPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                World world = player.level;
                TileEntity tileEntity = world.getBlockEntity(pkt.pos);

                if (tileEntity instanceof CobblestoneAnvilTileEntity) {
                    CobblestoneAnvilTileEntity anvil = (CobblestoneAnvilTileEntity) tileEntity;

                    // Увеличиваем прогресс
                    anvil.incrementProgress();

                    // Если достигли максимального прогресса, выполняем крафт
                    if (anvil.isComplete()) {
                        if (tryCraftMetalChunk(anvil, world.random, player)) {
                            anvil.resetProgress();
                        }
                    }

                    // Отправляем обновление прогресса клиенту
                    ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new CobblestoneAnvilProgressPacket(pkt.pos, anvil.getProgress())
                    );
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static boolean tryCraftMetalChunk(CobblestoneAnvilTileEntity anvil, Random random, ServerPlayerEntity player) {
        net.minecraftforge.items.ItemStackHandler inventory = anvil.getInventory();
        ItemStack metalStack = inventory.getStackInSlot(CobblestoneAnvilTileEntity.METAL_SLOT);
        ItemStack toolStack = inventory.getStackInSlot(CobblestoneAnvilTileEntity.TOOL_SLOT);
        ItemStack outputStack = inventory.getStackInSlot(CobblestoneAnvilTileEntity.OUTPUT_SLOT);

        if (metalStack.isEmpty() || toolStack.isEmpty()) {
            return false;
        }

        if (!outputStack.isEmpty()) {
            return false;
        }

        if (!(metalStack.getItem() instanceof SpongeMetalItem)) {
            return false;
        }

        if (toolStack.getItem() != ModItems.STONE_HAMMER.get()
                && toolStack.getItem() != ModItems.BONE_HAMMER.get()) {
            return false;
        }

        Item resultItem = getChunkForSponge(metalStack.getItem());
        if (resultItem == null) {
            return false;
        }

        ItemStack resultStack = new ItemStack(resultItem);
        MetalChunkItem.setState(resultStack, rollState(random));
        inventory.setStackInSlot(CobblestoneAnvilTileEntity.OUTPUT_SLOT, resultStack);

        metalStack.shrink(1);
        inventory.setStackInSlot(CobblestoneAnvilTileEntity.METAL_SLOT, metalStack);

        if (toolStack.hurt(1, random, player)) {
            toolStack.shrink(1);
        }
        inventory.setStackInSlot(CobblestoneAnvilTileEntity.TOOL_SLOT, toolStack);

        return true;
    }

    private static Item getChunkForSponge(Item spongeItem) {
        if (spongeItem == ModItems.SPONGE_IRON.get()) {
            return ModItems.IRON_CHUNK.get();
        }
        if (spongeItem == ModItems.SPONGE_TIN.get()) {
            return ModItems.TIN_CHUNK.get();
        }
        if (spongeItem == ModItems.SPONGE_GOLD.get()) {
            return ModItems.GOLD_CHUNK.get();
        }
        return null;
    }

    private static int rollState(Random random) {
        int roll = random.nextInt(100);
        if (roll < 20) {
            return MetalChunkItem.STATE_GOOD;
        }
        if (roll < 70) {
            return MetalChunkItem.STATE_MEDIUM;
        }
        return MetalChunkItem.STATE_BAD;
    }
}