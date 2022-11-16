package cn.evolvefield.mods.pvz.common.entity.zombie.pool;

import cn.evolvefield.mods.pvz.api.interfaces.util.IHasMultiPart;
import cn.evolvefield.mods.pvz.common.entity.PVZMultiPartEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.body.ZombieDropBodyEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.part.PVZZombiePartEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.PoolZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;

public class BobsleTeamEntity extends PVZZombieEntity implements IHasMultiPart {

	public static final int PART_NUM = 2;
	private static final int MAX_OUT_SNOW_TICK = 100;
	private PVZZombiePartEntity[] parts = new PVZZombiePartEntity[PART_NUM];
	private int outSnowTick;

	public BobsleTeamEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.setIsWholeBody();
		this.resetParts();
		this.setImmuneAllEffects();
		this.canBeMini = false;
	}

	@Override
	public VariantType getVariantType() {
		return VariantType.NORMAL;
	}

	@Override
	public void resetParts() {
		removeParts();
		for(int i = 0; i < PART_NUM; i++) {
			this.parts[i] = new PVZZombiePartEntity(this, 1f, 1.5f);
			this.parts[i].setOwner(this);
		}
	}

	@Override
	public void removeParts() {
		for(int i = 0; i < PART_NUM; i++) {
			if(this.parts[i] == null) {
				continue;
			}
			this.parts[i].remove(RemovalReason.KILLED);
			this.parts[i] = null;
		}
	}

	@Override
	public void updateParts() {
		for(int i = 0; i < PART_NUM; i++) {
			if(this.parts[i] == null) {
				continue;
			}
			if(! this.parts[i].isAddedToWorld()) {
				this.level.addFreshEntity(this.parts[i]);
			}
			float j = 2 * 3.14159f * this.yHeadRot / 360;
			float dis = this.getPartOffset(i);
			var pos = this.position();
			this.parts[i].yRotO = this.getYRot();
			this.parts[i].xRotO = this.getXRot();
			this.parts[i].moveTo(pos.x() - Math.sin(j) * dis, pos.y() + 0.05f, pos.z() + Math.cos(j) * dis, this.getYRot(), this.getXRot());
			this.parts[i].setOwner(this);
		}
	}

	public PVZMultiPartEntity[] getMultiParts() {
		return this.parts;
	}

	public float getPartOffset(int num) {
		if(num == 0) {
			return -1.5f;
		}else if(num == 1) {
			return -2.5f;
		}
		return 0;
	}

	@Override
	public void tick() {
		super.tick();
		updateParts();
	}

	@Override
	public void zombieTick() {
		super.zombieTick();
		if(!level.isClientSide) {
			if(this.isInWaterOrBubble() || (this.isOnGround() && !EntityUtil.isOnSnow(this) && !EntityUtil.isOnIce(this))) {
				++ this.outSnowTick;
				if(this.outSnowTick > MAX_OUT_SNOW_TICK) {
					this.onFallBody(DamageSource.DRY_OUT);
					this.onRemoveWhenDeath();
					this.remove(RemovalReason.KILLED);
				}
			} else {
				this.outSnowTick = 0;
			}
		}
	}

	@Override
	protected void onRemoveWhenDeath() {
		if(! level.isClientSide) {
			for(int i = 0; i < 4; ++ i) {
				BobsleZombieEntity zombie = EntityRegister.BOBSLE_ZOMBIE.get().create(level);
				ZombieUtil.copySummonZombieData(this, zombie);
				EntityUtil.onEntityRandomPosSpawn(level, zombie, this.blockPosition(), 2);
			}
		}
	}

	@Override
	protected void setBodyStates(ZombieDropBodyEntity body) {
		super.setBodyStates(body);
		body.setFriction(0.95F);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(1.25f, 1.4f);
	}

	@Override
	public float getLife() {
		return 60;
	}

	@Override
	public float getWalkSpeed() {
		return ZombieUtil.WALK_VERY_FAST;
	}

	@Override
	public float getEatDamage() {
		return ZombieUtil.NORMAL_DAMAGE;
	}

	@Override
	protected int getDeathTime() {
		return 2;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("out_snow_tick")) {
			this.outSnowTick = compound.getInt("out_snow_tick");
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("out_snow_tick", this.outSnowTick);
	}

	@Override
	public ZombieType getZombieType() {
		return PoolZombies.BOBSLE_TEAM;
	}

}
