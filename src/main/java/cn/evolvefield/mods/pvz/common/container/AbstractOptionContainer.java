package cn.evolvefield.mods.pvz.common.container;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public abstract class AbstractOptionContainer extends PVZContainer {

	public AbstractOptionContainer(MenuType<?> type, int id) {
		super(type, id);
	}

	public abstract boolean isCraftSlot(Slot slot);

//	public abstract boolean clearCraftSlots();

}
