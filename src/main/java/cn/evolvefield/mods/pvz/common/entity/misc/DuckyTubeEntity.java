package cn.evolvefield.mods.pvz.common.entity.misc;

import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieToolBase;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidType;

public class DuckyTubeEntity extends PVZZombieToolBase {

//	private static final float UP_SPEED = 0.05f;

	public DuckyTubeEntity(EntityType<? extends Mob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
	}

//	@Override
//	protected void func_110147_ax() {
//		super.func_110147_ax();
//		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ZombieUtil.LITTLE_FAST);
//	}

//	@Override
//	public void livingTick() {
//		super.livingTick();
//		if(!world.isRemote) {//swim up
//			if(this.isInWater() && this.getSubmergedHeight() > this.getEyeHeight()){
//				Vec3d v = this.getMotion();
//				this.setMotion(v.getX(), UP_SPEED, v.getZ());
//			}
//		}
//	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return new EntityDimensions(0.2f, 0.2f, false);
	}

	@Override
	public double getPassengersRidingOffset() {
		return -0.7f;
	}

	@Override
	protected float getWaterSlowDown() {
		return 0.93f;
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	@Override
	public boolean rideableUnderWater() {
		return true;
	}


	@Override
	public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
		return true;
	}

}
