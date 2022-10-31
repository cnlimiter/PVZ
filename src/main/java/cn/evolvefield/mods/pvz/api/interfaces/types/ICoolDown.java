package cn.evolvefield.mods.pvz.api.interfaces.types;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/30 20:20
 * Description: 召唤卡使用的冷却
 * {@link PlantType}
 */
public interface ICoolDown {
    /**
     *
     * @param lvl 等级
     * @return 冷却时间
     */
    int getCD(int lvl);

    /**
     *
     * @return 翻译的key
     */
    String getTranslateKey();

}
