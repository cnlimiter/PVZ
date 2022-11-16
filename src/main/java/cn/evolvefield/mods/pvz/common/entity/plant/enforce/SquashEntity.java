package cn.evolvefield.mods.pvz.common.entity.plant.enforce;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.ai.goal.target.PVZRandomTargetGoal;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;
public class SquashEntity extends PVZPlantEntity {

	private static final int TARGET_RANGE = 3;
	protected int extraChance = 0;

	public SquashEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(0, new PVZRandomTargetGoal(this, true, false, TARGET_RANGE, TARGET_RANGE));
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		if(!level.isClientSide) {
			if(EntityUtil.isEntityValid(this.getTarget())) {
				this.getLookControl().setLookAt(this.getTarget(), 30f, 30f);
			}
			if(this.getAttackTime() > 0) {
				if(this.isOnGround() || this.isInWaterOrBubble()) {
					this.dealDamage();
					//check death.
					if(this.extraChance > 0) {
						-- this.extraChance;
					}else {
						if(this.getRandom().nextFloat() > this.getAgainChance()) {
							this.remove(RemovalReason.KILLED);
						}
					}
				}
			} else {
				if(this.getTarget() != null) {
					EntityUtil.playSound(this, SoundRegister.SQUASH_HMM.get());
					this.jumpToTarget(this.getTarget());
				}
			}
		}
	}

	@Override
	protected boolean shouldCollideWithEntity(LivingEntity target) {
		if(EntityUtil.canTargetEntity(this, target)) {
			return false;
		}
		return super.shouldCollideWithEntity(target);
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		if(!level.isClientSide) {
		    this.extraChance += this.getSuperBonusChance();
		}
	}

	/**
	 * {@link #normalPlantTick()}
	 */
	protected void dealDamage(){
		this.setAttackTime(0);
		this.canCollideWithPlant = true;
		this.isImmuneToWeak = false;
		EntityUtil.playSound(this, SoundRegister.GROUND_SHAKE.get());
		final float range = 1F;
		for(Entity entity : EntityUtil.getWholeTargetableEntities(this, EntityUtil.getEntityAABB(this, range, range))) {
			entity.hurt(PVZEntityDamageSource.causeCrushDamage(this), this.getAttackDamage());
		}
	}

	/**
	 * jump to the top of the target.
	 * {@link #normalPlantTick()}
	 */
	private void jumpToTarget(LivingEntity target) {
		final int tick = 10;
		this.canCollideWithPlant = false;
		this.isImmuneToWeak = true;
		final var pos = target.position().add(target.getDeltaMovement().scale(tick * 0.8D));
		this.setPos(pos.x(), pos.y() + target.getBbHeight() + 1, pos.z());
		this.setDeltaMovement(this.getDeltaMovement().x(), 0, this.getDeltaMovement().z());
		this.setAttackTime(1);
	}

	@Override
	protected boolean shouldLockXZ() {
		return this.isOnGround();
	}

	/**
	 * extra smash times
	 */
	protected int getSuperBonusChance(){
		return 3;
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.addAll(Arrays.asList(
				Pair.of(PAZAlmanacs.ATTACK_DAMAGE, this.getAttackDamage()),
				Pair.of(PAZAlmanacs.AGAIN_CHANCE, this.getAgainChance())
		));
	}

	/**
	 * die chance for each smash
	 */
	public float getAgainChance(){
		return this.getSkillValue(SkillTypes.SQUASH_AGAIN);
	}

	public float getAttackDamage(){
		return this.getSkillValue(SkillTypes.NORMAL_ENHANCE_STRENGTH);
	}

	@Override
	public boolean isPlantImmuneTo(DamageSource source) {
		return super.isPlantImmuneTo(source) || PVZEntityDamageSource.isEnforceDamage(source);
	}

	@Override
	public int getSuperTimeLength() {
		return 20;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.9f, 1.5f);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("extra_chance")) {
			this.extraChance = compound.getInt("extra_chance");
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("extra_chance", this.extraChance);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.SQUASH;
	}

}
