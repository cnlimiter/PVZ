package cn.evolvefield.mods.pvz.common.entity.ai.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class LavaZombiePathNavigator extends GroundPathNavigation {

	public LavaZombiePathNavigator(Mob p_i45875_1_, Level p_i45875_2_) {
		super(p_i45875_1_, p_i45875_2_);
	}

	@Override
	protected boolean hasValidPathType(BlockPathTypes p_230287_1_) {
		return p_230287_1_ != BlockPathTypes.LAVA && p_230287_1_ != BlockPathTypes.DAMAGE_FIRE
				&& p_230287_1_ != BlockPathTypes.DANGER_FIRE ? super.hasValidPathType(p_230287_1_) : true;
	}

	@Override
	public boolean isStableDestination(BlockPos p_188555_1_) {
		return this.level.getBlockState(p_188555_1_).is(Blocks.LAVA) || super.isStableDestination(p_188555_1_);
	}

}
