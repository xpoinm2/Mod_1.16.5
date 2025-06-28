package com.example.examplemod.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

/**
 * Item representing angelica root. Eating it reduces poison duration.
 */
public class AngelicaRootItem extends Item {
    public AngelicaRootItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, world, entity);
        if (!world.isClientSide && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            EffectInstance poison = player.getEffect(Effects.POISON);
            if (poison != null) {
                int duration = Math.max(0, poison.getDuration() - 100);
                player.addEffect(new EffectInstance(Effects.POISON, duration, poison.getAmplifier(), poison.isAmbient(), poison.isVisible(), poison.showIcon));
            }
        }
        return result;
    }
}