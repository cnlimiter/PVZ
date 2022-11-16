package cn.evolvefield.mods.pvz.common.entity.zombie.zombotany;

import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.common.impl.zombie.Zombotanies;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class GatlingPeaZombieEntity extends PeaShooterZombieEntity {

	public GatlingPeaZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected int getShootNum() {
		return 4;
	}

	@Override
	public float getLife() {
		return 30;
	}

	@Override
	public ZombieType getZombieType() {
		return Zombotanies.GATLINGPEA_ZOMBIE;
	}

}
