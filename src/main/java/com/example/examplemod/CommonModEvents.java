package com.example.examplemod;

import com.example.examplemod.item.HotRoastedOreItem;
import com.example.examplemod.item.SpongeMetalItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommonModEvents {
    private CommonModEvents() {
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        // Временно убрали регистрацию генерации мира для диагностики
        event.enqueueWork(() -> {
            // WorldGenRegistry.register();
            // ModBiomes.setupBiomes();
        });
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level.isClientSide) return;

        PlayerEntity player = event.player;

        // Проверяем все предметы в инвентаре на горячие губчатые металлы и горячие руды
        // Это обеспечит урон даже когда предмет перетаскивается курсором
        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
            ItemStack stack = player.inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof SpongeMetalItem) {
                    int state = SpongeMetalItem.getState(stack);
                    if (state == SpongeMetalItem.STATE_HOT) {
                        player.hurt(DamageSource.HOT_FLOOR, 1.0F);
                        break; // Наносим урон только один раз за тик, даже если несколько горячих предметов
                    }
                } else if (stack.getItem() instanceof HotRoastedOreItem) {
                    int state = HotRoastedOreItem.getState(stack);
                    if (state == HotRoastedOreItem.STATE_HOT) {
                        player.hurt(DamageSource.ON_FIRE, 2.0F);
                        break; // Наносим урон только один раз за тик, даже если несколько горячих предметов
                    }
                }
            }
        }
    }
}