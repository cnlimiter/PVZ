package cn.evolvefield.mods.pvz.common.entity.misc;

import cn.evolvefield.mods.pvz.api.enums.PVZGroupType;
import cn.evolvefield.mods.pvz.common.entity.AbstractOwnerEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.other.CoffinEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.LevelUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class ZombieHandEntity extends AbstractOwnerEntity {

	private int lifeTick;
	private final int maxLifeTick = 40;

	public ZombieHandEntity(EntityType<? extends Projectile> entityTypeIn, Level worldIn) {
		super(entityTypeIn, worldIn);
		this.setInvulnerable(true);
		this.noPhysics = true;
	}

	@Override
	public void tick() {
		super.tick();
		if(this.lifeTick < maxLifeTick) {
			++ this.lifeTick;
		} else {
			if(! this.level.isClientSide) {
			    this.performAttack();
			    this.remove(RemovalReason.KILLED);
			}
		}
	}

	/**
	 * {@link CoffinEntity#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, MobSpawnType, SpawnGroupData, CompoundTag)}
	 */
	public static void spawnRangeZombieHands(Level world, PVZZombieEntity zombie, int range) {
		for(int i = - range; i <= range; ++ i) {
			for(int j = - range; j <= range; ++ j) {
				final ZombieHandEntity hand = EntityRegister.ZOMBIE_HAND.get().create(world);
				hand.summonByOwner(zombie);
				EntityUtil.onEntitySpawn(world, hand, LevelUtil.getSuitableHeightPos(world, zombie.blockPosition().offset(i, 0, j)));
			}
		}
	}

	protected void performAttack() {
		EntityUtil.getTargetableLivings(this, EntityUtil.getEntityAABB(this, 0.5f, 1f)).forEach(target -> {
		    target.hurt(PVZEntityDamageSource.normal(this, this.getOwnerOrSelf()), this.getAttackDamage(target));
			target.setPos(target.getX(), target.getY() - 3, target.getZ());
		});
	}

	@Override
	public PVZGroupType getInitialEntityGroup() {
		return PVZGroupType.ZOMBIES;
	}

	private float getAttackDamage(LivingEntity target) {
		return 10;
	}

	public int getTick() {
		return this.lifeTick;
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return new EntityDimensions(0.4f, 0.5f, false);
	}

	@Override
	public boolean isNoGravity() {
		return true;
	}

}
