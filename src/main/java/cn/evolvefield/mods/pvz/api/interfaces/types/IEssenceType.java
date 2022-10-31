package cn.evolvefield.mods.pvz.api.interfaces.types;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/31 0:53
 * Description:
 */
public interface IEssenceType {

    /**
     * tags contain blocks which can interact with {@link OriginBlock} to be radiated.
     */
    Optional<TagKey<Block>> getRadiationBlockTag();

    /**
     * corresponding block that can be radiated to essence ore.<br>
     * players can modify the block tag to add or delete corresponding blocks.
     */
    Optional<Block> getRadiationBlock();

    /**
     * corresponding essence item.
     */
    Item getEssenceItem();

    /**
     * corresponding essence ore.
     */
    Block getEssenceOre();

    String toString();
}
