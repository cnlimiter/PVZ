package cn.evolvefield.mods.pvz.common.enchantment.misc;

import cn.evolvefield.mods.pvz.common.enchantment.PVZEnchantment;
import net.minecraft.world.entity.EquipmentSlot;

public class EnergyTransferEnchantment extends PVZEnchantment {

	private static final Object EnchantmentType = ;

	public EnergyTransferEnchantment() {
		super(Rarity.UNCOMMON, EnchantmentType.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
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
