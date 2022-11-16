package cn.evolvefield.mods.pvz.common.entity.misc.bowling;

import cn.evolvefield.mods.pvz.common.entity.AbstractOwnerEntity;
import cn.evolvefield.mods.pvz.init.config.PVZConfig;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractBowlingEntity extends AbstractOwnerEntity {

	protected IntOpenHashSet hitEntities;
	private static final EntityDataAccessor<Integer> FACING = SynchedEntityData.defineId(AbstractBowlingEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DIRECTION = SynchedEntityData.defineId(AbstractBowlingEntity.class, EntityDataSerializers.INT);
	private int bowlingTick = 0;
	private int wallTick = 0;
	private boolean playSpawnSound = false;
	protected int hitCount = 0;

	public AbstractBowlingEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
		this.pushthrough = 1F;
	}

	public AbstractBowlingEntity(EntityType<? extends Projectile> type, Level worldIn, Player livingEntityIn) {
		this(type, worldIn);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(FACING, BowlingFacings.MID.ordinal());
		this.entityData.define(DIRECTION, Direction.NORTH.ordinal());
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void tick() {
		super.tick();
		if (! level.isClientSide) {
			if(this.tickCount <= 10 && ! this.playSpawnSound) {
				EntityUtil.playSound(this, SoundRegister.BOWLING.get());
				this.playSpawnSound = true;
			}
			if(this.tickCount >= this.getMaxLiveTick()) {
				this.remove(RemovalReason.KILLED);
			}
		}
		this.yRotO = this.getYRot();
		this.setYRot(this.getDirection().toYRot() + this.getBowlingFacing().offset);
		double angle = this.getYRot() * Math.PI / 180;
		double dx = - Math.sin(angle);
		double dz = Math.cos(angle);
		double speed = this.getBowlingSpeed();
		this.setDeltaMovement(dx * speed, this.getDeltaMovement().y(), dz * speed);
		this.tickRayTrace();
		this.tickMove();
		this.tickCollision();
		if(! this.level.isClientSide) {
			if(this.bowlingTick > 0) -- this.bowlingTick;
			if(this.wallTick > 0) -- this.wallTick;
			if(this.bowlingTick == 0 && this.horizontalCollision) {// collide with wall
				if(this.wallTick > 0) {
					this.remove(RemovalReason.KILLED);
				} else {
					this.wallTick = 15;
					this.changeDiretion();
				}
			}
		}
	}

	protected void tickCollision() {
		if(this.level.isClientSide) return ;
		if(this.bowlingTick > 0) return ;
		List<Entity> list = this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox(), (target) -> {
			return EntityUtil.canTargetEntity(this.getOwnerOrSelf(), target);
		});
		if(! list.isEmpty()) {
			this.dealDamageTo(list.get(0));
			this.changeDiretion();
		}
	}

	private void tickRayTrace() {
		double rayLen = 3D;
		double angle = (this.getDirection().toYRot() + this.getBowlingFacing().offset) * Math.PI / 180;
		double dx = Math.sin(angle);
		double dz = - Math.cos(angle);
		var start = this.position();
		var end = start.add(new Vec3(dx * rayLen, 0, dz * rayLen));
		HitResult result = this.level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		if(result.getType() != HitResult.Type.MISS) {// hit something
			end = result.getLocation();
		}
		EntityHitResult entityRay = this.rayTraceEntities(start, end);
		if(entityRay != null) {
			result = entityRay;
		}
		if(result != null && result.getType() != HitResult.Type.MISS && ! net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, result)) {
			this.onImpact(result);
		}
	}

	protected void changeDiretion() {
		if(this.getBowlingFacing() == BowlingFacings.MID) {
			this.setBowlingFacing(this.random.nextInt(2) == 0 ? BowlingFacings.LEFT :BowlingFacings.RIGHT);
		} else if(this.getBowlingFacing() == BowlingFacings.LEFT){
			this.setBowlingFacing(BowlingFacings.RIGHT);
		} else if(this.getBowlingFacing() == BowlingFacings.RIGHT){
			this.setBowlingFacing(BowlingFacings.LEFT);
		}
		this.bowlingTick = 10;
	}

	protected abstract void dealDamageTo(Entity entity);

	public void shoot(Player player) {
		Direction direction = player.getDirection();
		this.setDirection(direction);
		this.setYRot(direction.toYRot());
	}

	public double getBowlingSpeed() {
		return 0.3D;
	}

	protected void addHitEntity(Entity entity) {
		this.hitEntities.addAll(EntityUtil.getOwnerAndPartsID(entity));
	}

	protected boolean shouldHit(Entity target) {
		return EntityUtil.canTargetEntity(this.getOwnerOrSelf(), target);
	}

	/**
	 * Gets the EntityRayTraceResult representing the entity hit
	 */
	@Nullable
	protected EntityHitResult rayTraceEntities(Vec3 startVec, Vec3 endVec) {
		return ProjectileUtil.getEntityHitResult(this.level, this, startVec, endVec,
				this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (entity) -> {
			return entity.isPickable() && shouldHit(entity)
							&& (this.hitEntities == null|| ! this.hitEntities.contains(entity.getId()));
		});
	}

	protected void onImpact(HitResult result) {

	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.95F, 1F);
	}

	protected int getMaxLiveTick() {
		return PVZConfig.COMMON_CONFIG.EntitySettings.EntityLiveTick.BowlingLiveTick.get();
	}

	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("bowling_facings", this.getBowlingFacing().ordinal());
		compound.putInt("bowling_directions", this.getDirection().ordinal());
		compound.putInt("bowling_tick", this.bowlingTick);
		compound.putInt("bowling_hit_count", this.hitCount);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("bowling_facings")) {
			this.setBowlingFacing(BowlingFacings.values()[compound.getInt("bowling_facings")]);
		}
		if(compound.contains("bowling_directions")) {
			this.setDirection(Direction.values()[compound.getInt("bowling_directions")]);
		}
		if(compound.contains("bowling_tick")) {
			this.bowlingTick = compound.getInt("bowling_tick");
		}
		if(compound.contains("bowling_hit_count")) {
			this.hitCount = compound.getInt("bowling_hit_count");
		}
	}

	public Direction getDirection() {
		return Direction.values()[this.entityData.get(DIRECTION)];
	}

	public void setDirection(Direction drt) {
		this.entityData.set(DIRECTION, drt.ordinal());
	}

	public BowlingFacings getBowlingFacing() {
		return BowlingFacings.values()[this.entityData.get(FACING)];
	}

	public void setBowlingFacing(BowlingFacings facing) {
		this.entityData.set(FACING, facing.ordinal());
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
//		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
//			float f = MathHelper.sqrt(x * x + z * z);
//			this.rotationYaw = (float) (MathHelper.atan2(x, z) * (double) (180F / (float) Math.PI));
//			this.rotationPitch = (float) (MathHelper.atan2(y, (double) f) * (double) (180F / (float) Math.PI));
//			this.prevRotationYaw = this.rotationYaw;
//			this.prevRotationPitch = this.rotationPitch;
//			this.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw,
//					this.rotationPitch);
//		}
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	public static enum BowlingFacings {
		LEFT(- 45),
		MID(0),
		RIGHT(45),
		BOMB(0);
		public final float offset;

		private BowlingFacings(float offset) {
			this.offset = offset;
		}

	}

}
