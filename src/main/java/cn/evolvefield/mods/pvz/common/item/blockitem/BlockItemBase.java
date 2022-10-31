package cn.evolvefield.mods.pvz.common.item.blockitem;

import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class BlockItemBase extends BlockItem {

	public BlockItemBase(Block blockIn) {
		super(blockIn,new Properties().tab(PVZItemGroups.PVZ_MISC));
	}

}
