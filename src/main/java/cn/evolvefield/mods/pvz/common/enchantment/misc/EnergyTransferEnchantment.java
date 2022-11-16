package cn.evolvefield.mods.pvz.common.enchantment.misc;

import cn.evolvefield.mods.pvz.common.enchantment.PVZEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class EnergyTransferEnchantment extends PVZEnchantment {


	public EnergyTransferEnchantment() {
		super(Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMinCost(int enchantmentLevel) {
		return 30;
	}

	@Override
	public int getMaxCost(int enchantmentLevel) {
		return 100;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

}
