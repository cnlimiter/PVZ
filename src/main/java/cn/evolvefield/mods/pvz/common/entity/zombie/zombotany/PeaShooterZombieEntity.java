package cn.evolvefield.mods.pvz.common.entity.zombie.zombotany;

import cn.evolvefield.mods.pvz.common.entity.bullet.itembullet.PeaEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.common.impl.zombie.Zombotanies;
import cn.evolvefield.mods.pvz.init.registry.EffectRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class PeaShooterZombieEntity extends AbstractZombotanyEntity {

	private int shootTick = 0;

	public PeaShooterZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void normalZombieTick() {
		super.normalZombieTick();
		if(! level.isClientSide) {
			++ shootTick;
			if(this.shootTick >= this.getFixedShootCD()) {
				this.setAttackTime(this.getShootNum());
				this.shootTick = 0;
			}
			if(this.getAttackTime() > 0) {
				this.setAttackTime(this.getAttackTime() - 1);
				this.shootPea();
			}
		}
	}

	private void shootPea() {
		LivingEntity target = this.getTarget();
		if(target == null) {
			return ;
		}
		PeaEntity pea = new PeaEntity(level, this, PeaEntity.Type.NORMAL, PeaEntity.State.NORMAL);
		pea.setPos(getX(), getY() + this.getEyeHeight(), getZ());
		pea.shootToTarget(target, 1.5F);
		pea.summonByOwner(this);
		pea.setAttackDamage(this.getAttackDamage());
		level.addFreshEntity(pea);
		EntityUtil.playSound(this, SoundEvents.SNOW_GOLEM_SHOOT);
	}

	public float getAttackDamage() {
		return 2;
	}

	@Override
	public float getLife() {
		return 20;
	}

	protected int getFixedShootCD() {
		int now = this.getShootCD();
		if (this.hasEffect(EffectRegister.COLD_EFFECT.get())) {
			int lvl = this.getEffect(EffectRegister.COLD_EFFECT.get()).getAmplifier();
			now += 3 * lvl;
		}
		return now;
	}

	protected int getShootCD() {
		return 30;
	}

	protected int getShootNum() {
		return 1;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("shoot_tick")) {
			this.shootTick = compound.getInt("shoot_tick");
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("shoot_tick", this.shootTick);
	}

	@Override
	public ZombieType getZombieType() {
		return Zombotanies.PEASHOOTER_ZOMBIE;
	}

}
