package cn.evolvefield.mods.pvz.common.item;

import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class PVZMiscItem extends Item{

	public PVZMiscItem() {
		super(new Item.Properties().tab(PVZItemGroups.PVZ_MISC));
	}

	public PVZMiscItem(Properties p) {
		super(p.tab(PVZItemGroups.PVZ_MISC));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> textComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, world, textComponents, tooltipFlag);
	}

}
