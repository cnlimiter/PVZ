package cn.evolvefield.mods.pvz.common.entity.zombie.grass;

import cn.evolvefield.mods.pvz.common.impl.zombie.GrassZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class SundayEditionZombieEntity extends NewspaperZombieEntity{

	public SundayEditionZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.canBeCold = false;
	}

	@Override
	public int getAngryLevel() {
		return 10;
	}

	@Override
	public float getEatDamage() {
		return ZombieUtil.LITTLE_HIGH;
	}

	@Override
	public float getWalkSpeed() {
		return ZombieUtil.WALK_LITTLE_FAST;
	}

	@Override
	public float getLife() {
		return 350;
	}

	@Override
	public float getOuterLife() {
		return 100;
	}

	@Override
	public ZombieType getZombieType() {
		return GrassZombies.SUNDAY_EDITION_ZOMBIE;
	}

}
