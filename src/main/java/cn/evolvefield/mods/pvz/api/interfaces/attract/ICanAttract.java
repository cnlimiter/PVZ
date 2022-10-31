package cn.evolvefield.mods.pvz.api.interfaces.attract;

import net.minecraft.world.entity.LivingEntity;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/30 20:10
 * Description: {@link PlantAttractGoal}
 */
public interface ICanAttract {

    /**
     * @param target 目标
     * @return 是否能被吸住
     */
    boolean canAttract(LivingEntity target);

    /**
     *
     * @param target 要吸住的实体
     */
    void attract(LivingEntity target);

    /**
     *
     * @return 能被吸住的距离
     */
    float getAttractRange();
}
