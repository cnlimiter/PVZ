package cn.evolvefield.mods.pvz.common.entity.misc;

import cn.evolvefield.mods.pvz.common.entity.AbstractOwnerEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.enforce.ChomperEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class SmallChomperEntity extends AbstractOwnerEntity {

	private final int maxLifeTick = 20;
	private int lifeTick;

	public SmallChomperEntity(EntityType<? extends Projectile> entityTypeIn, Level worldIn) {
		super(entityTypeIn, worldIn);
		this.setInvulnerable(true);
		this.noPhysics = true;
	}

	@Override
	public void tick() {
		super.tick();
		if(this.lifeTick < maxLifeTick) {
			++ this.lifeTick;
		} else {
			if(! this.level.isClientSide) {
			    this.performAttack();
			    this.remove(RemovalReason.KILLED);
			}
		}
	}

	protected void performAttack() {
		Optional.ofNullable(this.getOwner()).ifPresent(owner -> {
			for(LivingEntity target : EntityUtil.getTargetableLivings(this, EntityUtil.getEntityAABB(this, 1.5F, 2F))) {
				target.hurt(PVZEntityDamageSource.eat(this, owner), getAttackDamage(owner));
			}
		});
		EntityUtil.playSound(this, SoundRegister.BIG_CHOMP.get());
	}

	/**
	 * get damage by owner.
	 */
	private float getAttackDamage(Entity owner) {
		if(owner instanceof ChomperEntity) {
			return ((ChomperEntity) owner).getAttackDamage();
		}
		return 40;
	}

	public int getTick() {
		return this.lifeTick;
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean isNoGravity() {
		return true;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return new EntityDimensions(0.4f, 0.5f, false);
	}

}
