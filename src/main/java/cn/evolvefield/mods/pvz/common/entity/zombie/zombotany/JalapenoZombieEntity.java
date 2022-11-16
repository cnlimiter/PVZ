package cn.evolvefield.mods.pvz.common.entity.zombie.zombotany;

import cn.evolvefield.mods.pvz.common.entity.plant.flame.JalapenoEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.common.impl.zombie.Zombotanies;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class JalapenoZombieEntity extends AbstractZombotanyEntity {

	public JalapenoZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ZombieUtil.WALK_FAST);
	}

	@Override
	public void die(DamageSource source) {
		if(! EntityUtil.isEntityCold(this) && ! EntityUtil.isEntityFrozen(this)) {
			this.startBomb();
		}
		super.die(source);
	}

	public void startBomb() {
		if(! level.isClientSide) {
			JalapenoEntity jalapeno = EntityRegister.JALAPENO.get().create(level);
			jalapeno.setImmuneToWeak(true);
			jalapeno.setCharmed(! this.isCharmed());
			jalapeno.setSkills(this.getSkills());
			EntityUtil.onEntitySpawn(level, jalapeno, this.blockPosition().above());
		}
	}

	public int getFireRange() {
		return 20;
	}

	@Override
	public float getLife() {
		return 44;
	}

	@Override
	public ZombieType getZombieType() {
		return Zombotanies.JALAPENO_ZOMBIE;
	}

}
