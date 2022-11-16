package cn.evolvefield.mods.pvz.common.entity.zombie.grass;

import cn.evolvefield.mods.pvz.api.interfaces.attract.ICanAttract;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.ai.goal.target.PVZRandomTargetGoal;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.body.ZombieDropBodyEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.pool.DiggerZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.impl.zombie.GrassZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EffectUtil;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.MathUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Optional;

public class PoleZombieEntity extends PVZZombieEntity {

	private static final EntityDataAccessor<Boolean> HAS_POLE = SynchedEntityData.defineId(DiggerZombieEntity.class, EntityDataSerializers.BOOLEAN);
	protected final float HorizontalJumpSpeed = 1.5F;
	protected final float VerticalJumpSpeed = 0.7F;
	protected Vec3 jumpDstPoint = Vec3.ZERO;
	protected int pole_jump_cnt;

	public PoleZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(HAS_POLE, true);
	}

	@Override
	protected void registerAttackGoals() {
		super.registerAttackGoals();
		this.goalSelector.addGoal(0, new PoleJumpGoal(this));
	}

	@Override
	protected void registerTargetGoals() {
		this.targetSelector.addGoal(0, new PVZRandomTargetGoal(this, true, true, ZombieUtil.NORMAL_TARGET_RANGE, ZombieUtil.LOW_TARGET_HEIGHT));
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
		super.onSyncedDataUpdated(data);
		if(data.equals(HAS_POLE)) {
			this.addEffect(EffectUtil.effect(MobEffects.MOVEMENT_SLOWDOWN, 1000000, 0));
		}
	}

	@Override
	public boolean canBeAttractedBy(ICanAttract defender) {
		if(defender instanceof PVZPlantEntity) {
			final IPlantType plant = ((PVZPlantEntity) defender).getPlantType();
			return plant == PVZPlants.TALL_NUT;
		}
		return true;
	}

	@Override
	public void attractBy(ICanAttract defender) {
		super.attractBy(defender);
		if(this.hasPole()) {
		    this.setPole(false);
		    this.setDeltaMovement(0, 0, 0);
		    EntityUtil.playSound(this, SoundRegister.HAMMER_BONK.get());
		}
	}

	@Override
	public boolean canBeTargetBy(LivingEntity living) {
		return super.canBeTargetBy(living) && ! this.isPoleJumping();
	}

	@Override
	protected boolean isZombieInvulnerableTo(DamageSource source) {
		return super.isZombieInvulnerableTo(source) || this.isPoleJumping();
	}

	@Override
	protected void setBodyStates(ZombieDropBodyEntity body) {
		super.setBodyStates(body);
		body.setHandDefence(this.hasPole());
	}

	/**
	 * set jump and motion.
	 * {@link PoleJumpGoal#tick()}
	 */
	public void perfromJump() {
		Optional.ofNullable(this.getTarget()).ifPresent(target -> {
			Vec3 vec = MathUtil.getHorizontalNormalizedVec(this.position(), this.jumpDstPoint);
			final double speedXZ = this.HorizontalJumpSpeed + (this.random.nextDouble() - 0.3D) / 2;
			final double speedY = this.VerticalJumpSpeed + (this.random.nextDouble() - 0.3D) / 2;
			this.setDeltaMovement(vec.x * speedXZ , speedY, vec.z * speedXZ);
			EntityUtil.playSound(this, SoundRegister.POLE_JUMP.get());
		});
	}

	/**
	 * how many times can it jump.
	 */
	public int getMaxJumpCount() {
		return 1;
	}

	@Override
	public float getWalkSpeed() {
		return ZombieUtil.WALK_FAST;
	}

	@Override
	public float getLife() {
		return 50;
	}

	/**
	 * time to anim jump.
	 */
	public int getPoleJumpCD() {
		return 20;
	}

	/**
	 * Common plants can not target jumping PoleZombie.
	 * {@link PVZPlantEntity#checkCanPAZTarget(Entity)}
	 */
	public boolean isPoleJumping() {
		return this.getAttackTime() > 0;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("zombie_has_pole")) {
			this.setPole(compound.getBoolean("zombie_has_pole"));
		}
		if(compound.contains("jump_dst_point")) {
			CompoundTag nbt = compound.getCompound("jump_dst_point");
			this.jumpDstPoint = new Vec3(nbt.getDouble("XXX"), nbt.getDouble("YYY"), nbt.getDouble("ZZZ"));
		}
		if(compound.contains("pole_jump_count")) {
			this.pole_jump_cnt = compound.getInt("pole_jump_count");
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("zombie_has_pole", this.hasPole());
		final CompoundTag nbt = new CompoundTag();
		nbt.putDouble("XXX", this.jumpDstPoint.x);
		nbt.putDouble("YYY", this.jumpDstPoint.y);
		nbt.putDouble("ZZZ", this.jumpDstPoint.z);
		compound.put("jump_dst_point", nbt);
		compound.putInt("pole_jump_count", this.pole_jump_cnt);
	}

	public void setPole(boolean has) {
		this.entityData.set(HAS_POLE, has);
	}

	public boolean hasPole() {
		return this.entityData.get(HAS_POLE);
	}

	@Override
	public ZombieType getZombieType() {
		return GrassZombies.POLE_ZOMBIE;
	}

	/**
	 * PoleZombie Jump condition:
	 * 1.it has a valid target and suitable distance.
	 * 2.it's on ground and it must has extra jump count.
	 * 3.its RayTrace up can not hit block.
	 */
	static class PoleJumpGoal extends Goal{

		private final PoleZombieEntity zombie;
		private int delayCnt = 0;

		public PoleJumpGoal(PoleZombieEntity zombie) {
			this.zombie = zombie;
			this.setFlags(EnumSet.of(Goal.Flag.JUMP, Flag.MOVE, Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			//already start last launch.
			if(this.zombie.getAttackTime() > 0) {
				return true;
			}
			if(this.delayCnt > 0) {
				-- this.delayCnt;
				return false;
			}
			LivingEntity target = zombie.getTarget();
			int left_jump_chance = this.zombie.getMaxJumpCount() - this.zombie.pole_jump_cnt;
			if(! EntityUtil.isEntityValid(target) ||
					! this.zombie.isOnGround() || left_jump_chance <= 0) {
				return false;
			}
			double dis = this.zombie.distanceToSqr(target);
			//can not be so close or so far.
			if(dis < 64 || dis > Math.max(100, 100 * left_jump_chance * left_jump_chance)) {
				return false;
			}
			Vec3 vec = MathUtil.getHorizontalNormalizedVec(zombie.position(), target.position())
					.scale(this.zombie.HorizontalJumpSpeed)
					.add(0, this.zombie.VerticalJumpSpeed * 2, 0);
			if(! EntityUtil.canEntityPass(zombie, vec, 10)) {
				this.delayCnt = this.zombie.random.nextInt(50);
				return false;
			}
			this.zombie.jumpDstPoint = target.position();
			this.zombie.setTarget(target);
			return true;
		}

		@Override
		public void start() {
			this.zombie.setAttackTime(zombie.getPoleJumpCD());
			this.zombie.setAggressive(false);
		}

		@Override
		public boolean canContinueToUse() {
			return this.zombie.getAttackTime() > 0;
		}

		@Override
		public void tick() {
			int time = this.zombie.getAttackTime();
			int cd = this.zombie.getPoleJumpCD();
			this.zombie.getLookControl().setLookAt(this.zombie.jumpDstPoint);
			if(time == cd * 3 / 4) {
				this.zombie.perfromJump();
			} else if(time == 1) {
				if(++ this.zombie.pole_jump_cnt == this.zombie.getMaxJumpCount()) {
					this.zombie.setPole(false);
				}
			}
			this.zombie.setAttackTime(Math.max(0, time - 1));
		}

	}
}
