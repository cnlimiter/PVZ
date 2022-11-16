package cn.evolvefield.mods.pvz.common.entity.misc;

import cn.evolvefield.mods.pvz.common.entity.AbstractOwnerEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class GardenRakeEntity extends AbstractOwnerEntity {

    private static final EntityDataAccessor<Integer> ATTACK_TIME = SynchedEntityData.defineId(GardenRakeEntity.class, EntityDataSerializers.INT);

	public GardenRakeEntity(EntityType<? extends Projectile> entityTypeIn, Level worldIn) {
		super(entityTypeIn, worldIn);
		this.setDeltaMovement(Vec3.ZERO);
	}

	public GardenRakeEntity(Level worldIn, LivingEntity livingEntityIn) {
		super(EntityRegister.GARDEN_RAKE.get(), worldIn, livingEntityIn);
		this.setYRot(livingEntityIn.getDirection().toYRot());
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ATTACK_TIME, 0);
	}

	@Override
	public void tick() {
		super.tick();
		if(! level.isClientSide) {
			if(this.isStartAttack()) {
				this.setAttackTime(this.getAttackTime() + 1);
				if(this.getAttackTime() >= this.getAnimTime()) {
					this.dealDamage();
				}
			} else {
				List<Entity> list = this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(0.2D), (target) -> EntityUtil.canTargetEntity(this, target));
				if(! list.isEmpty()) {
			       this.onStartAttack();
				}
			}
		}
		this.tickMove();
	}

	private void dealDamage() {
		this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(0.25D), (target) -> {
	        return EntityUtil.canTargetEntity(this, target);
        }).forEach((target) -> {
        	target.hurt(PVZEntityDamageSource.normal(this), 180F);
        });
		EntityUtil.playSound(this, SoundRegister.SWING.get());
		this.remove(RemovalReason.KILLED);
	}

	protected void onStartAttack() {
		this.setAttackTime(1);
	}

	@Override
	public InteractionResult interactAt(Player player, Vec3 vec3d, InteractionHand hand) {
		if(! this.isStartAttack() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
			if(! level.isClientSide) {
				player.addItem(new ItemStack(ItemRegister.GARDEN_RAKE.get()));
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

	public boolean isStartAttack() {
		return this.getAttackTime() > 0;
	}

	public int getAnimTime() {
		return 10;
	}

	public void setPlacer(Player player) {
		this.setOwner(player);
		this.setYRot(player.getDirection().toYRot());
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.9F, 0.8F);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("rake_attack_time")) {
			this.setAttackTime(compound.getInt("rake_attack_time"));
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("rake_attack_time", this.getAttackTime());
	}

	public int getAttackTime() {
		return entityData.get(ATTACK_TIME);
	}

	public void setAttackTime(int cd) {
		entityData.set(ATTACK_TIME, cd);
	}


}
