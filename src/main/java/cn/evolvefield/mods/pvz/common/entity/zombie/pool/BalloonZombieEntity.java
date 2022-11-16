package cn.evolvefield.mods.pvz.common.entity.zombie.pool;

import cn.evolvefield.mods.pvz.api.interfaces.attract.ICanAttract;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.PoolZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class BalloonZombieEntity extends PVZZombieEntity {

	private static final EntityDataAccessor<Boolean> HAS_BALLOON = SynchedEntityData.defineId(BalloonZombieEntity.class, EntityDataSerializers.BOOLEAN);
	private final MoveControl FlyController = new FlyingMoveControl(this, 360, true);
	private final MoveControl GroundController = new MoveControl(this);
	private PathNavigation FlyNavigator;
	private PathNavigation GroundNavigator;

	public BalloonZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(HAS_BALLOON, true);
	}

	@Override
	protected void registerGoals() {
		//define at here to avoid crash.
		this.FlyNavigator = new FlyingPathNavigation(this, level);
		this.GroundNavigator = new GroundPathNavigation(this, level);
		super.registerGoals();
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttribute(Attributes.FLYING_SPEED).setBaseValue(ZombieUtil.FLY_FAST);
		this.setNoGravity(this.hasBalloon());
		this.moveControl = this.hasBalloon() ? FlyController : GroundController;
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
		super.onSyncedDataUpdated(data);
		if(data.equals(HAS_BALLOON)) {
			this.setNoGravity(this.hasBalloon());
			this.moveControl = this.hasBalloon() ? FlyController : GroundController;
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if(this.hasBalloon() && this.canHitBalloon(source)) {
			this.onBalloonExplode();
			amount = 0;
		}
		return super.hurt(source, amount);
	}

	private boolean canHitBalloon(DamageSource source) {
		if(source.getDirectEntity() instanceof Arrow) {
			return true;
		}
		if(source instanceof PVZEntityDamageSource) {
			return ((PVZEntityDamageSource) source).isThornDamage();
		}
		return false;
	}

	/**
	 * trigger when balloon hit thorn.
	 * {@link #hurt(DamageSource, float)}
	 */
	public void onBalloonExplode(){
		if(! level.isClientSide) {
			EntityUtil.playSound(this, SoundRegister.BALLOON_POP.get());
		}
		this.setBalloon(false);
	}

	@Override
	protected boolean isZombieInvulnerableTo(DamageSource source) {
		if(this.hasBalloon() && source.isProjectile()){
			return true;
		}
		return super.isZombieInvulnerableTo(source);
	}

	@Override
	public boolean canBeTargetBy(LivingEntity living) {
		if(living instanceof PVZPlantEntity && this.hasBalloon()){
			return false;
		}
		return super.canBeTargetBy(living);
	}

	@Override
	public boolean canClimbWalls() {
		return super.canClimbWalls() && ! this.hasBalloon();
	}

	@Override
	public boolean canBeAttractedBy(ICanAttract defender) {
		return ! this.hasBalloon();
	}

	@Override
	public float getLife() {
		return 23;
	}

	@Override
	public float getWalkSpeed() {
		return ZombieUtil.WALK_LITTLE_SLOW;
	}

	@Override
	public boolean canBeButtered() {
		return ! this.hasBalloon();
	}

	@Override
	public boolean canBeFrozen() {
		return ! this.hasBalloon();
	}

	@Override
	public boolean canBeCold() {
		return ! this.hasBalloon();
	}

	@Override
	public PathNavigation getNavigation() {
		if(this.hasBalloon()) {
			if(! (this.navigation instanceof FlyingPathNavigation)) {
			    this.navigation = this.FlyNavigator;
			}
		} else {
			if(! (this.navigation instanceof GroundPathNavigation)) {
				this.navigation = this.GroundNavigator;
			}
		}
		return super.getNavigation();
	}

	@Override
	public Optional<SoundEvent> getSpawnSound() {
		return Optional.ofNullable(SoundRegister.BALLOON_INFLATE.get());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("has_balloon")) {
			this.setBalloon(compound.getBoolean("has_balloon"));
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("has_balloon", this.hasBalloon());
	}

	public void setBalloon(boolean has) {
		this.entityData.set(HAS_BALLOON, has);
	}

	public boolean hasBalloon() {
		return this.entityData.get(HAS_BALLOON);
	}

    @Override
    public ZombieType getZombieType() {
	    return PoolZombies.BALLOON_ZOMBIE;
    }

}
