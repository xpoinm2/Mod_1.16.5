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
        // Урон от горячих руд теперь обрабатывается в CommonModEvents.onPlayerTick
        // для корректной работы с перетаскиванием предметов
    }
}

