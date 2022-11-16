package cn.evolvefield.mods.pvz.common.entity.plant.spear;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.ai.goal.target.PVZGlobalTargetGoal;
import cn.evolvefield.mods.pvz.common.entity.bullet.AbstractBulletEntity;
import cn.evolvefield.mods.pvz.common.entity.bullet.ThornEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantShooterEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.pool.BalloonZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;

import java.util.HashSet;
public class CatTailEntity extends PlantShooterEntity {

	public HashSet<ThornEntity> thorns = new HashSet<>();
	private int powerCount = 0;
	private int powerTick = 0;
	private final int POWER_CD = 200;

	public CatTailEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(2, new FloatGoal(this));
	}

	@Override
	protected void addTargetGoals() {
		this.targetSelector.addGoal(0, new PVZGlobalTargetGoal(this, true, false, getShootRange(), getShootRange()));
	}

	@Override
	public void normalPlantTick() {
		super.normalPlantTick();
		if(! level.isClientSide) {
			if(this.powerTick > 0) {
				-- this.powerTick;
			}
			HashSet<ThornEntity> tmp = new HashSet<>();
			thorns.forEach(thorn -> {
				if(EntityUtil.isEntityValid(thorn) && thorn.isInControl()) {
					thorn.setThornTarget(this.getTarget());
					tmp.add(thorn);
				}
			});
			this.thorns.clear();
			this.thorns = tmp;
		}
	}

	@Override
	public void shootBullet() {
		this.performShoot(0, 0, 0.2, true, FORWARD_SHOOT_ANGLE);
	}

	@Override
	protected AbstractBulletEntity createBullet() {
		final ThornEntity thorn = new ThornEntity(level, this);
		thorn.setThornType(this.getThornShootType());
		thorn.setThornState(ThornEntity.ThornStates.NORMAL);
		thorn.setExtraHitCount(this.getExtraAttackCount());
		this.thorns.add(thorn);
		return thorn;
	}

	@Override
	public boolean canPAZTarget(Entity entity) {
		if(entity instanceof BalloonZombieEntity) {
			return true;
		}
		return super.canPAZTarget(entity);
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		this.powerCount += this.getPowerThornCount();
	}

	protected ThornEntity.ThornTypes getThornShootType() {
		if(this.powerCount > 0 && this.powerTick == 0) {
			this.powerTick = this.POWER_CD;
			-- this.powerCount;
			return ThornEntity.ThornTypes.AUTO;
		}
		return ThornEntity.ThornTypes.GUIDE;
	}

	@Override
	protected boolean canAttackNow() {
		return this.getAttackTime() > 0 && this.getAttackTime() % getAnimCD() == 0;
	}

	@Override
	public boolean checkY(Entity target) {
		return true;
	}

	public int getPowerThornCount() {
		return 1;
	}

	public int getExtraAttackCount() {
		return 1;
	}

	@Override
	public float getAttackDamage() {
		return this.getSkillValue(SkillTypes.MORE_THORN_DAMAGE);
	}

	public int getAnimCD() {
		return 8;
	}

	@Override
	public void startShootAttack() {
		this.setAttackTime(this.getAnimCD() * 2);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.8F, 1F);
	}

	@Override
	public float getShootRange() {
		return 40;
	}

	@Override
	public float getBulletSpeed() {
		return 0F;
	}

	@Override
	public int getSuperTimeLength() {
		return 40;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("power_thorn_count")){
			this.powerCount = compound.getInt("power_thorn_count");
		}
		if(compound.contains("power_shoot_tick")){
			this.powerTick = compound.getInt("power_shoot_tick");
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("power_thorn_count", this.powerCount);
		compound.putInt("power_shoot_tick", this.powerTick);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.CAT_TAIL;
	}

}
