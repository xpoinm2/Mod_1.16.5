package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.item.HotRoastedOreItem;
import com.example.examplemod.server.mechanics.IMechanicModule;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class HotOreDamageMechanic implements IMechanicModule {
    @Override
    public String id() {
        return "hot_ore_damage";
    }

    @Override
    public int playerIntervalTicks() {
        return 20; // раз в секунду
    }

    @Override
    public void onPlayerTick(ServerPlayerEntity player) {
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

