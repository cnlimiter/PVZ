package cn.evolvefield.mods.pvz.common.entity.zombie.pool;

import cn.evolvefield.mods.pvz.api.interfaces.attract.ICanAttract;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.ai.goal.attack.PVZZombieAttackGoal;
import cn.evolvefield.mods.pvz.common.entity.ai.navigator.ZombieWaterPathNavigator;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.impl.zombie.PoolZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.MathUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Optional;

public class DolphinRiderEntity extends PVZZombieEntity {

	private static final float UP_SPEED = 0.05f;
	protected final float HorizontalJumpSpeed = 1.5F;
	protected final float VerticalJumpSpeed = 0.7F;
	protected Vec3 jumpDstPoint = Vec3.ZERO;
	protected int dolphin_jump_cnt;

	public DolphinRiderEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		setPathfindingMalus(BlockPathTypes.WATER, 0);
		this.setIsWholeBody();
		this.canBeMini = false;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ZombieUtil.WATER_FAST);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(7, new MoveTowardsRestrictionGoal(this, 1.0D));
		this.goalSelector.addGoal(1, new PVZZombieAttackGoal(this, true));
		this.goalSelector.addGoal(0, new DolphinJumpGoal(this));
		this.registerTargetGoals();
	}

	@Override
	public void zombieTick() {
		super.zombieTick();
		if(! level.isClientSide) {//swim up
			if(this.isInWater()) {
				if(this.shouldUp()){
				    Vec3 v = this.getDeltaMovement();
				    this.setDeltaMovement(v.x(), UP_SPEED, v.z());
				}
				if(EntityUtil.isEntityValid(this) && this.dolphin_jump_cnt == this.getMaxJumpCount()) {
					this.separate();
					return ;
				}
			}
			if(this.isOnGround() && EntityUtil.isEntityValid(this)) {
				this.separate();
				return ;
			}
		}
	}

	/**
	 * set jump and motion.
	 * {@link DolphinJumpGoal#tick()}
	 */
	public void perfromJump() {
		Optional.ofNullable(this.getTarget()).ifPresent(target -> {
			Vec3 vec = MathUtil.getHorizontalNormalizedVec(this.position(), this.jumpDstPoint);
			final double speedXZ = this.HorizontalJumpSpeed + (this.random.nextDouble() - 0.6D) / 3;
			final double speedY = this.VerticalJumpSpeed + (this.random.nextDouble() - 0.3D) / 2;
			this.setDeltaMovement(vec.x * speedXZ , speedY, vec.z * speedXZ);
			EntityUtil.playSound(this, SoundRegister.DOLPHIN_JUMP.get());
		});
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
		this.separate();
		EntityUtil.playSound(this, SoundRegister.HAMMER_BONK.get());
	}

	@Override
	protected boolean isZombieInvulnerableTo(DamageSource source) {
		return super.isZombieInvulnerableTo(source) || this.isDolphinJumping();
	}

	/**
	 * set jump and motion.
	 * {@link DolphinJumpGoal#tick()}
	 */
	public void separate() {
		//summon dolphin rider zombie.
		DolphinRiderZombieEntity zombie = EntityRegister.DOLPHIN_RIDER_ZOMBIE.get().create(level);
		ZombieUtil.copySummonZombieData(this, zombie);
		EntityUtil.onEntityRandomPosSpawn(level, zombie, blockPosition(), 1);
		zombie.setZombieType(VariantType.NORMAL);
		//summon zombie dolphin.
		ZombieDolphinEntity dolphin = EntityRegister.ZOMBIE_DOLPHIN.get().create(level);
		ZombieUtil.copySummonZombieData(this, dolphin);
		EntityUtil.onEntityRandomPosSpawn(level, dolphin, blockPosition(), 3);

		this.remove();
	}

	/**
	 * how many times can it jump.
	 */
	public int getMaxJumpCount() {
		return 1;
	}

	protected boolean shouldUp() {
		return this.getFluidHeight(FluidTags.WATER) > this.getEyeHeight() / 2;
	}

	@Override
	public EntitySize getDimensions(Pose poseIn) {
		return EntitySize.scalable(0.7f, 1.6f);
	}

	@Override
	protected float getWaterSlowDown() {
		return 0.94f;
	}

	@Override
	protected PathNavigation createNavigation(Level worldIn) {
		return new ZombieWaterPathNavigator(this, worldIn);
	}

	@Override
	public float getLife() {
		return 60;
	}

	public boolean isDolphinJumping() {
		return this.getAttackTime() > 0;
	}

	/**
	 * time to anim jump.
	 */
	public int getJumpCD() {
		return 20;
	}

	@Override
	public ZombieType getZombieType() {
		return PoolZombies.DOLPHIN_RIDER;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("jump_dst_point")) {
			CompoundTag nbt = compound.getCompound("jump_dst_point");
			this.jumpDstPoint = new Vec3(nbt.getDouble("XXX"), nbt.getDouble("YYY"), nbt.getDouble("ZZZ"));
		}
		if(compound.contains("dolphin_jump_count")) {
			this.dolphin_jump_cnt = compound.getInt("dolphin_jump_count");
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		final CompoundTag nbt = new CompoundTag();
		nbt.putDouble("XXX", this.jumpDstPoint.x);
		nbt.putDouble("YYY", this.jumpDstPoint.y);
		nbt.putDouble("ZZZ", this.jumpDstPoint.z);
		compound.put("jump_dst_point", nbt);
		compound.putInt("dolphin_jump_count", this.dolphin_jump_cnt);
	}

	/**
	 * DolphinRider Jump condition:
	 * 1.it has a valid target and suitable distance.
	 * 2.it's in water and it must has extra jump count.
	 * 3.its RayTrace up can not hit block.
	 */
	static class DolphinJumpGoal extends Goal{

		private final DolphinRiderEntity zombie;
		private int delayCnt = 0;

		public DolphinJumpGoal(DolphinRiderEntity zombie) {
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
			int left_jump_chance = this.zombie.getMaxJumpCount() - this.zombie.dolphin_jump_cnt;
			if(! EntityUtil.isEntityValid(target) ||
					! this.zombie.isInWater() || left_jump_chance <= 0) {
				return false;
			}
			double dis = this.zombie.distanceToSqr(target);
			//can not be so close or so far.
			if(dis < 64 || dis > Math.max(120, 120 * left_jump_chance * left_jump_chance)) {
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
			this.zombie.setAttackTime(zombie.getJumpCD());
			this.zombie.setAggressive(false);
		}

		@Override
		public boolean canContinueToUse() {
			return this.zombie.getAttackTime() > 0;
		}

		@Override
		public void tick() {
			int time = this.zombie.getAttackTime();
			int cd = this.zombie.getJumpCD();
			this.zombie.getLookControl().setLookAt(this.zombie.jumpDstPoint);
			if(time == cd * 3 / 4) {
				this.zombie.perfromJump();
			} else if(time == 1) {
				++ this.zombie.dolphin_jump_cnt;
			}
			this.zombie.setAttackTime(Math.max(0, time - 1));
		}

	}

}
