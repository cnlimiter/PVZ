package cn.evolvefield.mods.pvz.common.entity.zombie.zombotany;

import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public abstract class AbstractZombotanyEntity extends PVZZombieEntity {

	public AbstractZombotanyEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected float getWaterSlowDown() {
		return 0.91f;
	}

}
