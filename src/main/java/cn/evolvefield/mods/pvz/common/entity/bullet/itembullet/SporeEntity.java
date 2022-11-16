package cn.evolvefield.mods.pvz.common.entity.bullet.itembullet;

import cn.evolvefield.mods.pvz.common.entity.plant.toxic.PuffShroomEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.toxic.SeaShroomEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import cn.evolvefield.mods.pvz.init.registry.ParticleRegister;
import cn.evolvefield.mods.pvz.utils.WorldUtil;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class SporeEntity extends PVZItemBulletEntity{

	public SporeEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public SporeEntity(Level worldIn, LivingEntity living) {
		super(EntityRegister.SPORE.get(), worldIn, living);
	}

	@Override
	public void tick() {
		super.tick();
		if(level.isClientSide) {
			for(int i = 0; i < 3; ++i) {
				WorldUtil.spawnRandomSpeedParticle(level, ParticleRegister.SPORE.get(), this.position(), 0);
	        }
		}
	}

	@Override
	protected int getMaxLiveTick() {
		if(this.getThrower() instanceof PuffShroomEntity || this.getThrower() instanceof SeaShroomEntity) {
			return 10;
		}
		return 24;
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(ItemRegister.SPORE.get());
	}

	@Override
	protected void onImpact(HitResult result) {
		boolean flag = false;
		if (result.getType() == HitResult.Type.ENTITY) {
			Entity target = ((EntityHitResult) result).getEntity();
			if (this.shouldHit(target)) {
				target.invulnerableTime = 0;
				this.dealSporeDamage(target); // attack
				flag = true;
			}
		}
		this.level.broadcastEntityEvent(this, (byte) 3);
		if (flag || !this.checkLive(result)) {
			this.remove(RemovalReason.KILLED);
		}
	}

	private void dealSporeDamage(Entity target) {
		target.hurt(PVZEntityDamageSource.spore(this, this.getThrower()), this.attackDamage);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.25f, 0.25f);
	}

	@Override
	protected float getGravityVelocity() {
		return 0.0012f;
	}

}
