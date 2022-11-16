package cn.evolvefield.mods.pvz.common.entity.bullet;

import cn.evolvefield.mods.pvz.common.entity.bullet.itembullet.PVZItemBulletEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.toxic.GloomShroomEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import cn.evolvefield.mods.pvz.init.registry.ParticleRegister;
import cn.evolvefield.mods.pvz.utils.LevelUtil;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class FumeEntity extends PVZItemBulletEntity {

	private int knockback = 0;

	public FumeEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public FumeEntity(Level worldIn, LivingEntity living) {
		super(EntityRegister.FUME.get(), worldIn, living);
	}

	@Override
	public void tick() {
		super.tick();
		if(level.isClientSide) {
			final int cnt = Math.max(2, Math.min(5, this.getMaxLiveTick() / this.tickCount));
			for(int i = 0; i < cnt; ++ i) {
				LevelUtil.spawnRandomSpeedParticle(level, ParticleRegister.FUME.get(), this.position(), 0.05F);
	        }
		}
	}

	@Override
	protected int getMaxLiveTick() {
		if(this.getThrower() instanceof GloomShroomEntity) {
			return 3;
		}
		return 10;
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(ItemRegister.SPORE.get());
	}

	@Override
	protected void onImpact(HitResult result) {
		boolean flag = false;
		if (result.getType() == HitResult.Type.ENTITY) {
			var target = ((EntityHitResult) result).getEntity();
			if (this.shouldHit(target)) {
				target.invulnerableTime = 0;
				this.dealFumeDamage(target); // attack
				if(this.hitEntities == null) {
					this.hitEntities = new IntOpenHashSet();
				}
				this.addHitEntity(target);
			}
		}
		this.level.broadcastEntityEvent(this, (byte) 3);
		if (flag || !this.checkLive(result)) {
			this.remove(RemovalReason.KILLED);
		}
	}

	@Override
	protected boolean checkLive(HitResult result) {
		if(result.getType() == HitResult.Type.BLOCK) {
    		var block = level.getBlockState(((BlockHitResult)result).getBlockPos()).getBlock();
    		if(block instanceof BushBlock) {
    			return true;
    		}
    		return false;
    	}
    	return true;
	}

	private void dealFumeDamage(Entity target) {
		target.hurt(PVZEntityDamageSource.fume(this, this.getThrower()), this.attackDamage);
		if(!level.isClientSide && this.knockback > 0) {
			var speed = target.getDeltaMovement();
			var now = this.getDeltaMovement();
			int lvl = this.knockback;
			target.setDeltaMovement(speed.add(now).multiply(lvl, lvl, lvl));
		}
	}

	public void setKnockback(int lvl) {
		this.knockback = lvl;
	}

	public int getKnockback() {
		return this.knockback;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.25f, 0.25f);
	}

	@Override
	protected float getGravityVelocity() {
		return 0.002f;
	}

}
