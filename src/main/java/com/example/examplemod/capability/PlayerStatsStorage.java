// === FILE src/main/java/com/example/examplemod/capability/PlayerStatsStorage.java
package com.example.examplemod.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.Capability;

public class PlayerStatsStorage implements IStorage<IPlayerStats> {
    @Override
    public CompoundNBT writeNBT(Capability<IPlayerStats> cap, IPlayerStats stats, net.minecraft.util.Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("Thirst",  stats.getThirst());
        tag.putInt("Fatigue", stats.getFatigue());
        tag.putInt("Disease", stats.getDisease());
        tag.putInt("Virus",   stats.getVirus());
        tag.putInt("Cold",    stats.getCold());
        tag.putInt("Hypothermia", stats.getHypothermia());
        tag.putInt("Blood",   stats.getBlood());
        return tag;
    }

    @Override
    public void readNBT(Capability<IPlayerStats> cap, IPlayerStats stats, net.minecraft.util.Direction side, net.minecraft.nbt.INBT in) {
        CompoundNBT tag = (CompoundNBT)in;
        stats.setThirst( tag.getInt("Thirst") );
        stats.setFatigue(tag.getInt("Fatigue"));
        stats.setDisease(tag.getInt("Disease"));
        stats.setVirus(  tag.getInt("Virus"));
        stats.setCold(   tag.getInt("Cold"));
        stats.setHypothermia(tag.getInt("Hypothermia"));
        stats.setBlood(  tag.getInt("Blood"));
    }
}

