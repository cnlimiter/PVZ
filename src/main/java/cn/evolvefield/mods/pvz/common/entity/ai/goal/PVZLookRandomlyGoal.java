package cn.evolvefield.mods.pvz.common.entity.ai.goal;

import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

public class PVZLookRandomlyGoal extends RandomLookAroundGoal {

	private Mob plant;

	public PVZLookRandomlyGoal(Mob entitylivingIn) {
		super(entitylivingIn);
		this.plant = entitylivingIn;
	}

	@Override
	public boolean canUse() {
		return this.canExecute() && super.canUse();
	}

	private boolean canExecute() {
		if(this.plant instanceof PVZPlantEntity && !((PVZPlantEntity) this.plant).canNormalUpdate()) {
			return false;
		}
		if(this.plant instanceof PVZZombieEntity && !((PVZZombieEntity) this.plant).canNormalUpdate()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canContinueToUse() {
		return this.canExecute() && super.canContinueToUse();
	}
}
