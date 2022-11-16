package cn.evolvefield.mods.pvz.common.entity.bullet;

import cn.evolvefield.mods.pvz.common.entity.plant.spear.CatTailEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class ThornEntity extends AbstractBulletEntity {

	private static final EntityDataAccessor<Integer> THORN_TYPE = SynchedEntityData.defineId(ThornEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> THORN_STATE = SynchedEntityData.defineId(ThornEntity.class,
			EntityDataSerializers.INT);
	private IntOpenHashSet set = new IntOpenHashSet();
	private LivingEntity thornTarget;
	private int extraHitCount = 0;

	public ThornEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public ThornEntity(Level worldIn, LivingEntity shooter) {
		super(EntityRegister.THORN.get(), worldIn, shooter);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(THORN_TYPE, ThornTypes.NORMAL.ordinal());
		entityData.define(THORN_STATE, ThornStates.NORMAL.ordinal());
	}

	@Override
	public void tick() {
		super.tick();
		this.noPhysics = true;
		if (! level.isClientSide) {
			//default code.
			var vec = getShootVec().normalize();
			if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
				float f = Mth.sqrt((float) distanceToSqr(vec));
				this.setYRot((float) (Mth.atan2(vec.x, vec.z) * (double) (180F / (float) Math.PI)));
				this.setXRot((float) (Mth.atan2(vec.y, (double) f) * (double) (180F / (float) Math.PI)));
				this.yRotO = this.getYRot();
				this.xRotO = this.getXRot();
			}
			//change speed.
			if (this.getThornType() == ThornTypes.GUIDE || this.getThornType() == ThornTypes.AUTO) {
				if (vec != Vec3.ZERO) {
					this.setDeltaMovement(vec.scale(this.getBulletSpeed()));
				}
			}
			if (this.getThornType() == ThornTypes.AUTO) {
				//find new target.
				if (! EntityUtil.isEntityValid(this.thornTarget) && this.tickCount % 20 == 0) {
					this.thornTarget = this.getRandomAttackTarget();
				}
			}
			if (EntityUtil.isEntityValid(this.thornTarget)) {//deal damage when close to target.
				if (this.distanceToSqr(thornTarget) <= 4) {
					this.onImpact(this.thornTarget);
				}
			}
			if(this.getThrower() == null) {
				this.remove(RemovalReason.KILLED);
			}
		}
	}

	/**
	 * get target by itself automatically.
	 * {@link #tick()}
	 */
	public LivingEntity getRandomAttackTarget() {
		final float range = 40F;
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, EntityUtil.getEntityAABB(this, range, range), entity -> {
			return ! entity.is(thornTarget) && EntityUtil.canSeeEntity(this, entity) && EntityUtil.canTargetEntity(this.getOwnerOrSelf(), entity);
		});
		if (list.size() == 0) {
			return null;
		}
		return list.get(this.random.nextInt(list.size()));
	}

	/**
	 * {@link #tick()}
	 */
	public Vec3 getShootVec() {
		if (this.thornTarget == null) {
			return Vec3.ZERO;
		}
		return this.thornTarget.position().add(0, this.thornTarget.getEyeHeight(), 0).subtract(this.position());
	}

	/**
	 * update target when in guide mode.
	 */
	public void setThornTarget(LivingEntity target) {
		this.thornTarget = target;
	}

	public void setExtraHitCount(int cnt) {
		this.extraHitCount = cnt;
	}

	public double getBulletSpeed() {
		if (this.getThrower() instanceof CatTailEntity) {
			return this.getThornType() == ThornTypes.AUTO ? 0.85D : 0.55D;
		}
		return 0.15D;
	}

	/**
	 */
	public boolean isInControl() {
		return this.getThornType() == ThornTypes.GUIDE;
	}

	@Override
	protected void onImpact(HitResult result) {
		if (result.getType() == HitResult.Type.ENTITY) {
			Entity target = ((EntityHitResult) result).getEntity();
			this.onImpact(target);
		}
	}

	private void onImpact(Entity target) {
		if (this.shouldHit(target)) {
			target.invulnerableTime = 0;
			this.dealThornDamage(target); // attack
			if (this.getThornType() == ThornTypes.GUIDE) {
				this.setThornType(ThornTypes.NORMAL);
				set.add(target.getId());
			} else if (this.getThornType() == ThornTypes.AUTO) {
				this.thornTarget = this.getRandomAttackTarget();
			} else {
				set.add(target.getId());
				-- this.extraHitCount;
			}
		}
		this.level.broadcastEntityEvent(this, (byte) 3);
		if ((! (this.getThornType() == ThornTypes.AUTO) && this.extraHitCount == 0)) {
			this.remove(RemovalReason.KILLED);
		}
	}

	protected void dealThornDamage(Entity target) {
		target.hurt(PVZEntityDamageSource.causeThornDamage(this, this), this.getAttackDamage());
	}

	@Override
	public float getAttackDamage() {
		float damage = this.attackDamage;
		if (this.getThornState() == ThornStates.POWER || this.getThornType() == ThornTypes.AUTO) {
			damage += 10;
		}
		return damage;
	}

	@Override
	protected boolean checkLive(HitResult result) {
		return true;
	}

	@Override
	protected boolean shouldHit(Entity target) {
		if (!super.shouldHit(target)) {
			return false;
		}
		if (this.getThornType() == ThornTypes.AUTO) {
			return target.equals(this.thornTarget);
		}
		return ! set.contains(target.getId());
	}

	@Override
	protected int getMaxLiveTick() {
		return this.getThornType() == ThornTypes.AUTO ? 500 : this.isInControl() ? 250 : 150;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.2f, 0.2f);
	}

	/**
	 * Updates the entity motion clientside, called by packets from the server
	 */
	@OnlyIn(Dist.CLIENT)
	public void lerpMotion(double x, double y, double z) {
		this.setDeltaMovement(x, y, z);
		if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
			float f = Mth.sqrt((float) (x * x + z * z));
			this.setXRot((float) (Mth.atan2(y, f) * (double) (180F / (float) Math.PI)));
			this.setYRot((float) (Mth.atan2(x, z) * (double) (180F / (float) Math.PI)));
			this.xRotO = this.getXRot();
			this.yRotO = this.getYRot();
			this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(),
					this.getXRot());
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("thorn_state", this.getThornState().ordinal());
		compound.putInt("thorn_type", this.getThornType().ordinal());
		compound.putInt("extra_hit_count", this.extraHitCount);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (this.getThrower() != null && this.getThrower() instanceof CatTailEntity && this.isInControl()) {
			((CatTailEntity) this.getThrower()).thorns.add(this);
		}
		if(compound.contains("thorn_state")) {
			this.setThornState(ThornStates.values()[compound.getInt("thorn_state")]);
		}
		if(compound.contains("thorn_type")) {
			this.setThornType(ThornTypes.values()[compound.getInt("thorn_type")]);
		}
		if(compound.contains("extra_hit_count")) {
			this.extraHitCount = compound.getInt("extra_hit_count");
		}
	}

	public ThornStates getThornState() {
		return ThornStates.values()[entityData.get(THORN_STATE)];
	}

	public void setThornState(ThornStates state) {
		entityData.set(THORN_STATE, state.ordinal());
	}

	public ThornTypes getThornType() {
		return ThornTypes.values()[entityData.get(THORN_TYPE)];
	}

	public void setThornType(ThornTypes type) {
		entityData.set(THORN_TYPE, type.ordinal());
	}

	public enum ThornStates {
		NORMAL, POWER,
	}

	public enum ThornTypes {
		NORMAL,
		GUIDE,//can change direction by owner.
		AUTO//automatic
	}

}
