package cn.evolvefield.mods.pvz.common.entity.bullet.itembullet;

import cn.evolvefield.mods.pvz.common.entity.plant.defence.WallNutEntity;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class NutEntity extends PVZItemBulletEntity {

	public NutEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
	}

//	public NutEntity(World worldIn, LivingEntity thrower) {
//		super(EntityRegister.NUT.get(), worldIn, thrower);
//	}
//
	public void shoot(double x, double y, double z) {
		this.setDeltaMovement(x, y, z);
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(ItemRegister.NUT.get());
	}

	@Override
	protected void onImpact(HitResult result) {
		boolean flag = false;
		if(result.getType() == HitResult.Type.BLOCK) {
			if(this.getThrower() != null && level.isEmptyBlock(this.blockPosition().above()) && this.random.nextInt(12) == 0) {
				WallNutEntity nut = EntityRegister.WALL_NUT.get().create(level);
				nut.setOwnerUUID(this.getThrower().getUUID());
				EntityUtil.onEntitySpawn(level, nut, this.blockPosition().above());
				flag = true;
			}
		} else if(result.getType() ==  HitResult.Type.ENTITY) {
			Entity target = ((EntityHitResult) result).getEntity();
			if (this.shouldHit(target)) {
				target.invulnerableTime = 0;
				this.dealNutDamage(target); // attack
				flag = true;
			}
		}
		this.level.broadcastEntityEvent(this, (byte) 3);
		if (flag) {
			this.remove(RemovalReason.KILLED);
		}
	}

	private void dealNutDamage(Entity target) {
//		target.hurt(PVZDamageSource.causeNormalDamage(this, this.getThrower()), 2);
	}

	@Override
	protected int getMaxLiveTick() {
		return 80;
	}

	@Override
	protected float getGravityVelocity() {
		return 0.06f;
	}

}
