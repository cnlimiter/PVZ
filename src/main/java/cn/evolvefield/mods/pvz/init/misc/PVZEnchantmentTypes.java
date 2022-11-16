package cn.evolvefield.mods.pvz.init.misc;

import cn.evolvefield.mods.pvz.common.item.spawn.card.PlantCardItem;
import cn.evolvefield.mods.pvz.common.item.spawn.card.SummonCardItem;
import cn.evolvefield.mods.pvz.init.misc.PVZItemTags;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * @program: pvzmod-1.16.5
 * @author: HungTeen
 * @create: 2022-02-06 14:24
 **/
public class PVZEnchantmentTypes {

    public static final EnchantmentCategory SUMMON_CARD = EnchantmentCategory.create("summon_card", (item) -> {
        return item instanceof SummonCardItem;
    });

    public static final EnchantmentCategory ENTITY_CARD = EnchantmentCategory.create("entity_card", (item) -> {
        if(item instanceof PlantCardItem) {
            return ! ((PlantCardItem) item).plantType.getPlantBlock().isPresent() && ! ((PlantCardItem) item).plantType.isOuterPlant();
        }
        return false;
    });

    public static final EnchantmentCategory PLANT_OR_OUTER_CARD = EnchantmentCategory.create("plant_or_outer_card", (item) -> {
        if(item instanceof PlantCardItem) {
            return ! ((PlantCardItem) item).plantType.getPlantBlock().isPresent();
        }
        return false;
    });

    public static final EnchantmentCategory NO_OUTER_PLANT_CARD = EnchantmentCategory.create("no_outer_plant_card", (item) -> {
        if(item instanceof PlantCardItem) {
            return ! ((PlantCardItem) item).plantType.isOuterPlant();
        }
        return false;
    });

    public static final EnchantmentCategory PLANT_CARD = EnchantmentCategory.create("plant_card", (item) -> {
        return item instanceof PlantCardItem;
    });

    public static final EnchantmentCategory SHOVEL = EnchantmentCategory.create("shovel", (item) -> {
        return item instanceof ShovelItem;
    });

    public static final EnchantmentCategory REACH = EnchantmentCategory.create("reach", (item) -> {
        return item.getDefaultInstance().is(PVZItemTags.REACH_ITEMS);
    });

    public static EnchantmentCategory[] getPVZEnchantmentCategorys(){
        return new EnchantmentCategory[]{SUMMON_CARD, ENTITY_CARD, PLANT_OR_OUTER_CARD, NO_OUTER_PLANT_CARD, PLANT_CARD, SHOVEL, REACH};
    }

}
