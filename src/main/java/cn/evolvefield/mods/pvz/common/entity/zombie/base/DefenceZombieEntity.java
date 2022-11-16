package cn.evolvefield.mods.pvz.common.entity.zombie.base;

import cn.evolvefield.mods.pvz.api.interfaces.util.IMultiPartZombie;
import cn.evolvefield.mods.pvz.common.entity.PVZMultiPartEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.part.PVZHealthPartEntity;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

/**
 * use outer defence life as extra life.
 */
public abstract class DefenceZombieEntity extends PVZZombieEntity implements IMultiPartZombie {

	protected PVZHealthPartEntity part;
	public boolean hitDefence = false;

	public DefenceZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		resetParts();
	}

	@Override
	public void tick() {
		super.tick();
		if(! this.level.isClientSide) {
			updateParts();
		}
	}

	@Override
	public void removeParts() {
		if(EntityUtil.isEntityValid(this.part)) {
			this.part.remove(RemovalReason.KILLED);
			this.part = null;
		}
	}

	@Override
	public void updateParts() {
		if(this.canPartsExist()) {
			if(! EntityUtil.isEntityValid(part)) {
				this.resetParts();
			}
			if(! this.part.isAddedToWorld()) {
				this.level.addFreshEntity(this.part);
			}
			float j = 2 * 3.14159f * this.getYRot() / 360;
			float dis = this.getPartWidthOffset();
			var pos = this.position();
			this.part.yRotO = this.getYRot();
			this.part.xRotO = this.getXRot();
			this.part.moveTo(pos.x() - Math.sin(j) * dis, pos.y() + this.getPartHeightOffset(), pos.z() + Math.cos(j) * dis, this.getYRot(), this.getXRot());
			this.part.setOwner(this);
		} else {
			this.removeParts();
		}
	}

	@Override
	public void onZombieBeMini() {
		super.onZombieBeMini();
		if(EntityUtil.isEntityValid(this.part)) {
			this.part.onOwnerBeMini(this);
		}
		this.setOuterDefenceLife(this.getOuterLife() * 0.6F);
	}

	@Override
	public PVZMultiPartEntity[] getMultiParts() {
		return new PVZMultiPartEntity[] {this.part};
	}

	@Override
	public boolean canPartsExist() {
		return this.getOuterDefenceLife() > 0;
	}

	@Override
	public void onOuterDefenceBroken() {
		super.onOuterDefenceBroken();
		if(! this.level.isClientSide){
			EntityUtil.playSound(this, this.getPartDeathSound());
		}
		this.hitDefence = false;
	}

	@Override
	public void onOuterDefenceHurt() {
		super.onOuterDefenceHurt();
		if(! this.level.isClientSide){
			EntityUtil.playSound(this, this.getPartHurtSound());
		}
		this.hitDefence = false;
	}

	@Override
	public boolean canOuterDefend(DamageSource source) {
		return super.canOuterDefend(source) && this.hitDefence;
	}

	protected float getPartHeightOffset() {
		if(this.isMiniZombie()) return 0.1F;
		return 0.2f;
	}

	public float getPartWidthOffset() {
		if(this.isMiniZombie()) return 0.3F;
		return 0.55f;
	}

	public SoundEvent getPartHurtSound() {
		return null;
	}

	public SoundEvent getPartDeathSound() {
		return null;
	}

}
