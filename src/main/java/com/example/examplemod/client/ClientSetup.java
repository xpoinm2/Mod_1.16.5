package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModFluids;
import com.example.examplemod.ModTileEntities;
import com.example.examplemod.client.render.FirepitRenderer;
import com.example.examplemod.tileentity.ClayPotTileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.fluid.Fluid;
import net.minecraft.tileentity.TileEntity;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Вода теперь рендерится через статические JSON модели, TESR не нужен

        // Register firepit smoke renderer
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.FIREPIT.get(), FirepitRenderer::new);
        event.enqueueWork(() -> {
            BlockColors blockColors = Minecraft.getInstance().getBlockColors();
            blockColors.register((state, world, pos, tintIndex) -> {
                if (tintIndex != 0) {
                    return 0xFFFFFF;
                }
                if (world != null && pos != null) {
                    TileEntity tile = world.getBlockEntity(pos);
                    if (tile instanceof ClayPotTileEntity) {
                        Fluid fluid = ((ClayPotTileEntity) tile).getTank().getFluid().getFluid();
                        if (fluid.isSame(ModFluids.DIRTY_WATER.get())
                                || fluid.isSame(ModFluids.DIRTY_WATER_FLOWING.get())) {
                            return 0x8B8B8B;
                        }
                    }
                }
                return 0x3F76E4;
            }, ModBlocks.CLAY_POT.get());
        });
    }
}