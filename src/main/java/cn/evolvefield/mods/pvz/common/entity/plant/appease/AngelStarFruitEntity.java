package cn.evolvefield.mods.pvz.common.entity.plant.appease;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.bullet.AbstractBulletEntity;
import cn.evolvefield.mods.pvz.common.entity.bullet.StarEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantShooterEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.OtherPlants;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;

public class AngelStarFruitEntity extends PlantShooterEntity {

	public static final float PER_ANGLE = 360F / 5;
	private static final float SHOOT_HEIGHT = 0.2F;
	public int lightTick = 0;

	public AngelStarFruitEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void normalPlantTick() {
		if(level.isClientSide) {
			if(this.lightTick > 0) {
				-- this.lightTick;
			}
			if(this.getAttackTime() > 0) {
			    this.lightTick = 8;
			}
		}
		super.normalPlantTick();
		if(this.isPlantInSuperMode()) {
			float now = this.getSuperTime() * 4;
			for(int i = 0; i < 5; ++ i) {
				this.shootByAngle(now, SHOOT_HEIGHT);
				now += PER_ANGLE;
			}
		}
	}

	@Override
	public void shootBullet() {
		float now = this.yHeadRot;
		for(int i = 0; i < 5; ++ i) {
			this.shootByAngle(now, SHOOT_HEIGHT);
			now += PER_ANGLE;
		}
		if(this.getRandom().nextInt(100) < this.getExtraAttackChance()) {
			now = this.yHeadRot + 36F;
			for(int i = 0; i < 5; ++ i) {
				this.shootByAngle(now, SHOOT_HEIGHT);
				now += PER_ANGLE;
			}
		}
		EntityUtil.playSound(this, SoundRegister.SNOW_SHOOT.get());
	}

	public float getExtraAttackChance() {
		return this.getSkillValue(SkillTypes.TEN_STARS);
	}

	@Override
	public float getAttackDamage() {
		return this.getSkillValue(SkillTypes.MORE_STAR_DAMAGE);
	}

	@Override
	protected boolean canAttackNow() {
		return this.getAttackTime() == 2 && ! this.isPlantInSuperMode();
	}

	@Override
	protected AbstractBulletEntity createBullet() {
		final StarEntity.StarTypes type = this.isPlantInSuperMode() ? StarEntity.StarTypes.BIG : StarEntity.StarTypes.NORMAL;
		return new StarEntity(level, this, type, StarEntity.StarStates.PINK);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.9F, 0.5F);
	}

	@Override
	public double getMaxShootAngle() {
		return 80;
	}

	@Override
	public void startShootAttack() {
		this.setAttackTime(2);
	}

	@Override
	public int getSuperTimeLength() {
		return 150;
	}

	@Override
	public IPlantType getPlantType() {
		return OtherPlants.ANGEL_STAR_FRUIT;
	}

}
