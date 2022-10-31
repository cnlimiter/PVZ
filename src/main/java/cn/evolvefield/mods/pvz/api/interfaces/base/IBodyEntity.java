package cn.evolvefield.mods.pvz.api.interfaces.base;

import cn.evolvefield.mods.pvz.api.enums.BodyType;
import cn.evolvefield.mods.pvz.api.interfaces.types.IZombieType;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/31 1:03
 * Description:
 */
public interface IBodyEntity {
    IZombieType getZombieType();

    BodyType getBodyType();

    boolean hasHandDefence();

    boolean isMini();

    int getAnimTime();
}
