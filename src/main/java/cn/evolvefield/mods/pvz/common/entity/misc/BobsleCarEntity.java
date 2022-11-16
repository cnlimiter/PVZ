package cn.evolvefield.mods.pvz.common.entity.misc;

import cn.evolvefield.mods.pvz.common.block.plants.LilyPadBlock;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class BobsleCarEntity extends Entity {

	private static final EntityDataAccessor<Integer> TIME_SINCE_HIT = SynchedEntityData.defineId(BobsleCarEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> DAMAGE_TAKEN = SynchedEntityData.defineId(BobsleCarEntity.class,
			EntityDataSerializers.FLOAT);
	private static final int MAX_PASSENGER_SIZE = 4;
	private static final float SNOW_SMOOTH = 0.991f;
	private static final float MAX_MOVE_SPEED = 0.042f;
	private static final float MAX_ROTATION = 1f;
	private Status status;
	private float momentum;
	private float deltaRotation;
	private int lerpSteps;
	private double lerpX;
	private double lerpY;
	private double lerpZ;
	private double lerpYaw;
	private double lerpPitch;
	private float boatGlide;

	public BobsleCarEntity(EntityType<?> entityTypeIn, Level worldIn) {
		super(entityTypeIn, worldIn);
		refreshDimensions();
	}

	protected void defineSynchedData() {
		this.entityData.define(TIME_SINCE_HIT, 0);
		this.entityData.define(DAMAGE_TAKEN, 0.0F);
	}

	public void tick() {
		super.tick();
		this.status = this.getCarStatus();
		if (this.status == Status.IN_WATER) {
			this.ejectPassengers();
			this.remove(RemovalReason.KILLED);
		}
		if (this.getTimeSinceHit() > 0) {
			this.setTimeSinceHit(this.getTimeSinceHit() - 1);
		}

		if (this.getDamageTaken() > 0.0F) {
			this.setDamageTaken(this.getDamageTaken() - 1.0F);
		}

		this.tickLerp();
		if (this.isControlledByLocalInstance()) {
			this.updateMotion();
			if (this.level.isClientSide) {
				this.updateControls();
			}

			this.move(MoverType.SELF, this.getDeltaMovement());
		} else {
			this.setDeltaMovement(Vec3.ZERO);
		}

		this.checkInsideBlocks();

		if (!level.isClientSide) {// check collide or passenger
			for (Entity entity : this.level.getEntities(this,
					this.getBoundingBox().inflate((double) 0.2F, (double) -0.01F, (double) 0.2F),
					EntitySelector.pushableBy(this))) {
				if (!entity.isPassenger()) {
					if (checkCanRideOn(entity)) {
						entity.startRiding(this);
					} else {
						this.push(entity);
					}
				}
			}
		}
	}

	private boolean checkCanRideOn(Entity entity) {
		return !(this.getControllingPassenger() instanceof Player)
				&& this.getPassengers().size() < MAX_PASSENGER_SIZE && !entity.isPassenger()
				&& entity.getBbWidth() < this.getBbWidth() && entity instanceof LivingEntity
				&& !(entity instanceof WaterAnimal) && !(entity instanceof Player);
	}



	@Override
	public InteractionResult interactAt(Player player, Vec3 vec3d, InteractionHand hand) {
		if (player.isSecondaryUseActive()) {
			return InteractionResult.FAIL;
		}
		if(! this.level.isClientSide) {
			player.startRiding(this);
			return InteractionResult.SUCCESS;
		} else {
			return InteractionResult.FAIL;
		}
	}

	@OnlyIn(Dist.CLIENT)
	protected void updateControls() {
		Minecraft mc = Minecraft.getInstance();
		if (this.isRidingPlayer(mc.player)) {
			float f = 0;
			boolean left = mc.options.keyLeft.isDown();
			boolean right = mc.options.keyRight.isDown();
			boolean forward = mc.options.keyUp.isDown();
			boolean back = mc.options.keyDown.isDown();
			if (left) {
				this.deltaRotation -= MAX_ROTATION;
			}
			if (right) {
				this.deltaRotation += MAX_ROTATION;
			}
			if (!right && !left) {
				this.deltaRotation = 0;
			}
			this.setYRot(this.getYRot() + this.deltaRotation);
			if (right != left && !forward && !back) {
				f += MAX_MOVE_SPEED / 10f;
			}
			if (forward) {
				f += MAX_MOVE_SPEED;
			}
			if (back) {
				f -= MAX_MOVE_SPEED / 3;
			}
			this.setDeltaMovement(
					this.getDeltaMovement().add((double) (Mth.sin(-this.getYRot() * ((float) Math.PI / 180F)) * f),
							0.0D, (double) (Mth.cos(this.getYRot() * ((float) Math.PI / 180F)) * f)));
		}
	}

	@OnlyIn(Dist.CLIENT)
	private boolean isRidingPlayer(Player player) {
		return player.getVehicle() != null && player.getVehicle() == this;
	}

	public boolean isPickable() {
		return !this.isRemoved();
	}

	@Nullable
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengers().size() < MAX_PASSENGER_SIZE;
	}


	@Override
	public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
		return false;
	}


	@Override
	public boolean rideableUnderWater() {
		return false;
	}

	@Override
	public boolean isSilent() {
		return true;
	}


	public double getPassengersRidingOffset() {
		return 0.2D;
	}

	@SuppressWarnings("deprecation")
	public boolean hurt(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (!this.level.isClientSide && !this.isRemoved()) {
			if (source instanceof IndirectEntityDamageSource && source.getEntity() != null
					&& this.hasPassenger(source.getEntity())) {
				return false;
			} else {
				this.setTimeSinceHit(10);
				this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
				this.markHurt();
				boolean flag = source.getEntity() instanceof Player
						&& ((Player) source.getEntity()).getAbilities().instabuild;
				if (flag || this.getDamageTaken() > 40.0F) {
					if (!flag && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
						this.spawnAtLocation(ItemRegister.BOBSLE_CAR.get());
					}

					this.remove(RemovalReason.KILLED);
				}

				return true;
			}
		} else {
			return true;
		}
	}

	/**
	 * Setups the entity to do the hurt animation. Only used by packets in
	 * multiplayer.
	 */
	@OnlyIn(Dist.CLIENT)
	public void animateHurt() {
		this.setTimeSinceHit(10);
		this.setDamageTaken(this.getDamageTaken() * 11.0F);
	}

	/**
	 * Sets a target for the client to interpolate towards over the next few ticks
	 */
	@OnlyIn(Dist.CLIENT)
	public void lerpTo(double x, double y, double z, float yaw, float pitch,
			int posRotationIncrements, boolean teleport) {
		this.lerpX = x;
		this.lerpY = y;
		this.lerpZ = z;
		this.lerpYaw = (double) yaw;
		this.lerpPitch = (double) pitch;
		this.lerpSteps = 10;
	}

	/**
	 * Gets the horizontal facing direction of this Entity, adjusted to take
	 * specially-treated entity types into account.
	 */
	public Direction getMotionDirection() {
		return this.getDirection().getClockWise();
	}

	private void tickLerp() {
		if (this.isControlledByLocalInstance()) {
			this.lerpSteps = 0;
			this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
		}

		if (this.lerpSteps > 0) {
			double d0 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
			double d1 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
			double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
			double d3 = Mth.wrapDegrees(this.lerpYaw - (double) this.getYRot());
			this.setYRot((float) ((double) this.getYRot() + d3 / (double) this.lerpSteps));
			this.setXRot((float) ((double) this.getXRot()
					+ (this.lerpPitch - (double) this.getXRot()) / (double) this.lerpSteps));
			--this.lerpSteps;
			this.setPos(d0, d1, d2);
			this.setRot(this.getYRot(), this.getXRot());
		}
	}

	private Status getCarStatus() {
		if (this.isInWater()) {
			return Status.IN_WATER;
		} else {
			float f = this.getCarGlide();
			if (f == SNOW_SMOOTH) {
				this.boatGlide = f;
				return Status.ON_SNOW;
			}
			if (f > 0.0F) {
				this.boatGlide = f;
				return Status.ON_LAND;
			} else {
				return Status.IN_AIR;
			}
		}
	}

	/**
	 * Decides how much the boat should be gliding on the land (based on any
	 * slippery blocks)
	 */
	public float getCarGlide() {
		if (this.level.getBlockState(this.blockPosition()).getBlock() == Blocks.SNOW
				|| this.level.getBlockState(this.blockPosition().below()).getBlock() == Blocks.SNOW_BLOCK) {
			return SNOW_SMOOTH;
		}
		AABB axisalignedbb = this.getBoundingBox();
		AABB axisalignedbb1 = new AABB(axisalignedbb.minX, axisalignedbb.minY - 0.001D,
				axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		int i = Mth.floor(axisalignedbb1.minX) - 1;
		int j = Mth.ceil(axisalignedbb1.maxX) + 1;
		int k = Mth.floor(axisalignedbb1.minY) - 1;
		int l = Mth.ceil(axisalignedbb1.maxY) + 1;
		int i1 = Mth.floor(axisalignedbb1.minZ) - 1;
		int j1 = Mth.ceil(axisalignedbb1.maxZ) + 1;
		VoxelShape voxelshape = Shapes.create(axisalignedbb1);
		float f = 0.0F;
		int k1 = 0;
		BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

	      for(int l1 = i; l1 < j; ++l1) {
	         for(int i2 = i1; i2 < j1; ++i2) {
	            int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);
	            if (j2 != 2) {
	               for(int k2 = k; k2 < l; ++k2) {
	                  if (j2 <= 0 || k2 != k && k2 != l - 1) {
	                     blockpos$mutable.set(l1, k2, i2);
	                     BlockState blockstate = this.level.getBlockState(blockpos$mutable);
	                     if (!(blockstate.getBlock() instanceof LilyPadBlock) && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level, blockpos$mutable).move((double)l1, (double)k2, (double)i2), voxelshape, BooleanOp.AND)) {
	                        f += blockstate.getFriction(this.level, blockpos$mutable, this);
	                        ++k1;
	                     }
	                  }
	               }
	            }
	         }
	      }

		return f / (float) k1;
	}

	/**
	 * Update the boat's speed, based on momentum.
	 */
	private void updateMotion() {
		double d1 = -0.04;
		this.momentum = 0.05F;
		if (this.status == Status.IN_AIR) {
			this.momentum = 0.9F;
		} else if (this.status == Status.ON_LAND) {
			this.momentum = this.boatGlide;
			if (this.getControllingPassenger() instanceof Player) {
				this.boatGlide /= 2.0F;
			}
		} else if (this.status == Status.ON_SNOW) {
			this.momentum = this.boatGlide;
			if (this.getControllingPassenger() instanceof Player) {
				this.boatGlide /= 2.0F;
			}
		}

		var vec3d = this.getDeltaMovement();
		this.setDeltaMovement(vec3d.x * (double) this.momentum, vec3d.y + d1, vec3d.z * (double) this.momentum);
		this.deltaRotation *= this.momentum;
	}

	@SuppressWarnings("deprecation")
	public void positionRider(Entity passenger) {
		if (this.hasPassenger(passenger)) {
			float f = 0.0F;
			float f1 = (float) ((this.isRemoved() ? (double) 0.01F : this.getPassengersRidingOffset()) + passenger.getMyRidingOffset());
			if (this.getPassengers().size() > 1) {
				int i = this.getPassengers().indexOf(passenger);
				f = 0.2F - 0.7f * i;
				if (passenger instanceof Animal) {
					f = (float) ((double) f + 0.2D);
				}
			}

			var vec3d = (new Vec3((double) f, 0.0D, 0.0D)).yRot(
					(-this.getYRot() * ((float) Math.PI / 180F) - ((float) Math.PI / 2F)));
			passenger.setPos(this.getX() + vec3d.x, this.getY() + (double) f1, this.getZ() + vec3d.z);
			passenger.setYRot(passenger.getYRot() + this.deltaRotation); ;
			passenger.setYHeadRot(passenger.getYHeadRot() + this.deltaRotation);
			this.applyYawToEntity(passenger);
			if (passenger instanceof Animal && this.getPassengers().size() > 1) {
				int j = passenger.getId() % 2 == 0 ? 90 : 270;
				passenger.setYBodyRot(((Animal) passenger).yBodyRot + (float) j);
				passenger.setYHeadRot(passenger.getYHeadRot() + (float) j);
			}

		}
	}

	/**
	 * Applies this boat's yaw to the given entity. Used to update the orientation
	 * of its passenger.
	 */
	protected void applyYawToEntity(Entity entityToUpdate) {
		entityToUpdate.setYBodyRot((this.getYRot()));
		float f = Mth.wrapDegrees(entityToUpdate.getYRot() - this.getYRot());
		float f1 = Mth.clamp(f, -105.0F, 105.0F);
		entityToUpdate.yRotO += f1 - f;
		entityToUpdate.setYRot(entityToUpdate.getYRot() + f1 - f); ;
		entityToUpdate.setYHeadRot(entityToUpdate.getYRot());
	}

	/**
	 * Applies this entity's orientation (pitch/yaw) to another entity. Used to
	 * update passenger orientation.
	 */
	@OnlyIn(Dist.CLIENT)
	public void onPassengerTurned(Entity entityToUpdate) {
		this.applyYawToEntity(entityToUpdate);
	}

	protected void addAdditionalSaveData(CompoundTag compound) {
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	protected void readAdditionalSaveData(CompoundTag compound) {
	}

	@SuppressWarnings("deprecation")
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
		if (!this.isPassenger()) {
			if (onGroundIn) {
				if (this.fallDistance > 3.0F) {
					if (this.status != Status.ON_LAND && this.status != Status.ON_SNOW) {
						this.fallDistance = 0.0F;
						return;
					}

					this.causeFallDamage(this.fallDistance, 1.0F, PVZEntityDamageSource.FALL);
					if (!this.level.isClientSide && !this.isRemoved()) {
						this.remove(RemovalReason.KILLED);
						if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
							this.spawnAtLocation(ItemRegister.BOBSLE_CAR.get());
						}
					}
				}
				this.fallDistance = 0.0F;
			} else if (!this.level.getFluidState(this.blockPosition().below()).is(FluidTags.WATER) && y < 0.0D) {
				this.fallDistance = (float) ((double) this.fallDistance - y);
			}

		}
	}


	/**
	 * Sets the damage taken from the last hit.
	 */
	public void setDamageTaken(float damageTaken) {
		this.entityData.set(DAMAGE_TAKEN, damageTaken);
	}

	/**
	 * Gets the damage taken from the last hit.
	 */
	public float getDamageTaken() {
		return this.entityData.get(DAMAGE_TAKEN);
	}

	/**
	 * Sets the time to count down from since the last time entity was hit.
	 */
	public void setTimeSinceHit(int timeSinceHit) {
		this.entityData.set(TIME_SINCE_HIT, timeSinceHit);
	}

	/**
	 * Gets the time since the last hit.
	 */
	public int getTimeSinceHit() {
		return this.entityData.get(TIME_SINCE_HIT);
	}

	// Forge: Fix MC-119811 by instantly completing lerp on board
	@Override
	protected void addPassenger(Entity passenger) {
		super.addPassenger(passenger);
		if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
			this.lerpSteps = 0;
			this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, (float) this.lerpYaw,
					(float) this.lerpPitch);
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(1.25f, 1.4f);
	}

	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public static enum Status {
		IN_WATER, ON_SNOW, ON_LAND, IN_AIR;
	}

}
