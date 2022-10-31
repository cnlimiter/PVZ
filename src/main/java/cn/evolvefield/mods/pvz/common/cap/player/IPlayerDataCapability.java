package cn.evolvefield.mods.pvz.common.cap.player;

import net.minecraft.world.entity.player.Player;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 14:10
 * Description:
 */
public interface IPlayerDataCapability {

    public void init(Player pl);

    public PlayerDataManager getPlayerData();
}
