package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BigBoneDropHandler {
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof CowEntity || entity instanceof SheepEntity ||
                entity instanceof HorseEntity || entity instanceof PolarBearEntity) {
            ItemEntity drop = new ItemEntity(entity.level,
                    entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(ModItems.BIG_BONE.get()));
            event.getDrops().add(drop);
        }
    }
}