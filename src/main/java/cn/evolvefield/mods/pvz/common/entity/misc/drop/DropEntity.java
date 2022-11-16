package cn.evolvefield.mods.pvz.common.entity.misc.drop;

import cn.evolvefield.mods.pvz.api.interfaces.base.ICollectible;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public abstract class DropEntity extends Mob implements ICollectible {

	private static final EntityDataAccessor<Integer> AMOUNT = SynchedEntityData.defineId(DropEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> STATE  = SynchedEntityData.defineId(DropEntity.class, EntityDataSerializers.INT);
	protected int liveTime = 0;

	public DropEntity(EntityType<? extends Mob> type, Level worldIn) {
		super(type, worldIn);
		this.setInvulnerable(true);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(AMOUNT, 1);
		this.entityData.define(STATE, DropStates.NORMAL.ordinal());
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(4, new FloatGoal(this));
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason,
										   SpawnGroupData spawnDataIn, CompoundTag dataTag) {
		this.onDropped();
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	public void tick() {
		super.tick();
		this.noPhysics = this.getDropState() != DropStates.NORMAL;

		if(! level.isClientSide) {
			if(this.getDropState() == DropStates.NORMAL) {
				++ this.liveTime;
			}
		    if(this.liveTime >= this.getMaxLiveTick()) {
			    this.remove(RemovalReason.KILLED);
		    }
		}
	}

	@Override
	public void playerTouch(Player entityIn) {// collect by colliding with entity.
		if(this.canCollectBy(entityIn)) {
			this.onCollect(entityIn);
		}
	}

	@Override
	public boolean canCollectBy(LivingEntity living) {
		return living instanceof Player && EntityUtil.isEntityValid(this) && this.getDropState() != DropStates.STEAL;
	}

	@Override
	public void onCollect(LivingEntity living) {
		if(living instanceof Player) {
			this.onCollectedByPlayer((Player) living);
		}
		this.remove(RemovalReason.KILLED);
	}

	/**
	 * run when collect the drop.
	 */
	public abstract void onCollectedByPlayer(Player player);

	/**
	 * drop live tick,read from config file
	 */
	protected abstract int getMaxLiveTick();

	/**
	 * called when first join to world.
	 */
	protected void onDropped() {

	}

	@Override
	protected void doPush(Entity entityIn) {
	}

	@Override
	protected void pushEntities() {
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if(AMOUNT.equals(key)) {
			this.refreshDimensions();
		}
		super.onSyncedDataUpdated(key);
	}


	@Override
	public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
		return false;
	}

	@Override
	protected void playBlockFallSound() {
		return ;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("live_time")) {
			this.liveTime = compound.getInt("live_time");
		}
		if(compound.contains("drop_amount")) {
			this.setAmount(compound.getInt("drop_amount"));
		}
		if(compound.contains("drop_state")) {
			this.setDropState(DropStates.values()[compound.getInt("drop_state")]);
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("live_time", this.liveTime);
		compound.putInt("drop_amount", this.getAmount());
		compound.putInt("drop_state", this.getDropState().ordinal());
	}

	public int getAmount(){
		return this.entityData.get(AMOUNT);
	}

	public void setAmount(int num){
		this.entityData.set(AMOUNT, num);
	}

	public DropStates getDropState(){
		return DropStates.values()[this.entityData.get(STATE)];
	}

	public void setDropState(DropStates state){
		this.entityData.set(STATE, state.ordinal());
	}

	public static enum DropStates {
		NORMAL,
		ABSORB,
		STEAL
	}

}
