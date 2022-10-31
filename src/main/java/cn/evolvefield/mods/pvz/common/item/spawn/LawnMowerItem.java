package cn.evolvefield.mods.pvz.common.item.spawn;

import cn.evolvefield.mods.pvz.common.item.PVZToolItem;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import com.hungteen.pvz.common.entity.misc.LawnMowerEntity;
import net.minecraft.core.Direction;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

public class LawnMowerItem extends PVZToolItem {

	public LawnMowerItem() {
		super(new Properties());
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		var world = context.getLevel();
		var player = context.getPlayer();
		var hand = context.getHand();
		var stack = player.getItemInHand(hand);
		var pos = context.getClickedPos();
		var spawnPos = pos;
		if (! world.getBlockState(pos).getCollisionShape(world, pos).isEmpty()) {
			spawnPos = pos.relative(context.getClickedFace());
		}
		if (context.getClickedFace() == Direction.UP && world.isEmptyBlock(pos.above())) {// can plant here
			if(!world.isClientSide) {
				LawnMowerEntity entity = (LawnMowerEntity) EntityRegister.LAWN_MOWER.get().spawn((ServerLevel) player.level, stack, player,
					spawnPos, SpawnReason.SPAWN_EGG, true, true);
			    if (entity == null) {
				    System.out.println("Error : lawn mower entity spawn error!");
				    return InteractionResult.FAIL;
			    }
			    entity.setPlacer(player);
			    if (! player.abilities.instabuild) {// reset
				    stack.shrink(1);
			    }
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

}
