package cn.evolvefield.mods.pvz.common.entity.zombie.part;

import cn.evolvefield.mods.pvz.common.entity.zombie.base.DefenceZombieEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class PVZHealthPartEntity extends PVZZombiePartEntity{

	protected DefenceZombieEntity zombie;

	public PVZHealthPartEntity(EntityType<?> entityTypeIn, Level worldIn) {
		super(entityTypeIn, worldIn);
	}

	public PVZHealthPartEntity(DefenceZombieEntity owner, float sizeX, float sizeY) {
		super(owner, sizeX, sizeY);
		this.zombie = owner;
	}

	@Override
	public boolean hurt(DamageSource source, float damage) {
		//the source can pass through defence and deal damage to owner directly.
		if(source instanceof PVZEntityDamageSource) {
			if(((PVZEntityDamageSource) source).isThroughDamage()) {
				return super.hurt(source, damage);
			}
			((PVZEntityDamageSource) source).setDefended(true);
		}
		this.zombie.hitDefence = true;
		return super.hurt(source, damage);
	}

	@Override
	public boolean canExist() {
		return super.canExist() && this.zombie.canPartsExist();
	}

}
