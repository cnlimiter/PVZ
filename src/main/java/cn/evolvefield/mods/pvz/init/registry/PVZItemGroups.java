package cn.evolvefield.mods.pvz.init.registry;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 18:22
 * Description:
 */
public class PVZItemGroups {
    public static final CreativeModeTab PVZ_MISC = new CreativeModeTab("pvz_misc") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemRegister.PEA.get());
        }

    }.setEnchantmentCategories(PVZEnchantmentTypes.getPVZEnchantmentTypes());

    public static final CreativeModeTab PVZ_PLANT_CARD = new CreativeModeTab("pvz_plant_card") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemRegister.PEA_SHOOTER_CARD.get());
        }
    };

    public static final CreativeModeTab PVZ_USEFUL = new CreativeModeTab("pvz_useful") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemRegister.PEA_GUN.get());
        }
    };

    public static final CreativeModeTab PVZ_ENVELOPE = new CreativeModeTab("pvz_envelope") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemRegister.CHALLENGE_ENVELOPE.get());
        }
    };
}
