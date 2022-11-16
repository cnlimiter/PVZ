package cn.evolvefield.mods.pvz.common.entity.ai.goal;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PVZSwimGoal extends Goal {

	protected final Mob mob;

	public PVZSwimGoal(Mob entity) {
		this.mob = entity;
		this.setFlags(EnumSet.of(Flag.JUMP));
		this.mob.getNavigation().setCanFloat(true);
	}

	@Override
	public boolean canUse() {
		return this.mob.isInWater() && this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getFluidJumpThreshold();
	}

	public void tick() {
		if (this.mob.getRandom().nextFloat() < 0.8F) {
			this.mob.getJumpControl().jump();
		}
	}

}
