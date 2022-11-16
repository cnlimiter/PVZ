package cn.evolvefield.mods.pvz.common.entity.plant.enforce;


import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.ai.goal.target.PVZNearestTargetGoal;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.pool.BalloonZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.OtherPlants;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class BonkChoyEntity extends PVZPlantEntity {

	public BonkChoyEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new BonkChoyAttackGoal(this));
		this.targetSelector.addGoal(0, new PVZNearestTargetGoal(this, true, false, 3, 3));
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		if(! level.isClientSide) {
			if(this.isPlantInSuperMode() && this.getSuperTime() % 5 == 0) {
				final float range = 5F;
				EntityUtil.getTargetableEntities(this, EntityUtil.getEntityAABB(this, range, range)).forEach((target) -> {
					target.hurt(PVZEntityDamageSource.normal(this), this.getAttackDamage() * 5);
					EntityUtil.spawnParticle(target, 7);
					EntityUtil.playSound(this, SoundRegister.SWING.get());
				});
			}
		}
	}

	public void attackTarget(LivingEntity target) {
		EntityUtil.playSound(this, SoundRegister.SWING.get());
		EntityUtil.spawnParticle(target, 7);
		target.hurt(PVZEntityDamageSource.normal(this), this.getAttackDamage());
	}

	@Override
	public boolean canPAZTarget(Entity entity) {
		if(entity instanceof BalloonZombieEntity) {
			return true;
		}
		return super.canPAZTarget(entity);
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.addAll(Arrays.asList(
				Pair.of(PAZAlmanacs.ATTACK_DAMAGE, this.getAttackDamage()),
				Pair.of(PAZAlmanacs.ATTACK_CD, this.getAttackCD())
		));
	}

	public int getAttackCD() {
		return 10;
	}

	public float getAttackDamage() {
		return this.getSkillValue(SkillTypes.MORE_SWING_DAMAGE);
	}

	@Override
	public int getSuperTimeLength() {
		return 120;
	}

	@Override
	public IPlantType getPlantType() {
		return OtherPlants.BONK_CHOY;
	}

	private final class BonkChoyAttackGoal extends Goal {

		private final BonkChoyEntity attacker;

		public BonkChoyAttackGoal(BonkChoyEntity attacker) {
			this.attacker = attacker;
			this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			LivingEntity living = this.attacker.getTarget();
			if (! EntityUtil.isEntityValid(living)) return false;
			return this.attacker.canCollideWith(living) && EntityUtil.getAttackRange(attacker, living, 3F) >= EntityUtil.getNearestDistance(this.attacker, living);
		}

		@Override
		public boolean canContinueToUse() {
			LivingEntity living = this.attacker.getTarget();
			if (! EntityUtil.isEntityValid(living)) return false;
			return this.attacker.canCollideWith(living) && EntityUtil.getAttackRange(attacker, living, 3F) >= EntityUtil.getNearestDistance(this.attacker, living);
		}

		@Override
		public void stop() {
			this.attacker.setTarget(null);
			this.attacker.setAttackTime(0);
		}

		@Override
		public void tick() {
			LivingEntity target = this.attacker.getTarget();
			this.attacker.getLookControl().setLookAt(target, 30F, 30F);
			if(this.attacker.getAttackTime() == 0) {
				if(this.attacker.getAttackDamage() >= EntityUtil.getCurrentHealth(target)) {
					this.attacker.setAttackTime(1);
				} else {
					this.attacker.setAttackTime(- 1);
				}
			} else if(this.attacker.getAttackTime() > 0) {
				this.attacker.setAttackTime(this.attacker.getAttackTime() + 1);
				if(this.attacker.getAttackTime() == this.attacker.getAttackCD() * 4 / 5) {
				    this.attacker.attackTarget(target);
				} else if(this.attacker.getAttackTime() >= this.attacker.getAttackCD()) {
					this.attacker.setAttackTime(0);
				}
			} else {
				this.attacker.setAttackTime(this.attacker.getAttackTime() - 1);
				if(- this.attacker.getAttackTime() == this.attacker.getAttackCD() * 4 / 5) {
				    this.attacker.attackTarget(target);
				} else if(- this.attacker.getAttackTime() >= this.attacker.getAttackCD()) {
					this.attacker.setAttackTime(0);
				}
			}
		}

	}

}
