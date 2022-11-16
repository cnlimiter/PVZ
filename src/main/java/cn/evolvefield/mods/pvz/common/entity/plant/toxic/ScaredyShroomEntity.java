package cn.evolvefield.mods.pvz.common.entity.plant.toxic;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.bullet.AbstractBulletEntity;
import cn.evolvefield.mods.pvz.common.entity.bullet.itembullet.SporeEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantShooterEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.MathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
public class ScaredyShroomEntity extends PlantShooterEntity {

	private static final EntityDataAccessor<Integer> SCARE_TIME = SynchedEntityData.defineId(ScaredyShroomEntity.class, EntityDataSerializers.INT);
	protected static final double SHOOT_OFFSET = 0.2D;
	public static final int SCARE_ANIM_CD = 15;

	public ScaredyShroomEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SCARE_TIME, 0);
	}
	@Override
	public float getAttackDamage() {
		return this.getSkillValue(SkillTypes.SPORE_DAMAGE);
	}

	@Override
	public void normalPlantTick() {
		super.normalPlantTick();
		if(!this.level.isClientSide) {
			if(EntityUtil.isEntityValid(this.getTarget())) {//has target
				final double dis = getScareDistance();
				if(this.distanceToSqr(this.getTarget()) <= dis * dis) {//close to this
					this.setScareTime(Mth.clamp(this.getScareTime() + 1, 0, SCARE_ANIM_CD));
					return ;
				}
			}
			this.setScareTime(Mth.clamp(this.getScareTime() - 1, 0, SCARE_ANIM_CD));
		}
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
		super.onSyncedDataUpdated(data);
		if(data.equals(SCARE_TIME)) {
			this.refreshDimensions();
		}
	}

	@Override
	public boolean canShoot() {
		return super.canShoot() && ! this.isScared();
	}

	@Override
	public void shootBullet() {
		if(this.isPlantInSuperMode()) {
			final int cnt = this.getSuperShootCount();
			for(int i = 0; i < cnt; ++ i) {
				final float offset = MathUtil.getRandomFloat(getRandom()) / 3;
				final float offsetH = MathUtil.getRandomFloat(getRandom()) / 3;
				this.performShoot(SHOOT_OFFSET, offset, offsetH, this.getExistTick() % 10 == 0, FORWARD_SHOOT_ANGLE);
			}
		} else {
			this.performShoot(SHOOT_OFFSET, 0, 0, this.getAttackTime() == 1, FORWARD_SHOOT_ANGLE);
		}
	}

	@Override
	protected AbstractBulletEntity createBullet() {
		return new SporeEntity(this.level, this);
	}

	@Override
	protected SoundEvent getShootSound() {
		return SoundRegister.PUFF.get();
	}

	/**
	 * get how many spores need shoot per tick, when super.
	 */
	public int getSuperShootCount() {
//		final int min = this.isPlantInStage(3) ? 2 : 1;
//		final int max = this.isPlantInStage(1) ? 2 : 3;
//		return MathUtil.getRandomMinMax(getRandom(), min, max);
		return 2;
	}

	public float getScareDistance() {
		return 5;
//		return MathUtil.getProgressByDif(4, -1, this.getSkills(), PlantUtil.MAX_PLANT_LEVEL, 5, 1);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.6f, 1.6f - this.getScareTime() * 1.0f / SCARE_ANIM_CD);
	}

	@Override
	public void startShootAttack() {
		this.setAttackTime(1);
	}

	@Override
	public int getSuperTimeLength() {
		return 100;
	}

	@Override
	public float getShootRange() {
		return 35;
	}

	/**
	 * {@link #canShoot()}
	 */
    public boolean isScared() {
    	return this.getScareTime() > 0;
    }

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("scare_time", this.getScareTime());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("scare_time")) {
			this.setScareTime(compound.getInt("scare_time"));
		}
	}

	public int getScareTime() {
    	return this.entityData.get(SCARE_TIME);
    }

    public void setScareTime(int time) {
    	this.entityData.set(SCARE_TIME, time);
    }

    @Override
	public IPlantType getPlantType() {
		return PVZPlants.SCAREDY_SHROOM;
	}

}
