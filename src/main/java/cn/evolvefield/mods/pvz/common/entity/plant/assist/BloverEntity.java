package cn.evolvefield.mods.pvz.common.entity.plant.assist;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.pool.BalloonZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.MathUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BloverEntity extends PVZPlantEntity {

	public BloverEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		if(! level.isClientSide) {
			if(this.getExistTick() == 5) {
			    this.blow();
			} else if(this.getExistTick() > 60) {
				this.remove(RemovalReason.KILLED);
			}
		}
	}

	public void blow() {
		if(! this.level.isClientSide) {
			final float len = this.getBlowRange();
			//deal damage.
			EntityUtil.getWholeTargetableEntities(this, EntityUtil.getEntityAABB(this, len, len)).forEach(target -> {
				if(EntityUtil.isEntityInSky(target)) {
					target.hurt(PVZEntityDamageSource.normal(this).setMustHurt(), this.getAttackDamage());
					final Vec3 speed = target.getDeltaMovement();
					final double lvl = this.getForceLevel() * 2.5F;
					final Vec3 delta = MathUtil.getHorizontalNormalizedVec(this.position(), target.position()).scale(lvl);
					target.setDeltaMovement(speed.x + delta.x, speed.y, speed.z + delta.z);
				}
			});
		}
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.addAll(Arrays.asList(
				Pair.of(PAZAlmanacs.ATTACK_DAMAGE, this.getAttackDamage()),
				Pair.of(PAZAlmanacs.ATTACK_RANGE, this.getBlowRange())
		));
	}

	@Override
	public boolean canPAZTarget(Entity entity) {
		if(entity instanceof BalloonZombieEntity) {
			return true;
		}
		return super.canPAZTarget(entity);
	}

	@Override
	public Optional<SoundEvent> getSpawnSound() {
		return Optional.ofNullable(SoundRegister.BLOVER.get());
	}

	public float getAttackDamage(){
		return this.getSkillValue(SkillTypes.BLOW_STRENGTH);
	}

	public int getForceLevel() {
		return 2;
	}

	public float getBlowRange(){
		return 30;
	}

	public int getReadyTime() {
		return 40;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.5F, 1.5F);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.BLOVER;
	}

}
