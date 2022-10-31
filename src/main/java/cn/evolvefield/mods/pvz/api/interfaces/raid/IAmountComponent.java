package cn.evolvefield.mods.pvz.api.interfaces.raid;

import com.google.gson.JsonElement;

public interface IAmountComponent {

    int getSpawnAmount();

    /**
     * make sure constructer has no argument,
     * and use this method to initiate instance.
     */
    void readJson(JsonElement json);
}
