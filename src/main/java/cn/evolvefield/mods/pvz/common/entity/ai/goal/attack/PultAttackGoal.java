package cn.evolvefield.mods.pvz.common.entity.ai.goal.attack;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.interfaces.util.IPult;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PultAttackGoal extends Goal {

	private final IPult pult;
	protected final Mob attacker;
	protected LivingEntity target;
	protected final boolean checkSight;
	protected int attackTime;

	public PultAttackGoal(IPult pult) {
		this(pult, true);
	}

	public PultAttackGoal(IPult pult, boolean checkSight) {
		this.pult = pult;
		this.checkSight = checkSight;
		this.attacker = (Mob) pult;
		if(! (pult instanceof Mob)) {
			Static.LOGGER.warn("Error : Wrong pult attacker !");
			return ;
		}
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		//attacker can not pult because of itself.
		if(! this.pult.shouldPult()) {
			return false;
		}
		final LivingEntity target = this.attacker.getTarget();
		//attacker can not pult because of its target, so clear target.
		if(! EntityUtil.isEntityValid(target) || ! this.checkTarget(target)) {
			this.stop();
			return false;
		}
		this.target = target;
		return true;
	}

	@Override
	public boolean canContinueToUse() {
		return this.canUse();
	}

	@Override
	public void stop() {
		this.target = null;
		this.attacker.setTarget(null);
	}

	@Override
	public void tick() {
		++ this.attackTime;
		if(this.attackTime >= this.pult.getPultCD()) {
			this.attackTime = 0;
			this.pult.startPultAttack();
		}
		this.attacker.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
	}

	protected boolean checkTarget(LivingEntity target) {
		if(EntityUtil.checkCanEntityBeAttack(this.attacker, target)) {
			return ! this.checkSight || this.attacker.getSensing().hasLineOfSight(target);
		}
		return false;
	}

}
