package cn.evolvefield.mods.pvz.common.entity.bullet;

import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class KernelEntity extends PultBulletEntity {

	public KernelEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public KernelEntity(Level worldIn, LivingEntity shooter) {
		super(EntityRegister.KERNEL.get(), worldIn, shooter);
	}

	protected void dealDamage(Entity target) {
		target.hurt(PVZEntityDamageSource.kernel(this, this.getThrower()), this.getAttackDamage() / 2F);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.4F, 0.4F);
	}

}
