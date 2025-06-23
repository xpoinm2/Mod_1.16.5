// === FILE src/main/java/com/example/examplemod/BlockBreakHandler.java
package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.util.Hand;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockBreakHandler {

    @SubscribeEvent
    public static void onBlockBreak(BreakEvent event) {
        // 1) Получаем мир и проверяем — это сервер?
        World world = (World) event.getWorld();
        if (world.isClientSide()) {
            return;
        }

        // 2) Получаем состояние блока
        BlockState state = event.getState();
        // 3) Если это любой лог или листья по тегам
        if (state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)) {
            // 4) Берём игрока и его основной слот
            PlayerEntity player = (PlayerEntity) event.getPlayer();
            ItemStack held = event.getPlayer().getItemInHand(Hand.MAIN_HAND);
            // 5) Если в руках не топор — отменяем ломание
            if (!(held.getItem() instanceof AxeItem)) {
                event.setCanceled(true);
            }
        }
    }
}
