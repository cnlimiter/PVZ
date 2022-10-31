package cn.evolvefield.mods.pvz.api.interfaces.types;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Optional;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/30 20:19
 * Description:
 */
public interface IPAZType extends IIDType {
    /**
     * get the sun cost of role.
     * how many sun amount will cost when use its card.
     */
    int getSunCost();

    /**
     * get limit maxLevel to use its card.
     * if tree maxLevel doesn't reach requirement, then players can not use its card.
     */
    int getRequiredLevel();

    /**
     * get the experience point of role.
     */
    int getXpPoint();

    /**
     * get the cool down of summon card of current type.
     */
    ICoolDown getCoolDown();

    /**
     * get the rank of type.
     */
    IRankType getRank();

    /**
     * get the essence type that the plant belongs to.
     */
    IEssenceType getEssence();

    /**
     * get the entity type of current type.
     */
    Optional<EntityType<? extends PathfinderMob>> getEntityType();

    /**
     * get summon card item of type.
     */
    Optional<? extends Item> getSummonCard();

    /**
     * get enjoy card item of type.
     */
    Optional<? extends Item> getEnjoyCard();

    List<ISkillType> getSkills();

    /**
     * get type corresponding id in type list.
     * used to sort card item list.
     */
    int getId();

    /**
     * the priority of this group to stay forward in list. <br>
     * larger means higher priority. <br>
     * such as show front in Almanac.
     */
    int getSortPriority();

    /**
     * 类型组的名字。
     */
    String getCategoryName();

    /**
     * 模型的默认渲染比例。
     */
    //@OnlyIn(Dist.CLIENT)
    float getRenderScale();

    /**
     * 渲染类型的纹理
     */
    //@OnlyIn(Dist.CLIENT)
    ResourceLocation getRenderResource();

    ResourceLocation getLootTable();
}
