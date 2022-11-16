package cn.evolvefield.mods.pvz.common.entity.zombie.other;

import cn.evolvefield.mods.pvz.common.entity.misc.ZombieHandEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.base.AbstractBossZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.CustomZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.init.misc.PVZLoot;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.MathUtil;
import cn.evolvefield.mods.pvz.utils.WorldUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.List;

public class NobleZombieEntity extends AbstractBossZombieEntity {

	private static final EntityDataAccessor<Integer> TP_TICK = SynchedEntityData.defineId(NobleZombieEntity.class,
			EntityDataSerializers.INT);
	private int summonTick;
	private final int minSummonTick = 300;
	private final int maxSummonTick = 600;
	private final int minTpCD = 400;
	private final int maxTpCD = 850;
	private final int minSleepAttackCD = 360;
	private final int maxSleepAttackCD = 1000;

	public NobleZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.setAttackTime(this.maxSleepAttackCD / 5);
		this.summonTick = this.maxSummonTick / 3;
		this.setTpTick(- this.maxTpCD / 2);
		this.xpReward = 1000;
		this.setIsWholeBody();
		this.canBeFrozen = true;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(TP_TICK, 0);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ZombieUtil.WALK_VERY_SLOW);
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ZombieUtil.LITTLE_HIGH);
	}


	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason,
										SpawnGroupData spawnDataIn, CompoundTag dataTag) {
		if (! level.isClientSide) {
			//TODO MournerZombie level deleted
			EntityUtil.playSound(this, SoundRegister.DIRT_RISE.get());
			ZombieHandEntity.spawnRangeZombieHands(level, this, 6);
//			for(int i = 0; i < this.getSkills() / 2 + 5; ++ i) {
//				MournerZombieEntity zombie = EntityRegister.MOURNER_ZOMBIE.get().create(level);
//				this.onBossSummon(zombie, WorldUtil.getSuitableHeightRandomPos(level, blockPosition(), 10, 20));
//			}
		}
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	public void zombieTick() {
		super.zombieTick();
		if(!this.level.isClientSide && ! this.canNormalUpdate()) {
			this.setTpTick(- this.maxTpCD);
		}
	}

	@Override
	public void normalZombieTick() {
		super.normalZombieTick();
		if (!level.isClientSide) {
			// summon MournerZombie
			if (this.summonTick > 0) {
				-- this.summonTick;
			} else {
				this.summonTick = MathUtil.getRandomMinMax(getRandom(), this.minSummonTick, this.maxSummonTick);
				this.checkAndSummonMournerZombie();
			}
			// make plants fall in sleep.
			if(this.getAttackTime() > 0) {
				this.setAttackTime(this.getAttackTime() - 1);
			} else {
				this.setAttackTime(MathUtil.getRandomMinMax(getRandom(), this.minSleepAttackCD, this.maxSleepAttackCD));
				this.checkAndSleepPlant();
			}
		} else {
			if(this.getAttackTime() < 20) {
				this.level.addParticle(ParticleTypes.NOTE, getX(), getY() + 2f, getZ(), 0, 0, 0);
			}
		}
		if(! this.level.isClientSide) {
			if (this.getTpTick() < 0) {
			    this.setTpTick(this.getTpTick() + 1);
		    } else if (this.getTpTick() == 0) {
			    if (this.getRandom().nextInt(5) == 0) {
				    this.setTpTick(1);
			    }
		    } else {
			    if (this.getTpTick() >= this.getTpCD()) {
				    this.checkAndTeleport();
			    }
			    this.setTpTick(this.getTpTick() + 1);
		    }
		} else {
			if(this.getTpTick() > 0) {
				for(int i = 0; i < 9; ++ i) {
					level.addParticle(ParticleTypes.PORTAL, this.getX() + (this.getRandom().nextInt(5) - 2), this.getY() + (this.getRandom().nextInt(3)), this.getZ() + (this.getRandom().nextInt(5) - 2), 0, 0, 0);
				}
			}
		}
	}

	/**
	 * Skill 1 : Teleport and make explosion
	 */
	private void checkAndTeleport() {
		final BlockPos teleportPos = (this.getTarget() == null ? this.blockPosition() : this.getTarget().blockPosition());
		this.setTpTick(-this.getRandom().nextInt(this.maxTpCD - this.minTpCD + 1) - this.minTpCD);
		this.teleportToPos(teleportPos.getX(), teleportPos.getY(), teleportPos.getZ());
		ZombieHandEntity.spawnRangeZombieHands(level, this, 2);
	}

	/**
	 * Skill 2 : Summon Mourner Zombies.
	 */
	protected void checkAndSummonMournerZombie() {
		if (this.nearbyZombieCount >= this.maxZombieSurround) {// there are so many zombies in range.
			this.checkAndSummonZombieHand();
			return;
		}
		final int max = Math.min(this.maxZombieSurround - this.nearbyZombieCount, 3);
		for (int i = 0; i < MathUtil.getRandomMinMax(getRandom(), 1 + max / 2, max + 1); ++i) {
			MournerZombieEntity zombie = EntityRegister.MOURNER_ZOMBIE.get().create(level);
			this.onBossSummon(zombie, (this.getTarget() == null ? this.blockPosition() : this.getTarget().blockPosition()));
		}
	}

	/**
	 * Skill 3 : Sleep plants in a specific area.
	 */
	private void checkAndSleepPlant() {
		final float range = 50;
		List<PVZPlantEntity> list = this.level.getEntitiesOfClass(PVZPlantEntity.class, EntityUtil.getEntityAABB(this, range, range), plant -> {
			return EntityUtil.canTargetEntity(this, plant);
		});
		if(list.isEmpty()) {
			return ;
		}
		int pos = this.getRandom().nextInt(list.size());
		final PVZPlantEntity plant = list.get(pos);
		final float radius = 3F;
		for(PVZPlantEntity target : this.level.getEntitiesOfClass(PVZPlantEntity.class, EntityUtil.getEntityAABB(plant, radius, radius), p -> {
			return EntityUtil.canTargetEntity(this, p);
		})) {
			target.sleepTime = 2400;
		}
	}

	/**
	 * Skill 4 : Summon Zombie's Hands.
	 */
	private void checkAndSummonZombieHand() {
		int cnt = this.getHandSummonNum();
		List<LivingEntity> list = EntityUtil.getTargetableLivings(this, EntityUtil.getEntityAABB(this, 50, 50));
		for(int i = 0; i < list.size(); ++ i) {
			if(i + cnt >= list.size() || this.getRandom().nextInt(5) == 0) {
				ZombieHandEntity hand = EntityRegister.ZOMBIE_HAND.get().create(level);
				hand.summonByOwner(this);
				EntityUtil.onEntitySpawn(level, hand, WorldUtil.getSuitableHeightPos(level, list.get(i).blockPosition()));
				if(-- cnt <= 0) {
					break;
				}
			}
		}
	}

	/**
	 * Skill 5 : Heal itself.
	 */
	public void checkAndHeal(float percent) {
		if(percent < 1f / 2) {
			this.heal(0.4f);
		} else if(percent < 1f / 6) {
			this.heal(0.8f);
		}
	}

	public void teleportToPos(double x, double y, double z) {
		BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(x, y, z);
		while (blockpos$mutable.getY() > 0
				&& !this.level.getBlockState(blockpos$mutable).getMaterial().blocksMotion()) {
			blockpos$mutable.move(Direction.DOWN);
		}
		var blockstate = this.level.getBlockState(blockpos$mutable);
		boolean flag = blockstate.getMaterial().blocksMotion();
		if (flag) {
			boolean flag2 = this.randomTeleport(x, y, z, true);
			if (flag2) {
				this.level.playSound((Player) null, this.xo, this.yo, this.zo,
						SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
				this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
			}
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if(amount >= 10) {
			amount /= 3f;
		}else {
			amount /= 2f;
		}
		return super.hurt(source, amount);
	}

	@Override
	public float getLife() {
		return 1000;
	}

	@Override
	public float getInnerLife() {
		return 1000;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.8f, 1.9f);
	}

	protected int getTpCD() {
		final float percent = this.bossInfo.getProgress();
		if (percent < 1f / 3) {
			return 60;
		} else if (percent < 2f / 3) {
			return 80;
		}
		return 100;
	}

	protected int getHandSummonNum() {
		final float percent = this.bossInfo.getProgress();
		return percent < 1f / 3 ? this.nearbyPlantCount / 5 + 3 :
			percent < 2f / 3 ? this.nearbyPlantCount / 8 + 2 : this.nearbyPlantCount / 10 + 1;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("summon_zombie_tick")) {
			this.summonTick = compound.getInt("summon_zombie_tick");
		}
		if(compound.contains("zombie_tp_tick")) {
			this.setTpTick(compound.getInt("zombie_tp_tick"));
		}
		if (this.hasCustomName()) {
			this.bossInfo.setName(this.getDisplayName());
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("summon_zombie_tick", this.summonTick);
		compound.putInt("zombie_tp_tick", this.getTpTick());
	}

	public int getTpTick() {
		return this.entityData.get(TP_TICK);
	}

	public void setTpTick(int tick) {
		this.entityData.set(TP_TICK, tick);
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return PVZLoot.NOBLE_ZOMBIE;
	}

	@Override
	public ZombieType getZombieType() {
		return CustomZombies.NOBLE_ZOMBIE;
	}

}
