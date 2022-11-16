package cn.evolvefield.mods.pvz.common.entity.zombie.base;

import cn.evolvefield.mods.pvz.api.enums.BodyType;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.body.ZombieDropBodyEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public abstract class CarZombieEntity extends PVZZombieEntity {

	public CarZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.setIsWholeBody();
		this.canBeFrozen = false;
		this.canBeMini = false;
	}

	@Override
	public void zombieTick() {
		super.zombieTick();
		if(level.isClientSide && this.isCarShaking()) {
			for(int i = 1; i <= 3; i ++) {
			    this.level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), (this.getRandom().nextFloat() - 0.5) / 10, 0.05, (this.getRandom().nextFloat() - 0.5) / 10);
			}
		}
	}

	@Override
	protected void onRemoveWhenDeath() {
		if(! level.isClientSide) {
			EntityUtil.playSound(this, SoundRegister.CAR_EXPLOSION.get());
		}
		else {
			for(int i = 0; i < 4; ++ i) {
			    this.level.addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
			}
		}
		super.onRemoveWhenDeath();
	}


	@Override
	protected void onFallBody(DamageSource source) {
		ZombieDropBodyEntity body = EntityRegister.ZOMBIE_DROP_BODY.get().create(level);
		body.specialDropBody(this, source, BodyType.HEAD);
		this.setBodyStates(body);
		level.addFreshEntity(body);
	}

	@Override
	public float getKBValue() {
		return 1;
	}

	public boolean isCarShaking() {
		return this.getHealth() <= this.getMaxHealth() / 4;
	}

	@Override
	public boolean canBreakPlantBlock() {
		return false;
	}

	@Override
	protected PVZEntityDamageSource getZombieAttackDamageSource() {
		return PVZEntityDamageSource.causeCrushDamage(this);
	}

	@Override
	protected float getModifyAttackDamage(Entity entity, float f) {
		if(entity instanceof LivingEntity) {
			return EntityUtil.getMaxHealthDamage(((LivingEntity) entity));
		}
		return f;
	}

	@Override
	protected int getDeathTime() {
		return 2;
	}
}
