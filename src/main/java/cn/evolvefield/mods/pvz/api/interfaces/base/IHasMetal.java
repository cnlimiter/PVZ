package cn.evolvefield.mods.pvz.api.interfaces.base;

import cn.evolvefield.mods.pvz.api.enums.MetalTypes;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/30 20:13
 * Description:僵尸是否有额外装备
 */
public interface IHasMetal {
    /**
     *
     * @return 是否有装备
     */
    boolean hasMetal();

    /**
     * 减少装备
     */
    void decreaseMetal();

    /**
     * 增加装备
     */
    void increaseMetal();

    /**
     *
     * @return 获取装备的种类
     */
    MetalTypes getMetalType();
}
