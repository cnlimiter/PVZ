package cn.evolvefield.mods.pvz.common.entity.plant.arma;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.bullet.ButterEntity;
import cn.evolvefield.mods.pvz.common.entity.bullet.KernelEntity;
import cn.evolvefield.mods.pvz.common.entity.bullet.PultBulletEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantPultEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.init.registry.EffectRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class KernelPultEntity extends PlantPultEntity {

	private static final EntityDataAccessor<Integer> CURRENT_BULLET = SynchedEntityData.defineId(KernelPultEntity.class, EntityDataSerializers.INT);
	private static final int BUTTER_CHANCE = 10;
	private KernelPultEntity upgradeEntity;

	public KernelPultEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(CURRENT_BULLET, CornTypes.KERNEL.ordinal());
	}

	@Override
	public void onPlantUpgrade(PVZPlantEntity plantEntity) {
		super.onPlantUpgrade(plantEntity);
		if(this.upgradeEntity != null) {
			this.upgradeEntity.remove(RemovalReason.KILLED);
		}
	}

	@Override
	public boolean canBeUpgrade(Player player) {
		this.upgradeEntity = this.getNearByPult(player);
		return super.canBeUpgrade(player) && EntityUtil.isEntityValid(this.upgradeEntity);
	}

	private KernelPultEntity getNearByPult(Player player){
		final float range = 1.5F;
		List<KernelPultEntity> list = level.getEntitiesOfClass(KernelPultEntity.class, EntityUtil.getEntityAABB(this, range, range), pult -> {
			return ! pult.is(this) && pult.getPlantType() == PVZPlants.KERNEL_PULT && ! EntityUtil.canAttackEntity(pult, player);
		});
		return list.size() == 0 ? null : list.get(0);
	}

	@Override
	public void startPultAttack() {
		super.startPultAttack();
		this.changeBullet();
	}

	/**
	 * switch butter or kernel.
	 */
	protected void changeBullet() {
		if(this.isPlantInSuperMode() && ! this.isSuperOut) {
			this.setCurrentBullet(CornTypes.BUTTER);
			return ;
		}
		this.setCurrentBullet(this.getRandom().nextInt(BUTTER_CHANCE) == 0 ? CornTypes.BUTTER : CornTypes.KERNEL);
	}

	@Override
	public void performPult(LivingEntity target1) {
		super.performPult(target1);
		this.setCurrentBullet(CornTypes.KERNEL);
	}

	@Override
	protected PultBulletEntity createBullet() {
		if(this.isPlantInSuperMode() || this.getCurrentBullet() == CornTypes.BUTTER) {
			return new ButterEntity(level, this);
		}
		return new KernelEntity(level, this);
	}

	@Override
	public float getSuperDamage() {
		return 2 * this.getAttackDamage();
	};

	@Override
	public float getAttackDamage() {
		return this.getSkillValue(SkillTypes.MORE_KERNEL_DAMAGE);
	}

	public MobEffectInstance getButterEffect() {
		return new MobEffectInstance(EffectRegister.BUTTER_EFFECT.get(), this.getButterDuration(), 1, false, false);
	}

	public int getButterDuration() {
		return 100;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.8F, 1F);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("current_bullet_type")) {
			this.setCurrentBullet(CornTypes.values()[compound.getInt("current_bullet_type")]);
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("current_bullet_type", this.getCurrentBullet().ordinal());
	}

	public void setCurrentBullet(CornTypes type) {
		this.entityData.set(CURRENT_BULLET, type.ordinal());
	}

	public CornTypes getCurrentBullet() {
		return CornTypes.values()[this.entityData.get(CURRENT_BULLET)];
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.KERNEL_PULT;
	}

	public static enum CornTypes{
		KERNEL,
		BUTTER,
		ROCKET
	}

}
