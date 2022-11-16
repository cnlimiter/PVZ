package cn.evolvefield.mods.pvz.common.entity.plant.appease;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.utils.AnimationUtil;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
public class ThreePeaterEntity extends PeaShooterEntity {

	private static final double RIGHT_OFFSET = 0.6D;
	private static final double DOWN_OFFSET = - 0.35D;
	private static final int SUPER_CD = 100;

	public ThreePeaterEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void shootBullet() {
		if(this.isPlantInSuperMode()) {
			final int tick = SUPER_CD - this.getSuperTime() % SUPER_CD;
			final double angle = AnimationUtil.upDown(tick, SUPER_CD, 60F);
			this.performShoot(SHOOT_OFFSET, 0, 0, this.getExistTick() % 10 == 0, FORWARD_SHOOT_ANGLE);
			this.performShoot(SHOOT_OFFSET, - RIGHT_OFFSET, DOWN_OFFSET, false, - angle);
			this.performShoot(SHOOT_OFFSET, RIGHT_OFFSET, DOWN_OFFSET, false, angle);
		} else {
			this.performShoot(SHOOT_OFFSET, 0, 0, true, FORWARD_SHOOT_ANGLE);
			this.performShoot(SHOOT_OFFSET, - RIGHT_OFFSET, DOWN_OFFSET, false, FORWARD_LEFT_SHOOT_ANGLE);
			this.performShoot(SHOOT_OFFSET, RIGHT_OFFSET, DOWN_OFFSET, false, FORWARD_RIGHT_SHOOT_ANGLE);
		}
	}

	@Override
	public int getSuperTimeLength() {
		return SUPER_CD;
//		return (this.isPlantInStage(1) ? 1 : this.isPlantInStage(2) ? 2 : 3) * SUPER_CD;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return new EntityDimensions(0.9f, 1.7f, false);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.THREE_PEATER;
	}

}
