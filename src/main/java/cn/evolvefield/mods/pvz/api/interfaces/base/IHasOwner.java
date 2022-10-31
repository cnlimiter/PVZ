package cn.evolvefield.mods.pvz.api.interfaces.base;

import java.util.Optional;
import java.util.UUID;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/30 20:03
 * Description:
 */
public interface IHasOwner {
    /**
     * @return 所有者uuid
     */
    Optional<UUID> getOwnerUUID();
}
