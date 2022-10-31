package cn.evolvefield.mods.pvz.common.enchantment.card.plantcard;

import cn.evolvefield.mods.pvz.common.enchantment.EnchantmentRegister;
import cn.evolvefield.mods.pvz.common.enchantment.PVZEnchantment;
import cn.evolvefield.mods.pvz.common.enchantment.PVZEnchantmentTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class SoillessPlantEnchantment extends PVZEnchantment {

	public SoillessPlantEnchantment() {
		super(Rarity.VERY_RARE, PVZEnchantmentTypes.NO_OUTER_PLANT_CARD, new EquipmentSlot[] { EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND });
		this.isTradeable = false;
	}

	public static boolean isSoilless(ItemStack stack) {
		return EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegister.SOILLESS_PLANT.get(), stack) > 0;
	}

    @Override
	public int getMinCost(int enchantmentLevel) {
		return 40;
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
