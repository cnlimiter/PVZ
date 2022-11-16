package cn.evolvefield.mods.pvz.common.entity.misc;

import cn.evolvefield.mods.pvz.common.entity.effect.OriginEffectEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class DoomFixerEntity extends OriginEffectEntity {

	public DoomFixerEntity(EntityType<?> entityTypeIn, Level worldIn) {
		super(entityTypeIn, worldIn);
		this.maxEffectTick = 40;
	}

}
