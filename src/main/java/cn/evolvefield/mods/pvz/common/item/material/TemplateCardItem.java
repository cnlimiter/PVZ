package cn.evolvefield.mods.pvz.common.item.material;

import cn.evolvefield.mods.pvz.api.interfaces.types.IRankType;
import cn.evolvefield.mods.pvz.common.item.PVZMiscItem;
import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import net.minecraft.world.item.Item;

public class TemplateCardItem extends PVZMiscItem {

	public final IRankType Rank;

	public TemplateCardItem(IRankType rank) {
		super(new Item.Properties().tab(PVZItemGroups.PVZ_MISC));
		this.Rank = rank;
	}

}
