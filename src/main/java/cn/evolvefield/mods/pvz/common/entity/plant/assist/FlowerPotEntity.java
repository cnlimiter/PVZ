package cn.evolvefield.mods.pvz.common.entity.plant.assist;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class FlowerPotEntity extends PVZPlantEntity{

	public FlowerPotEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.FLOWER_POT;
	}

	@Override
	public int getSuperTimeLength() {
		return 0;
	}

}
