package cn.evolvefield.mods.pvz.api.interfaces.types;

import net.minecraft.world.level.block.Block;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/31 1:01
 * Description:
 */
public interface ICardPlacement {
    /**
     * {@link PVZPlantEntity#shouldWilt()}
     */
    boolean canPlaceOnBlock(Block block);

}
