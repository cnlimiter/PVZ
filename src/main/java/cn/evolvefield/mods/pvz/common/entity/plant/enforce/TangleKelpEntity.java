package cn.evolvefield.mods.pvz.common.entity.plant.enforce;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantCloserEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.PlantUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidType;

import java.util.Arrays;
import java.util.List;

public class TangleKelpEntity extends PlantCloserEntity {

	public TangleKelpEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void focusOnTarget(LivingEntity target) {
		super.focusOnTarget(target);
		if(target.getVehicle() == null) {
			EntityUtil.playSound(this, SoundRegister.DRAG.get());
			this.getTarget().startRiding(this, true);
		}
		this.setDeltaMovement(0, - 0.4F, 0);
	}

	@Override
	public void performAttack(LivingEntity target) {
		target.hurt(PVZEntityDamageSource.normal(this), this.getAttackDamage());
		this.remove(RemovalReason.KILLED);
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		if(!level.isClientSide) {
			int cnt = this.getSuperCount();
			for(LivingEntity target : EntityUtil.getTargetableLivings(this, EntityUtil.getEntityAABB(this, 25, 3))) {
				TangleKelpEntity entity = EntityRegister.TANGLE_KELP.get().create(level);
				entity.setPos(target.getX(), target.getY(), target.getZ());
				PlantUtil.copyPlantData(entity, this);
				level.addFreshEntity(entity);
				entity.setTarget(target);
				target.startRiding(entity, true);
				EntityUtil.playSound(entity, SoundRegister.DRAG.get());
				if(-- cnt <= 0) {
					break;
				}
			}
		}
	}

	@Override
	public boolean canPAZTarget(Entity entity) {
		return super.canPAZTarget(entity) && (entity.getVehicle() == null || entity.getVehicle().is(this));
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.addAll(Arrays.asList(
				Pair.of(PAZAlmanacs.ATTACK_DAMAGE, this.getAttackDamage())
		));
	}

	public float getAttackDamage(){
		return this.getSkillValue(SkillTypes.NORMAL_ENHANCE_STRENGTH);
	}

	public int getSuperCount(){
		return 3;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return new EntityDimensions(0.6f, 1f, false);
	}

	@Override
	public double getPassengersRidingOffset() {
		return 0;
	}

	@Override
	public boolean rideableUnderWater() {
		return true;
	}


	@Override
	public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
		return true;
	}

	@Override
	public boolean isNoGravity() {
		return this.isInWater();
	}

	@Override
	public int getSuperTimeLength() {
		return 20;
	}

	@Override
	protected boolean canBeImmuneToEnforce(Entity entity) {
		return true;
	}

	@Override
	public int getAttackCD() {
		return 20;
	}

	@Override
	public float getCloseHeight() {
		return 2;
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.TANGLE_KELP;
	}

}
