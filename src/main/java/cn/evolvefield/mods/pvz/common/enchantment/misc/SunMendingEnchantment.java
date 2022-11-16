package cn.evolvefield.mods.pvz.common.enchantment.misc;

import cn.evolvefield.mods.pvz.init.registry.EnchantmentRegister;
import cn.evolvefield.mods.pvz.common.enchantment.PVZEnchantment;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import static cn.evolvefield.mods.pvz.common.enchantment.misc.EnergyTransferEnchantment.EnchantmentType;

public class SunMendingEnchantment extends PVZEnchantment {

	public SunMendingEnchantment() {
		super(Rarity.VERY_RARE, EnchantmentType.BREAKABLE, EquipmentSlot.values());
		this.isTradeable = false;
		this.isTreasureOnly = true;
	}

	public static void repairItem(ItemStack stack, int amount) {
		if (! stack.isEmpty() && stack.isDamaged()) {
			final int lvl = getLevel(stack);
			final int needSunEach = Math.max(5, 30 - 5 * lvl);
			final int repairDamage = Math.min(stack.getDamageValue(), Mth.floor(amount * stack.getXpRepairRatio() / needSunEach));
            stack.setDamageValue(stack.getDamageValue() - repairDamage);
        }
	}

	public static int getLevel(ItemStack stack) {
		return EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegister.SUN_MENDING.get(), stack);
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
	protected boolean checkCompatibility(Enchantment enchant) {
		return super.checkCompatibility(enchant) || enchant == Enchantments.MENDING;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

}
