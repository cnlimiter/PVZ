package cn.evolvefield.mods.pvz.common.entity.plant.base;

import cn.evolvefield.mods.pvz.common.entity.ai.goal.target.PVZNearestTargetGoal;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * Closer Plants will be attack after several ticks focusing on its target.
 * use ATTACK_TIME to perform scale animation.
 */
public abstract class PlantCloserEntity extends PVZPlantEntity {

	protected float closeWidth = 1.5F;
	protected float closeHeight = 1.5F;

	public PlantCloserEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(0, new PVZNearestTargetGoal(this, true, false, this.getCloseWidth(), this.getCloseHeight()));
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		if(! this.level.isClientSide && this.canCheckDistance()) {
			if(EntityUtil.isEntityValid(this.getTarget())) {//target is close enough
				this.focusOnTarget(this.getTarget());
				final int time = this.getAttackTime();
				if(time >= this.getAttackCD()) {
					this.performAttack(this.getTarget());
				}
				this.setAttackTime(time + 1);
			} else {
				this.setAttackTime(0);
			}
		}
	}

	/**
	 * perform when target is in range.
	 * {@link #normalPlantTick()}
	 */
	public void focusOnTarget(@Nonnull LivingEntity target) {
		this.getLookControl().setLookAt(this.getTarget(), 30f, 30f);
	}

	/**
	 * whether plants can check distance and perform attack.
	 * {@link #normalPlantTick()}
	 */
	public boolean canCheckDistance() {
		return true;
	}

	/**
	 * perform attack when target is close.
	 * {@link #normalPlantTick()}
	 */
	public abstract void performAttack(LivingEntity target);

	@Override
	public boolean isPlantImmuneTo(DamageSource source) {
		if(this.canBeImmuneToEnforce(source.getEntity())) {
			return super.isPlantImmuneTo(source) || PVZEntityDamageSource.isEnforceDamage(source);
		}
		return super.isPlantImmuneTo(source);
	}

	protected boolean canBeImmuneToEnforce(Entity entity) {
		return checkCanPAZTarget(entity);
	}

	@Override
	public int getSuperTimeLength() {
		return 20;
	}

	/**
	 * how long it takes to attack.
	 */
	public int getAttackCD() {
		return 10;
	}

	/**
	 * horizontal distance needed when attack.
	 * {@link #registerGoals()}
	 */
	public float getCloseWidth() {
		return 1.5F;
	}

	/**
	 * vertical distance needed when attack.
	 * {@link #registerGoals()}
	 */
	public float getCloseHeight() {
		return 1.5F;
	}

}
