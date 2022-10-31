package cn.evolvefield.mods.pvz.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class PVZEnchantment extends Enchantment {

	protected boolean isTradeable = true;
	protected boolean isCurse = false;
	protected boolean isTreasureOnly = false;

	public PVZEnchantment(Rarity rarity, EnchantmentCategory enchant, EquipmentSlot[] slotTypes) {
		super(rarity, enchant, slotTypes);
	}

	@Override
	public boolean isTradeable() {
		return this.isTradeable;
	}

	@Override
	public boolean isCurse() {
		return this.isCurse;
	}

	@Override
	public boolean isTreasureOnly() {
		return this.isTreasureOnly;
	}

}
