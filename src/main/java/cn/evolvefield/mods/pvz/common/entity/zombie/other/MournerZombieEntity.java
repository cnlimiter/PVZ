package cn.evolvefield.mods.pvz.common.entity.zombie.other;

import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.grass.TombStoneEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.CustomZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.misc.PVZLoot;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class MournerZombieEntity extends PVZZombieEntity {

	private static final EntityDataAccessor<Boolean> RIGHT_SHAKE = SynchedEntityData.defineId(MournerZombieEntity.class, EntityDataSerializers.BOOLEAN);
	public static final int SHAKE_CD = 10;

	public MournerZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.setRightShake(this.getRandom().nextInt(2) == 0 ? true : false);
		this.canLostHand = false;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(RIGHT_SHAKE, true);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ZombieUtil.WALK_LITTLE_SLOW);
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ZombieUtil.VERY_LOW);
	}

	@Override
	public float getLife() {
		return 48;
	}

	@Override
	protected int getDeathTime() {
		return 1;
	}

	@Override
	public boolean doHurtTarget(Entity entityIn) {
		this.setAttackTime(SHAKE_CD);
		final float scale = 3;
		if(! entityIn.isOnGround() && entityIn instanceof LivingEntity) {
			entityIn.hurt(PVZEntityDamageSource.normal(this), EntityUtil.getMaxHealthDamage((LivingEntity) entityIn, 0.2F));
		}
		entityIn.setDeltaMovement(0, Math.sqrt(this.getRandom().nextFloat()) * scale, 0);
		return super.doHurtTarget(entityIn);
	}

	@Override
	protected PVZEntityDamageSource getZombieAttackDamageSource() {
		return PVZEntityDamageSource.normal(this);
	}

	@Override
	public void tick() {
		super.tick();
		if(this.getAttackTime() > 0) {
			this.setAttackTime(this.getAttackTime() - 1);
		}
	}

	@Override
	protected void onRemoveWhenDeath() {
		if(!level.isClientSide) {
			TombStoneEntity tomb = EntityRegister.TOMB_STONE.get().create(level);
			ZombieUtil.onZombieSpawn(this, tomb, blockPosition());
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("is_right_shake")) {
			this.setRightShake(compound.getBoolean("is_right_shake"));
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("is_right_shake", this.isRightShake());
	}

	public boolean isRightShake() {
		return this.entityData.get(RIGHT_SHAKE);
	}

	public void setRightShake(boolean is) {
		this.entityData.set(RIGHT_SHAKE, is);
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return PVZLoot.MOURNER_ZOMBIE;
	}

	@Override
	public ZombieType getZombieType() {
		return CustomZombies.MOURNER_ZOMBIE;
	}

}
