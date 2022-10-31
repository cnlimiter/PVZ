package cn.evolvefield.mods.pvz.api.interfaces.raid;

import com.google.gson.JsonObject;

import java.util.List;

public interface IWaveComponent {

    /**
     * make sure constructer has no argument, <br>
     * and use this method to initiate instance.
     */
    boolean readJson(JsonObject json);

    /**
     * how many ticks needed for players to prepare this wave.
     */
    int getPrepareCD();

    /**
     * how many ticks will this wave last.
     */
    int getLastDuration();

    /**
     * get spawn component list.
     */
    List<ISpawnComponent> getSpawns();

    IPlacementComponent getPlacement();
}
