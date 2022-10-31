package cn.evolvefield.mods.pvz.utils;

import cn.evolvefield.mods.pvz.common.enchantment.EnchantmentRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Mth;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EnchantmentUtil {

	public static int getSunShovelAmount(ItemStack stack, int amount) {
		int lvl = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegister.SUN_SHOVEL.get(), stack);
		float percent = Math.min(1F, 0.1F * lvl);
		return Mth.floor(percent * amount);
	}

}
