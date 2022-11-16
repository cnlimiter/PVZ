package cn.evolvefield.mods.pvz.common.entity.plant.explosion;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.ai.goal.target.PVZRandomTargetGoal;
import cn.evolvefield.mods.pvz.common.entity.bullet.CornEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class CobCannonEntity extends PVZPlantEntity {

	protected static final EntityDataAccessor<Integer> CORN_NUM = SynchedEntityData.defineId(CobCannonEntity.class,
			EntityDataSerializers.INT);
	protected Optional<LivingEntity> lockTarget = Optional.empty();
	protected Optional<BlockPos> lockPos = Optional.empty();
	protected int cornCnt = 0;
	protected final int MaxCornCnt = 16;
	protected int preTick = 0;
	private int climbTick = 0;

	public CobCannonEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.canCollideWithPlant = false;
		this.isImmuneToWeak = true;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(CORN_NUM, 1);
	}

	protected void initAttributes() {
		super.initAttributes();
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(2, new PVZRandomTargetGoal(this, true, false, 10, 48, 16));
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		if (!level.isClientSide) {
			++ this.preTick;
			if (this.getAttackTime() == 0 && this.preTick >= this.getPreCD()) {
				this.preTick = 0;
				this.setCornNum(Math.min(2, this.getCornNum() + 1));
			}
			if(this.getCornNum() >= 2 && !this.isPlayerRiding() && this.getTarget() != null) {
				this.setAttackTime(this.getAnimCD());
				this.setCornNum(this.getCornNum() - 1);
			}
			if (this.getAttackTime() > 0) {
				this.setAttackTime(this.getAttackTime() - 1);
				this.getPassengers().forEach((entity) -> {
					entity.stopRiding();
				});
				if (this.getAttackTime() == this.getAnimCD() / 2) {
					this.startAttack();
				}
			}
		}
	}

	/**
	 * {@link EntityInteractPacket.Handler#onMessage(EntityInteractPacket, java.util.function.Supplier)}
	 */
	public void checkAndAttack() {
		//is in player's control and not in attacking.
		if(this.getAttackTime() == 0 && this.isPlayerRiding()) {
			final Player player = (Player) this.getPassengers().get(0);
			final Vec3 look = player.getLookAngle();
		    final Vec3 start = player.position().add(0, player.getEyeHeight(), 0);
		    final double range = 60;
		    Vec3 end = start.add(look.normalize().multiply(range, range, range));
		    var ray = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
		    HitResult result = level.clip(ray);
		    if(result.getType() != HitResult.Type.MISS) {// hit something
			    end = result.getLocation();
		    }
			var entityRay = this.rayTraceEntities(level, player, range, start, end);
		    if(entityRay != null && entityRay.getType() == HitResult.Type.ENTITY) {
			    if(entityRay.getEntity() instanceof LivingEntity) {//attack entity
			    	this.setAttackTime(this.getAnimCD());
			    	this.setCornNum(this.getCornNum() - 1);
			    	this.lockTarget = Optional.ofNullable((LivingEntity) entityRay.getEntity());
			    }
		    } else if(result.getType() == HitResult.Type.BLOCK) {//attack block.
		    	this.setAttackTime(this.getAnimCD());
		    	BlockPos pos = new BlockPos(end.x(), end.y(), end.z());
		    	this.setCornNum(this.getCornNum() - 1);
		    	this.lockPos = Optional.ofNullable(pos);
		    }
		}
	}

	protected void startAttack() {
		if(this.lockTarget.isPresent()) {
			this.shootCorn(this.lockTarget.get());
			this.lockTarget = Optional.empty();
			return ;
		}
		if(this.lockPos.isPresent()) {
			this.shootCorn(this.lockPos.get());
			this.lockPos = Optional.empty();
			return ;
		}
		final float range = 45F;
		final List<LivingEntity> list = EntityUtil.getViewableTargetableEntity(this, EntityUtil.getEntityAABB(this, range, range));
		final int num = (this.isPlantInSuperMode() ? this.getSuperCornNum() : 1);
		for(int i = 0; i < num; ++ i) {
			LivingEntity res = this;
		    if(! list.isEmpty()) {
			    int pos = this.getRandom().nextInt(list.size());
			    res = list.get(pos);
		    }
		    this.shootCorn(res);
		}
	}

	/**
	 * shoot to entity.
	 * {@link #startAttack()}
	 */
	protected void shootCorn(LivingEntity target) {
		CornEntity corn = new CornEntity(level, this);
		this.onShootCorn(corn);
		corn.shootPultBullet(target);
		level.addFreshEntity(corn);
	}

	/**
	 * shoot to block.
	 * {@link #startAttack()}
	 */
	protected void shootCorn(BlockPos pos) {
		CornEntity corn = new CornEntity(level, this);
		this.onShootCorn(corn);
		corn.shootPultBullet(pos);
		level.addFreshEntity(corn);
	}

	private void onShootCorn(CornEntity corn) {
		corn.setPos(this.getX(), this.getY() + 1.5D, this.getZ());
		corn.setAttackDamage(this.getAttackDamage());
		corn.cornCnt = this.cornCnt;
		this.cornCnt = 0;
		EntityUtil.playSound(this, SoundRegister.COB_LAUNCH.get());
	}

	@Override
	public InteractionResult interactAt(Player player, Vec3 vec3d, InteractionHand hand) {
		if (player.isSecondaryUseActive() || EntityUtil.canTargetEntity(this, player)) {
			return InteractionResult.FAIL;
		}
		ItemStack stack = player.getItemInHand(hand);
		if(this.getAttackTime() == 0 && stack.isEmpty()) {
			if(this.mountTo(player)) {
				return InteractionResult.SUCCESS;
			}
		} else if(stack.getItem() == ItemRegister.CORN.get()) {
			if(this.cornCnt < this.MaxCornCnt) {
			    ++ this.cornCnt;
			    stack.shrink(Math.min(stack.getCount(), this.MaxCornCnt - this.cornCnt));
			}
			return InteractionResult.CONSUME;
		}
		return super.interactAt(player, vec3d, hand);
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		this.setAttackTime(this.getAnimCD());
	}

	@Override
	protected boolean shouldLockXZ() {
		return !this.isPlayerRiding();
	}

	/**
	 * Gets the EntityRayTraceResult representing the entity hit
	 */
	@Nullable
	protected EntityHitResult rayTraceEntities(Level world, Player player, double range, Vec3 startVec, Vec3 endVec) {
		return ProjectileUtil.getEntityHitResult(world, player, startVec, endVec,
				player.getBoundingBox().inflate(range), entity -> {
			return EntityUtil.isEntityValid(entity) && entity instanceof LivingEntity && ! entity.is(this);
		});
	}

	public boolean isPlayerRiding() {
		for (Entity entity : this.getPassengers()) {
			if (entity instanceof Player)
				return true;
		}
		return false;
	}

	@Override
	public boolean canPlaceOuterPlant() {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	private boolean isRidingPlayer(Player player) {
		return player.getVehicle() != null && player.getVehicle() == this;
	}

	public void travel(Vec3 p_213352_1_) {
		if (this.isAlive()) {
			if (this.isVehicle() && this.isPlayerRiding()) {
				Player player = (Player) this.getPassengers().get(0);
				if (player == null) {
					System.out.println("ERROR : Wrong judge !");
					return;
				}
				this.setYRot(player.getYRot());
				this.yRotO = this.getYRot();
				this.setXRot(player.getXRot() * 0.5F);
				this.setRot(this.getYRot(), this.getXRot());
				this.yBodyRot = this.getYRot();
				this.yHeadRot = this.yBodyRot;
				float f = player.xxa * 0.5F;
				float f1 = player.zza;
				if (f1 <= 0.0F) {
					f1 *= 0.25F;
				}
				//jump
				if(this.horizontalCollision) {
					if(++ this.climbTick <= 8) {
						final Vec3 Vec3 = this.getDeltaMovement();
	                    this.setDeltaMovement(Vec3.x, 0.2D, Vec3.z);
					}
				} else {
					this.climbTick = 0;
				}
				this.flyingSpeed = this.getSpeed() * 0.1F;
				this.setSpeed((float) this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
				super.travel(new Vec3((double) f, p_213352_1_.y, (double) f1));
				this.animationSpeedOld = this.animationSpeed;
				double d2 = this.getX() - this.xo;
				double d3 = this.getZ() - this.zo;
				float f4 = Mth.sqrt((float) (d2 * d2 + d3 * d3)) * 4.0F;
				if (f4 > 1.0F) {
					f4 = 1.0F;
				}
				this.animationSpeed += (f4 - this.animationSpeed) * 0.4F;
				this.animationPosition += this.animationSpeed;
			} else {
				this.flyingSpeed = 0.02F;
				super.travel(p_213352_1_);
			}
		}
	}

	@Override
	public boolean shouldWilt() {
		return this.isInWaterOrBubble();
	}

	protected boolean mountTo(Player player) {
		if (!this.level.isClientSide) {
			this.setYRot(player.getYRot());
			this.setXRot(player.getXRot());
			player.startRiding(this);
			return true;
		}
		return false;
	}

	@Override
	public double getPassengersRidingOffset() {
		return 0.8D;
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return ! EntityUtil.canTargetEntity(this, passenger) && super.canAddPassenger(passenger);
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
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.addAll(Arrays.asList(
				Pair.of(PAZAlmanacs.PREPARE_CD, this.getPreCD()),
				Pair.of(PAZAlmanacs.ATTACK_DAMAGE, this.getAttackDamage())
		));
	}

	public int getPreCD() {
		return 1000;
	}

	public int getSuperCornNum() {
		return 4;
	}

	public float getAttackDamage() {
		return this.getSkillValue(SkillTypes.NORMAL_BOMB_DAMAGE);
	}

	public int getAnimCD() {
		return 60;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(1.25f, 1f);
	}

	@Override
	public int getSuperTimeLength() {
		return 80;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("cannon_pre_tick")) {
			this.preTick = compound.getInt("cannon_pre_tick");
		}
		if (compound.contains("cannon_corn_num")) {
			this.setCornNum(compound.getInt("cannon_corn_num"));
		}
		if(compound.contains("cannon_pop_corn_cnt")) {
			this.cornCnt = compound.getInt("cannon_pop_corn_cnt");
		}
		if(compound.contains("cannon_lock_target")) {
			Entity entity = level.getEntity(compound.getInt("cannon_lock_target"));
			if(entity instanceof LivingEntity) {
				this.lockTarget = Optional.ofNullable((LivingEntity) entity);
			}
		}
		if(compound.contains("cannon_lock_pos")) {
			CompoundTag nbt = compound.getCompound("cannon_lock_pos");
			this.lockPos = Optional.ofNullable(new BlockPos(nbt.getInt("lock_posX"), nbt.getInt("lock_posY"), nbt.getInt("lock_posZ")));
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("cannon_pre_tick", this.preTick);
		compound.putInt("cannon_corn_num", this.getCornNum());
		compound.putInt("cannon_pop_corn_cnt", this.cornCnt);
		if(this.lockTarget.isPresent()) {
			compound.putInt("cannon_lock_target", this.lockTarget.get().getId());
		}
		if(this.lockPos.isPresent()) {
			CompoundTag nbt = new CompoundTag();
			nbt.putInt("lock_posX", this.lockPos.get().getX());
			nbt.putInt("lock_posY", this.lockPos.get().getY());
			nbt.putInt("lock_posZ", this.lockPos.get().getZ());
			compound.put("cannon_lock_pos", nbt);
		}
	}

	public int getCornNum() {
		return this.entityData.get(CORN_NUM);
	}

	public void setCornNum(int num) {
		this.entityData.set(CORN_NUM, num);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.COB_CANNON;
	}

}
