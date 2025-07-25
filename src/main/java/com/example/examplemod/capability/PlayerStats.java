// === FILE src/main/java/com/example/examplemod/capability/PlayerStats.java
package com.example.examplemod.capability;

public class PlayerStats implements IPlayerStats {
    private int thirst  = 40;
    private int fatigue = 40;
    private int disease = 0;
    private int poison  = 0;
    private int virus   = 0;
    private int cold    = 0;
    private int hypothermia = 0;
    private int blood   = 100;

    @Override public int  getThirst()        { return thirst; }
    @Override public void setThirst(int v)   { this.thirst = v; }
    @Override public int  getFatigue()       { return fatigue; }
    @Override public void setFatigue(int v)  { this.fatigue = v; }
    @Override public int  getDisease()       { return disease; }
    @Override public void setDisease(int v)  { this.disease = v; }
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
