package cn.evolvefield.mods.pvz.api.interfaces.util;

import net.minecraft.world.effect.MobEffectInstance;

import java.util.Optional;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/11/15 13:17
 * Description:
 */
public interface IIceEffect {
    Optional<MobEffectInstance> getColdEffect();

    Optional<MobEffectInstance> getFrozenEffect();
}
