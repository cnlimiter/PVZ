package cn.evolvefield.mods.pvz.common.entity;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.interfaces.util.IHasMultiPart;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public abstract class PVZMultiPartEntity extends Entity {

	private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(PVZMultiPartEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> WIDTH = SynchedEntityData.defineId(PVZMultiPartEntity.class,
			EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(PVZMultiPartEntity.class,
			EntityDataSerializers.FLOAT);
	private IHasMultiPart parent;
	protected final float MaxHeight;
	protected final float MaxWidth;

	public PVZMultiPartEntity(EntityType<?> entityTypeIn, Level worldIn) {
		super(entityTypeIn, worldIn);
		this.MaxHeight = 0.5F;
		this.MaxWidth = 0.5F;
	}

	public PVZMultiPartEntity(EntityType<?> entityTypeIn, LivingEntity owner, float sizeX, float sizeY) {
		super(entityTypeIn, owner.level);
		if(owner instanceof IHasMultiPart) {
			this.parent = (IHasMultiPart) owner;
		} else {
			Static.LOGGER.warn("Error Multipart Owner");
		}
		this.setOwner(owner);
		this.MaxWidth = sizeX;
		this.MaxHeight = sizeY;
		this.setPartWidth(this.MaxWidth);
		this.setPartHeight(this.MaxHeight);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(OWNER_ID, Integer.valueOf(0));
		this.entityData.define(WIDTH, 0.5F);
		this.entityData.define(HEIGHT, 0.5F);
	}

	@Override
	public void tick() {
		if(this.tickCount <= 5) {
			refreshDimensions();
		}
		if (! level.isClientSide) {
			if (this.canExist()) {//has owner.
				this.markHurt();
				this.collideWithNearbyEntities();
			} else {
				this.remove(RemovalReason.KILLED);
			}
		}
		super.tick();
	}

	/**
	 * {@link #tick()}
	 */
	public boolean canExist() {
		return EntityUtil.isEntityValid(this.getOwner());
	}

	/**
	 * get the owner of current part.
	 */
	@Nullable
	public LivingEntity getOwner() {
		final int id = this.getOwnerId();
		Entity entity = level.getEntity(id);
		return entity instanceof LivingEntity ? (LivingEntity) entity : null;
	}

	public IHasMultiPart getParent() {
		return this.parent;
	}

	public void setOwner(LivingEntity entity) {
		this.setOwnerId(entity.getId());
	}

	@Override
	public boolean is(Entity entity) {
		return this == entity || this.getOwner() == entity;
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	/**
	 * collide with entities.
	 */
	public void collideWithNearbyEntities() {
	}

	@Override
	public InteractionResult interactAt(Player pPlayer, Vec3 pVec, InteractionHand pHand) {
		return InteractionResult.FAIL;
	}

	@Override
	public boolean hurt(DamageSource pSource, float pAmount) {
		return false;
	}

	@Override
	public EntityDimensions getDimensions(Pose pPose) {
		return EntityDimensions.scalable(this.getPartWidth(), this.getPartHeight());
	}



	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
	}

	private int getOwnerId() {
		return this.entityData.get(OWNER_ID);
	}

	public void setOwnerId(int OwnerId) {
		this.entityData.set(OWNER_ID, OwnerId);
	}

	public float getPartWidth() {
		return this.entityData.get(WIDTH);
	}

	public void setPartWidth(float scale) {
		this.entityData.set(WIDTH, scale);
	}

	public float getPartHeight() {
		return this.entityData.get(HEIGHT);
	}

	public void setPartHeight(float scale) {
		this.entityData.set(HEIGHT, scale);
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
