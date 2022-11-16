package cn.evolvefield.mods.pvz.common.entity.bullet;

import cn.evolvefield.mods.pvz.api.enums.PVZGroupType;
import cn.evolvefield.mods.pvz.common.entity.AbstractOwnerEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantShooterEntity;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public abstract class AbstractBulletEntity extends AbstractOwnerEntity {

	protected IntOpenHashSet hitEntities;
	protected float airSlowDown = 0.99F;
	protected float attackDamage = 0F;

	public AbstractBulletEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
		this.setNoGravity(true);
	}

	public AbstractBulletEntity(EntityType<? extends Projectile> type, Level worldIn, LivingEntity livingEntityIn) {
		super(type, worldIn, livingEntityIn);
		this.summonByOwner(livingEntityIn);
		this.setNoGravity(true);
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void tick() {
		super.tick();
		if (! level.isClientSide && this.tickCount >= this.getMaxLiveTick()) {
			this.remove(RemovalReason.KILLED);
		}
		//on hit
		if(! level.isClientSide) {
			var start = this.position();
		    var end = start.add(this.getDeltaMovement());
		    HitResult result = this.level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		    if(result.getType() != HitResult.Type.MISS) {// hit something
		    	end = result.getLocation();
		    }
		    EntityHitResult entityRay = this.rayTraceEntities(start, end);
		    if(entityRay != null) {
			    result = entityRay;
		    }
		    if(result != null && result.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, result)) {//on hit
			    this.onImpact(result);
		    }
		}
		this.tickMove();
	}

	/**
	 * Gets the EntityRayTraceResult representing the entity hit.
	 * {@link #tick()}
	 */
	@Nullable
	protected EntityHitResult rayTraceEntities(Vec3 startVec, Vec3 endVec) {
		return EntityUtil.rayTraceEntities(level, this, startVec, endVec, entity ->
		    entity.isPickable() && shouldHit(entity) && (this.hitEntities == null || !this.hitEntities.contains(entity.getId())
		));
	}

	/**
	 * {@link #rayTraceEntities(Vec3, Vec3)}
	 */
	protected boolean shouldHit(Entity target) {
		return EntityUtil.canTargetEntity(this.getOwnerOrSelf(), target);
	}

	public void addHitEntity(Entity entity) {
		this.hitEntities.addAll(EntityUtil.getOwnerAndPartsID(entity));
	}

	@Override
	protected void tickMove() {
		Vec3 vec3d = this.getDeltaMovement();
		double d0 = this.getX() + vec3d.x;
		double d1 = this.getY() + vec3d.y;
		double d2 = this.getZ() + vec3d.z;
		float f = Mth.sqrt((float) distanceToSqr(vec3d));
		this.setXRot((float) (Mth.atan2(vec3d.x, vec3d.z) * (double) (180F / (float) Math.PI)));
		for (this.setXRot((float) (Mth.atan2(vec3d.y, (double) f)
				* (double) (180F / (float) Math.PI))); this.getXRot()
						- this.xRotO < -180.0F; this.xRotO -= 360.0F) {
			;
		}
		while (this.getXRot() - this.xRotO >= 180.0F) {
			this.xRotO += 360.0F;
		}
		while (this.getXRot() - this.yRotO < -180.0F) {
			this.yRotO -= 360.0F;
		}
		while (this.getYRot() - this.yRotO >= 180.0F) {
			this.yRotO += 360.0F;
		}
		this.setXRot(Mth.lerp(0.2F, this.xRotO, this.getXRot()));
		this.setYRot(Mth.lerp(0.2F, this.yRotO, this.getYRot()));
		float f1;
		if (this.isInWater()) {
			for (int i = 0; i < 4; ++i) {
				this.level.addParticle(ParticleTypes.BUBBLE, d0 - vec3d.x * 0.25D, d1 - vec3d.y * 0.25D,
						d2 - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
			}
			f1 = 0.8F;
		} else {
			f1 = this.airSlowDown;
		}
		this.setDeltaMovement(vec3d.scale((double) f1));
		if (!this.isNoGravity()) {
			Vec3 vec3d1 = this.getDeltaMovement();
			this.setDeltaMovement(vec3d1.x, vec3d1.y - (double) this.getGravityVelocity(), vec3d1.z);
		}
		this.setPos(d0, d1, d2);
	}

	/**
	 * shoot bullet such as pea or spore
	 */
	public void shootPea(Vec3 vec, double speed, double angleOffset) {
		this.shootPea(vec.x, vec.y, vec.z, speed, angleOffset);
	}

	/**
	 * shoot bullet such as pea or spore
	 */
	public void shootPea(double dx, double dy, double dz, double speed, double angleOffset) {
		final double down = this.getShootPeaAngle();
		final double dxz = Math.sqrt(dx * dx + dz * dz);
		if(down != 0){
			dy = Mth.clamp(dy, - dxz / down, dxz / down);//fix dy by angle
		}
//		System.out.println(dy + "," + dxz);
		final double degree = Mth.atan2(dz, dx) + Math.toRadians(angleOffset);
		dx = Math.cos(degree) * dxz;
		dz = Math.sin(degree) * dxz;
		final double totSpeed = Math.sqrt(dxz * dxz + dy * dy);
		this.setDeltaMovement(new Vec3(dx / totSpeed, dy / totSpeed, dz / totSpeed).scale(speed));
	}

	public void shootToTarget(LivingEntity target, double speed) {
		this.setDeltaMovement(target.position().add(0, target.getEyeHeight(), 0).subtract(this.position()).normalize().scale(speed));
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.<br>
	 * only in server side.
	 */
	protected abstract void onImpact(HitResult result);

	protected abstract int getMaxLiveTick();

	protected boolean checkLive(HitResult result) {
		if (result.getType() == HitResult.Type.ENTITY) {// attack entity
			if (EntityUtil.canTargetEntity(getThrower(), ((EntityHitResult) result).getEntity())) {
				return false;
			}
			return true;
		} else if (result.getType() == HitResult.Type.BLOCK) {
			final Block block = level.getBlockState(((BlockHitResult) result).getBlockPos()).getBlock();
			if (block instanceof BushBlock) {
				return true;
			}
		}
		return false;
	}

	@Nullable
	public LivingEntity getThrower() {
		return (LivingEntity) this.getOwner();
	}

	public float getAttackDamage() {
		return this.attackDamage;
	}

	public void setAttackDamage(float damage) {
		this.attackDamage = damage;
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	protected float getGravityVelocity() {
		return 0.03F;
	}

	/**
	 * get how much angle can shoot by thrower
	 */
	public double getShootPeaAngle() {
		if (this.getThrower() instanceof PlantShooterEntity) {
			return ((PlantShooterEntity) this.getThrower()).getMaxShootAngle();
		}
		return 0;
	}

	/**
	 * Checks if the entity is in range to render.
	 */
	@OnlyIn(Dist.CLIENT)
	public boolean shouldRenderAtSqrDistance(double distance) {
		double d0 = this.getBoundingBox().getSize() * 4.0D;
		if (Double.isNaN(d0)) {
			d0 = 4.0D;
		}

		d0 = d0 * 64.0D;
		return distance < d0 * d0;
	}

	/**
	 * Updates the entity motion clientside, called by packets from the server
	 */
	@OnlyIn(Dist.CLIENT)
	public void lerpMotion(double x, double y, double z) {
		this.setDeltaMovement(x, y, z);
		if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
			float f = Mth.sqrt((float) (x * x + z * z));
			this.setYRot((float) (Mth.atan2(x, z) * (double) (180F / (float) Math.PI)));
			this.setXRot((float) (Mth.atan2(y, (double) f) * (double) (180F / (float) Math.PI)));
			this.yRotO = this.getYRot();
			this.xRotO = this.getXRot();
			this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(),
					this.getXRot());
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("bullet_attack_damage")) {
			this.attackDamage = compound.getFloat("bullet_attack_damage");
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putFloat("bullet_attack_damage", this.attackDamage);
	}

	@Override
	public PVZGroupType getInitialEntityGroup() {
		return PVZGroupType.PLANTS;
	}

}
