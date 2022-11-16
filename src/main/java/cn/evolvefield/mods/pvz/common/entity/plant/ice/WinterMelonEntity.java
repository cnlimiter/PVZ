package cn.evolvefield.mods.pvz.common.entity.plant.ice;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.api.interfaces.util.IIceEffect;
import cn.evolvefield.mods.pvz.common.entity.bullet.MelonEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.arma.MelonPultEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.init.registry.EffectRegister;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class WinterMelonEntity extends MelonPultEntity implements IIceEffect {

	public WinterMelonEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	public int getColdLvl() {
		return 7;
	}

	public int getColdTick() {
		return 80;
	}

	@Override
	public Optional<MobEffectInstance> getColdEffect() {
		return Optional.ofNullable(new MobEffectInstance(EffectRegister.COLD_EFFECT.get(), this.getColdTick(), this.getColdLvl(), false, false));
	}

	@Override
	public Optional<MobEffectInstance> getFrozenEffect() {
		return Optional.empty();
	}

	@Override
	protected MelonEntity.MelonStates getThrowMelonState() {
		return MelonEntity.MelonStates.ICE;
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.WINTER_MELON;
	}

}
