package cn.evolvefield.mods.pvz.common.entity.zombie.grass;

import cn.evolvefield.mods.pvz.common.entity.zombie.base.DefenceZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.body.ZombieDropBodyEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.part.PVZHealthPartEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.GrassZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EffectUtil;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.MathUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class NewspaperZombieEntity extends DefenceZombieEntity {

	public NewspaperZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
	}

	@Override
	public void resetParts() {
		removeParts();
		this.part = new PVZHealthPartEntity(this, 1f, 1f);
		this.part.setOwner(this);
	}

	@Override
	protected float getPartHeightOffset() {
		if(this.isMiniZombie()) return 0.2F;
		return 0.7f;
	}

	@Override
	public void onOuterDefenceBroken() {
		super.onOuterDefenceBroken();
		if(! this.level.isClientSide){
			this.addEffect(EffectUtil.effect(MobEffects.MOVEMENT_SPEED, 120000, 1));
			this.addEffect(EffectUtil.effect(MobEffects.DAMAGE_BOOST, 120000, MathUtil.getRandomMinMax(this.random, 1, this.getAngryLevel())));
			EntityUtil.playSound(this, SoundRegister.ZOMBIE_ANGRY.get());
		}
	}

	@Override
	public boolean canLostHand() {
		return super.canLostHand() && this.isAngry();
	}

	@Override
	protected void setBodyStates(ZombieDropBodyEntity body) {
		super.setBodyStates(body);
		body.setHandDefence(! this.isAngry());
	}

	@Override
	public SoundEvent getPartDeathSound() {
		return SoundRegister.PAPER_BROKEN.get();
	}

	@Override
	public float getWalkSpeed() {
		return ZombieUtil.WALK_LITTLE_SLOW;
	}

	public int getAngryLevel(){
		return 3;
	}

	@Override
	public float getLife() {
		return 22;
	}

	@Override
	public float getOuterLife() {
		return 10;
	}

	public boolean isAngry() {
		return ! this.canPartsExist();
	}

	@Override
	public ZombieType getZombieType() {
		return GrassZombies.NEWSPAPER_ZOMBIE;
	}

}
