package cn.evolvefield.mods.pvz.common.entity.plant.ice;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.api.interfaces.util.IIceEffect;
import cn.evolvefield.mods.pvz.common.advancement.trigger.EntityEffectAmountTrigger;
import cn.evolvefield.mods.pvz.common.entity.misc.ElementBallEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantBomberEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EffectRegister;
import cn.evolvefield.mods.pvz.init.registry.ParticleRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
public class IceShroomEntity extends PlantBomberEntity implements IIceEffect {

	public IceShroomEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void startBomb(boolean server) {
		if(server) {
			//frozen enemies.
			final float len = this.getExplodeRange();
			final var aabb = EntityUtil.getEntityAABB(this, len, len);
			int cnt = 0;
			for(LivingEntity entity : EntityUtil.getTargetableLivings(this, aabb)) {
				 PVZEntityDamageSource source = PVZEntityDamageSource.causeIceDamage(this, this);
				 this.getColdEffect().ifPresent(e -> source.addEffect(e));
				 this.getFrozenEffect().ifPresent(e -> source.addEffect(e));
				 entity.hurt(source, this.getExplodeDamage());
				 if(EntityUtil.isEntityCold(entity)) {
					 ++ cnt;
				 }
			}
			EntityUtil.playSound(this, SoundRegister.FROZEN.get());
			//trigger advancement.
			final Player player = EntityUtil.getEntityOwner(level, this);
			if(player != null && player instanceof ServerPlayer) {
				EntityEffectAmountTrigger.INSTANCE.trigger((ServerPlayer) player, this, cnt);
			}
			//kill flame ball.
			ElementBallEntity.killElementBalls(this, 40, ElementBallEntity.ElementTypes.FLAME);
		} else {
			for(int i = 0;i < 3; ++ i) {
		        this.level.addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
	 	    }
		    for(int i = 0; i < 15; ++ i) {
			    this.level.addParticle(ParticleRegister.SNOW_FLOWER.get(), this.getX(), this.getY(), this.getZ(), (this.getRandom().nextFloat() - 0.5f) / 4, this.getRandom().nextFloat() / 5, (this.getRandom().nextFloat() - 0.5f) / 4);
		    }
		}
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.addAll(Arrays.asList(
				Pair.of(PAZAlmanacs.COLD_LEVEL, this.getColdLvl()),
				Pair.of(PAZAlmanacs.COLD_TIME, this.getColdTick()),
				Pair.of(PAZAlmanacs.FROZEN_LEVEL, this.getFrozenLvl()),
				Pair.of(PAZAlmanacs.FROZEN_TIME, this.getFrozenTick())
		));
	}

	@Override
	public int getReadyTime() {
		return 20;
	}

	@Override
	public float getExplodeRange(){
		return 20;
	}

	public float getExplodeDamage() {
		return 0.1F;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.85f, 1.35f);
	}

	public int getColdLvl() {
		return 1;
	}

	public int getColdTick() {
		return 0;
	}

	public int getFrozenLvl() {
		return 0;
	}

	public int getFrozenTick() {
		return 100;
	}

	@Override
	public Optional<MobEffectInstance> getColdEffect() {
		return Optional.ofNullable(new MobEffectInstance(EffectRegister.COLD_EFFECT.get(), this.getColdTick() + this.getFrozenTick(), this.getColdLvl(), false, false));
	}

	@Override
	public Optional<MobEffectInstance> getFrozenEffect() {
		return Optional.ofNullable(new MobEffectInstance(EffectRegister.FROZEN_EFFECT.get(), this.getFrozenTick(), this.getFrozenLvl(), false, false));
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.ICE_SHROOM;
	}

}
