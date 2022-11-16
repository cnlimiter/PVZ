package cn.evolvefield.mods.pvz.common.entity.plant.assist;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.enums.MetalTypes;
import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.base.IHasMetal;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.ai.goal.target.PVZNearestTargetGoal;
import cn.evolvefield.mods.pvz.common.entity.ai.goal.target.PVZTargetGoal;
import cn.evolvefield.mods.pvz.common.entity.bullet.itembullet.MetalItemEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.AlgorithmUtil;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MagnetShroomEntity extends PVZPlantEntity {

	private static final EntityDataAccessor<Integer> METAL_TYPE = SynchedEntityData.defineId(MagnetShroomEntity.class,
			EntityDataSerializers.INT);

	public MagnetShroomEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(METAL_TYPE, MetalTypes.EMPTY.ordinal());
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(0, new PVZNearestTargetGoal(this, true, false, this.getAbsorbRange(), this.getAbsorbRange()));
		this.targetSelector.addGoal(1, new TargetLadderGoal(this, true, false, this.getAbsorbRange()));
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		if(! level.isClientSide) {
			if(this.getAttackTime() > 0) {
				this.setAttackTime(this.getAttackTime() - 1);
			} else if(this.getAttackTime() == 0) {
				this.setMetalType(MetalTypes.EMPTY);
			}
			LivingEntity target = this.getTarget();
			if(EntityUtil.isEntityValid(target) && this.isMagnetActive()) {
				this.dragMetal(target);
			}
		}
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		final float range = this.getAbsorbRange();
		int cnt = this.getSuperDragCnt();
		EntityUtil.playSound(this, SoundRegister.MAGNET.get());
		for(LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, EntityUtil.getEntityAABB(this, range, range), (entity) -> {
			return this.checkCanPAZTarget(entity);
		})){
			if(! (target instanceof IHasMetal)) continue;
			((IHasMetal) target).decreaseMetal();
			MetalItemEntity metal = new MetalItemEntity(level, this, ((IHasMetal) target).getMetalType());
			metal.setMetalState(MetalItemEntity.MetalStates.BULLET);
			metal.setPos(target.getX(), target.getY() + target.getEyeHeight(), target.getZ());
			metal.summonByOwner(this);
			metal.setAttackDamage(this.getAttackDamage());
			level.addFreshEntity(metal);
			if(-- cnt == 0) return ;
		};
	}

	/**
	 * {@link #normalPlantTick()}
	 */
	public void dragMetal(LivingEntity target) {
		if(target instanceof IHasMetal) {
			((IHasMetal) target).decreaseMetal();
			this.setAttackTime(this.getWorkCD());
			MetalItemEntity metal = new MetalItemEntity(level, this, ((IHasMetal) target).getMetalType());
			metal.setPos(target.getX(), target.getY() + target.getEyeHeight(), target.getZ());
			metal.summonByOwner(this);
			level.addFreshEntity(metal);
			EntityUtil.playSound(this, SoundRegister.MAGNET.get());
		} else {
			Static.LOGGER.warn("Wrong target for MagnetShroom.");
		}
	}

	public int getAttackDamage() {
		return 100;
	}

	public int getSuperDragCnt() {
		return 4;
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.addAll(Arrays.asList(
				Pair.of(PAZAlmanacs.WORK_CD, this.getWorkCD()),
				Pair.of(PAZAlmanacs.WORK_RANGE, this.getAbsorbRange())
		));
	}

	public float getAbsorbRange(){
		return 15;
	}

	public int getWorkCD() {
		return (int) this.getSkillValue(SkillTypes.LESS_WORK_CD);
	}

	/**
	 * is not consuming metal.
	 */
	public boolean isMagnetActive() {
		return this.getAttackTime() == 0;
	}

	public ItemStack getMetalRenderItem() {
		if(this.getMetalType() == null) return ItemStack.EMPTY;
		return new ItemStack(MetalTypes.getMetalItem(getMetalType()));
	}

	@Override
	public boolean canPAZTarget(Entity entity) {
		return entity instanceof IHasMetal && ((IHasMetal) entity).hasMetal();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("metal_type", this.getMetalType().ordinal());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("metal_type")) {
			this.setMetalType(MetalTypes.values()[compound.getInt("metal_type")]);
		}
	}

	public MetalTypes getMetalType() {
		return MetalTypes.values()[entityData.get(METAL_TYPE)];
	}

	public void setMetalType(MetalTypes type) {
		entityData.set(METAL_TYPE, type.ordinal());
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.5f, 1.3f);
	}

	@Override
	public int getSuperTimeLength() {
		return 60;
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.MAGNET_SHROOM;
	}

	private static class TargetLadderGoal extends PVZTargetGoal {

		protected final AlgorithmUtil.EntitySorter sorter;

		public TargetLadderGoal(MagnetShroomEntity mobIn, boolean checkSight, boolean mustReach, float range) {
			super(mobIn, checkSight, mustReach, range, range);
			this.sorter = new AlgorithmUtil.EntitySorter(mob);
		}

		@Override
		public boolean canUse() {
			if (this.targetChance > 0 && this.mob.getRandom().nextInt(this.targetChance) != 0) {
				return false;
			}
			List<LivingEntity> list1 = new ArrayList<LivingEntity>();
			this.mob.level.getEntitiesOfClass(PVZPlantEntity.class, getAABB()).forEach(plant -> {
				if(! EntityUtil.canTargetEntity(mob, plant) && this.checkSenses(plant)) {
					if(plant.hasMetal()) {
						list1.add(plant);
					}
				}
			});
			if (list1.isEmpty()) {
				return false;
			}
			Collections.sort(list1, this.sorter);
			this.targetMob = list1.get(0);
			return true;
		}

		@Override
		public boolean canContinueToUse() {
			LivingEntity entity = this.mob.getTarget();
			if (entity == null) {
				entity = this.targetMob;
			}
			if(entity == null || ! entity.isAlive()) {
				return false;
			}
			if(! EntityUtil.canTargetEntity(mob, entity) && this.checkSenses(entity)) {
				this.mob.setTarget(entity);
				return true;
			}
			return false;
		}

	}

}
