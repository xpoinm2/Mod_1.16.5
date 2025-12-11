package com.example.examplemod.network;

import com.example.examplemod.ModItems;
import com.example.examplemod.container.BoneTongsContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.function.Supplier;

public class OpenBoneTongsItemPacket {
    private final int entityId;

    public OpenBoneTongsItemPacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(OpenBoneTongsItemPacket message, PacketBuffer buffer) {
        buffer.writeInt(message.entityId);
    }

    public static OpenBoneTongsItemPacket decode(PacketBuffer buffer) {
        return new OpenBoneTongsItemPacket(buffer.readInt());
    }

    public static void handle(OpenBoneTongsItemPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            World world = player.level;
            Entity entity = world.getEntity(message.entityId);
            if (!(entity instanceof ItemEntity itemEntity) || !itemEntity.isAlive()) {
                return;
            }

            ItemStack stack = itemEntity.getItem();
            if (stack.isEmpty() || stack.getItem() != ModItems.BONE_TONGS.get()) {
                return;
            }

            NetworkHooks.openGui(player,
                    new SimpleNamedContainerProvider(
                            (windowId, playerInventory, serverPlayer) -> new BoneTongsContainer(windowId, playerInventory, stack),
                            new TranslationTextComponent("container.examplemod.bone_tongs")
                    ),
                    buffer -> buffer.writeItem(stack));
        });
        context.setPacketHandled(true);
    }
}

