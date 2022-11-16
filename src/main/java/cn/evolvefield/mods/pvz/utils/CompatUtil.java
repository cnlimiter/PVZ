package cn.evolvefield.mods.pvz.utils;

import cn.evolvefield.mods.pvz.common.entity.AbstractPAZEntity;
import cn.evolvefield.mods.pvz.common.entity.misc.GiftBoxEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/11/16 19:53
 * Description:
 */
public class CompatUtil {
    /**
     * stop bucket my plants and zombies !
     */
    public static boolean canBucketEntity(Level world, Entity entity, ItemStack stack){
        if(stack.getItem().equals(Items.BUCKET)){
            if(entity instanceof AbstractPAZEntity || entity instanceof GiftBoxEntity){
                return false;
            }
        }
        return true;
    }
}
