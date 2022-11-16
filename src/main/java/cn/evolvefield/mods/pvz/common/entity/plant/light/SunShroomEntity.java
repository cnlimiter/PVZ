package cn.evolvefield.mods.pvz.common.entity.plant.light;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.misc.drop.SunEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantProducerEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;
public class SunShroomEntity extends PlantProducerEntity {

	public static final int GROW_CD = 24000;
	public static final int GROW_ANIM_CD = 10;

	public SunShroomEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.addAll(Arrays.asList(
				Pair.of(PAZAlmanacs.SMALL_GEN_SUN_AMOUNT, this.getSunAmountInStage(1)),
				Pair.of(PAZAlmanacs.GEN_SUN_AMOUNT, this.getSunAmountInStage(2))
		));
	}

	@Override
	public void genSomething() {
		this.genSun(this.getCurrentSunAmount(), 1);
	}

	@Override
	public void genSuper() {
		SunEntity.spawnSunsByAmount(level, blockPosition(), this.getSuperSunAmount(), 100, 3);
		EntityUtil.playSound(this, SoundEvents.EXPERIENCE_ORB_PICKUP);
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		if(! this.isInGrowStage(3)) {
			this.growUpTo(3);
		}
	}

	/**
	 * sun shroom grow up progress
	 */
	protected void growUpTo(int stage) {
		this.setExistTick(GROW_CD * (stage - 1) - GROW_ANIM_CD - 2);
		EntityUtil.playSound(this, SoundRegister.PLANT_GROW.get());
	}

	/**
	 * {@link #getCurrentSunAmount()}
	 */
	public boolean isInGrowStage(int stage) {
		return this.getExistTick() > GROW_CD * (stage - 1);
	}

	/**
	 * get current sun gen num;
	 */
	protected int getCurrentSunAmount() {
		return this.isInGrowStage(3) ? this.getSunAmountInStage(3):
				this.isInGrowStage(2) ? this.getSunAmountInStage(2) :
						this.getSunAmountInStage(1);
	}

	/**
	 * get sun amount when grow up.
	 */
	public int getSunAmountInStage(int stage){
		return stage == 3 ? 35 : stage == 2 ? 25 : 15;
	}

	/**
	 * get normal gen sun amount by maxLevel.
	 */
	public int getSuperSunAmount(){
		return 500;
	}

	@Override
	public int getGenCD() {
		return 600;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return this.isInGrowStage(3) ? EntityDimensions.scalable(0.8f, 1.2f):
			   this.isInGrowStage(2) ? EntityDimensions.scalable(0.6f, 1f) :
			   EntityDimensions.scalable(0.4f, 0.4f);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.SUN_SHROOM;
	}

}
