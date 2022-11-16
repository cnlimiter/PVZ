package cn.evolvefield.mods.pvz.common.entity.plant.flame;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.bullet.itembullet.PeaEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.init.registry.ParticleRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.WorldUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;

import java.util.List;

public class TorchWoodEntity extends PVZPlantEntity {

	private static final EntityDataAccessor<Integer> FLAME_TYPE = SynchedEntityData.defineId(TorchWoodEntity.class, EntityDataSerializers.INT);

	public TorchWoodEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(FLAME_TYPE, FlameTypes.YELLOW.ordinal());
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		if(! level.isClientSide) {
			this.heatPeas();
		}else {
			var particle = ParticleRegister.YELLOW_FLAME.get();
			if(this.getFlameType() == FlameTypes.BLUE) {
				particle = ParticleRegister.BLUE_FLAME.get();
			}
			WorldUtil.spawnRandomSpeedParticle(this.level, particle, this.position().add(0, 1.5F, 0), 0.1F);
		}
	}

	/**
	 * {@link #normalPlantTick()}
	 */
	public void heatPeas() {
		final float range = this.getHeatRange();
		level.getEntitiesOfClass(PeaEntity.class, EntityUtil.getEntityAABB(this, range, range)).forEach(pea -> {
			pea.heatBy(this);
		});
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		this.setFlameType(FlameTypes.BLUE);
	}

	@Override
	public boolean canStartSuperMode() {
		return super.canStartSuperMode() && this.getFlameType() == FlameTypes.YELLOW;
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.add(Pair.of(PAZAlmanacs.HEAT_PEA_RANGE, this.getHeatRange()));
	}

	/**
	 * {@link #heatPeas()}
	 */
	public float getHeatRange() {
		return this.getSkillValue(SkillTypes.HEAT_PEA_RANGE);
	}

	@Override
	protected float getLife() {
		return this.getSkillValue(SkillTypes.WOOD_MORE_LIFE);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return new EntityDimensions(0.95f, 1.95f, false);
	}

	@Override
	public int getSuperTimeLength() {
		return 0;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("flame_type")) {
			this.setFlameType(FlameTypes.values()[compound.getInt("flame_type")]);
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("flame_type", this.getFlameType().ordinal());
	}

	public FlameTypes getFlameType() {
		return FlameTypes.values()[entityData.get(FLAME_TYPE)];
	}

	public void setFlameType(FlameTypes type) {
		entityData.set(FLAME_TYPE, type.ordinal());
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.TORCH_WOOD;
	}

	public enum FlameTypes{
		YELLOW,
		BLUE,
		PURPLE
	}

}
