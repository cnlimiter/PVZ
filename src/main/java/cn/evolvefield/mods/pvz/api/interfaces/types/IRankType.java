package cn.evolvefield.mods.pvz.api.interfaces.types;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/30 20:21
 * Description:
 */
public interface IRankType {
    int getWeight();

    int getValue();

    String getName();

    /**
     * 获取相应的模板卡
     */
    Item getTemplateCard();

    /**
     * 获取卡的tag
     */
    TagKey<Item> getCardTag();

    /**
     * 需要的材料
     */
    TagKey<Item> getMaterial();

    /**
     * 阳光点
     */
    int getEnchantPoint();
}
