package cn.evolvefield.mods.pvz.common.enchantment.misc;

import cn.evolvefield.mods.pvz.common.enchantment.PVZEnchantment;
import cn.evolvefield.mods.pvz.init.misc.PVZEnchantmentTypes;
import net.minecraft.world.entity.EquipmentSlot;

public class SunShovelEnchantment extends PVZEnchantment {

	public SunShovelEnchantment() {
		super(Rarity.RARE, PVZEnchantmentTypes.SHOVEL, new EquipmentSlot[] { EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND });
		this.isTradeable = false;
	}

	@Override
	public int getMaxCost(int enchantmentLevel) {
		return this.getMinCost(enchantmentLevel) + 10;
	}

	@Override
	public int getMinCost(int enchantmentLevel) {
		return 10 * enchantmentLevel + 20;
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

}
