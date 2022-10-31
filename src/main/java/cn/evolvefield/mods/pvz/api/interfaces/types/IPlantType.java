package cn.evolvefield.mods.pvz.api.interfaces.types;

import cn.evolvefield.mods.pvz.api.interfaces.paz.IPlantEntity;
import cn.evolvefield.mods.pvz.api.interfaces.paz.IPlantInfo;
import cn.evolvefield.mods.pvz.api.interfaces.paz.IPlantModel;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/31 1:01
 * Description:
 */
public interface IPlantType extends IPAZType {

    /**
     * get (plant type, plant entity interface) pair.
     */
//	Optional<Pair<MobEntity, IPlantEntity>> getPlantEntityType();

    /**
     * get the entity model of plant (Client Side).
     */
    //@OnlyIn(Dist.CLIENT)
    Optional<IPlantModel<? extends IPlantEntity>> getPlantModel();

    /**
     * what type the plant upgrade from.
     */
    Optional<IPlantType> getUpgradeFrom();

    /**
     * what type the plant upgrade to.
     */
    Optional<IPlantType> getUpgradeTo();

    /**
     * can plant card place on.
     */
    ICardPlacement getPlacement();

    /**
     * the shroom type : it need sleep at night. <br>
     * such as Puff Shroom.
     */
    boolean isShroomPlant();

    /**
     * the block type : it's not an entity, but a block. <br>
     * get corresponding block, such as Lily Pad.
     */
    Optional<Block> getPlantBlock();

    /**
     * the water type : it only lives in water. <br>
     * such as Tangle Kelp.
     */
    boolean isWaterPlant();

    /**
     * the outer type : it's not an entity, but a render layer. <br>
     * such as Pumpkin.
     */
    boolean isOuterPlant();

    /**
     * get corresponding outer plant info.
     */
    Optional<IPlantInfo> getOuterPlant();
}
