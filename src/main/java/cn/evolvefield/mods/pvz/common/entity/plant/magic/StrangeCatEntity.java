package cn.evolvefield.mods.pvz.common.entity.plant.magic;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.ai.goal.target.PVZNearestTargetGoal;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.MemePlants;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.config.PVZConfig;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.PlantUtil;
import com.hungteen.pvz.common.event.PVZLivingEvents;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.Arrays;
import java.util.List;
public class StrangeCatEntity extends PVZPlantEntity {

	public static final int REST_CD = 1000;
	public static final int ANIM_CD = 10;
	private int restTick = REST_CD;

	public StrangeCatEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.isImmuneToWeak = true;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(0, new PVZNearestTargetGoal(this, true, false, 5, 5));
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		if (! level.isClientSide) {
			if(EntityUtil.isEntityValid(this.getTarget())) {
				this.lookControl.setLookAt(this.getTarget(), 30f, 30f);
			}
			if(this.getAttackTime() > 0) {
				this.setAttackTime(this.getAttackTime() - 1);
			} else if(this.restTick == 0){
				this.setAttackTime(0);
				if(EntityUtil.isEntityValid(this.getTarget())) {
					this.performAttack(getTarget());
				}
			} else {
				this.setAttackTime(- 1);
			}
			this.restTick = Math.max(0, this.restTick - 1);
		}
	}

	/**
	 * deal damage
	 */
	protected void performAttack(LivingEntity target) {
		target.hurt(PVZEntityDamageSource.normal(this), this.getAttackDamage());
		EntityUtil.playSound(this, SoundRegister.BRUH.get());
		this.setAttackTime(ANIM_CD);
		this.restTick = REST_CD;
	}

	/**
	 * {@link PVZLivingEvents#onLivingDeath(LivingDeathEvent)}
	 */
	public static void handleCopyCat(final LivingDeathEvent ev) {
		if(! ev.getEntity().level.isClientSide && ev.getSource().getEntity() instanceof StrangeCatEntity) {
			final float range = 10F;
			final int count = ev.getEntity().level.getEntitiesOfClass(StrangeCatEntity.class, EntityUtil.getEntityAABB(ev.getEntity(), range, range)).size();
			if(count < PVZConfig.COMMON_CONFIG.EntitySettings.PlantSetting.StrangeCatCount.get()) {
				((StrangeCatEntity) ev.getSource().getEntity()).onSelfCopy(ev.getEntity());
			}
		}
	}

	public void onSelfCopy(LivingEntity target) {
		StrangeCatEntity cat = EntityRegister.STRANGE_CAT.get().create(level);
		PlantUtil.copyPlantData(cat, this);
		EntityUtil.onEntitySpawn(level, cat, target.blockPosition());
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		EntityUtil.playSound(this, SoundRegister.BRUH.get());
		this.setAttackTime(ANIM_CD);
		EntityUtil.getRandomLivingInRange(level, this, EntityUtil.getEntityAABB(this, 20, 20), getSuperAttackCount()).forEach((target) ->{
			target.hurt(PVZEntityDamageSource.normal(this), this.getAttackDamage());
		});
	}

	public boolean isResting() {
		return this.getAttackTime() < 0;
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.addAll(Arrays.asList(
				Pair.of(PAZAlmanacs.ATTACK_DAMAGE, this.getAttackDamage()),
				Pair.of(PAZAlmanacs.REST_TIME, REST_CD)
		));
	}

	/**
	 * max damage to target
	 */
	public float getAttackDamage() {
		return 200;
	}

	public int getSuperAttackCount() {
		return 4;
	}

	@Override
	public int getSuperTimeLength() {
		return 30;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return new EntityDimensions(0.8f, 1f, false);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("rest_tick", this.restTick);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("rest_tick")) {
			this.restTick = compound.getInt("rest_tick");
		}
	}

	@Override
	public IPlantType getPlantType() {
		return MemePlants.STRANGE_CAT;
	}

}
