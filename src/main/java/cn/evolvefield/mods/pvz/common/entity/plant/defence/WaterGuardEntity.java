package cn.evolvefield.mods.pvz.common.entity.plant.defence;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantDefenderEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.CustomPlants;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
public class WaterGuardEntity extends PlantDefenderEntity{

	public WaterGuardEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(2, new FloatGoal(this));
	}

	@Override
	public float getLife() {
		return 200;
	}

	@Override
	public float getSuperLife() {
		return 300;
	}

	@Override
	public int getSuperTimeLength() {
		return 20;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return new EntityDimensions(0.8f, 0.8f, false);
	}

	@Override
	public IPlantType getPlantType() {
		return CustomPlants.WATER_GUARD;
	}

}
