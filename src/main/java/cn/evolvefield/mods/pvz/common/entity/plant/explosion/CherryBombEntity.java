package cn.evolvefield.mods.pvz.common.entity.plant.explosion;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.advancement.trigger.EntityEffectAmountTrigger;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantBomberEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.ParticleRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class CherryBombEntity extends PlantBomberEntity{

	public CherryBombEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void startBomb(boolean server) {
		if(server) {
			int deathCnt = 0;
			final float range = getExplodeRange();
			final AABB aabb = EntityUtil.getEntityAABB(this, range, range);
			for(Entity target : EntityUtil.getWholeTargetableEntities(this, aabb)) {
				target.hurt(PVZEntityDamageSource.explode(this), this.getExplodeDamage());
				if(! EntityUtil.isEntityValid(target)) {
					++ deathCnt;
				}
			}
			PVZPlantEntity.clearLadders(this, aabb);
			EntityUtil.playSound(this, SoundRegister.CHERRY_BOMB.get());
			//trigger advancement.
			Player owner = EntityUtil.getEntityOwner(level, this);
			if(owner != null && owner instanceof ServerPlayer) {
				EntityEffectAmountTrigger.INSTANCE.trigger((ServerPlayer) owner, this, deathCnt);
			}
		} else {
			for(int i = 0; i < 5; ++ i) {
		        this.level.addParticle(ParticleRegister.RED_BOMB.get(), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
			}
		}
	}

	/**
	 * explosion damage.
	 * {@link #startBomb(boolean server)}
	 */
	@Override
	public float getExplodeDamage(){
		return this.getSkillValue(SkillTypes.NORMAL_BOMB_DAMAGE);
	}

	@Override
	public float getExplodeRange(){
		return 4.5F;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return new EntityDimensions(0.9f, 1f, false);
	}

	@Override
	public int getReadyTime() {
		return 30;
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.CHERRY_BOMB;
	}

}
