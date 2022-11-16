package cn.evolvefield.mods.pvz.common.entity.ai.goal.attack;

import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class PVZZombieAttackGoal extends PVZMeleeAttackGoal {

	protected final PVZZombieEntity zombie;
	protected final int LeapCD = 50;
	protected int leapTick = 0;

	public PVZZombieAttackGoal(PVZZombieEntity creature, boolean useLongMemory) {
		super(creature, 1.0, useLongMemory);
		this.zombie = creature;
	}

	@Override
	public void tick() {
		if(! this.zombie.canNormalUpdate()) {
			this.zombie.setAggressive(false);
			return ;
		}
		super.tick();
	}

	@Override
	protected void checkAndPerformAttack(LivingEntity target) {
		final double dis = EntityUtil.getNearestDistance(this.attacker, target);
		double range = this.getAttackReachSqr(target);
		if (range >= dis) {
			if(this.attackTick <= 0) {
			    this.attackTick = this.zombie.getAttackCD();
			    this.attacker.swing(InteractionHand.MAIN_HAND);
			    this.attacker.doHurtTarget(target);
			}
		} else {
			//stop move.
			if(this.zombie.canNormalUpdate() && this.attacker.getDeltaMovement().length() <= 0.1D){
				this.checkLeapToTarget(target);
			}
		}
		this.attacker.setAggressive(dis <= 20);
	}

	/**
	 * leap to target when zombie can not reach there.
	 */
	protected void checkLeapToTarget(LivingEntity target){
		if(++ this.leapTick >= this.LeapCD){
			if((this.attacker.getNavigation().getPath() == null || this.attacker.getNavigation().getPath().isDone()) && this.attacker.isOnGround()) {
				//ground jump or change target.
				final float random = this.zombie.getRandom().nextFloat();
				if(random < 0.55) {
					var speed = target.position().subtract(this.attacker.position()).normalize();
					this.attacker.setDeltaMovement(speed.scale(this.attacker.getRandom().nextDouble() * 0.4 + 0.4).scale(this.attacker.getAttributeValue(Attributes.MOVEMENT_SPEED)));
				} else if(this.zombie.getLastHurtByMob() != null) {
					if(random < 0.85) {
						if(this.zombie.distanceTo(target) >= this.zombie.distanceTo(this.zombie.getLastHurtByMob())) {
							this.zombie.setTarget(this.zombie.getLastHurtByMob());
						}
					} else {
						this.zombie.setTarget(this.zombie.getLastHurtByMob());
					}
				}
				this.leapTick = 0;
			} else if(this.attacker.isInWater()) {
				var speed = target.position().subtract(this.attacker.position()).normalize();
				this.attacker.setDeltaMovement(speed.scale(this.attacker.getRandom().nextDouble() * 0.3 + 0.2).scale(this.attacker.getAttributeValue(Attributes.MOVEMENT_SPEED)));
				this.leapTick = 0;
			}
		}
	}

}
