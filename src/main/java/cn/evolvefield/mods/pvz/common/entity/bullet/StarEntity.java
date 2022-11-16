package cn.evolvefield.mods.pvz.common.entity.bullet;

import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StarEntity extends AbstractBulletEntity {

	private static final EntityDataAccessor<Integer> STAR_TYPE = SynchedEntityData.defineId(StarEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> STAR_STATE = SynchedEntityData.defineId(StarEntity.class,
			EntityDataSerializers.INT);

	public StarEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public StarEntity(Level worldIn, LivingEntity livingEntityIn, StarTypes starType, StarStates starState) {
		super(EntityRegister.STAR.get(), worldIn, livingEntityIn);
		this.setStarType(starType);
		this.setStarState(starState);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(STAR_TYPE, 0);
		this.entityData.define(STAR_STATE, 0);
	}

	@Override
	protected void onImpact(HitResult result) {
		boolean flag = false;
		if (result.getType() == HitResult.Type.ENTITY) {
			var target = ((EntityHitResult) result).getEntity();
			if (this.shouldHit(target)) {
				target.invulnerableTime = 0;
				this.dealStarDamage(target); // attack
				flag = true;
			}
		}
		this.level.broadcastEntityEvent(this, (byte) 3);
		if (flag || !this.checkLive(result)) {
			this.remove(RemovalReason.KILLED);
		}
	}

	private void dealStarDamage(Entity target) {
		target.hurt(PVZEntityDamageSource.star(this, this.getThrower()), this.getAttackDamage());
	}

	@Override
	protected int getMaxLiveTick() {
		return 30;
	}

	public float getAttackDamage() {
		float damage = this.attackDamage;
		if(this.getStarType() == StarTypes.BIG) {
			damage += 5;
		}
		if(this.getStarType() == StarTypes.HUGE) {
			damage += 10;
		}
		return damage;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		if(this.getStarType() == StarTypes.BIG) {
			return EntityDimensions.scalable(0.5f, 0.2f);
		}
		if(this.getStarType() == StarTypes.HUGE) {
			return EntityDimensions.scalable(0.8f, 0.2f);
		}
		return EntityDimensions.scalable(0.2f, 0.2f);
	}

	@Override
	protected float getGravityVelocity() {
		return 0f;
	}

	/**
	 * Updates the entity motion clientside, called by packets from the server
	 */
	@OnlyIn(Dist.CLIENT)
	public void lerpMotion(double x, double y, double z) {
		this.setDeltaMovement(x, y, z);
		if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
		    this.setYRot( this.getYRot() + 10); ;
		    this.yRotO = this.getYRot();
		    this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("star_state")) {
		    this.setStarState(StarStates.values()[compound.getInt("star_state")]);
		}
		if(compound.contains("star_type")) {
			this.setStarType(StarTypes.values()[compound.getInt("star_type")]);
		}

	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("star_state", this.getStarState().ordinal());
		compound.putInt("star_type", this.getStarType().ordinal());
	}

	public StarStates getStarState() {
		return StarStates.values()[entityData.get(STAR_STATE)];
	}

	public void setStarState(StarStates state) {
		entityData.set(STAR_STATE, state.ordinal());
	}

	public StarTypes getStarType() {
		return StarTypes.values()[entityData.get(STAR_TYPE)];
	}

	public void setStarType(StarTypes type) {
		entityData.set(STAR_TYPE, type.ordinal());
	}

	public enum StarStates {
		YELLOW, PINK
	}

	public enum StarTypes {
		NORMAL, BIG, HUGE
	}

}
