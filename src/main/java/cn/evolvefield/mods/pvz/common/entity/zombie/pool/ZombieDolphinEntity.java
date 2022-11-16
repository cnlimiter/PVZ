package cn.evolvefield.mods.pvz.common.entity.zombie.pool;

import cn.evolvefield.mods.pvz.common.entity.ai.goal.attack.PVZZombieAttackGoal;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.PoolZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.fluids.FluidType;

public class ZombieDolphinEntity extends PVZZombieEntity {

	public ZombieDolphinEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
		this.moveControl = new MoveHelperController(this);
		this.lookControl = new SmoothSwimmingLookControl(this, 10);
		this.canBeMini = false;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new BreathAirGoal(this));
		this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 2.0F));
		this.goalSelector.addGoal(5, new ZombieDolphinJumpGoal(this, 10));
		this.goalSelector.addGoal(8, new FollowBoatGoal(this));
		this.goalSelector.addGoal(0, new PVZZombieAttackGoal(this, true));
		this.registerTargetGoals();
	}

	@Override
	public VariantType getVariantType() {
		return VariantType.NORMAL;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(1.2F);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(1f, 0.7f);
	}

	@Override
	public float getEatDamage() {
		return ZombieUtil.VERY_LOW;
	}

	@Override
	public float getLife() {
		return 12;
	}

	@Override
	protected float getWaterSlowDown() {
		return 0.89f;
	}

	@Override
	public double getPassengersRidingOffset() {
		return -0.5f;
	}

	public boolean checkSpawnObstruction(LevelReader worldIn) {
		return worldIn.isUnobstructed(this);
	}

	@Override
	public boolean rideableUnderWater() {
		return true;
	}

	@Override
	public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
		return true;
	}


	@Override
	public int getAmbientSoundInterval() {
		return 200;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegister.DOLPHIN_SAY.get();
	}

	@Override
	public MobType getMobType() {
		return MobType.WATER;
	}

	@Override
	protected PathNavigation createNavigation(Level worldIn) {
		return new WaterBoundPathNavigation(this, worldIn);
	}

	@Override
	public ZombieType getZombieType() {
		return PoolZombies.ZOMBIE_DOLPHIN;
	}

	static class MoveHelperController extends MoveControl {
		private final ZombieDolphinEntity dolphin;

		public MoveHelperController(ZombieDolphinEntity dolphinIn) {
			super(dolphinIn);
			this.dolphin = dolphinIn;
		}

		public void tick() {
			if (this.dolphin.isInWater()) {
				this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
			}

			if (this.operation == Operation.MOVE_TO && !this.dolphin.getNavigation().isDone()) {
				double d0 = this.wantedX - this.dolphin.getX();
				double d1 = this.wantedY - this.dolphin.getY();
				double d2 = this.wantedZ - this.dolphin.getZ();
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;
				if (d3 < (double) 2.5000003E-7F) {
					this.mob.setZza(0.0F);
				} else {
					float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
					this.dolphin.setYRot(this.rotlerp(this.dolphin.getYRot(), f, 10.0F));
					this.dolphin.yBodyRot = this.dolphin.getYRot();
					this.dolphin.yHeadRot = this.dolphin.getYRot();
					float f1 = (float) (this.speedModifier
							* this.dolphin.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					if (this.dolphin.isInWater()) {
						this.dolphin.setSpeed(f1 * 0.02F);
						float f2 = -((float) (Mth.atan2(d1, (double) Mth.sqrt((float) (d0 * d0 + d2 * d2)))
								* (double) (180F / (float) Math.PI)));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85.0F, 85.0F);
						this.dolphin.setXRot(this.rotlerp(this.dolphin.getXRot(), f2, 5.0F));
						float f3 = Mth.cos(this.dolphin.getXRot() * ((float) Math.PI / 180F));
						float f4 = Mth.sin(this.dolphin.getXRot() * ((float) Math.PI / 180F));
						this.dolphin.zza = f3 * f1;
						this.dolphin.yya = -f4 * f1;
					} else {
						this.dolphin.setSpeed(f1 * 0.1F);
					}

				}
			} else {
				this.dolphin.setSpeed(0.0F);
				this.dolphin.setXxa(0.0F);
				this.dolphin.setYya(0.0F);
				this.dolphin.setZza(0.0F);
			}
		}
	}

	static class ZombieDolphinJumpGoal extends JumpGoal {
		private static final int[] JUMP_DISTANCES = new int[] { 0, 1, 4, 5, 6, 7 };
		private final ZombieDolphinEntity dolphin;
		private final int chance;
		private boolean breached;

		public ZombieDolphinJumpGoal(ZombieDolphinEntity zombie, int chance) {
			this.dolphin = zombie;
			this.chance = chance;
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean canUse() {
			if (this.dolphin.getRandom().nextInt(this.chance) != 0) {
				return false;
			} else {
				Direction direction = this.dolphin.getMotionDirection();
				int i = direction.getStepX();
				int j = direction.getStepZ();
				BlockPos blockpos = this.dolphin.blockPosition();

				for (int k : JUMP_DISTANCES) {
					if (!this.canJumpTo(blockpos, i, j, k) || !this.isAirAbove(blockpos, i, j, k)) {
						return false;
					}
				}

				return true;
			}
		}

		private boolean canJumpTo(BlockPos pos, int dx, int dz, int scale) {
			BlockPos blockpos = pos.offset(dx * scale, 0, dz * scale);
			return this.dolphin.level.getFluidState(blockpos).is(FluidTags.WATER)
					&& !this.dolphin.level.getBlockState(blockpos).getMaterial().blocksMotion();
		}

		@SuppressWarnings("deprecation")
		private boolean isAirAbove(BlockPos pos, int dx, int dz, int scale) {
			return this.dolphin.level.getBlockState(pos.offset(dx * scale, 1, dz * scale)).isAir()
					&& this.dolphin.level.getBlockState(pos.offset(dx * scale, 2, dz * scale)).isAir();
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean canContinueToUse() {
			double d0 = this.dolphin.getDeltaMovement().y;
			return (!(d0 * d0 < (double) 0.03F) || this.dolphin.getXRot() == 0.0F
					|| !(Math.abs(this.dolphin.getXRot()) < 10.0F) || !this.dolphin.isInWater())
					&& !this.dolphin.onGround;
		}

		public boolean isInterruptable() {
			return false;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void start() {
			Direction direction = this.dolphin.getMotionDirection();
			this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement().add((double) direction.getStepX() * 0.6D, 0.7D,
					(double) direction.getStepZ() * 0.6D));
			this.dolphin.getNavigation().stop();
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by
		 * another one
		 */
		public void stop() {
			this.dolphin.setXRot(0.0F);
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		@SuppressWarnings("deprecation")
		public void tick() {
			boolean flag = this.breached;
			if (!flag) {
				FluidState ifluidstate = this.dolphin.level.getFluidState(this.dolphin.blockPosition());
				this.breached = ifluidstate.is(FluidTags.WATER);
			}

			if (this.breached && !flag) {
				this.dolphin.playSound(SoundEvents.DOLPHIN_JUMP, 1.0F, 1.0F);
			}

			var vec3d = this.dolphin.getDeltaMovement();
			if (vec3d.y * vec3d.y < (double) 0.03F && this.dolphin.getXRot() != 0.0F) {
				this.dolphin.setXRot(Mth.rotlerp(this.dolphin.getXRot(), 0.0F, 0.2F));
			} else {
				double d0 = Math.sqrt(Entity.getHorizontalDistanceSqr(vec3d));
				double d1 = Math.signum(-vec3d.y) * Math.acos(d0 / vec3d.length()) * (double) (180F / (float) Math.PI);
				this.dolphin.setXRot((float) d1);
			}

		}
	}

}
