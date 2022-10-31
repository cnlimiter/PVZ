package cn.evolvefield.mods.pvz.common.item.tool.zombie;

import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class BobsleCarItem extends Item {

    public BobsleCarItem() {
        super(new Properties().tab(PVZItemGroups.PVZ_USEFUL).stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var hand = context.getHand();
        var player = context.getPlayer();
        var stack = player.getItemInHand(hand);
        var pos = context.getClickedPos();
        var world = context.getLevel();
        if (hand == InteractionHand.OFF_HAND) {//only use right hand can plant
            return InteractionResult.FAIL;
        }
        if (!world.isClientSide && context.getClickedFace() == Direction.UP && world.isEmptyBlock(pos.above())) {//can plant here
            stack.shrink(1);
            var car = EntityRegister.BOBSLE_CAR.get().create(world);
            car.yRot = player.yRot;
            car.setPos(pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D);
            world.addFreshEntity(car);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResult.SUCCESS;
    }

}
