package cn.evolvefield.mods.pvz.common.entity.zombie.grass;

import cn.evolvefield.mods.pvz.api.enums.MetalTypes;
import cn.evolvefield.mods.pvz.common.impl.zombie.GrassZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.utils.EffectUtil;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.MathUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class GigaFootballZombieEntity extends FootballZombieEntity {

	protected boolean isRushing = false;
	private final int minRushCD = 200;
	private final int maxRushCD = 600;

	public GigaFootballZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.canCollideWithZombie = false;
	}

	@Override
	public void normalZombieTick() {
		super.normalZombieTick();
		if(! this.level.isClientSide) {
			if(!this.isRushing && this.getAttackTime() == 0) {
				this.updateRush(true);
			}
			this.setAttackTime(Math.max(0, this.getAttackTime() - 1));
		}
	}

	protected void updateRush(boolean is) {
		this.isRushing = is;
		if(! is) {
			this.setAttackTime(MathUtil.getRandomMinMax(getRandom(), minRushCD, maxRushCD));
		} else{
			this.addEffect(EffectUtil.effect(MobEffects.MOVEMENT_SPEED, 1000000, 1));
		}
	}

	@Override
	protected boolean shouldCollideWithEntity(LivingEntity target) {
		return EntityUtil.canTargetEntity(this, target);
	}

	@Override
	protected void doPush(Entity target) {
		if(this.isRushing() && target instanceof LivingEntity) {
			target.hurt(PVZEntityDamageSource.causeCrushDamage(this), EntityUtil.getMaxHealthDamage((LivingEntity) target));
			this.updateRush(false);
		}
	}

	@Override
	protected double getCollideWidthOffset() {
		return 0.4D;
	}

	@Override
	public float getInnerLife() {
		return 300;
	}

	@Override
	public float getEatDamage() {
		return ZombieUtil.NORMAL_DAMAGE;
	}

	@Override
	public float getWalkSpeed() {
		return ZombieUtil.WALK_FAST;
	}

	@Override
	public MetalTypes getMetalType() {
		return MetalTypes.GIGA_HELMET;
	}

	@Override
	public boolean canBeCold() {
		return ! this.isRushing();
	}

	@Override
	public boolean canBeButtered() {
		return ! this.isRushing();
	}

	public boolean isRushing() {
		return this.isRushing;
	}

	@Override
	public float getLife() {
		return 100;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("is_zombie_rushing")) {
			this.isRushing = compound.getBoolean("is_zombie_rushing");
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("is_zombie_rushing", this.isRushing);
	}

	@Override
	public ZombieType getZombieType() {
		return GrassZombies.GIGA_FOOTBALL_ZOMBIE;
	}

}
