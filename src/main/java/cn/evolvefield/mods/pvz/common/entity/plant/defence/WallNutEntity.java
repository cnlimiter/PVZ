package cn.evolvefield.mods.pvz.common.entity.plant.defence;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantDefenderEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
public class WallNutEntity extends PlantDefenderEntity {

	public WallNutEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public float getLife() {
		return this.getSkillValue(SkillTypes.NUT_MORE_LIFE);
	}

	@Override
	public float getSuperLife() {
		return 400;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.9f, 1.1f);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.WALL_NUT;
	}

}
