package cn.evolvefield.mods.pvz.common.enchantment.card.plantcard;

import cn.evolvefield.mods.pvz.init.registry.EnchantmentRegister;
import cn.evolvefield.mods.pvz.common.enchantment.PVZEnchantment;
import cn.evolvefield.mods.pvz.init.misc.PVZEnchantmentTypes;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class BreakOutEnchantment extends PVZEnchantment {

	public BreakOutEnchantment() {
		super(Rarity.VERY_RARE, PVZEnchantmentTypes.PLANT_OR_OUTER_CARD, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
		this.isTradeable = false;
	}

	public static void checkAndBreakOut(PVZPlantEntity plantEntity, ItemStack stack) {
		if(plantEntity.canStartSuperMode() && plantEntity.getRandom().nextFloat() * 100 < BreakOutEnchantment.getPlantBreakOutChance(stack)) {
			plantEntity.startSuperMode(false);
		}
	}

	private static float getPlantBreakOutChance(ItemStack stack) {
		final int lvl = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegister.BREAK_OUT.get(), stack);
		return lvl == 0 ? 0F : 2.5F * (lvl + 1);
	}

	@Override
	public int getMinCost(int enchantmentLevel) {
		return enchantmentLevel * 25;
	}

	@Override
	public int getMaxCost(int enchantmentLevel) {
		return getMinCost(enchantmentLevel) + 10;
	}

	@Override
	public int getMaxLevel() {
		return 4;
	}

}
