package cn.evolvefield.mods.pvz.common.entity.plant.arma;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class ButterPultEntity extends KernelPultEntity {

	public ButterPultEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.setCurrentBullet(CornTypes.BUTTER);
	}

	@Override
	protected PultBulletEntity createBullet() {
		return new ButterEntity(level, this);
	}

	@Override
	protected void changeBullet() {
		this.setCurrentBullet(CornTypes.BUTTER);
	}

	@Override
	public float getAttackDamage() {
		return 0.1F;
	}

	@Override
	public int getButterDuration() {
		return 90;
	}

	@Override
	public IPlantType getPlantType() {
		return CustomPlants.BUTTER_PULT;
	}

}
