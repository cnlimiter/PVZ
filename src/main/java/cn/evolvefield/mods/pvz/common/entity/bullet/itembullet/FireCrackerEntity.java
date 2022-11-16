package cn.evolvefield.mods.pvz.common.entity.bullet.itembullet;

import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FireCrackerEntity extends PVZItemBulletEntity{

	private static final float SPEED = 1.5F;
	protected Entity target = null;

	public FireCrackerEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public FireCrackerEntity(Level worldIn, LivingEntity owner) {
		super(EntityRegister.FIRE_CRACKER.get(), worldIn, owner);
	}

	@Override
	public void tick() {
		super.tick();
		if(! level.isClientSide && EntityUtil.isEntityValid(target)) {
			this.shoot(this.target);
		}
	}

	public void shoot(Vec3 vec) {
		this.setDeltaMovement(vec.scale(SPEED));
	}

	public void shoot(Entity target) {
		this.target = target;
		var vec = target.position().subtract(this.position()).normalize();
		this.shoot(vec);
	}

	@Override
	protected void onImpact(HitResult result) {
		boolean flag = false;
		if (result.getType() == HitResult.Type.ENTITY) {
			Entity target = ((EntityHitResult) result).getEntity();
			if (this.shouldHit(target)) {
				target.invulnerableTime = 0;
				this.dealDamage(target); // attack
				flag = true;
			}
		}
		this.level.broadcastEntityEvent(this, (byte) 3);
		if (flag) {
			this.remove();
		} else if(! this.checkLive(result)) {
			this.dealDamage(null);
			this.remove();
		}
	}

	private void dealDamage(Entity target) {
		if(! level.isClientSide) {
			EntityUtil.playSound(this, SoundRegister.POTATO_MINE.get());
		    float range = 3F;
		    EntityUtil.getTargetableEntities(this.getOwnerOrSelf(), EntityUtil.getEntityAABB(this, range, range)).forEach((entity) -> {
			    entity.hurt(PVZEntityDamageSource.explode(this, this.getThrower()), this.getAttackDamage());
		    });
		    for(int i = 0;i < 3; ++ i) {
			    EntityUtil.spawnParticle(this, 5);
		    }
		}
	}

	@Override
	protected int getMaxLiveTick() {
		return 50;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("target_entity_id")) {
			this.target = (Entity) level.getEntity(compound.getInt("target_entity_id"));
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		if(this.target != null) {
			compound.putInt("target_entity_id", this.target.getId());
		}
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(ItemRegister.FIRE_CRACKER.get());
	}

	@Override
	public boolean isNoGravity() {
		return true;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.5F, 0.5F);
	}

}
