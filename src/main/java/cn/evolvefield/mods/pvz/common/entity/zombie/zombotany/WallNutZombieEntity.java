package cn.evolvefield.mods.pvz.common.entity.zombie.zombotany;

import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.common.impl.zombie.Zombotanies;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class WallNutZombieEntity extends AbstractZombotanyEntity {

	public WallNutZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public float getLife() {
		return 160;
	}

	@Override
	public ZombieType getZombieType() {
		return Zombotanies.WALLNUT_ZOMBIE;
	}

}
