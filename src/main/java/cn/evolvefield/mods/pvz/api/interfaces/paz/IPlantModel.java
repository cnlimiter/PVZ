package cn.evolvefield.mods.pvz.api.interfaces.paz;

import net.minecraft.client.model.EntityModel;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/31 1:02
 * Description:
 */
public interface IPlantModel<T extends PVZPlantEntity> {
    EntityModel<T> getPlantModel();
}
