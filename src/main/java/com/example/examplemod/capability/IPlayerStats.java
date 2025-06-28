// === FILE src/main/java/com/example/examplemod/capability/IPlayerStats.java
package com.example.examplemod.capability;

public interface IPlayerStats {
    int  getThirst();
    void setThirst(int v);
    int  getFatigue();
    void setFatigue(int v);
    int  getDisease();
    void setDisease(int v);
    int  getCold();
    void setCold(int v);
    int  getHypothermia();
    void setHypothermia(int v);
    int  getBlood();
    void setBlood(int v);
}
