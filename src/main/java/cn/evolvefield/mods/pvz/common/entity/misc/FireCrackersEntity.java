package cn.evolvefield.mods.pvz.common.entity.misc;

import cn.evolvefield.mods.pvz.common.entity.AbstractOwnerEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.explosion.BambooLordEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class FireCrackersEntity extends AbstractOwnerEntity {

	private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(FireCrackersEntity.class,
			EntityDataSerializers.INT);

	public FireCrackersEntity(EntityType<?> entityTypeIn, Level worldIn) {
		super(entityTypeIn, worldIn);
	}

	public FireCrackersEntity(Level worldIn, LivingEntity living) {
		super(EntityRegister.FIRE_CRACKERS.get(), worldIn, living);
	}

	protected void defineSynchedData() {
		this.entityData.define(FUSE, 80);
	}

	@Override
	public void tick() {
		super.tick();
		if(! level.isClientSide) {
			if(this.getFuse() > 0) {
				this.setFuse(this.getFuse() - 1);
				if(this.getFuse() <= 0) {
					this.explode();
					this.remove(RemovalReason.KILLED);
				}
			}
		}
		this.tickMove();
	}

	protected void explode() {
		final float range = 2F;
		EntityUtil.playSound(this, SoundEvents.GENERIC_EXPLODE);
		EntityUtil.getTargetableEntities(this, EntityUtil.getEntityAABB(this, range, range)).forEach(target -> {
			target.hurt(PVZEntityDamageSource.explode(this, this.getOwner()), 50F);
			target.setDeltaMovement(target.getDeltaMovement().add(0, BambooLordEntity.UP_SPEED, 0));
		});
		for(int i = 0; i < 2; ++ i) {
		    EntityUtil.spawnParticle(this, 5);
	    }
	}

	protected boolean isMovementNoisy() {
		return false;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("fuse_tick")) {
			this.setFuse(compound.getInt("fuse_tick"));
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("fuse_tick", this.getFuse());
	}

	public void setFuse(int tick) {
		this.entityData.set(FUSE, tick);
	}

	public int getFuse() {
		return this.entityData.get(FUSE);
	}

}
