package cn.evolvefield.mods.pvz.common.cap.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 14:28
 * Description:
 */
public class PlayerDataCapability implements IPlayerDataCapability, INBTSerializable<CompoundTag> {
    private PlayerDataManager playerDataManager = null;
    @Override
    public void init(Player pl) {
        if (playerDataManager == null) {
            playerDataManager = new PlayerDataManager(pl);
        }
    }

    @Override
    public PlayerDataManager getPlayerData() {
        return playerDataManager;
    }

    @Override
    public CompoundTag serializeNBT() {
        return playerDataManager.saveToNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        playerDataManager.loadFromNBT(nbt);
    }
}
