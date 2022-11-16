package cn.evolvefield.mods.pvz.common.entity.zombie.base;

import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public abstract class SwimmerZombieEntity extends PVZZombieEntity {

	private static final float UP_DISTANCE = 10;

	public SwimmerZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		setPathfindingMalus(BlockPathTypes.WATER, 0);
	}

	@Override
	public void tick() {
		super.tick();
		if(!level.isClientSide) {
			if(this.isInWater()) {
				if(this.getTarget() != null && this.distanceToSqr(this.getTarget()) <= UP_DISTANCE) {
					this.setPose(Pose.SPIN_ATTACK);
				}else {
					this.setPose(Pose.SWIMMING);
				}
			}else {
				this.setPose(Pose.STANDING);
			}
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		if(this.isMiniZombie()) {
			return new EntityDimensions(0.5f, 0.6f, false);
		}
		if(this.getPose() == Pose.SPIN_ATTACK) {
			return new EntityDimensions(0.7f, 1.4f, false);
		}else if(this.getPose() == Pose.SWIMMING) {
			return new EntityDimensions(0.7f, 0.9f, false);
		}
		return new EntityDimensions(0.7f, 1.9f, false);
	}

	@Override
	protected float getWaterSlowDown() {
		return 0.94f;
	}

}
