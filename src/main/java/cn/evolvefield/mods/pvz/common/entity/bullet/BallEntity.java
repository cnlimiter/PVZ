package cn.evolvefield.mods.pvz.common.entity.bullet;

import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class BallEntity extends PultBulletEntity {

	public BallEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.4F, 0.4F);
	}

	@Override
	protected void dealDamage(Entity target) {
		target.hurt(PVZEntityDamageSource.ball(this, this.getOwnerOrSelf()), this.getAttackDamage());
	}

}
