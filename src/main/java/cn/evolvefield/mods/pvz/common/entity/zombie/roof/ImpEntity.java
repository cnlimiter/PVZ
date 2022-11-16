package cn.evolvefield.mods.pvz.common.entity.zombie.roof;

import cn.evolvefield.mods.pvz.api.interfaces.attract.ICanAttract;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.RoofZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class ImpEntity extends PVZZombieEntity {

	protected boolean isFalling = false;
	protected int protectTick = 0;

	public ImpEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason,
										   SpawnGroupData spawnDataIn, CompoundTag dataTag) {
		if(! level.isClientSide) {
			int now = this.getRandom().nextInt(10);
			if(now == 0) this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1));
			else if(now == 1) this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 1));
			else if(now == 2) this.addEffect(new MobEffectInstance(MobEffects.JUMP, 600, 1));
			else if(now == 3) this.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 600, 1));
			else if(now == 3) this.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 1));
		}
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ZombieUtil.WALK_FAST);
	}

	@Override
	public void zombieTick() {
		super.zombieTick();
		if(!this.level.isClientSide) {
			if(this.onGround && this.isFalling) {
				this.isFalling = false;
			}
			if(this.protectTick > 0) {
				-- this.protectTick;
			}
		}
	}

	@Override
	public boolean canNormalUpdate() {
		return super.canNormalUpdate() && ! this.isFalling;
	}

	@Override
	protected boolean isZombieInvulnerableTo(DamageSource source) {
		return super.isZombieInvulnerableTo(source) || this.protectTick > 0;
	}

	public void throwByGargantuar(GargantuarEntity entity, LivingEntity target) {
		var vec = new Vec3(entity.getRandom().nextFloat() - 0.5, entity.getRandom().nextFloat() / 4, entity.getRandom().nextFloat() - 0.5).normalize();
		if(target != null) {
			double speed = 2F;
			vec = target.position().subtract(entity.position()).normalize().scale(speed);
		} else {
			double speed = 0.5F;
			vec = vec.scale(speed);
		}
		this.setDeltaMovement(vec);
		ZombieUtil.copySummonZombieData(entity, this);
		this.isFalling = true;
		this.protectTick = 60;
	}

	@Override
	public boolean canBeAttractedBy(ICanAttract defender) {
		return ! this.isFalling && super.canBeAttractedBy(defender);
	}

	@Override
	public float getLife() {
		return 10;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		if(this.isMiniZombie()) {
			return EntityDimensions.scalable(0.2F, 0.45F);
		}
		return EntityDimensions.scalable(0.6F, 1.2F);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("protect_tick")) {
			this.protectTick = compound.getInt("protect_tick");
		}
		if(compound.contains("fall_from_throw")) {
			this.isFalling = compound.getBoolean("fall_from_throw");
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("protect_tick", this.protectTick);
		compound.putBoolean("fall_from_throw", this.isFalling);
	}

	@Override
    public ZombieType getZombieType() {
	    return RoofZombies.IMP;
    }

}
