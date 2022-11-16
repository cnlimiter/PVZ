package cn.evolvefield.mods.pvz.common.entity.plant.spear;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class SpikeRockEntity extends SpikeWeedEntity {

    public SpikeRockEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.setSpikeNum(this.getSpikesCount());
	}

	public int getSpikesCount() {
		return (int) this.getSkillValue(SkillTypes.MORE_SPIKE);
	}

	@Override
	public int getAttackCD() {
		return 20;
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.SPIKE_ROCK;
	}

}
