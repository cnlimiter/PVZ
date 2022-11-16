package cn.evolvefield.mods.pvz.common.entity.zombie.roof;

import cn.evolvefield.mods.pvz.api.enums.MetalTypes;
import cn.evolvefield.mods.pvz.api.interfaces.base.IHasMetal;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantDefenderEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.base.DefenceZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.body.ZombieDropBodyEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.part.PVZHealthPartEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.RoofZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class LadderZombieEntity extends DefenceZombieEntity implements IHasMetal {

	public LadderZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void resetParts() {
		removeParts();
		this.part = new PVZHealthPartEntity(this, 1f, 1.7f);
		this.part.setOwner(this);
	}

	@Override
	public boolean doHurtTarget(Entity entityIn) {
		if(this.hasMetal() && canTargetPutLadder(entityIn)) {
			this.putLadderOn(entityIn);
		}
		return super.doHurtTarget(entityIn);
	}

	/**
	 * {@link #doHurtTarget(Entity)}
	 */
	public void putLadderOn(Entity entity) {
		if(entity instanceof PVZPlantEntity) {
			((PVZPlantEntity) entity).increaseMetal();
		}
		this.decreaseMetal();
	}

	public static boolean canTargetPutLadder(Entity target) {
		//can not put ladder or already has ladder on.
		if(! (target instanceof PVZPlantEntity) || hasLadderOnEntity(target)) {
			return false;
		}
		if(target instanceof PlantDefenderEntity) {
			return true;
		}
		PVZPlantEntity plant = (PVZPlantEntity) target;
		return plant.getOuterPlantInfo().isPresent();
	}

	/**
	 * {@link #canTargetPutLadder(Entity)}
	 */
	private static boolean hasLadderOnEntity(Entity target) {
		if(! (target instanceof PVZPlantEntity)) {
			return false;
		}
		return ((PVZPlantEntity) target).hasMetal();
	}

	@Override
	public boolean hasMetal() {
		return this.getOuterDefenceLife() > 0;
	}

	@Override
	public boolean canLostHand() {
		return super.canLostHand() && ! this.hasMetal();
	}

	@Override
	protected void setBodyStates(ZombieDropBodyEntity body) {
		super.setBodyStates(body);
		body.setHandDefence(this.hasMetal());
	}

	@Override
	public void decreaseMetal() {
		this.setOuterDefenceLife(0);
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ZombieUtil.WALK_NORMAL);
	}

	@Override
	public void increaseMetal() {
		this.setOuterDefenceLife(this.getOuterLife());
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ZombieUtil.WALK_FAST);
		this.resetParts();
	}

	@Override
	public MetalTypes getMetalType() {
		return MetalTypes.LADDER;
	}

	@Override
	public SoundEvent getPartHurtSound() {
		return SoundRegister.METAL_HIT.get();
	}

	@Override
	public float getLife() {
		return 65;
	}

	@Override
	public float getOuterLife() {
		return 250;
	}

	@Override
	public float getWalkSpeed() {
		return ZombieUtil.WALK_FAST;
	}

	@Override
    public ZombieType getZombieType() {
	    return RoofZombies.LADDER_ZOMBIE;
    }

}
