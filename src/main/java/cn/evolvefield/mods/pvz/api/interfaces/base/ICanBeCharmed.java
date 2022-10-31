package cn.evolvefield.mods.pvz.api.interfaces.base;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/30 20:07
 * Description: 检查生物是否被吸引
 */
public interface ICanBeCharmed {

    boolean isCharmed();

    void onCharmedBy(@Nullable LivingEntity entity);
}
