package cn.evolvefield.mods.pvz.common.entity.zombie.pool;

import cn.evolvefield.mods.pvz.common.entity.misc.drop.JewelEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.PoolZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.init.config.PVZConfig;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class YetiZombieEntity extends PVZZombieEntity {

	private final int DROP_JEWEL_NUM = 4;
	private final double [] DD = new double[]{- 0.5D, 0.5D, 0.5D, -0.5D};
	private int live_tick = 0;
	private boolean hasInvis = false;

	public YetiZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.canBeFrozen = false;
	}

	@Override
	public float getLife() {
		return 135;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ZombieUtil.LITTLE_LOW);
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ZombieUtil.WALK_LITTLE_FAST);
	}

	@Override
	public void normalZombieTick() {
		super.normalZombieTick();
		if(! level.isClientSide) {
			++ this.live_tick;
			if(this.live_tick > this.getYetiMaxLiveTick() / 2) {
				if(! this.hasInvis) {
				    this.hasInvis = true;
				    this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 2000, 2, false, true));
				}
				this.heal(0.5f);
			} else if(this.live_tick >= this.getYetiMaxLiveTick()) {
				this.remove(RemovalReason.KILLED);
			}
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		if(this.isMiniZombie()) return EntityDimensions.scalable(0.6F, 1.2F);
		return EntityDimensions.scalable(1f, 2.6f);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if(amount >= 100) {
			amount /= 10;
		}
		return super.hurt(source, amount);
	}

	@Override
	protected void spawnSpecialDrops() {
		for(int i = 0; i < this.DROP_JEWEL_NUM; ++ i) {
			JewelEntity jewel = EntityRegister.JEWEL.get().create(level);
		    EntityUtil.onEntitySpawn(level, jewel, blockPosition().offset(this.DD[i], getRandom().nextDouble(), this.DD[(i + 1) % this.DROP_JEWEL_NUM]));
		}
	}

	public int getYetiMaxLiveTick() {
		return PVZConfig.COMMON_CONFIG.EntitySettings.EntityLiveTick.YetiLiveTick.get();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("yeti_live_tick", this.live_tick);
		compound.putBoolean("yeti_invis", this.hasInvis);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("yeti_live_tick")) {
			this.live_tick = compound.getInt("yeti_live_tick");
		}
		if(compound.contains("yeti_invis")) {
			this.hasInvis = compound.getBoolean("yeti_invis");
		}
	}

	@Override
    public ZombieType getZombieType() {
	    return PoolZombies.YETI_ZOMBIE;
    }

}
