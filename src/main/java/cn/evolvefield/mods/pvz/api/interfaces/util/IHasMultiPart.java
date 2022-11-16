package cn.evolvefield.mods.pvz.api.interfaces.util;

import cn.evolvefield.mods.pvz.common.entity.PVZMultiPartEntity;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/11/14 0:25
 * Description:
 */
public interface IHasMultiPart {

    void resetParts();

    void removeParts();

    void updateParts();

    /**
     * get all part entities the zombie own.
     */
    PVZMultiPartEntity[] getMultiParts();
}
