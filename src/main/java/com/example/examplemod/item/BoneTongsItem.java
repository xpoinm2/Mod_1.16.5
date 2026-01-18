package com.example.examplemod.item;

import com.example.examplemod.ModContainers;
import com.example.examplemod.container.BoneTongsContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class BoneTongsItem extends Item {
    public BoneTongsItem(Properties properties) {
        super(properties.durability(20));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new BoneTongsCapabilityProvider(stack);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!world.isClientSide && player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player,
                    new SimpleNamedContainerProvider(
                            (windowId, playerInventory, playerEntity) -> new BoneTongsContainer(windowId, playerInventory, held),
                            new TranslationTextComponent("container.examplemod.bone_tongs")),
                    buffer -> buffer.writeItem(held));
        }
        return ActionResult.success(held);
    }
}
