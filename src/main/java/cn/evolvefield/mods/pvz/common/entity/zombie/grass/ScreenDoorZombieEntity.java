package cn.evolvefield.mods.pvz.common.entity.zombie.grass;

import cn.evolvefield.mods.pvz.api.enums.MetalTypes;
import cn.evolvefield.mods.pvz.api.interfaces.base.IHasMetal;
import cn.evolvefield.mods.pvz.common.entity.zombie.base.DefenceZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.body.ZombieDropBodyEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.part.PVZHealthPartEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.GrassZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class ScreenDoorZombieEntity extends DefenceZombieEntity implements IHasMetal {

	public ScreenDoorZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void resetParts() {
		removeParts();
		this.part = new PVZHealthPartEntity(this, 1f, 1.7f);
		this.part.setOwner(this);
	}

	@Override
	public boolean hasMetal() {
		return this.getOuterDefenceLife() > 0;
	}

	@Override
	public void decreaseMetal() {
		this.setOuterDefenceLife(0);
	}

	@Override
	public void increaseMetal() {
		this.setOuterDefenceLife(this.getOuterLife());
		this.resetParts();
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
	public MetalTypes getMetalType() {
		return MetalTypes.SCREEN_DOOR;
	}

	@Override
	public SoundEvent getPartHurtSound() {
		return SoundRegister.METAL_HIT.get();
	}

	@Override
	public float getLife() {
		return 24;
	}

	@Override
	public float getOuterLife() {
		return 200;
	}

	@Override
	public ZombieType getZombieType() {
		return GrassZombies.SCREENDOOR_ZOMBIE;
	}

}
