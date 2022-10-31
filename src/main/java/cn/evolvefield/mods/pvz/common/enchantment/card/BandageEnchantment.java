package cn.evolvefield.mods.pvz.common.enchantment.card;

import cn.evolvefield.mods.pvz.common.enchantment.EnchantmentRegister;
import cn.evolvefield.mods.pvz.common.enchantment.PVZEnchantment;
import cn.evolvefield.mods.pvz.common.enchantment.PVZEnchantmentTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class BandageEnchantment extends PVZEnchantment {

    public BandageEnchantment() {
        super(Rarity.UNCOMMON, PVZEnchantmentTypes.SUMMON_CARD, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    public static float getHealPercent(ItemStack stack){
        final int lvl = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegister.CARD_HEAL.get(), stack);
        return 0.2F * (lvl + 1);
    }
    @Override
    public int getMinCost(int enchantmentLevel) {
        return enchantmentLevel * 10;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return this.getMinCost(enchantmentLevel) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }
}
