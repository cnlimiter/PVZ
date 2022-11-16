package cn.evolvefield.mods.pvz.common.entity.zombie.grass;

import cn.evolvefield.mods.pvz.common.impl.zombie.GrassZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class OldZombieEntity extends NewspaperZombieEntity{

	public OldZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public int getAngryLevel() {
		return 5;
	}

	@Override
	public float getEatDamage() {
		return ZombieUtil.LITTLE_LOW;
	}

	@Override
	public float getLife() {
		return 90;
	}

	@Override
	public float getOuterLife() {
		return 40;
	}

	@Override
	public ZombieType getZombieType() {
		return GrassZombies.OLD_ZOMBIE;
	}
}
