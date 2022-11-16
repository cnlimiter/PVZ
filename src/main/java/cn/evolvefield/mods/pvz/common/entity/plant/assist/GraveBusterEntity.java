package cn.evolvefield.mods.pvz.common.entity.plant.assist;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.ai.goal.target.PVZNearestTargetGoal;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.grass.AbstractTombStoneEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.PlantUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

import java.util.List;

public class GraveBusterEntity extends PVZPlantEntity {

	private static final int MAX_LIVE_TICK = 100;
	private int killCount = 0;

	public GraveBusterEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.canCollideWithPlant = false;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new PVZNearestTargetGoal(this, true, false, 5, 5));
		this.goalSelector.addGoal(0, new EatTombStoneGoal(this));
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		if(!this.level.isClientSide) {
			if(this.isEatingTomb()) {
			    if(this.getAttackTime() % 20 == 10) {
				    EntityUtil.playSound(this, SoundRegister.CHOMP.get());
			    }
			    this.setExistTick(0);
			}
			if(this.getExistTick() > MAX_LIVE_TICK) {
				this.remove(RemovalReason.KILLED);
			}
		}
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		final float range = 10;
		int cnt = this.getSuperAttackCnt();
		for (LivingEntity target : EntityUtil.getTargetableLivings(this, EntityUtil.getEntityAABB(this, range, range))) {
			if(! (target instanceof AbstractTombStoneEntity)) {
				continue;
			}
			GraveBusterEntity buster = EntityRegister.GRAVE_BUSTER.get().create(level);
			PlantUtil.copyPlantData(buster, this);
			EntityUtil.onEntitySpawn(level, buster, target.blockPosition());
			buster.startRiding(target);
			buster.setTarget(target);
			if (-- cnt == 0) {
				break;
			}
		}
	}

	/**
	 * {@link EatTombStoneGoal#canUse()}
	 */
	public boolean isEatingTomb() {
		return this.getAttackTime() > 0 && this.getVehicle() instanceof AbstractTombStoneEntity;
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.add(Pair.of(PAZAlmanacs.WORK_TIME, this.getEatTombCD()));
	}

	public int getEatTombCD() {
		return 100;
	}

	public int getMaxKillCnt() {
		return 1;
	}

	/**
	 * how many gravebuster to summon.
	 */
	public int getSuperAttackCnt() {
		return 2;
	}

	@Override
	public boolean canPAZTarget(Entity entity) {
		return entity instanceof AbstractTombStoneEntity && (entity.getPassengers().isEmpty() || entity.is(this.getVehicle()));
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(1f, 1.6f);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("kill_cnt", this.killCount);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("kill_cnt")) {
			this.killCount = compound.getInt("kill_cnt");
		}
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.GRAVE_BUSTER;
	}

	@Override
	public int getSuperTimeLength() {
		return 20;
	}

	static class EatTombStoneGoal extends Goal {

		private GraveBusterEntity buster;
		private LivingEntity target;

		public EatTombStoneGoal(GraveBusterEntity buster) {
			this.buster = buster;
		}

		@Override
		public boolean canUse() {
			final LivingEntity target = this.buster.getTarget();
			if(this.buster.isEatingTomb()) {
				return true;
			}
			if(! EntityUtil.isEntityValid(target) || ! this.buster.canPAZTarget(target)) {
				this.buster.setTarget(null);
			    this.target = null;
				return false;
			}
			this.target = target;
			this.buster.startRiding(this.target, true);
			return true;
		}

		@Override
		public void stop() {
			this.target = null;
		}

		@Override
		public boolean canContinueToUse() {
			return this.buster.isEatingTomb();
		}

		@Override
		public void tick() {
			if(! this.buster.canNormalUpdate()) {
				return ;
			}
			final int tick = this.buster.getAttackTime();
			if(tick >= this.buster.getEatTombCD()) {
				this.buster.setAttackTime(0);
				++ this.buster.killCount;
				this.target.hurt(PVZEntityDamageSource.eat(this.buster), EntityUtil.getMaxHealthDamage(this.buster.getTarget(), 1.5F));
			    if(this.buster.killCount >= this.buster.getMaxKillCnt()) {
					this.buster.remove(RemovalReason.KILLED);
				}
			} else {
				this.buster.setAttackTime(tick + 1);
			}
		}
	}
}
