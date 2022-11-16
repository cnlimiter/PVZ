package cn.evolvefield.mods.pvz.common.entity;

import cn.evolvefield.mods.pvz.api.enums.PVZGroupType;
import cn.evolvefield.mods.pvz.api.interfaces.base.IHasGroup;
import cn.evolvefield.mods.pvz.api.interfaces.base.IHasOwner;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractOwnerEntity extends PVZProjectileBase implements IHasGroup, IHasOwner {

	protected Entity owner;
	protected UUID ownerId;
	protected PVZGroupType groupType;

	public AbstractOwnerEntity(EntityType<? extends Projectile> entityTypeIn, Level worldIn) {
		super(entityTypeIn, worldIn);
		this.groupType = this.getInitialEntityGroup();
	}

	public AbstractOwnerEntity(EntityType<? extends Projectile> type, Level worldIn, Entity livingEntityIn) {
		super(type, worldIn);
		this.summonByOwner(livingEntityIn);
	}

	/**
	 * sync some data from owner.
	 */
	public void summonByOwner(Entity owner) {
		this.owner = owner;
		this.ownerId = owner.getUUID();
		this.groupType = EntityUtil.getEntityGroup(owner);
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	public void setOwner(LivingEntity player) {
		this.owner = player;
	}

	@Nullable
	public Entity getOwner() {
		if (EntityUtil.isEntityValid(this.owner) && this.ownerId != null && this.level instanceof ServerLevel) {
			this.owner = ((ServerLevel) this.level).getEntity(this.ownerId);
		}
		return this.owner;
	}

	public Entity getOwnerOrSelf() {
		return this.getOwner() == null ? this : this.getOwner();
	}

	@Override
	public Optional<UUID> getOwnerUUID() {
		return Optional.ofNullable(this.uuid);
	}

	public PVZGroupType getInitialEntityGroup() {
		return PVZGroupType.NEUTRALS;
	}

	@Override
	public PVZGroupType getEntityGroupType() {
		return this.groupType;
	}

	public void addAdditionalSaveData(CompoundTag compound) {
		if (this.ownerId != null) {
			compound.putUUID("owner", this.ownerId);
		}
		compound.putInt("entity_tick_exist", this.tickCount);
		compound.putInt("group_owner_type", this.groupType.ordinal());
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readAdditionalSaveData(CompoundTag compound) {
		this.owner = null;
		if (compound.contains("owner", 10)) {
			this.ownerId = compound.getUUID("owner");
		}
		if(compound.contains("entity_tick_exist")) {
			this.tickCount = compound.getInt("entity_tick_exist");
		}
		if(compound.contains("group_owner_type")) {
			this.groupType = EntityGroupHander.getGroup(compound.getInt("group_owner_type"));
		}
	}

}
