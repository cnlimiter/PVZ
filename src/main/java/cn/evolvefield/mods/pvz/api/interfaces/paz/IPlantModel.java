package cn.evolvefield.mods.pvz.api.interfaces.paz;

import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/31 1:02
 * Description:
 */
public interface IPlantModel<T extends PVZPlantEntity> {
    @OnlyIn(Dist.CLIENT)
    EntityModel<T> getPlantModel();
}
