package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.item.HotRoastedOreItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.TickEvent;

public class HotOreDamageHandler {
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tick(event.player);
    }

    /**
     * Вынесено для менеджера механик: можно вызывать напрямую без создания TickEvent.
     */
    public static void tick(PlayerEntity player) {
        if (player.level.isClientSide) return;

        // Наносим урон не каждый тик, а раз в секунду (иначе это слишком жёстко и дорого по CPU при множестве механик)
        if ((player.tickCount % 20) != 0) return;

        boolean hasHotOre = false;

        for (ItemStack stack : player.inventory.items) {
            if (stack.getItem() instanceof HotRoastedOreItem) {
                hasHotOre = true;
                break;
            }
        }

        if (!hasHotOre) {
            for (ItemStack stack : player.inventory.armor) {
                if (stack.getItem() instanceof HotRoastedOreItem) {
                    hasHotOre = true;
                    break;
                }
            }
        }

        if (!hasHotOre) {
            for (ItemStack stack : player.inventory.offhand) {
                if (stack.getItem() instanceof HotRoastedOreItem) {
                    hasHotOre = true;
                    break;
                }
            }
        }

        if (hasHotOre) {
            // 1 сердце/сек = 2 HP
            player.hurt(DamageSource.ON_FIRE, 2.0F);
        }
    }
}
