package cn.evolvefield.mods.pvz.api.interfaces.attract;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/30 20:08
 * Description:实体是否会被防御性植物吸住，如核桃
 */
public interface ICanBeAttracted {
    /**
     * @param defender 防御者
     * @return 是否吸住
     */
    boolean canBeAttractedBy(ICanAttract defender);

    void attractBy(ICanAttract defender);
}

