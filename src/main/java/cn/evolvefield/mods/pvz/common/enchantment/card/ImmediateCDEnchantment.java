package cn.evolvefield.mods.pvz.common.enchantment.card;

import cn.evolvefield.mods.pvz.common.enchantment.EnchantmentRegister;
import cn.evolvefield.mods.pvz.common.enchantment.PVZEnchantment;
import cn.evolvefield.mods.pvz.common.enchantment.PVZEnchantmentTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Random;

public class ImmediateCDEnchantment extends PVZEnchantment {

	public ImmediateCDEnchantment() {
		super(Rarity.UNCOMMON, PVZEnchantmentTypes.SUMMON_CARD, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
	}

	public static boolean canImmediateCD(ItemStack stack, Random rand) {
		final int lvl = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegister.IMMEDIATE_CD.get(), stack);
		final float chance = (lvl == 1 ? 0.05F : lvl == 2 ? 0.1F : 0.2F);
		return lvl > 0 && rand.nextFloat() < chance;
	}

	@Override
	public int getMinCost(int enchantmentLevel) {
		return enchantmentLevel * 20 - 15;
	}

	@Override
	public int getMaxCost(int enchantmentLevel) {
		return this.getMinCost(enchantmentLevel) + 15;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

}
