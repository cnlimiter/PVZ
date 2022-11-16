package cn.evolvefield.mods.pvz.common.entity.plant.explosion;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.bullet.itembullet.PotatoEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantCloserEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.pool.DiggerZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.ParticleRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.MathUtil;
import cn.evolvefield.mods.pvz.utils.WorldUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;
public class PotatoMineEntity extends PlantCloserEntity {

	public static final int RISING_ANIM_CD = 20;

	public PotatoMineEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		if(! this.level.isClientSide) {
			if(this.getExistTick() == this.getPrepareCD() - RISING_ANIM_CD + 1) {
				EntityUtil.playSound(this, SoundRegister.DIRT_RISE.get());
			}
		} else {
			if(this.isRisingFromDirt()) {
				for(int i = 0; i < 1; ++ i) {
					var offset = new Vec3(MathUtil.getRandomFloat(getRandom()), 0, MathUtil.getRandomFloat(getRandom())).normalize();
					WorldUtil.spawnRandomSpeedParticle(level, ParticleRegister.DIRT_BURST_OUT.get(), this.position().add(offset), MathUtil.getRandomFloat(getRandom()) / 8, 0.06F);
				}
			}
		}
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.addAll(Arrays.asList(
				Pair.of(PAZAlmanacs.EXPLODE_DAMAGE, this.getExplodeDamage()),
				Pair.of(PAZAlmanacs.EXPLODE_DAMAGE, this.getExplodeRange()),
				Pair.of(PAZAlmanacs.PREPARE_CD, this.getPrepareCD())
		));
	}

	@Override
	public void performAttack(LivingEntity target1) {
		if(! this.level.isClientSide) {
			final float range = 1.6F;
			final AABB aabb = EntityUtil.getEntityAABB(this, range, range);
			EntityUtil.getWholeTargetableEntities(this, aabb).forEach(target -> {
				target.hurt(PVZEntityDamageSource.explode(this), this.getExplodeDamage());
			});
			PVZPlantEntity.clearLadders(this, aabb);
			EntityUtil.playSound(this, SoundRegister.POTATO_MINE.get());
			for(int i = 1; i <= 10; ++ i) {
				EntityUtil.spawnParticle(this, 3);
				EntityUtil.spawnParticle(this, 4);
			}
			this.remove(RemovalReason.KILLED);
		}
	}

	@Override
	public boolean canPAZTarget(Entity target) {
		if(target instanceof DiggerZombieEntity) {
			return true;
		}
		return super.canPAZTarget(target);
	}

	@Override
	public boolean canStartSuperMode() {
		return super.canStartSuperMode() && this.getAttackTime() <= 0;
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		shootPotatos();
		if(! this.isRisingFromDirt() && ! this.isMineReady()) {
			this.setRisingFromDirt();
		}
	}

	/**
	 * shoot some potato to the sky
	 */
	protected void shootPotatos() {
		int num = this.getShootNum();
		for(int i = 1; i <= num; ++ i) {
			PotatoEntity potato = new PotatoEntity(level, this);
			potato.setPos(this.getX(), this.getY() + 1, this.getZ());
		    final float dx = MathUtil.getRandomFloat(getRandom()) * i / 2;
		    final float dy = 0.5F;
		    final float dz = MathUtil.getRandomFloat(getRandom()) * i / 2;
		    potato.shoot(dx, dy, dz);
		    potato.summonByOwner(this);
		    this.level.addFreshEntity(potato);
		}
	}

	public int getShootNum() {
		return 3;
	}

	public float getExplodeRange(){
		return 1.8F;
	}

	public float getExplodeDamage(){
		return this.getSkillValue(SkillTypes.NORMAL_BOMB_DAMAGE);
	}

	public int getPrepareCD(){
		return (int) this.getSkillValue(SkillTypes.MINE_FAST_PREPARE);
	}

	/**
	 */
	public boolean isMineReady() {
		return this.getExistTick() > this.getPrepareCD();
	}

	/**
	 */
	public boolean isRisingFromDirt() {
		return this.getExistTick() >= this.getPrepareCD() - RISING_ANIM_CD && this.getExistTick() <= this.getPrepareCD();
	}


	public void setRisingFromDirt() {
		this.setExistTick(this.getPrepareCD() - RISING_ANIM_CD - 2);
	}



	@Override
	protected boolean canBeImmuneToEnforce(Entity entity) {
		return super.canBeImmuneToEnforce(entity) && (this.isMineReady() || this.isRisingFromDirt());
	}

	@Override
	public boolean canCheckDistance() {
		return this.isMineReady();
	}


	public int getSignChangeCD(){
		return this.isMineReady() ? 10 : 20;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return new EntityDimensions(0.6f, 0.4f, false);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.POTATO_MINE;
	}

}
