package cn.evolvefield.mods.pvz.common.enchantment.card;

import cn.evolvefield.mods.pvz.common.enchantment.PVZEnchantment;
import cn.evolvefield.mods.pvz.common.enchantment.PVZEnchantmentTypes;
import net.minecraft.world.entity.EquipmentSlot;

public class CharmEnchantment extends PVZEnchantment {

	public CharmEnchantment() {
		super(Rarity.VERY_RARE, PVZEnchantmentTypes.ENTITY_CARD, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
		this.isTradeable = false;
		this.isTreasureOnly = true;
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
