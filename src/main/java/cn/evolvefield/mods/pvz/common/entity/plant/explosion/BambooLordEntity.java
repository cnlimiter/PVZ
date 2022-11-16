package cn.evolvefield.mods.pvz.common.entity.plant.explosion;


import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.bullet.itembullet.FireCrackerEntity;
import cn.evolvefield.mods.pvz.common.entity.misc.FireCrackersEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantCloserEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.OtherPlants;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.WorldUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class BambooLordEntity extends PlantCloserEntity {

	public static final int UP_CD = 20;
	public static final float UP_SPEED = 2F;

	public BambooLordEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void focusOnTarget(LivingEntity target1) {
		super.focusOnTarget(target1);
		if(this.getAttackTime() == 1) {//fly together.
			final float range = 2F;
			EntityUtil.getTargetableEntities(this, EntityUtil.getEntityAABB(this, range, range)).forEach((target) -> {
				target.setDeltaMovement(target.getDeltaMovement().add(0, UP_SPEED, 0));
			});
			this.setDeltaMovement(this.getDeltaMovement().add(0, UP_SPEED, 0));
			for(int i = 0; i < 3; ++ i) {
				EntityUtil.spawnParticle(this, 5);
			}
			EntityUtil.playSound(this, SoundRegister.POTATO_MINE.get());
		}
	}

	@Override
	public void performAttack(LivingEntity target1) {
		for(int i = 0; i < 3; ++ i) {
			EntityUtil.spawnParticle(this, 5);
		}
		final float range = 3F;
		EntityUtil.getTargetableEntities(this, EntityUtil.getEntityAABB(this, range, range)).forEach((target) -> {
			target.hurt(PVZEntityDamageSource.explode(this), this.getAttackDamage());
			target.setDeltaMovement(target.getDeltaMovement().add(0, UP_SPEED, 0));
		});
		this.split();
		EntityUtil.playSound(this, SoundRegister.POTATO_MINE.get());
	}

	/**
	 * {@link #performAttack(LivingEntity)}
	 */
	protected void split() {
		final float range = 30;
		List<LivingEntity> list = new ArrayList<>();
		EntityUtil.getTargetableLivings(this, EntityUtil.getEntityAABB(this, range, range)).forEach(target -> {
			if(this.getSensing().hasLineOfSight(target)) {
				list.add(target);
			}
		});
		if(! list.isEmpty()) {
			for(int i = 0; i < this.getSplitCount(); ++ i) {
				int pos = this.getRandom().nextInt(list.size());
				FireCrackerEntity entity = new FireCrackerEntity(level, this);
				entity.setPos(this.getX(), this.getY(), this.getZ());
				entity.shoot(list.get(pos));
				entity.summonByOwner(this);
				entity.setAttackDamage(this.getAttackDamage() * 2);
				level.addFreshEntity(entity);
			}
		}
		for(int i = 0; i < 3; ++ i) {
			EntityUtil.spawnParticle(this, 5);
		}
		this.remove(RemovalReason.KILLED);
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.add(Pair.of(PAZAlmanacs.EXPLODE_DAMAGE, this.getAttackDamage()));
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		for(int i = 0; i < this.getCrackersNum(); ++ i) {
			this.generateCracker();
		}
	}

	protected void generateCracker() {
		final int range = 10;
		final BlockPos pos = WorldUtil.getSuitableHeightRandomPos(this.level, this.blockPosition(), range);
		FireCrackersEntity entity = new FireCrackersEntity(level, this);
		entity.setPos(pos.getX(), pos.getY(), pos.getZ());
		entity.setFuse(10);
		entity.summonByOwner(this);
		level.addFreshEntity(entity);
	}

	public float getAttackDamage() {
		return this.getSkillValue(SkillTypes.SMALL_BOMB_DAMAGE);
	}

	public int getSplitCount() {
		return 1;
	}

	public int getCrackersNum() {
		return 10;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.6F, 1F);
	}

	@Override
	public IPlantType getPlantType() {
		return OtherPlants.BAMBOO_LORD;
	}

}
