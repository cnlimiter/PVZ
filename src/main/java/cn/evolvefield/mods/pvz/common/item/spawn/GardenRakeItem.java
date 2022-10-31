package cn.evolvefield.mods.pvz.common.item.spawn;

import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import com.hungteen.pvz.common.entity.misc.GardenRakeEntity;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class GardenRakeItem extends Item {

	public GardenRakeItem() {
		super(new Properties().tab(PVZItemGroups.PVZ_USEFUL));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		var world = context.getLevel();
		Player player = context.getPlayer();
		var hand = context.getHand();
		var stack = player.getItemInHand(hand);
		var pos = context.getClickedPos();
		var spawnPos = pos;
		if (! world.getBlockState(pos).getCollisionShape(world, pos).isEmpty()) {
			spawnPos = pos.relative(context.getClickedFace());
		}
		if (context.getClickedFace() == Direction.UP && world.isEmptyBlock(pos.above())) {// can plant here
			if(!world.isClientSide) {
				GardenRakeEntity entity = (GardenRakeEntity) EntityRegister.GARDEN_RAKE.get().spawn((ServerLevel) player.level, stack, player,
					spawnPos, MobSpawnType.SPAWN_EGG, true, true);
			    if (entity == null) {
				    System.out.println("Error : garden rake entity spawn error!");
				    return InteractionResult.FAIL;
			    }
			    entity.setPlacer(player);
			    entity.summonByOwner(player);
			    if (! player.getAbilities().instabuild) {// reset
				    stack.shrink(1);
			    }
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

}
