package cn.evolvefield.mods.pvz.common.entity.plant.toxic;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.bullet.AbstractBulletEntity;
import cn.evolvefield.mods.pvz.common.entity.bullet.FumeEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantShooterEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;

public class GloomShroomEntity extends PlantShooterEntity {

	private static final float SHOOT_HEIGHT = 0.2F;
	private static final int SHOOT_NUM = 8;

	public GloomShroomEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void shootBullet() {
		float now = this.yHeadRot;
		for(int i = 0; i < SHOOT_NUM; ++ i) {
			this.shootByAngle(now, SHOOT_HEIGHT);
			now += 360F / SHOOT_NUM;
		}
        EntityUtil.playSound(this, SoundRegister.FUME.get());
	}

	@Override
	protected AbstractBulletEntity createBullet() {
		final FumeEntity fume = new FumeEntity(this.level, this);
        if(this.isPlantInSuperMode()) {
        	fume.setKnockback(1);
        }
        return fume;
	}

	@Override
	public void startShootAttack() {
		this.setAttackTime(1);
	}

    @Override
	public int getSuperTimeLength() {
		return 80;
	}

    @Override
    public int getShootCD() {
    	return 8;
    }

	@Override
	public float getAttackDamage() {
		return this.getSkillValue(SkillTypes.SPORE_DAMAGE);
	}

	@Override
	public float getShootRange() {
		return 4;
	}

    @Override
    public EntityDimensions getDimensions(Pose poseIn) {
    	return EntityDimensions.scalable(0.9F, 0.8F);
    }

    @Override
	public IPlantType getPlantType() {
		return PVZPlants.GLOOM_SHROOM;
	}

}
