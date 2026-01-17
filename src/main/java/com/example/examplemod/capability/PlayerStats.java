// === FILE src/main/java/com/example/examplemod/capability/PlayerStats.java
package com.example.examplemod.capability;

import com.example.examplemod.Config;

public class PlayerStats implements IPlayerStats {
    private int thirst;
    private int fatigue;
    private int poison  = 0;
    private int virus   = 0;
    private int cold    = 0;
    private int hypothermia = 0;
    private int blood   = 100;

    public PlayerStats() {
        // Берём дефолты из конфига, чтобы стартовые значения были едины во всех механиках/GUI.
        // Если конфиг ещё не подгрузился, Forge всё равно отдаст дефолт из SPEC.
        this.thirst = clamp01to100(Config.THIRST.get());
        this.fatigue = clamp01to100(Config.FATIGUE.get());
    }

    private static int clamp01to100(int v) {
        return Math.max(0, Math.min(100, v));
    }

    @Override public int  getThirst()        { return thirst; }
    @Override public void setThirst(int v)   { this.thirst = v; }
    @Override public int  getFatigue()       { return fatigue; }
    @Override public void setFatigue(int v)  { this.fatigue = v; }
    @Override public int  getPoison()        { return poison; }
    @Override public void setPoison(int v)   { this.poison = v; }
    @Override public int  getVirus()         { return virus; }
    @Override public void setVirus(int v)    { this.virus = v; }
    @Override public int  getCold()          { return cold; }
    @Override public void setCold(int v)     { this.cold = v; }
    @Override public int  getHypothermia()   { return hypothermia; }
    @Override public void setHypothermia(int v) { this.hypothermia = v; }
    @Override public int  getBlood()         { return blood; }
    @Override public void setBlood(int v)    { this.blood = v; }
}
