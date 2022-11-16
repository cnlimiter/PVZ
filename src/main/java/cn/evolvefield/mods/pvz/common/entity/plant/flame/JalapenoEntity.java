package cn.evolvefield.mods.pvz.common.entity.plant.flame;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.misc.ElementBallEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantBomberEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.zombotany.JalapenoZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

public class JalapenoEntity extends PlantBomberEntity {

	public JalapenoEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void startBomb(boolean server) {
		final float range = this.getExplodeRange();
		if(server) {
			//deal damage.
			fireMob(this, range, 1F);
		    fireMob(this, 1F, range);
		    //clear ice balls.
		    ElementBallEntity.killElementBalls(this, 40, ElementBallEntity.ElementTypes.ICE);
			EntityUtil.playSound(this, SoundRegister.JALAPENO.get());
		}
		clearSnowAndSpawnFlame(this, (int) range);
	}

	/**
	 * jalapeno fire mobs.
	 * {@link #startBomb(boolean)}
	 */
	public static void fireMob(LivingEntity entity, float dx, float dz) {
		final var aabb = new AABB(entity.position().add(dx, 1, dz), entity.position().add(- dx, - 1, - dz));
		for(Entity target : EntityUtil.getWholeTargetableEntities(entity, aabb)) {
			float damage = 0;
			if(entity instanceof JalapenoEntity) {
				damage = ((JalapenoEntity) entity).getExplodeDamage();
			} else if(entity instanceof JalapenoZombieEntity) {
				if(target instanceof LivingEntity) {
					damage = EntityUtil.getMaxHealthDamage((LivingEntity) target, 2);
				} else {
					damage = 100F;
				}
			}
			target.hurt(PVZEntityDamageSource.causeFlameDamage(entity, entity).setExplosion(), damage);
		}
		clearLadders(entity, aabb);
	}

	/**
	 * spawn flame particle and clear snow.
	 * {@link #startBomb(boolean)}
	 */
	public static void clearSnowAndSpawnFlame(LivingEntity entity, int range) {
		final boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(entity.level, entity);
		//handle cross .
		for(int i = - range; i <= range; ++ i) {
			spawnFlame(entity, i, 0);
			spawnFlame(entity, 0, i);
			if(flag) {
				clearSnow(entity, i, 0);
				clearSnow(entity, 0, i);
			}
		}
		//clear range snow.
		if(flag) {
			for(int i = - range / 2; i <= range / 2; ++ i) {
			    for(int j = - range / 2; j <= range / 2; ++ j) {
				    clearSnow(entity, i, j);
			    }
			}
		}
	}

	/**
	 * spawn flame particle.
	 */
	private static void spawnFlame(LivingEntity entity, int dx, int dz) {
		if(entity.level.isClientSide) {
			for(int i = 0; i < 20; ++ i) {
				WorldUtil.spawnRandomSpeedParticle(entity.level, ParticleTypes.FLAME, entity.position().add(dx, 0, dz), 0.1F);
			}
		}
	}

	/**
	 * clear snow around.
	 */
	private static void clearSnow(LivingEntity entity, int dx, int dz) {
		if(! entity.level.isClientSide) {
		    final BlockPos pos = entity.blockPosition().offset(dx, 0, dz);
		    if(entity.level.getBlockState(pos).getBlock() == Blocks.SNOW || entity.level.getBlockState(pos).getBlock() == Blocks.SNOW_BLOCK) {
			    entity.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		    }
		}
	}

	@Override
	public float getExplodeRange() {
		return 20;
	}

	@Override
	public float getExplodeDamage() {
		return this.getSkillValue(SkillTypes.NORMAL_BOMB_DAMAGE);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.7f, 1.5f);
	}

	@Override
	public int getReadyTime() {
		return 20;
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.JALAPENO;
	}

}
