package cn.evolvefield.mods.pvz.api.interfaces.types;

import net.minecraft.network.chat.MutableComponent;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/30 20:16
 * Description:
 */
public interface IIDType {
    /**
     * 获取名字
     * @return 名字
     */
    String toString();

    /**
     * 用于储存nbt数据
     * [mod id]:[type name], 例如 pvz:pea_shooter.
     * @return id名
     */
    String getIdentity();

    /**
     *
     * @return 获取翻译
     */
    MutableComponent getText();

    /**
     *
     * @return 特殊的模组ID
     */
    String getModID();
}
