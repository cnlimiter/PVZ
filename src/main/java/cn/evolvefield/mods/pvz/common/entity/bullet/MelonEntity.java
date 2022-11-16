package cn.evolvefield.mods.pvz.common.entity.bullet;

import cn.evolvefield.mods.pvz.common.entity.plant.ice.WinterMelonEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class MelonEntity extends PultBulletEntity {

	private static final DataParameter<Integer> MELON_STATE = EntityDataManager.defineId(MelonEntity.class, DataSerializers.INT);
	private Entity attackEntity = null;

	public MelonEntity(EntityType<?> type, Level worldIn) {
		super(type, worldIn);
	}

	public MelonEntity(Level worldIn, LivingEntity shooter) {
		super(EntityRegister.MELON.get(), worldIn, shooter);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(MELON_STATE, MelonStates.NORMAL.ordinal());
	}

	@Override
	protected void dealDamage(Entity target) {
		if(this.getMelonState() == MelonStates.ICE) {
			PVZEntityDamageSource source = PVZEntityDamageSource.winterMelon(this, this.getThrower());
			this.getColdEffect().ifPresent(e -> source.addEffect(e));
			target.hurt(source, this.getAttackDamage());
		} else{
			target.hurt(PVZEntityDamageSource.melon(this, this.getThrower()), this.getAttackDamage());
		}
		this.attackEntity = target;
		this.dealSplashDamage();
	}

	@Override
	protected void onHitBlock() {
		this.dealSplashDamage();
	}

	/**
	 * {@link #onHitBlock()}
	 * {@link #dealDamage(Entity)}
	 */
	public void dealSplashDamage() {
		final float range = 3F;
		EntityUtil.getTargetableEntities(this.getOwnerOrSelf(), EntityUtil.getEntityAABB(this, range, range)).forEach(entity -> {
			if(! entity.is(attackEntity) && this.shouldHit(entity)) {
				if(this.getMelonState() == MelonStates.ICE) {
					PVZEntityDamageSource source = PVZEntityDamageSource.winterMelon(this, this.getThrower());
					this.getColdEffect().ifPresent(e -> source.addEffect(e));
					entity.hurt(source, this.getAttackDamage() / 2);
				} else {
					PVZEntityDamageSource source = PVZEntityDamageSource.melon(this, this.getThrower());
				    entity.hurt(source, this.getAttackDamage() / 2);
				}
			}
		});
		for(int i = 0; i < 10; ++ i) {
			EntityUtil.spawnParticle(this, (this.getMelonState() == MelonStates.ICE ? 2 : 1));
		}
		EntityUtil.playSound(this, SoundRegister.MELON_HIT.get());
	}

	protected Optional<MobEffectInstance> getColdEffect(){
		if(this.getThrower() instanceof WinterMelonEntity) {
			return ((WinterMelonEntity) this.getThrower()).getColdEffect();
		}
		return Optional.empty();
	}

	@Override
	public EntitySize getDimensions(Pose poseIn) {
		return EntitySize.scalable(0.6F, 0.6F);
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("melon_state")) {
			this.setMelonState(MelonStates.values()[compound.getInt("melon_state")]);
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("melon_state", this.getMelonState().ordinal());
	}

	public void setMelonState(MelonStates type) {
		this.entityData.set(MELON_STATE, type.ordinal());
	}

	public MelonStates getMelonState() {
		return MelonStates.values()[this.entityData.get(MELON_STATE)];
	}

	public static enum MelonStates {
		NORMAL,
		ICE,
	}

}
