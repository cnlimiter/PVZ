package cn.evolvefield.mods.pvz.common.entity.plant.ice;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.api.interfaces.util.IIceEffect;
import cn.evolvefield.mods.pvz.common.entity.plant.appease.PeaShooterEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.init.registry.EffectRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import com.hungteen.pvz.common.entity.bullet.itembullet.PeaEntity.State;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
public class SnowPeaEntity extends PeaShooterEntity implements IIceEffect {

	public SnowPeaEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
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
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.addAll(Arrays.asList(
				Pair.of(PAZAlmanacs.COLD_LEVEL, this.getColdLvl()),
				Pair.of(PAZAlmanacs.COLD_TIME, this.getColdTick())
		));
	}

	/**
	 * cold effect maxLevel.
	 */
	public int getColdLvl() {
		return 5;
	}

	/**
	 * cold effect duration.
	 */
	public int getColdTick() {
		return 80;
	}

	@Override
	protected SoundEvent getShootSound() {
		return SoundRegister.SNOW_SHOOT.get();
	}

	@Override
	protected State getShootState() {
		return State.ICE;
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.SNOW_PEA;
	}

}
