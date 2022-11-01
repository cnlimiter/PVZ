package cn.evolvefield.mods.pvz.common.block.plants;

import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;

public class CornBlock extends CropBlock {

	public CornBlock(Properties builder) {
		super(builder);
	}



	@Override
	protected ItemLike getBaseSeedId() {
		return ItemRegister.CORN_SEEDS.get();
	}

}
