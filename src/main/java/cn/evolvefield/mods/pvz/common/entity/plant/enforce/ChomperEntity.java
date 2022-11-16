package cn.evolvefield.mods.pvz.common.entity.plant.enforce;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.ai.goal.target.PVZNearestTargetGoal;
import cn.evolvefield.mods.pvz.common.entity.misc.SmallChomperEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;
public class ChomperEntity extends PVZPlantEntity {

	private static final EntityDataAccessor<Integer> REST_TICK = SynchedEntityData.defineId(ChomperEntity.class, EntityDataSerializers.INT);
    public static final int ATTACK_ANIM_CD = 30;

	public ChomperEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(REST_TICK, 0);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(0, new PVZNearestTargetGoal(this, true, false, 3, 2));
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		if (!level.isClientSide && this.getTarget() != null) {
			this.lookControl.setLookAt(this.getTarget(), 30f, 30f);
		}
		if (!level.isClientSide) {
			if (this.getAttackTime() < ATTACK_ANIM_CD / 2) {//pre attack stage.
				if (this.getRestTick() > 0) {// rest time cannot attack.
					this.setAttackTime(0);
					this.setRestTick(this.getRestTick() - 1);
					return;
				}
				if (! EntityUtil.isEntityValid(this.getTarget())) {//no target.
					this.setAttackTime(0);
					return;
				}
				this.setAttackTime(this.getAttackTime() + 1);
				if (this.getAttackTime() == ATTACK_ANIM_CD / 2) {// attack
					this.performAttack();
				}
			} else {
				this.setAttackTime(this.getAttackTime() + 1);
				if (this.getAttackTime() > ATTACK_ANIM_CD) {
					this.setAttackTime(0);
				}
			}
		}
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		//stop rest.
		this.setRestTick(0);
		//summon ground chomper
		final float range = 30F;
		int cnt = this.getSuperAttackCnt();
		for (LivingEntity target : EntityUtil.getTargetableLivings(this, EntityUtil.getEntityAABB(this, range, range))) {
			if(! target.isOnGround()) {
				continue;
			}
			SmallChomperEntity chomper = EntityRegister.SMALL_CHOMPER.get().create(level);
			chomper.summonByOwner(this);
			EntityUtil.onEntitySpawn(level, chomper, target.blockPosition());
			if (-- cnt == 0) {
				break;
			}
		}
	}

	/**
	 * deal damage to target.
	 * {@link #normalPlantTick()}
	 */
	protected void performAttack() {
		final LivingEntity target = this.getTarget();
		final float damage = this.getAttackDamage();
		if (EntityUtil.getCurrentHealth(target) <= this.getAttackDamage()) {//can eat to death need rest
			target.hurt(PVZEntityDamageSource.eat(this), damage);
			this.setRestTick(this.getRestCD());
		} else {
			target.hurt(PVZEntityDamageSource.eat(this), damage / 50F);
		}
		EntityUtil.playSound(this, SoundRegister.BIG_CHOMP.get());
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.addAll(Arrays.asList(
				Pair.of(PAZAlmanacs.ATTACK_DAMAGE, this.getAttackDamage()),
				Pair.of(PAZAlmanacs.REST_TIME, this.getRestCD())
		));
	}

	/**
	 * max damage to target.
	 */
	public float getAttackDamage() {
		return this.getSkillValue(SkillTypes.NORMAL_ENHANCE_STRENGTH);
	}

	/**
	 * rest time after each kill.
	 */
	public int getRestCD() {
		return 800;
	}

	/**
	 * how many ground chomper to summon.
	 */
	public int getSuperAttackCnt() {
		return 3;
	}

	/**
	 */
	public boolean isResting() {
		return this.getRestTick() > 0;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return new EntityDimensions(0.9f, 1.9f, false);
	}

	@Override
	public int getSuperTimeLength() {
		return 20;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("rest_tick", this.getRestTick());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("rest_tick")) {
			this.setRestTick(compound.getInt("rest_tick"));
		}
	}

	public int getRestTick() {
		return this.entityData.get(REST_TICK);
	}

	public void setRestTick(int tick) {
		this.entityData.set(REST_TICK, tick);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.CHOMPER;
	}

}
