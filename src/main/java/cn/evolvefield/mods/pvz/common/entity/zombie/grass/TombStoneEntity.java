package cn.evolvefield.mods.pvz.common.entity.zombie.grass;

import cn.evolvefield.mods.pvz.common.entity.plant.assist.GraveBusterEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.other.NobleZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.roof.Edgar090505Entity;
import cn.evolvefield.mods.pvz.common.impl.zombie.GrassZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.Level;

public class TombStoneEntity extends AbstractTombStoneEntity {

	protected int waveSummonTick = 0;
	protected int currentSummonCD;
	private final int MinSummonCD = 360;
	private final int MaxSummonCD = 1200;

	public TombStoneEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new TombStoneSummonZombieGoal(this));
		this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, NobleZombieEntity.class, true));
		this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Edgar090505Entity.class, true));
	}

	public void activateByWave() {
		this.waveSummonTick = 40;
	}

	public static void spawnTombStone(Level world, BlockPos pos) {
		TombStoneEntity tomb = EntityRegister.TOMB_STONE.get().create(world);
		tomb.setZombieRising();
		EntityUtil.onEntitySpawn(world, tomb, pos);
	}

	@Override
	public void normalZombieTick() {
		super.normalZombieTick();
		if(! level.isClientSide) {//update wave spawn.
			if(this.waveSummonTick > 0) {
				-- this.waveSummonTick;
				if(this.waveSummonTick == 1) {
					this.summonZombie();
				}
			}
		}
	}

	/**
	 * tombstone summon zombies.
	 * it will select suitable zombie for that day.
	 * {@link TombStoneSummonZombieGoal} and {@link #normalZombieTick()}
	 */
	public void summonZombie() {
//		final List<IZombieType> list = InvasionCache.getOrDefaultZombieList(ZombieUtil.DEFAULT_ZOMBIES);
//		final IZombieType zombieType = list.get(this.random.nextInt(list.size()));
//		zombieType.getEntityType().ifPresent(type -> {
			//TODO tombstone zombie-summoning
//			CreatureEntity zombie = type.create(maxLevel);
//			zombie.setZombieRising();
//			ZombieUtil.copySummonZombieData(this, zombie);
//			EntityUtil.onEntitySpawn(maxLevel, zombie, blockPosition());
//		});
	}

	/**
	 * check can summon zombie or not.
	 * it need a valid boss target.
	 */
	protected boolean canSummonZombie() {
		return EntityUtil.isEntityValid(this.getTarget());
	}

	@Override
	protected boolean isZombieInvulnerableTo(DamageSource source) {
		return super.isZombieInvulnerableTo(source) || ! (source.getDirectEntity() instanceof GraveBusterEntity);
	}

	/**
	 * next summon zombie cd for next time.
	 */
	protected int genSummonCD() {
		return MathUtil.getRandomMinMax(getRandom(), MinSummonCD, MaxSummonCD);
	}

	@Override
	public ZombieType getZombieType() {
		return GrassZombies.TOMB_STONE;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("wave_summon_tick")) {
			this.waveSummonTick = compound.getInt("wave_summon_tick");
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("wave_summon_tick", this.waveSummonTick);
	}

	static class TombStoneSummonZombieGoal extends Goal {

		protected final TombStoneEntity tomb;

		public TombStoneSummonZombieGoal(TombStoneEntity tomb) {
			this.tomb = tomb;
		}

		@Override
		public boolean canUse() {
			if(this.tomb.getAttackTime() > 0) {
				return true;
			}
			if(this.tomb.random.nextInt(10) == 0 && this.tomb.canSummonZombie()) {
				this.tomb.setAttackTime(1);
				this.tomb.currentSummonCD = this.tomb.genSummonCD();
				return true;
			}
			return false;
		}

		@Override
		public void start() {

		}

		@Override
		public boolean canContinueToUse() {
			return this.tomb.getAttackTime() > 0 && this.tomb.canSummonZombie();
		}

		@Override
		public void tick() {
			int time = this.tomb.getAttackTime();
			if(time >= this.tomb.currentSummonCD) {
				this.tomb.summonZombie();
				this.tomb.setAttackTime(0);
			} else {
				this.tomb.setAttackTime(time + 1);
			}
		}

		@Override
		public void stop() {
			this.tomb.setAttackTime(0);
		}

	}

}
