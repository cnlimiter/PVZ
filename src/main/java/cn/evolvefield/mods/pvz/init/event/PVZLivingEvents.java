package cn.evolvefield.mods.pvz.init.event;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.common.entity.AbstractPAZEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.magic.StrangeCatEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.event.handler.LivingEventHandler;
import cn.evolvefield.mods.pvz.init.event.handler.PlayerEventHandler;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Static.MOD_ID)
public class PVZLivingEvents {

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent ev) {
		/* handle player or its creature kill entity */
		if(! ev.getEntity().level.isClientSide) {
			Player player = EntityUtil.getEntityOwner(ev.getEntity().level, ev.getSource().getEntity());
			if(player == null) { //true source has no owner
				if(ev.getSource().getEntity() instanceof Player) {
					PlayerEventHandler.onPlayerKillEntity((Player) ev.getSource().getEntity(), ev.getSource(), ev.getEntity());
				}
			} else {
				PlayerEventHandler.onPlayerKillEntity(player, ev.getSource(), ev.getEntity());
				CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger((ServerPlayer) player, ev.getEntity(), ev.getSource());
			}
		}

		/* handle player death */
		if(ev.getEntity() instanceof Player) {
		    PlayerEventHandler.handlePlayerDeath(ev, (Player) ev.getEntity());
		}

		/* strange cat copy */
		StrangeCatEntity.handleCopyCat(ev);
	}

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent ev) {
		if(! ev.getEntity().level.isClientSide) {
			AbstractPAZEntity.damageOuterDefence(ev);
			if(ev.getSource() instanceof PVZEntityDamageSource) {
				ev.getEntity().invulnerableTime = 0;
				LivingEventHandler.handleHurtEffects(ev.getEntity(), (PVZEntityDamageSource) ev.getSource());
				LivingEventHandler.handleHurtSounds(ev.getEntity(), (PVZEntityDamageSource) ev.getSource());
			}
			LivingEventHandler.handleHurtDamage(ev);
		}
	}

	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent ev) {
		AbstractPAZEntity.damageInnerDefence(ev);
	}

}
