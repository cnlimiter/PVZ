package cn.evolvefield.mods.pvz.api.interfaces.base;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/31 1:24
 * Description:
 */
public interface IChallenge {
    BlockPos getCenter();

    ServerLevel getWorld();
}
