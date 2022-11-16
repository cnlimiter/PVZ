package cn.evolvefield.mods.pvz.init.event.handler;

import cn.evolvefield.mods.pvz.api.interfaces.paz.IPAZEntity;
import cn.evolvefield.mods.pvz.api.interfaces.paz.IZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.base.AbstractBossZombieEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.event.PVZLivingEvents;
import cn.evolvefield.mods.pvz.init.registry.EffectRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.ConfigUtil;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class LivingEventHandler {

	/**
	 * apply potion effects on living.
	 * {@link PVZLivingEvents#onLivingHurt(LivingHurtEvent)}
	 */
	public static void handleHurtEffects(LivingEntity target, PVZEntityDamageSource source) {
		if(! source.isDefended()) {//source not defended by armor.
			if(source.isFlameDamage()) {
				if(target.hasEffect(EffectRegister.COLD_EFFECT.get())){
				    target.removeEffect(EffectRegister.COLD_EFFECT.get());
				}
				if(target.hasEffect(EffectRegister.FROZEN_EFFECT.get())){
				    target.removeEffect(EffectRegister.FROZEN_EFFECT.get());
				}
			}
			source.getEffects().forEach(effect -> EntityUtil.addPotionEffect(target, effect));
		}
	}

	/**
	 * handle sound when living hurt.
	 * {@link PVZLivingEvents#onLivingHurt(LivingHurtEvent)}
	 */
	public static void handleHurtSounds(LivingEntity target, PVZEntityDamageSource source) {
		if(source.isEatDamage()) {
			EntityUtil.playSound(target, SoundRegister.CHOMP.get());
		}
		if(source.isFlameDamage() && source.isAppease()) {
			EntityUtil.playSound(target, SoundRegister.FLAME_HIT.get());
		}
	}

	/**
	 * balance the damage between different mods.
	 * {@link PVZLivingEvents#onLivingHurt(LivingHurtEvent)}
	 */
	public static void handleHurtDamage(final LivingHurtEvent ev) {
		//all paz entity can not deal more than limit damage to other entities.
		if(ev.getSource() instanceof PVZEntityDamageSource && ! (ev.getEntity() instanceof IPAZEntity && ev.getSource().getEntity() instanceof IPAZEntity)){
			ev.setAmount(Math.min(ConfigUtil.getLimitDamage(), ev.getAmount()));
		}
		//(not boss)zombie damage to zombie or both are boss entity.
		if(ev.getEntity() instanceof IZombieEntity && ev.getSource().getEntity() instanceof IZombieEntity && (! (ev.getSource().getEntity() instanceof AbstractBossZombieEntity) || (ev.getEntity() instanceof AbstractBossZombieEntity))){
			ev.setAmount(Math.min(100, ev.getAmount()));
		}
		//avoid instant kill mod.
		if(ev.getSource() != DamageSource.OUT_OF_WORLD && ev.getAmount() > ev.getEntity().getMaxHealth() * 0.8 && ev.getEntity() instanceof AbstractBossZombieEntity){
			ev.setAmount(ConfigUtil.getLimitDamage());
		}
	}

}
