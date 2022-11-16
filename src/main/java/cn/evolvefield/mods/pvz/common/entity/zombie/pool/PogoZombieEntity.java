package cn.evolvefield.mods.pvz.common.entity.zombie.pool;

import cn.evolvefield.mods.pvz.api.enums.MetalTypes;
import cn.evolvefield.mods.pvz.api.interfaces.attract.ICanAttract;
import cn.evolvefield.mods.pvz.api.interfaces.base.IHasMetal;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.impl.zombie.PoolZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.MathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class PogoZombieEntity extends PVZZombieEntity implements IHasMetal {

	private static final EntityDataAccessor<Boolean> HAS_POGO = SynchedEntityData.defineId(PogoZombieEntity.class, EntityDataSerializers.BOOLEAN);
	private static final int JUMP_CD = 10;

	public PogoZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(HAS_POGO, true);
	}

	@Override
	public void normalZombieTick() {
		super.normalZombieTick();
		if(! level.isClientSide) {
			this.setAttackTime((this.getAttackTime() + 1) % JUMP_CD);
			if(this.hasPogo() && (this.isOnGround() || this.isInWaterOrBubble()) && this.getAttackTime() % JUMP_CD == 0) {
				double motionY = 0.85D + MathUtil.getRandomFloat(getRandom()) * 0.1D;
				this.setDeltaMovement(this.getDeltaMovement().x(), motionY, this.getDeltaMovement().z());
				EntityUtil.playSound(this, SoundRegister.POGO.get());
			}
		}
	}

	@Override
	public boolean canBreakPlantBlock() {
		return super.canBreakPlantBlock() && ! this.hasMetal();
	}

	@Override
	public boolean canBeAttractedBy(ICanAttract defender) {
		if(defender instanceof PVZPlantEntity) {
			final IPlantType plant = ((PVZPlantEntity) defender).getPlantType();
			return plant == PVZPlants.TALL_NUT;
		}
		return true;
	}

	@Override
	public void attractBy(ICanAttract defender) {
		super.attractBy(defender);
		if(this.hasPogo()) {
		    this.setPogo(false);
		    EntityUtil.playSound(this, SoundRegister.HAMMER_BONK.get());
		}
	}

	@Override
	public boolean hasMetal() {
		return this.hasPogo();
	}

	@Override
	public void decreaseMetal() {
		this.setPogo(false);
	}

	@Override
	public void increaseMetal() {
		this.setPogo(true);
	}

	@Override
	public MetalTypes getMetalType() {
		return MetalTypes.POGO;
	}

	@Override
	public boolean canBeFrozen() {
		return ! this.hasPogo();
	}

	@Override
	public boolean canBeButtered() {
		return ! this.hasPogo();
	}

	@Override
	public float getLife() {
		return 48;
	}

	public boolean hasPogo() {
		return this.entityData.get(HAS_POGO);
	}

	public void setPogo(boolean has) {
		this.entityData.set(HAS_POGO, has);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("has_pogo")) {
			this.setPogo(compound.getBoolean("has_pogo"));
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("has_pogo", this.hasPogo());
	}

	@Override
    public ZombieType getZombieType() {
	    return PoolZombies.POGO_ZOMBIE;
    }

}
