package cn.evolvefield.mods.pvz.common.item.material;

import cn.evolvefield.mods.pvz.api.interfaces.types.IEssenceType;
import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import net.minecraft.world.item.Item;

public class EssenceItem extends Item {

	public final IEssenceType essence;

	public EssenceItem(IEssenceType essence) {
		super(new Properties().tab(PVZItemGroups.PVZ_MISC));
		this.essence = essence;
	}

}
