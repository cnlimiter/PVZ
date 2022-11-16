package cn.evolvefield.mods.pvz.common.entity.zombie.custom;

import cn.evolvefield.mods.pvz.common.entity.zombie.grass.AbstractTombStoneEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.CustomZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class GigaTombStoneEntity extends AbstractTombStoneEntity {

	public GigaTombStoneEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.canBeCharm = false;
	}

	@Override
	public ZombieType getZombieType() {
		return CustomZombies.GIGA_TOMBSTONE;
	}

}
