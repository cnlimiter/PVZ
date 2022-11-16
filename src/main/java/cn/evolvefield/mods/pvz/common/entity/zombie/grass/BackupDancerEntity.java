package cn.evolvefield.mods.pvz.common.entity.zombie.grass;

import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.GrassZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.MathUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.Optional;

public class BackupDancerEntity extends PVZZombieEntity {

	public static final int DANCE_CD = DancingZombieEntity.DANCE_CD;
	protected Optional<DancingZombieEntity> owner = Optional.empty();
	private static final int MIN_REST_CD = 60;
	private static final int MAX_REST_CD = 300;
	private int restTick = 0;

	public BackupDancerEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.canCollideWithZombie = false;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new ZombieDanceGoal(this));
	}

	@Override
	public void normalZombieTick() {
		super.normalZombieTick();
		if(! level.isClientSide) {
			this.updateSpeed();
			if(this.needFollow()) {
				this.owner.ifPresent(dancer -> {
					this.setAttackTime(dancer.getAttackTime());
			        this.setYRot(dancer.getYRot());
			        this.setXRot(dancer.getXRot());
			        this.yHeadRot = dancer.yHeadRot;
				});
		    }
		}
	}

	@Override
	public float getLife() {
		return 20;
	}

	/**
	 * still need follow the prompt of dancing zombie or not.
	 * {@link ZombieDanceGoal#canUse()}
	 */
	public boolean needFollow() {
		return this.owner.isPresent() && EntityUtil.isEntityValid(this.owner.get());
	}

	/**
	 * {@link DancingZombieEntity#summonEmptyDancers()}
	 */
    public void setDancingOwner(DancingZombieEntity dancer) {
		this.owner = Optional.ofNullable(dancer);
	}

    /**
	 * {@link ZombieDanceGoal#tick()}
	 */
    protected void setRestTick() {
		this.restTick = MathUtil.getRandomMinMax(random, MIN_REST_CD, MAX_REST_CD);
	}

    /**
     * change speed when toggle dance and walk.
     */
	protected void updateSpeed() {
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getAttackTime() > 0 ? 0 : ZombieUtil.WALK_LITTLE_SLOW);
	}

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
    	super.readAdditionalSaveData(compound);
    	if(compound.contains("zombie_rest_tick")) {
    		this.restTick = compound.getInt("zombie_rest_tick");
    	}
    	if(compound.contains("dancing_owner")) {
    		var entity = level.getEntity(compound.getInt("dancing_owner"));
    		if(EntityUtil.isEntityValid(entity) && entity instanceof DancingZombieEntity) {
    			this.owner = Optional.ofNullable((DancingZombieEntity) entity);
    		}
    	}
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
    	super.addAdditionalSaveData(compound);
    	compound.putInt("zombie_rest_tick", this.restTick);
    	this.owner.ifPresent(dancer -> compound.putInt("dancing_owner", dancer.getId()));
    }

    @Override
	public ZombieType getZombieType() {
		return GrassZombies.BACKUP_DANCER;
	}

	static class ZombieDanceGoal extends Goal {

		private final BackupDancerEntity dancer;

		public ZombieDanceGoal(BackupDancerEntity dancer) {
			this.dancer = dancer;
			this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			if(this.dancer.needFollow()) {//dancing zombie not die.
				return false;
			}
			if(this.dancer.getAttackTime() > 0) {//dancer is dancing at last game.
				return true;
			}
			if(this.dancer.restTick > 0) {//still in rest time, so can not dance.
				-- this.dancer.restTick;
				return false;
			}
			if(this.dancer.getRandom().nextFloat() < 0.05F) {
				this.dancer.setAttackTime(DANCE_CD);
				return true;
			}
			return false;
		}

		@Override
		public void start() {
			this.dancer.updateSpeed();
		}

		@Override
		public boolean canContinueToUse() {
			return this.dancer.getAttackTime() > 0;
		}

		@Override
		public void tick() {
			int tick = this.dancer.getAttackTime();
			if(tick == 1) {
				this.dancer.setRestTick();
			}
			this.dancer.setAttackTime(Math.max(0, tick - 1));
		}

		@Override
		public void stop() {
			this.dancer.updateSpeed();
		}

	}

}
