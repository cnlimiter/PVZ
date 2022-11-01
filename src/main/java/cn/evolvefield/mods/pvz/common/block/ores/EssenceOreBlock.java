package cn.evolvefield.mods.pvz.common.block.ores;

import cn.evolvefield.mods.pvz.api.interfaces.types.IEssenceType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolType;

public class EssenceOreBlock extends PVZOreBlock {

	public final IEssenceType essence;

	public EssenceOreBlock(IEssenceType e, int light) {
		super(Properties.copy(Blocks.DIAMOND_ORE)
				.strength(9, 9)
				.harvestTool(ToolType.PICKAXE)
				.requiresCorrectToolForDrops()
				.lightLevel(i -> light)
				.harvestLevel(2));
		this.essence = e;
	}

}
