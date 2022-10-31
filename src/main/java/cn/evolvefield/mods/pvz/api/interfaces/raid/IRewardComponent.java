package cn.evolvefield.mods.pvz.api.interfaces.raid;

import cn.evolvefield.mods.pvz.api.interfaces.base.IChallenge;
import com.google.gson.JsonElement;
import net.minecraft.server.level.ServerPlayer;

public interface IRewardComponent {

    void reward(ServerPlayer player);

    void rewardGlobally(IChallenge challenge);

    /**
     * make sure constructer has no argument,
     * and use this method to initiate instance.
     */
    void readJson(JsonElement json);
}
