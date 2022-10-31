package cn.evolvefield.mods.pvz.common.enchantment.misc;

import cn.evolvefield.mods.pvz.common.enchantment.EnchantmentRegister;
import cn.evolvefield.mods.pvz.common.enchantment.PVZEnchantment;
import cn.evolvefield.mods.pvz.common.enchantment.PVZEnchantmentTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class RangeReachEnchantment extends PVZEnchantment {

    public RangeReachEnchantment() {
        super(Rarity.UNCOMMON, PVZEnchantmentTypes.REACH, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    public static float getReachDistance(ItemStack stack, float range){
        final int lvl = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegister.RANGE_REACH.get(), stack);
        return range + range * (lvl * 0.2F);
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 10 * enchantmentLevel;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return 20 + getMinCost(enchantmentLevel);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

}
