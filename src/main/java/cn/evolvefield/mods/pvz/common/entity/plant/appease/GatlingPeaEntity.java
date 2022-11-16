package cn.evolvefield.mods.pvz.common.entity.plant.appease;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class GatlingPeaEntity extends RepeaterEntity{

	public int animTime = 0;

	public GatlingPeaEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void tick() {
		super.tick();
		if(level.isClientSide) {
			if(this.getAttackTime() > 0) {
				this.animTime = 15;
			} else {
				if(this.getAttackTime() == 0 && this.animTime > 0) {
					-- this.animTime;
				}
			}
		}
	}

	@Override
	protected int getBigPeaNum() {
		return 1;
//		return this.getThreeStage(2, 4, 6);
	}

	@Override
	public int getSuperTimeLength() {
		return 200;
	}

	@Override
	public void startShootAttack() {
		this.setAttackTime(4);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.GATLING_PEA;
	}

}
