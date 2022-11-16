package cn.evolvefield.mods.pvz.common.entity.zombie.zombotany;

import cn.evolvefield.mods.pvz.common.entity.plant.enforce.SquashEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.common.impl.zombie.Zombotanies;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class SquashZombieEntity extends AbstractZombotanyEntity {

	public SquashZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.canLostHead = false;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ZombieUtil.WALK_VERY_FAST);
	}

	@Override
	public void normalZombieTick() {
		super.normalZombieTick();
		if(! level.isClientSide) {
			LivingEntity target = this.getTarget();
			if(target != null && this.distanceToSqr(target) <= 10) {
				SquashEntity squash = EntityRegister.SQUASH.get().create(level);
				squash.setCharmed(! this.isCharmed());
				squash.setTarget(target);
				EntityUtil.onEntitySpawn(level, squash, blockPosition().above(2));
				this.remove(RemovalReason.KILLED);
			}
		}
	}

	@Override
	public float getLife() {
		return 25;
	}

	@Override
	public ZombieType getZombieType() {
		return Zombotanies.SQUASH_ZOMBIE;
	}

}
