package cn.evolvefield.mods.pvz.common.item.tool.zombie;

import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ScreenDoorItem extends ShieldItem {

	public ScreenDoorItem() {
		super(new Properties().stacksTo(1).durability(1600).tab(PVZItemGroups.PVZ_USEFUL));
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 144000;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		return repair.getItem() == Items.IRON_INGOT;
	}

	@Override
	public boolean isShield(ItemStack stack, LivingEntity entity) {
		return true;
	}

}
