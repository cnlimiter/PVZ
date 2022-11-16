package cn.evolvefield.mods.pvz.common.entity.plant.base;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.util.IPult;
import cn.evolvefield.mods.pvz.common.entity.ai.goal.attack.PultAttackGoal;
import cn.evolvefield.mods.pvz.common.entity.ai.goal.target.PVZNearestTargetGoal;
import cn.evolvefield.mods.pvz.common.entity.bullet.PultBulletEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class PlantPultEntity extends PVZPlantEntity implements IPult {

	protected boolean isSuperOut = false;

	public PlantPultEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new PultAttackGoal(this));
		this.targetSelector.addGoal(0, new PVZNearestTargetGoal(this, true, false, getPultRange(), this.getPultHeight()));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(this.getPultRange());
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		if(! level.isClientSide && this.getAttackTime() > 0) {
			this.setAttackTime(this.getAttackTime() - 1);
			if(this.getAttackTime() == this.getPultAnimTime() / 2) {
				if(this.isPlantInSuperMode() && ! this.isSuperOut) {
					this.isSuperOut = true;
					this.superAttack();
				} else {
					this.pultBullet();
				}
			}
		}
	}

	public void performPult(LivingEntity target1){
		Optional.ofNullable(target1).ifPresent(target -> {
			PultBulletEntity bullet = this.createBullet();
			bullet.setPos(this.getX(), this.getY() + 1.7f, this.getZ());
			bullet.shootPultBullet(target);
			bullet.summonByOwner(this);
			bullet.setAttackDamage(this.isPlantInSuperMode() ? this.getSuperDamage() : this.getAttackDamage());
	        this.level.addFreshEntity(bullet);
	        EntityUtil.playSound(this, SoundRegister.PULT_THROW.get());
		});
	}

	protected void superAttack() {
		final float range = this.getSuperRange();
		EntityUtil.getTargetableLivings(this, EntityUtil.getEntityAABB(this, range, range)).forEach(this::doSuperAttackToTarget);
	}

	@Override
	public void pultBullet() {
		this.performPult(this.getTarget());
	}

	/**
	 * {@link #superAttack()}
	 */
	protected void doSuperAttackToTarget(LivingEntity target) {
		this.performPult(target);
	}

	/**
	 * {@link #performPult(LivingEntity))}
	 */
	protected abstract PultBulletEntity createBullet();

	/**
	 * {@link #performPult(LivingEntity))}
	 */
	public abstract float getSuperDamage();

	@Override
	public boolean shouldPult() {
		return this.canNormalUpdate();
	}

	@Override
	public boolean canPAZTarget(Entity entity) {
		return this.checkY(entity) && super.canPAZTarget(entity);
	}

	protected boolean checkY(Entity target) {
		return this.getY() + this.getPultHeight() >= target.getY() + target.getBbHeight();
	}

	@Override
	public void startPultAttack() {
		this.setAttackTime(this.getPultAnimTime());
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		this.isSuperOut = false;
		this.startPultAttack();
	}

	public int getPultAnimTime() {
		return 20;
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.addAll(Arrays.asList(
				Pair.of(PAZAlmanacs.BULLET_DAMAGE, this.getAttackDamage()),
				Pair.of(PAZAlmanacs.ATTACK_CD, this.getPultCD()),
				Pair.of(PAZAlmanacs.ATTACK_RANGE, this.getPultRange())
		));
	}

	public abstract float getAttackDamage();

	@Override
	public int getPultCD() {
		return 60;
	}

	@Override
	public int getSuperTimeLength() {
		return 60;
	}

	public float getSuperRange() {
		return 15;
	}

	@Override
	public float getPultRange() {
		return 30;
	}

	/**
	 * max target height.
	 */
	public float getPultHeight() {
		return 15;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("is_plant_super_out")) {
			this.isSuperOut = compound.getBoolean("is_plant_super_out");
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("is_plant_super_out", this.isSuperOut);
	}

}
