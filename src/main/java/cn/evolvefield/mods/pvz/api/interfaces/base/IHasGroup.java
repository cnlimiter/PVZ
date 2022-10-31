package cn.evolvefield.mods.pvz.api.interfaces.base;

import cn.evolvefield.mods.pvz.api.enums.PVZGroupType;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/30 20:04
 * Description:
 */
public interface IHasGroup {
    /**
     *
     * @return 实体所在的组
     */
    PVZGroupType getEntityGroupType();
}
