package cn.evolvefield.mods.pvz.common.entity.plant.ice;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.api.interfaces.util.IIceEffect;
import cn.evolvefield.mods.pvz.common.advancement.trigger.EntityEffectAmountTrigger;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantCloserEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.OtherPlants;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EffectRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;
public class IcebergLettuceEntity extends PlantCloserEntity implements IIceEffect {

	private static final int FROZEN_TICK = 200;

	public IcebergLettuceEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void performAttack(LivingEntity target) {
		for(int i = 0; i < 2; ++ i) {
			EntityUtil.spawnParticle(this, 5);
		}
		EntityUtil.playSound(this, SoundRegister.FROZEN.get());
		this.dealDamageTo(target);
		this.remove(Entity.RemovalReason.KILLED);
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		final float range = this.getSuperRange();
		for(int i = 0; i < 2; ++ i) {
			EntityUtil.spawnParticle(this, 5);
		}
		EntityUtil.playSound(this, SoundRegister.FROZEN.get());
		int cnt = 0;
		for(Entity target : EntityUtil.getTargetableEntities(this, EntityUtil.getEntityAABB(this, range, range))) {
			this.dealDamageTo(target);
			if(target instanceof LivingEntity && EntityUtil.isEntityCold((LivingEntity) target)) {
				++ cnt;
			}
		};
		Player player = EntityUtil.getEntityOwner(level, this);
		if(player != null && player instanceof ServerPlayer) {
			EntityEffectAmountTrigger.INSTANCE.trigger((ServerPlayer) player, this, cnt);
		}
	}

	private void dealDamageTo(Entity target) {
		PVZEntityDamageSource source = PVZEntityDamageSource.causeIceDamage(this, this);
		this.getColdEffect().ifPresent(e -> source.addEffect(e));
		this.getFrozenEffect().ifPresent(e -> source.addEffect(e));
		target.hurt(source, 0.001F);
	}

    @Override
	public Optional<MobEffectInstance> getColdEffect() {
		return Optional.ofNullable(new MobEffectInstance(EffectRegister.COLD_EFFECT.get(), FROZEN_TICK + this.getColdDuration(), this.getColdLevel(), false, false));
	}

    @Override
	public Optional<MobEffectInstance> getFrozenEffect() {
    	return Optional.ofNullable(new MobEffectInstance(EffectRegister.FROZEN_EFFECT.get(), FROZEN_TICK, 1, false, false));
	}

    public int getColdLevel() {
		return 1;
    }

    public int getColdDuration() {
		return 100;
    }

    public float getSuperRange() {
		return 20;
    }

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.fixed(0.6F, 0.6F);
	}

	@Override
	public IPlantType getPlantType() {
		return OtherPlants.ICEBERG_LETTUCE;
	}

}
