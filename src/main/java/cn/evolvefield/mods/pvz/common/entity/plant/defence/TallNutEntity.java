package cn.evolvefield.mods.pvz.common.entity.plant.defence;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
public class TallNutEntity extends WallNutEntity{

	public TallNutEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public float getSuperLife() {
		return 800;
	}

	@Override
	public int getArmor() {
		return 15;
	}

	@Override
	public int getArmorToughness() {
		return 10;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return new EntityDimensions(0.9f, 1.9f, false);
	}

	@Override
	public float getAttractRange() {
		return 3.5F;
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.TALL_NUT;
	}

}
