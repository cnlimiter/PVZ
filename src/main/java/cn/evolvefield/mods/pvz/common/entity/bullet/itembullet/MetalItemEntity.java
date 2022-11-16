package cn.evolvefield.mods.pvz.common.entity.bullet.itembullet;

import cn.evolvefield.mods.pvz.api.enums.MetalTypes;
import cn.evolvefield.mods.pvz.common.entity.plant.assist.MagnetShroomEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class MetalItemEntity extends PVZItemBulletEntity {

	private static final EntityDataAccessor<Integer> METAL_TYPE = SynchedEntityData.defineId(MetalItemEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> METAL_STATE = SynchedEntityData.defineId(MetalItemEntity.class,
			EntityDataSerializers.INT);
	private ItemStack stack = null;

	public MetalItemEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public MetalItemEntity(Level worldIn, LivingEntity shooter, MetalTypes metalType) {
		super(EntityRegister.METAL.get(), worldIn, shooter);
		this.setMetalType(metalType);
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(METAL_TYPE, MetalTypes.EMPTY.ordinal());
		entityData.define(METAL_STATE, MetalStates.ABSORB.ordinal());
	}

	@Override
	public void tick() {
		super.tick();
		this.noPhysics = true;
		if(! level.isClientSide && this.getThrower() instanceof MagnetShroomEntity) {
			final MagnetShroomEntity thrower = (MagnetShroomEntity) this.getThrower();
			if(this.distanceToSqr(thrower) <= 3) {
				// near the thrower
				if(this.getMetalState() == MetalStates.ABSORB) {
					thrower.setMetalType(getMetalType());
				    this.remove(RemovalReason.KILLED);
				} else if(this.getMetalState() == MetalStates.BULLET){
					this.setMetalState(MetalStates.WAIT);
				}
			}
			if(this.getMetalState() == MetalStates.BULLET || this.getMetalState() == MetalStates.ABSORB) {
				var	 vec = thrower.position().add(0, thrower.getBbHeight(), 0).subtract(this.position());
			    this.setDeltaMovement(vec.normalize().scale(0.8D));
			} else if(this.getMetalState() == MetalStates.WAIT){
				LivingEntity target = this.getAttackTarget(thrower);
				if(target == null) {
					this.setDeltaMovement(0, 0, 0);
					return ;
				}
				this.setMetalState(MetalStates.SHOOT);
				var vec = target.position().add(0, target.getEyeHeight(), 0).subtract(this.position());
				this.shootPea(vec.x, vec.y, vec.z, 1.4F, 0);
			}
		}
	}

	private LivingEntity getAttackTarget(MagnetShroomEntity thrower) {
		if(this.tickCount % 50 != 0) return null;
		final float range = 20;
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, EntityUtil.getEntityAABB(thrower, range, range), entity -> EntityUtil.checkCanEntityBeTarget(thrower, entity));
		if(list.size() == 0) {
			return null;
		}
		int pos = thrower.getRandom().nextInt(list.size());
		return list.get(pos);
	}

	@Override
	protected void onImpact(HitResult result) {
		boolean flag = false;
		if (result.getType() == HitResult.Type.ENTITY) {
			Entity target = ((EntityHitResult) result).getEntity();
			if (this.shouldHit(target)) {
				target.invulnerableTime = 0;
				target.hurt(PVZEntityDamageSource.metal(this, this.getThrower()), this.getAttackDamage());
				EntityUtil.playSound(this, SoundRegister.METAL_HIT.get());
				flag = true;
			}
		}
		this.level.broadcastEntityEvent(this, (byte) 3);
		if (flag || ! this.checkLive(result)) {
			this.remove(RemovalReason.KILLED);
		}
	}

	@Override
	protected boolean shouldHit(Entity target) {
		return this.getMetalState() == MetalStates.SHOOT && EntityUtil.canTargetEntity(this, target);
	}

	@Override
	protected boolean checkLive(HitResult result) {
		if(this.getMetalState() != MetalStates.SHOOT || result.getType() == HitResult.Type.BLOCK) {
			return true;
		}
		return super.checkLive(result);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return new EntityDimensions(0.2f, 0.2f, false);
	}

	@Override
	protected int getMaxLiveTick() {
		return 1200;
	}

	@Override
	public ItemStack getItem() {
		return this.stack == null ? this.stack = new ItemStack(MetalTypes.getMetalItem(this.getMetalType())) : this.stack;
	}

	@Override
	protected float getGravityVelocity() {
		return 0;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("metal_type", this.getMetalType().ordinal());
		compound.putInt("metal_state", this.getMetalState().ordinal());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("metal_type")) {
			this.setMetalType(MetalTypes.values()[compound.getInt("metal_type")]);
		}
		if(compound.contains("metal_state")) {
			this.setMetalState(MetalStates.values()[compound.getInt("metal_state")]);
		}
	}

	public MetalTypes getMetalType() {
		return MetalTypes.values()[entityData.get(METAL_TYPE)];
	}

	public void setMetalType(MetalTypes type) {
		entityData.set(METAL_TYPE, type.ordinal());
	}

	public MetalStates getMetalState() {
		return MetalStates.values()[entityData.get(METAL_STATE)];
	}

	public void setMetalState(MetalStates type) {
		entityData.set(METAL_STATE, type.ordinal());
	}

	public enum MetalStates {
		ABSORB,
		BULLET,
		WAIT,
		SHOOT;
	}

}
