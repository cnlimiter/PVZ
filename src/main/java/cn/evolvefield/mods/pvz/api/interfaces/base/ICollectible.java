package cn.evolvefield.mods.pvz.api.interfaces.base;

import net.minecraft.world.entity.LivingEntity;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/11/1 2:01
 * Description:
 */
public interface ICollectible {
    boolean canCollectBy(LivingEntity living);

    void onCollect(LivingEntity living);
}
