package com.example.examplemod.client.network;

import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.client.FogClientState;
import com.example.examplemod.client.HurricaneClientState;
import com.example.examplemod.client.screen.CobblestoneDialogScreen;
import com.example.examplemod.network.CobblestoneAnvilProgressPacket;
import com.example.examplemod.network.SyncAllStatsPacket;
import com.example.examplemod.tileentity.CobblestoneAnvilTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Client-only handlers for packets that touch Minecraft client classes.
 */
public final class ClientPacketHandlers {
    private ClientPacketHandlers() {
    }

    public static void openCobblestoneDialog(BlockPos pos) {
        CobblestoneDialogScreen.targetPos = pos;
        Minecraft.getInstance().setScreen(new CobblestoneDialogScreen());
    }

    public static void updateCobblestoneAnvilProgress(CobblestoneAnvilProgressPacket pkt) {
        Minecraft.getInstance().execute(() -> {
            if (Minecraft.getInstance().level != null) {
                TileEntity tileEntity = Minecraft.getInstance().level.getBlockEntity(pkt.getPos());
                if (tileEntity instanceof CobblestoneAnvilTileEntity) {
                    ((CobblestoneAnvilTileEntity) tileEntity).setProgress(pkt.getProgress());
                }
            }
        });
    }

    public static void syncAllStats(SyncAllStatsPacket pkt) {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        Minecraft.getInstance().player
                .getCapability(PlayerStatsProvider.PLAYER_STATS_CAP)
                .ifPresent(stats -> {
                    stats.setThirst(pkt.getThirst());
                    stats.setFatigue(pkt.getFatigue());
                    stats.setCold(pkt.getCold());
                    stats.setHypothermia(pkt.getHypothermia());
                    stats.setVirus(pkt.getVirus());
                    stats.setPoison(pkt.getPoison());
                    stats.setBlood(pkt.getBlood());
                    stats.setWindSpeed(pkt.getWindSpeed());
                });
    }

    public static void setHurricaneActive(boolean active) {
        HurricaneClientState.setActive(active);
    }

    public static void setFogActive(boolean active) {
        FogClientState.setActive(active);
    }
}
