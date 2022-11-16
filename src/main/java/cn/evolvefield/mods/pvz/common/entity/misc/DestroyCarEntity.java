package cn.evolvefield.mods.pvz.common.entity.misc;

import cn.evolvefield.mods.pvz.common.entity.AbstractOwnerEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.MathUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;

public class DestroyCarEntity extends AbstractOwnerEntity {

	protected final float height = 20;

	public DestroyCarEntity(EntityType<?> entityTypeIn, Level worldIn) {
		super(entityTypeIn, worldIn);
	}

	@Override
	public void tick() {
		super.tick();
		if(! level.isClientSide) {
			if(this.tickCount >= 100 || this.isOnGround()) {
				this.remove();
			}
		}
		this.tickMove();
		this.tickCollision();
	}

	/**
     * Pult shoot
     */
    public void shootPultBullet(LivingEntity target) {
    	if(target == null) {
    		System.out.println("Warn: No target at all .");
    		return ;
    	}
    	double g = this.getGravityVelocity();
    	double t1 = Mth.sqrt(2 * height / g);//go up time
    	double t2 = 0;
    	if(this.getY() + height - target.getY() - target.getBbHeight() >= 0) {//random pult
    		t2 = Mth.sqrt(2 * (this.getY() + height - target.getY() - target.getBbHeight()) / g);//go down time
    	}
    	double dx = target.getX() + target.getDeltaMovement().x() * (t1 + t2) - this.getX();
    	double dz = target.getZ() + target.getDeltaMovement().z() * (t1 + t2) - this.getZ();
    	double dxz = Mth.sqrt(dx * dx + dz * dz);
    	double vxz = dxz / (t1 + t2);
    	double vy = g * t1;
    	this.setDeltaMovement(vxz * dx / dxz, vy + MathUtil.getRandomFloat(this.random) / 10, vxz * dz / dxz);
    }

	private void tickCollision() {
		if(! level.isClientSide && this.tickCount % 15 == 0) {
			EntityUtil.getTargetableEntities(this.getOwnerOrSelf(), this.getBoundingBox().inflate(1F)).forEach((target) -> {
				if(target instanceof LivingEntity) {
					target.hurt(PVZEntityDamageSource.causeDeadlyDamage(this, this.getOwner()), EntityUtil.getCurrentMaxHealth((LivingEntity) target));
				}
			});
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(2F, 2F);
	}

}
