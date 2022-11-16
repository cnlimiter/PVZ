package cn.evolvefield.mods.pvz.common.entity.plant.arma;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.bullet.MelonEntity;
import cn.evolvefield.mods.pvz.common.entity.bullet.PultBulletEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantPultEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;

public class MelonPultEntity extends PlantPultEntity {

	public MelonPultEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected PultBulletEntity createBullet() {
		final MelonEntity melon = new MelonEntity(level, this);
		melon.setMelonState(this.getThrowMelonState());
		return melon;
	}

	protected MelonEntity.MelonStates getThrowMelonState() {
		return MelonEntity.MelonStates.NORMAL;
	}

	public float getAttackDamage() {
		return this.getSkillValue(SkillTypes.MORE_MELON_DAMAGE);
	}

	@Override
	public float getSuperDamage() {
		return this.getAttackDamage() + 15;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.9F, 1F);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.MELON_PULT;
	}

}
