package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ListIterator;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DirtDropReplacementHandler {

    private DirtDropReplacementHandler() {
    }

    @SubscribeEvent
    public static void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        if (event.getWorld().isClientSide()) {
            return;
        }

        ListIterator<ItemStack> iterator = event.getDrops().listIterator();
        while (iterator.hasNext()) {
            ItemStack drop = iterator.next();
            if (drop.getItem() == Items.DIRT) {
                iterator.set(new ItemStack(ModItems.HANDFUL_OF_DIRT.get(), drop.getCount()));
            }
        }
    }
}
