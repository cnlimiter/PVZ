package cn.evolvefield.mods.pvz.common.entity.zombie.pool;

import cn.evolvefield.mods.pvz.api.enums.MetalTypes;
import cn.evolvefield.mods.pvz.api.interfaces.base.IHasMetal;
import cn.evolvefield.mods.pvz.common.cap.CapabilityHandler;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.body.ZombieDropBodyEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.PoolZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.misc.PVZLoot;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.MathUtil;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class JackInBoxZombieEntity extends PVZZombieEntity implements IHasMetal {

	private static final EntityDataAccessor<Boolean> HAS_BOX = SynchedEntityData.defineId(JackInBoxZombieEntity.class, EntityDataSerializers.BOOLEAN);
	public static final int JACK_EXPLODE_CD = 30;
	private final int MinExplodeTime = 300;
	private final int MaxExplodeTime = 3000;

	public JackInBoxZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.setExplosionTime();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(HAS_BOX, true);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new PlayJackBoxGoal(this));
	}

	@Override
	public void normalZombieTick() {
		super.normalZombieTick();
		if(! level.isClientSide && this.hasBox()) {
			final int tick = this.getAttackTime();
			if(tick < 0) {
				if(tick == -1 && this.canJackExplode()) {
					this.setAttackTime(JACK_EXPLODE_CD);
					EntityUtil.playSound(this, SoundRegister.JACK_SURPRISE.get());
				} else {
					this.setAttackTime(Math.min(tick + 1, - 1));
				}
			} else {
				if(tick == 1) {
					this.doJackExplode();
				}
				this.setAttackTime(Math.max(tick - 1, 0));
			}
		}
		if(this.level.isClientSide && this.hasBox() && this.getAttackTime() == 3) {
			for(int i = 0; i < 2; ++ i) {
			    level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
		    }
		}
	}

	/**
	 * jack box explode.
	 * {@link #normalZombieTick()}
	 */
	private void doJackExplode() {
		final float range = 3.5F;
		EntityUtil.getWholeTargetableEntities(this, EntityUtil.getEntityAABB(this, range, range)).forEach(target -> {
			final PVZEntityDamageSource source = PVZEntityDamageSource.explode(this);
			if (target instanceof PVZPlantEntity) {
				target.hurt(source, EntityUtil.getMaxHealthDamage((LivingEntity) target));
			}
		});
		EntityUtil.playSound(this, SoundRegister.CAR_EXPLOSION.get());
		Explosion.BlockInteraction mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
		final float strenth = this.level.getDifficulty() == Difficulty.HARD ? 2.4F :
				this.level.getDifficulty() == Difficulty.NORMAL ? 2F : 1.6F;
		this.level.explode(this, getX(), getY(), getZ(), strenth, mode);
		this.remove(RemovalReason.KILLED);
	}

	@Override
	public boolean canBeTargetBy(LivingEntity living) {
		return super.canBeTargetBy(living) && ! this.isInExplosion();
	}

	@Override
	protected boolean isZombieInvulnerableTo(DamageSource source) {
		return super.isZombieInvulnerableTo(source) || this.isInExplosion();
	}

	/**
	 * play sound to surround players.
	 * {@link PlayJackBoxGoal#tick()}
	 */
	protected void playJackBoxSound() {
		level.players().stream().filter(player -> this.distanceToSqr(player) <= 150).forEach(player -> {
			player.getCapability(CapabilityHandler.PLAYER_DATA_CAPABILITY).ifPresent((l) -> {
				if(l.getPlayerData().getOtherStats().playSoundTick == 0) {
					PlayerUtil.playClientSound(player, SoundRegister.JACK_MUSIC.get());
					l.getPlayerData().getOtherStats().playSoundTick = 300;
				}
			});
		});
	}

	@Override
	public boolean canLostHand() {
		return super.canLostHand() && ! this.hasBox();
	}

	@Override
	protected void setBodyStates(ZombieDropBodyEntity body) {
		super.setBodyStates(body);
		body.setHandDefence(this.hasBox());
	}

	/**
	 * can explode if time is enough.
	 * {@link #normalZombieTick()}
	 */
	private boolean canJackExplode() {
		if(this.getTarget() == null) {//no target not explode.
			return false;
		}
		return this.distanceToSqr(this.getTarget()) <= 50;
	}

	@Override
	public int getAmbientSoundInterval() {
		return 200;
	}

	/**
	 * attack time < 0 means normal, > 0 means in explosion.
	 */
	private void setExplosionTime() {
		this.setAttackTime(- MathUtil.getRandomMinMax(getRandom(), MinExplodeTime, MaxExplodeTime));
	}

	@Override
	public float getLife() {
		return 40;
	}

	@Override
	public float getWalkSpeed() {
		return ZombieUtil.WALK_FAST;
	}

	/**
	 * {@link #isZombieInvulnerableTo(DamageSource)}
	 * {@link #canBeTargetBy(LivingEntity)}
	 */
	public boolean isInExplosion() {
		return this.getAttackTime() > 0;
	}

	@Override
	public boolean hasMetal() {
		return this.hasBox();
	}

	@Override
	public void decreaseMetal() {
		this.setBox(false);
	}

	@Override
	public void increaseMetal() {
		this.setBox(true);
	}

	@Override
	public MetalTypes getMetalType() {
		return MetalTypes.JACK_BOX;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("has_jack_box")) {
			this.setBox(compound.getBoolean("has_jack_box"));
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("has_jack_box", this.hasBox());
	}

	public void setBox(boolean has) {
		this.entityData.set(HAS_BOX, has);
	}

	public boolean hasBox() {
		return this.entityData.get(HAS_BOX);
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return PVZLoot.JACK_IN_BOX_ZOMBIE;
	}

	@Override
    public ZombieType getZombieType() {
	    return PoolZombies.JACK_IN_BOX_ZOMBIE;
    }

	static class PlayJackBoxGoal extends Goal {

		private final JackInBoxZombieEntity zombie;
		private final int PlayCD = 50;
		private int delayTick = 20;

		public PlayJackBoxGoal(JackInBoxZombieEntity zombie) {
			this.zombie = zombie;
		}

		@Override
		public boolean canUse() {
			if(-- this.delayTick > 0 || ! this.zombie.hasBox()) {
				return false;
			}
			return this.zombie.getRandom().nextFloat() < 0.2F;
		}

		@Override
		public void start() {
		}

		@Override
		public boolean canContinueToUse() {
			return this.zombie.hasBox();
		}

		@Override
		public void tick() {
			if(this.zombie.tickCount % this.PlayCD == 0) {
				this.zombie.playJackBoxSound();
			}
		}

		@Override
		public void stop() {
			this.delayTick = 20;
		}

	}

}