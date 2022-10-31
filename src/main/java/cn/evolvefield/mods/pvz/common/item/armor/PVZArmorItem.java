package cn.evolvefield.mods.pvz.common.item.armor;

import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

/**
 * @program: pvzmod-1.16.5
 * @author: HungTeen
 * @create: 2022-02-05 21:14
 **/
public abstract class PVZArmorItem extends ArmorItem {

    public PVZArmorItem(ArmorMaterial armorMaterial, EquipmentSlot slotType) {
        super(armorMaterial, slotType, new Properties().tab(PVZItemGroups.PVZ_USEFUL));
    }
}
