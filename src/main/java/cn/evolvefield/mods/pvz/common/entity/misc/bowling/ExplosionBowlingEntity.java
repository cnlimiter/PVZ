package cn.evolvefield.mods.pvz.common.entity.misc.bowling;

import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.ParticleRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class ExplosionBowlingEntity extends AbstractBowlingEntity {

	public ExplosionBowlingEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public ExplosionBowlingEntity(EntityType<? extends Projectile> type, Level worldIn, Player entity) {
		super(type, worldIn, entity);
	}

	@Override
	protected void tickCollision() {
		if(this.getBowlingFacing() == BowlingFacings.BOMB) {
			this.bomb();
			this.remove(RemovalReason.KILLED);
		} else {
			super.tickCollision();
		}
	}

	private void bomb() {
		if(! level.isClientSide) {
		} else {
			this.level.addParticle(ParticleRegister.RED_BOMB.get(), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
		}
	}

	@Override
	protected void changeDiretion() {
		float len = 2.5F;
		var aabb = EntityUtil.getEntityAABB(this, len, len);
		EntityUtil.getTargetableEntities(this.getOwnerOrSelf(), aabb).forEach((target) -> {
			target.hurt(PVZEntityDamageSource.explode(this), 180);
		});
		EntityUtil.playSound(this, SoundRegister.CHERRY_BOMB.get());
		this.setBowlingFacing(BowlingFacings.BOMB);
	}

	@Override
	protected void dealDamageTo(Entity entity) {

	}

}
