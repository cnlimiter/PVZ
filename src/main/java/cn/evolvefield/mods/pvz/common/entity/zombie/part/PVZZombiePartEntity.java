package cn.evolvefield.mods.pvz.common.entity.zombie.part;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.common.entity.PVZMultiPartEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class PVZZombiePartEntity extends PVZMultiPartEntity {

	public PVZZombiePartEntity(EntityType<?> entityTypeIn, Level worldIn) {
		super(entityTypeIn, worldIn);
	}

	public PVZZombiePartEntity(PVZZombieEntity owner, float sizeX, float sizeY) {
		super(EntityRegister.ZOMBIE_PART.get(), owner, sizeX, sizeY);
	}

	/**
	 * when owner is mini zombie, then the part need shrink size.
	 */
	public void onOwnerBeMini(PVZZombieEntity zombie) {
		float scale = zombie.isMiniZombie() ? 0.4F : 1F;
		this.setPartWidth(this.MaxWidth * scale);
	    this.setPartHeight(this.MaxHeight * scale);
	}

	@Override
	public boolean hurt(DamageSource source, float damage) {
		if(EntityUtil.isEntityValid(getOwner())) {
			return getOwner().hurt(source, damage);
		}
		return false;
	}

	public Optional<PVZZombieEntity> getZombie() {
		LivingEntity owner = this.getOwner();
		if(! (owner instanceof PVZZombieEntity)) {
			Static.LOGGER.warn("Wrong Owner Entity for Zombie's Part !");
			return null;
		}
		return Optional.ofNullable((PVZZombieEntity) owner);
	}

}
