package cn.evolvefield.mods.pvz.common.entity.misc;

import cn.evolvefield.mods.pvz.common.entity.AbstractOwnerEntity;
import cn.evolvefield.mods.pvz.init.config.PVZConfig;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class LawnMowerEntity extends AbstractOwnerEntity {

	private static final EntityDataAccessor<Boolean> START_RUN = SynchedEntityData.defineId(LawnMowerEntity.class, EntityDataSerializers.BOOLEAN);

	public LawnMowerEntity(EntityType<? extends Projectile> entityTypeIn, Level worldIn) {
		super(entityTypeIn, worldIn);
		this.setDeltaMovement(Vec3.ZERO);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(START_RUN, false);
	}

	@Override
	public void tick() {
		super.tick();
		if(! this.level.isClientSide) {
			if(this.isInWater() || this.tickCount >= PVZConfig.COMMON_CONFIG.EntitySettings.EntityLiveTick.LawnMowerLiveTick.get()) {
				this.remove(RemovalReason.KILLED);
				return ;
			}
			if(this.isStartRun()) {
				this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(0.5D), entity -> {
			        return EntityUtil.canTargetEntity(this.getOwnerOrSelf(), entity);
		        }).forEach(this::checkAndRemoveEntity);
				double angle = this.getYRot() * Math.PI / 180;
				double dx = - Math.sin(angle);
				double dz = Math.cos(angle);
				double speed = 0.4D;
				this.setDeltaMovement(dx * speed, this.getDeltaMovement().y(), dz * speed);
			} else {
				-- tickCount;
				List<Entity> list = this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(1), (target) -> {
					 return EntityUtil.canTargetEntity(this.getOwnerOrSelf(), target);
		        });
				if(! list.isEmpty()) {
			       this.onStartRun(list.get(0));
				}
			}
		}
		if(this.tickCount < 5) {
			BlockPos pos = this.blockPosition();
			this.setPos(pos.getX() + 0.5D, this.getY(), pos.getZ() + 0.5D);
		}
		this.tickMove();
	}

	public void checkAndRemoveEntity(Entity target) {
		if(EntityUtil.canEntityBeRemoved(target)) {
    		target.remove(RemovalReason.KILLED);// kill all entity pass by.
    	}
	}

	@Override
	public InteractionResult interactAt(Player player, Vec3 vec3d, InteractionHand hand) {
		if(! this.isStartRun() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
			if(! level.isClientSide) {
				player.addItem(new ItemStack(ItemRegister.LAWN_MOWER.get()));
			    this.remove(RemovalReason.KILLED);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	private void onStartRun(Entity target) {
		this.lookAt(EntityAnchorArgument.Anchor.FEET, target.position());
		this.setStartRun(true);
		EntityUtil.playSound(this, SoundRegister.LAWN_MOWER.get());
	}

	public void setPlacer(Player player) {
		this.setOwner(player);
		this.setYRot(player.getDirection().toYRot());
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
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.8F, 0.8F);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("start_running")) {
			this.setStartRun(compound.getBoolean("start_running"));
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("start_running", this.isStartRun());
	}

	public void setStartRun(boolean is) {
		this.entityData.set(START_RUN, is);
	}

	public boolean isStartRun() {
		return this.entityData.get(START_RUN);
	}

}
