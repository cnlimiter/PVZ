package cn.evolvefield.mods.pvz.common.entity.zombie.roof;

import cn.evolvefield.mods.pvz.common.entity.ai.goal.attack.PVZZombieAttackGoal;
import cn.evolvefield.mods.pvz.common.entity.plant.spear.SpikeRockEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.body.ZombieDropBodyEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.RoofZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.EnumSet;

public class GargantuarEntity extends PVZZombieEntity {

	private static final EntityDataAccessor<Boolean> HAS_IMP = SynchedEntityData.defineId(GargantuarEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> TOOL_TYPE = SynchedEntityData.defineId(GargantuarEntity.class, EntityDataSerializers.INT);
	public static final int DEATH_ANIM_CD = 100;
	public boolean isSad = false;

	public GargantuarEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.setIsWholeBody();
	}


	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason,
										   SpawnGroupData spawnDataIn, CompoundTag dataTag) {
		if(! level.isClientSide) {
			this.setToolType(GargantuarType.values()[this.getRandom().nextInt(GargantuarType.values().length)]);
		}
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(HAS_IMP, true);
		this.entityData.define(TOOL_TYPE, GargantuarType.DOLL.ordinal());
	}

	@Override
	protected void registerAttackGoals() {
		this.goalSelector.addGoal(3, new GargantuarMoveToTargetGoal(this, true));
		this.goalSelector.addGoal(2, new CrushAttackGoal(this));
		this.goalSelector.addGoal(1, new ThrowImpGoal(this));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ZombieUtil.WALK_SLOW);
	}

	public void throwImp(LivingEntity target) {
		EntityUtil.playSound(this, SoundRegister.IMP_HAPPY.get());
		ImpEntity imp = EntityRegister.IMP.get().create(level);
		imp.throwByGargantuar(this, target);
		EntityUtil.onEntitySpawn(level, imp, blockPosition().offset(0, this.getBbHeight(), 0));
		this.setHasImp(false);
	}

	@Override
	protected void setBodyStates(ZombieDropBodyEntity body) {
		super.setBodyStates(body);
		body.setMaxLiveTick(DEATH_ANIM_CD);
		body.setHandDefence(this.isSad);
	}

	@Override
	public boolean canPAZTarget(Entity target) {
		if(target instanceof SpikeRockEntity) {
			return true;
		}
		return super.canPAZTarget(target);
	}

	@Override
	public boolean doHurtTarget(Entity entityIn) {
		if(! level.isClientSide) {
			EntityUtil.playSound(this, SoundRegister.GROUND_SHAKE.get());
		}
		if(! EntityUtil.isEntityValid(entityIn)) {
			return false;
		}
		return super.doHurtTarget(entityIn);
	}

	@Override
	protected PVZEntityDamageSource getZombieAttackDamageSource() {
		return PVZEntityDamageSource.causeCrushDamage(this);
	}

	@Override
	protected float getModifyAttackDamage(Entity entity, float f) {
		if(entity instanceof LivingEntity) {
			return EntityUtil.getMaxHealthDamage(((LivingEntity) entity), 2);
		}
		return f;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		if(this.isMiniZombie()) {
			return EntityDimensions.scalable(0.6F, 1.8F);
		}
		return EntityDimensions.scalable(0.8f, 4f);
	}

	public boolean canThrowImp() {
		return this.canNormalUpdate() && this.getHealth() / this.getMaxHealth() < 0.5F;
	}

	@Override
	public boolean canBreakPlantBlock() {
		return false;
	}

	@Override
	public float getKBValue() {
		return 1;
	}

	public int getCrushCD() {
		return 40;
	}

	public int getThrowCD() {
		return 30;
	}

	@Override
	public float getLife() {
		return 300;
	}

	@Override
	public int getArmorToughness() {
		return 5;
	}

	@Override
	protected int getDeathTime() {
		return 40;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegister.GARGANTUAR_DEATH.get();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegister.GARGANTUAR_GROAN.get();
	}

	@Override
	public int getAmbientSoundInterval() {
		return 200;
	}

	@Override
    public ZombieType getZombieType() {
	    return RoofZombies.GARGANTUAR;
    }

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("has_imp")) {
			this.setHasImp(compound.getBoolean("has_imp"));
		}
		if(compound.contains("weapon_type")) {
			this.setToolType(GargantuarType.values()[compound.getInt("weapon_type")]);
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("has_imp", this.hasImp());
		compound.putInt("weapon_type", this.getToolType().ordinal());
	}

	public void setHasImp(boolean has) {
		this.entityData.set(HAS_IMP, has);
	}

	public boolean hasImp() {
		return this.entityData.get(HAS_IMP);
	}

	public void setToolType(GargantuarType type) {
		this.entityData.set(TOOL_TYPE, type.ordinal());
	}

	public GargantuarType getToolType() {
		return GargantuarType.values()[this.entityData.get(TOOL_TYPE)];
	}

	public enum GargantuarType {
		POLE,
		SIGN,
		DOLL
	}

	private final class GargantuarMoveToTargetGoal extends PVZZombieAttackGoal {

		public GargantuarMoveToTargetGoal(GargantuarEntity creature, boolean useLongMemory) {
			super(creature, useLongMemory);
		}

		@Override
		public boolean canUse() {
			return this.zombie.getAttackTime() == 0 && super.canUse();
		}

		@Override
		public boolean canContinueToUse() {
			return this.zombie.getAttackTime() == 0 && super.canContinueToUse();
		}

		@Override
		protected void checkAndPerformAttack(LivingEntity target) {
			double dis = EntityUtil.getNearestDistance(this.attacker, target);
			double range = this.getAttackReachSqr(target);
			if (range >= dis && this.attackTick <= 0) {
				this.attackTick = this.zombie.getAttackCD();
				this.attacker.swing(InteractionHand.MAIN_HAND);
				this.zombie.setAttackTime(GargantuarEntity.this.getCrushCD());
			}
		}

	}

	private final class CrushAttackGoal extends Goal {

		protected final GargantuarEntity attacker;

		public CrushAttackGoal(GargantuarEntity creature) {
			this.attacker = creature;
			this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			return this.attacker.getAttackTime() > 0;
		}

		@Override
		public boolean canContinueToUse() {
			return this.attacker.getAttackTime() > 0;
		}

		@Override
		public void tick() {
			if(this.attacker.canNormalUpdate() && this.attacker.getAttackTime() > 0) {
				this.attacker.setAttackTime(this.attacker.getAttackTime() - 1);
				if(this.attacker.getAttackTime() == this.attacker.getCrushCD() * 1 / 3) {
					this.attacker.doHurtTarget(this.attacker.getTarget());
				}
			}
		}

	}

	private final class ThrowImpGoal extends Goal {

		protected final GargantuarEntity attacker;

		public ThrowImpGoal(GargantuarEntity creature) {
			this.attacker = creature;
			this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			return this.attacker.hasImp() && this.attacker.canThrowImp();
		}

		@Override
		public void start() {
			this.attacker.setAttackTime(- this.attacker.getThrowCD());
		}

		@Override
		public boolean canContinueToUse() {
			return this.attacker.getAttackTime() < 0;
		}

		@Override
		public void stop() {
			this.attacker.setAttackTime(0);
		}

		@Override
		public void tick() {
			if(this.attacker.canNormalUpdate() && this.attacker.getAttackTime() < 0) {
				this.attacker.setAttackTime(this.attacker.getAttackTime() + 1);
				if(this.attacker.hasImp() && - this.attacker.getAttackTime() == this.attacker.getThrowCD() * 1 / 4) {
					this.attacker.throwImp(this.attacker.getTarget());
				}
			}
		}

	}


}
