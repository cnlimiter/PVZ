package cn.evolvefield.mods.pvz.utils;

import cn.evolvefield.mods.pvz.init.registry.EnchantmentRegister;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EnchantmentUtil {

	public static int getSunShovelAmount(ItemStack stack, int amount) {
		int lvl = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegister.SUN_SHOVEL.get(), stack);
		float percent = Math.min(1F, 0.1F * lvl);
		return Mth.floor(percent * amount);
	}

}
