package cn.evolvefield.mods.pvz.common.entity.plant.spear;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.base.IHasWheel;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class SpikeWeedEntity extends PVZPlantEntity {

	private static final EntityDataAccessor<Integer> SPIKE_NUM = SynchedEntityData.defineId(SpikeWeedEntity.class, EntityDataSerializers.INT);
	public static final int ATTACK_ANIM_CD = 10;

	public SpikeWeedEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.setSpikeNum(this.getSpikesCount());
		this.canBeStealByBungee = false;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SPIKE_NUM, 1);
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		if(! level.isClientSide) {
			if(this.getSpikeNum() <= 0) {
				this.remove(RemovalReason.KILLED);
			}
			if(this.getAttackTime() > 0) {
				this.setAttackTime(this.getAttackTime() - 1);
			}
			if(this.getExistTick() % this.getAttackCD() == 10) {
				this.spikeAttack();
			}
		}
	}

	/**
	 * {@link #normalPlantTick()}
	 */
	public void spikeAttack() {
		final float range = 0.6F;
		EntityUtil.getTargetableEntities(this, EntityUtil.getEntityAABB(this, range, range)).forEach(target -> {
			this.spikeNormalAttack(target);
		});
	}

	/**
	 * {@link #spikeAttack()}
	 */
	public void spikeNormalAttack(@Nonnull Entity target) {
		if(target instanceof IHasWheel) {
			((IHasWheel) target).spikeWheelBy(this);
			this.setSpikeNum(this.getSpikeNum() - 1);
		} else {
			target.hurt(PVZEntityDamageSource.causeThornDamage(this), this.getAttackDamage());
		}
		this.setAttackTime(ATTACK_ANIM_CD);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if(source instanceof PVZEntityDamageSource) {
			if(((PVZEntityDamageSource) source).isCrushDamage()) {
				if(this.getSpikeNum() > 0) {
					this.setSpikeNum(this.getSpikeNum() - 1);
					return false;
				}
			}
		}
		return super.hurt(source, amount);
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		this.setSpikeNum(this.getSpikeNum() + this.getSuperSpikeCount());
	}

	@Override
	public boolean isPlantImmuneTo(DamageSource source) {
		return super.isPlantImmuneTo(source) || (! (source instanceof PVZEntityDamageSource) && ! source.isProjectile());
	}

	@Override
	protected boolean shouldCollideWithEntity(LivingEntity target) {
		if(EntityUtil.canTargetEntity(this, target)) {
			return false;
		}
		return super.shouldCollideWithEntity(target);
	}

	@Override
	public boolean canBeTargetBy(LivingEntity living) {
		return false;
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.addAll(Arrays.asList(
				Pair.of(PAZAlmanacs.SPIKE_COUNT, this.getSpikesCount()),
				Pair.of(PAZAlmanacs.ATTACK_DAMAGE, this.getAttackDamage()),
				Pair.of(PAZAlmanacs.ATTACK_CD, this.getAttackCD())
		));
	}

	public float getAttackDamage(){
		return this.getSkillValue(SkillTypes.SPIKE_DAMAGE);
	}

	/**
	 * get extra spike when start super.
	 * {@link #startSuperMode(boolean)}
	 */
	public int getSuperSpikeCount() {
		return 3;
	}

	public int getAttackCD() {
		return 60;
	}

	public int getSpikesCount() {
		return 1;
	}

	@Override
	public int getSuperTimeLength() {
		return 20;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("spike_num")) {
			this.setSpikeNum(compound.getInt("spike_num"));
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("spike_num", this.getSpikeNum());
	}

	public int getSpikeNum() {
		return this.entityData.get(SPIKE_NUM);
	}

	public void setSpikeNum(int num) {
		this.entityData.set(SPIKE_NUM, num);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return new EntityDimensions(0.95f, 0.4f, false);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.SPIKE_WEED;
	}

}
