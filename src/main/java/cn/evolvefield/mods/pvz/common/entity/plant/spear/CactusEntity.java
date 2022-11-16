package cn.evolvefield.mods.pvz.common.entity.plant.spear;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.bullet.AbstractBulletEntity;
import cn.evolvefield.mods.pvz.common.entity.bullet.ThornEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantShooterEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.pool.BalloonZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
public class CactusEntity extends PlantShooterEntity {

	private static final EntityDataAccessor<Float> CACTUS_HEIGHT = SynchedEntityData.defineId(CactusEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Boolean> POWERED = SynchedEntityData.defineId(CactusEntity.class, EntityDataSerializers.BOOLEAN);
	public static final float MAX_SEGMENT_NUM = 4;
	public static final float SEGMENT_HEIGHT = 0.54F;
	private static final float MIN_SHOOT_HEIGHT = 1.25F;
	private static final float MAX_SHOOT_HEIGHT = MIN_SHOOT_HEIGHT + MAX_SEGMENT_NUM * SEGMENT_HEIGHT;
	protected static final double SHOOT_OFFSET = 0.3D; //pea position offset


	public CactusEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(CACTUS_HEIGHT, 0f);
		this.entityData.define(POWERED, false);
	}

	@Override
	public void normalPlantTick() {
		super.normalPlantTick();
		if(! level.isClientSide) {
			final LivingEntity target = this.getTarget();
			if(EntityUtil.isEntityValid(target)) {
				if(! this.isSuitableHeight(target)) {
					final float dh = SEGMENT_HEIGHT;
				    if(this.getY() < target.getY()) {
					    this.setCactusHeight(Math.min(this.getCactusHeight() + dh, SEGMENT_HEIGHT * MAX_SEGMENT_NUM));
				    } else {
				    	this.setCactusHeight(Math.max(this.getCactusHeight() - dh, 0));
				    }
				}
			} else {
				this.setCactusHeight(0);
			}
		}
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
		super.onSyncedDataUpdated(data);
		if(data.equals(CACTUS_HEIGHT)){
			this.refreshDimensions();
		}
	}

	@Override
	public void shootBullet() {
		this.performShoot(SHOOT_OFFSET, 0, 0, this.getAttackTime() == 1, FORWARD_SHOOT_ANGLE);
	}

	@Override
	protected AbstractBulletEntity createBullet() {
		final ThornEntity thorn = new ThornEntity(level, this);
		thorn.setThornType(ThornEntity.ThornTypes.NORMAL);
		thorn.setThornState(this.isCactusPowered() ? ThornEntity.ThornStates.POWER : ThornEntity.ThornStates.NORMAL);
		thorn.setExtraHitCount(this.isCactusPowered() ? this.getThornCount() : 1);
		return thorn;
	}


//	@Override
//	public boolean hurt(DamageSource source, float amount) {
//		if(EntityUtil.canAttackEntity(this, source.getEntity())) {
//			final float damage = this.isCactusPowered() ? this.getAttackDamage() * 4 : this.getAttackDamage() * 2;
//			source.getEntity().hurt(PVZDamageSource.causeThornDamage(this, this), damage);
//		}
//		return super.hurt(source, amount);
//	}

	@Override
	public float getAttackDamage() {
		return this.getSkillValue(SkillTypes.MORE_THORN_DAMAGE);
	}

	public int getThornCount() {
		return 3;
	}

	public float getCurrentHeight() {
		return this.getCactusHeight() + MIN_SHOOT_HEIGHT;
	}

	/**
	 * {@link #normalPlantTick()}
	 */
	private boolean isSuitableHeight(Entity target) {
		double dx = target.getX() - this.getX();
		double ly = target.getY() - this.getY() - this.getCurrentHeight();
		double ry = ly + target.getBbHeight();
		double dz = target.getZ() - this.getZ();
		double dis = Math.sqrt(dx * dx + dz * dz);
		double y = dis / getMaxShootAngle();
		return ly <= y && ry >= - y;
	}

	@Override
	public boolean canPAZTarget(Entity entity) {
		if(entity instanceof BalloonZombieEntity) {
			return true;
		}
		return super.canPAZTarget(entity);
	}

	@Override
	public boolean checkY(Entity target) {
		final double dx = target.getX() - this.getX();
		final double dz = target.getZ() - this.getZ();
		final double dis = Math.sqrt(dx * dx + dz * dz);
		final double y = dis / getMaxShootAngle();
		return this.getY() + MAX_SHOOT_HEIGHT + y >= target.getY() &&  this.getY() + MIN_SHOOT_HEIGHT - y <= target.getY() + target.getBbHeight();
	}

	@Override
	public void startShootAttack() {
		this.setAttackTime(1);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.8F, 2.0F + this.getCactusHeight());
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		this.setCactusPowered(true);
	}

	@Override
	public boolean canStartSuperMode() {
		return super.canStartSuperMode() && ! this.isCactusPowered();
	}

	@Override
	public int getSuperTimeLength() {
		return 20;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("cactus_powered")) {
			this.setCactusPowered(compound.getBoolean("cactus_powered"));
		}
		if(compound.contains("cactus_height")) {
			this.setCactusHeight(compound.getFloat("cactus_height"));
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("cactus_powered", this.isCactusPowered());
		compound.putFloat("cactus_height", this.getCactusHeight());
	}

	public float getCactusHeight() {
		return this.entityData.get(CACTUS_HEIGHT);
	}

	public void setCactusHeight(float h) {
		this.entityData.set(CACTUS_HEIGHT, h);
	}

	public boolean isCactusPowered() {
		return this.entityData.get(POWERED);
	}

	public void setCactusPowered(boolean is) {
		this.entityData.set(POWERED, is);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.CACTUS;
	}

}
