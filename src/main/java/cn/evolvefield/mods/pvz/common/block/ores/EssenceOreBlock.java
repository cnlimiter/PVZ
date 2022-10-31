package cn.evolvefield.mods.pvz.common.block.ores;

import com.hungteen.pvz.api.types.IEssenceType;
import net.minecraft.block.Blocks;
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
