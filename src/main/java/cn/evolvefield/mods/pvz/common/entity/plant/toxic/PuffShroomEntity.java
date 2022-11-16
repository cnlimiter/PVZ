package cn.evolvefield.mods.pvz.common.entity.plant.toxic;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.advancement.trigger.EntityEffectAmountTrigger;
import cn.evolvefield.mods.pvz.common.entity.bullet.AbstractBulletEntity;
import cn.evolvefield.mods.pvz.common.entity.bullet.itembullet.SporeEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantShooterEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;

public class PuffShroomEntity extends PlantShooterEntity {

	protected static final double SHOOT_OFFSET = 0.1D;

	public PuffShroomEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void shootBullet() {
		if(this.isPlantInSuperMode()) {
			this.performShoot(SHOOT_OFFSET, 0, -0.2F, this.getExistTick() % 5 == 0, FORWARD_SHOOT_ANGLE);
		} else {
			this.performShoot(SHOOT_OFFSET, 0, -0.2F, this.getAttackTime() == 1, FORWARD_SHOOT_ANGLE);
		}
	}

	@Override
	public float getAttackDamage() {
		return this.getSkillValue(SkillTypes.SPORE_DAMAGE);
	}
	@Override
	protected AbstractBulletEntity createBullet() {
		return new SporeEntity(this.level, this);
	}

	@Override
	protected SoundEvent getShootSound() {
		return SoundRegister.PUFF.get();
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		if(first) {
			int cnt = 1;
			final int range = 20;
			for(PuffShroomEntity shroom : level.getEntitiesOfClass(PuffShroomEntity.class, EntityUtil.getEntityAABB(this, range, range), shroom -> {
				return this.canSuperTogether(shroom);
			})) {
				if(shroom.canStartSuperMode()) {
				    shroom.startSuperMode(false);
				    ++ cnt;
				    if(cnt >= this.getMaxSuperCnt()) {
				    	break;
				    }
				}
			}
			var player = EntityUtil.getEntityOwner(level, this);
			if(player != null && player instanceof ServerPlayer) {
				EntityEffectAmountTrigger.INSTANCE.trigger((ServerPlayer) player, this, cnt);
			}
		}
	}

	/**
	 * {@link #startSuperMode(boolean)}
	 */
	protected boolean canSuperTogether(PuffShroomEntity entity) {
		if(EntityUtil.canTargetEntity(this, entity) || entity.getPlantType() != this.getPlantType()) {
			return false;
		}
		return this.getOwnerUUID().isPresent() && entity.getOwnerUUID().isPresent() && entity.getOwnerUUID().get().equals(this.getOwnerUUID().get());
	}

	public int getMaxSuperCnt() {
		return 10;
	}

	@Override
	public void startShootAttack() {
		this.setAttackTime(1);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.5f, 0.5f);
	}

	@Override
	public int getSuperTimeLength() {
		return 20;
	}

	@Override
	public float getLife() {
		return 10;
	}

	@Override
	public float getShootRange() {
		return 10;
	}

	@Override
	public float getShootHeight() {
		return 2;
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.PUFF_SHROOM;
	}

}
