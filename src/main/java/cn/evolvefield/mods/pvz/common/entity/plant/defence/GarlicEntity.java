package cn.evolvefield.mods.pvz.common.entity.plant.defence;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantDefenderEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.AlgorithmUtil;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

public class GarlicEntity extends PlantDefenderEntity {

	protected final AlgorithmUtil.EntitySorter sorter;
	private GarlicEntity garlic;

	public GarlicEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.sorter = new AlgorithmUtil.EntitySorter(this);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if(source instanceof PVZEntityDamageSource && ((PVZEntityDamageSource) source).isEatDamage() && source.getEntity() instanceof Mob) {
			this.updateGarlic();
			if(this.garlic != null) {
				EntityUtil.playSound(source.getEntity(), SoundRegister.YUCK.get());
				((Mob) source.getEntity()).setTarget(this.garlic);
			}
		}
		return super.hurt(source, amount);
	}

	private void updateGarlic() {
		if(! EntityUtil.isEntityValid(garlic) || ! this.getSensing().hasLineOfSight(garlic)) {
			this.garlic = null;
			final float range = this.getChangeRange();
			List<GarlicEntity> list = level.getEntitiesOfClass(GarlicEntity.class, EntityUtil.getEntityAABB(this, range, range), target -> {
				return ! target.is(this) && EntityUtil.isEntityValid(target) && this.getSensing().hasLineOfSight(target) && ! EntityUtil.canTargetEntity(this, target);
			});
			if(list.isEmpty()) {
				return ;
			}
			Collections.sort(list, this.sorter);
			this.garlic = list.get(0);
		}
	}

	public float getChangeRange() {
		return 10;
	}

	@Override
	public float getLife() {
		return this.getSkillValue(SkillTypes.MORE_GARLIC_LIFE);
	}

	@Override
	public float getSuperLife() {
		return 0;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.8F, 1.2F);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.GARLIC;
	}

}
