package cn.evolvefield.mods.pvz.api.interfaces.types;

import cn.evolvefield.mods.pvz.api.interfaces.paz.IZombieEntity;
import cn.evolvefield.mods.pvz.api.interfaces.paz.IZombieModel;

import java.util.Optional;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/31 1:04
 * Description:
 */
public interface IZombieType extends IPAZType {

    /**
     * get (zombie type, zombie entity interface) pair.
     */
//	Optional<Pair<MobEntity, IZombieEntity>> getZombieEntityType();

    /**
     * get the entity model of plant (Client Side).
     */
    //@OnlyIn(Dist.CLIENT)
    Optional<IZombieModel<? extends IZombieEntity>> getZombieModel1();

    /**
     * get the entity model of plant (Client Side).
     */
    //@OnlyIn(Dist.CLIENT)
    Optional<IZombieModel<? extends IZombieEntity>> getZombieModel2();
}
