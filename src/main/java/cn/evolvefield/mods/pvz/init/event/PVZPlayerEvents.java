package cn.evolvefield.mods.pvz.init.event;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.enums.Resources;
import cn.evolvefield.mods.pvz.common.cap.CapabilityHandler;
import cn.evolvefield.mods.pvz.common.datapack.PVZDataPackManager;
import cn.evolvefield.mods.pvz.common.item.tool.plant.BowlingGloveItem;
import cn.evolvefield.mods.pvz.common.item.tool.plant.PeaGunItem;
import cn.evolvefield.mods.pvz.common.world.invasion.InvasionManager;
import cn.evolvefield.mods.pvz.init.event.events.SummonCardUseEvent;
import cn.evolvefield.mods.pvz.init.event.handler.PlayerEventHandler;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.CompatUtil;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import com.hungteen.pvz.PVZMod;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= Static.MOD_ID)
public class PVZPlayerEvents {

	@SubscribeEvent
	public static void tickPlayer(TickEvent.PlayerTickEvent ev) {
		if(! ev.player.level.isClientSide) {
			if (ev.player.tickCount < 2) {
				PlayerUtil.getOptManager(ev.player).ifPresent(l -> l.loadSummonCardCDs());
			}
			PeaGunItem.checkHeadShoot(ev.player);
			ev.player.getCapability(CapabilityHandler.PLAYER_DATA_CAPABILITY).ifPresent((l) -> {
				if (l.getEntityData().getOtherStats().playSoundTick > 0) {
					--l.getEntityData().getOtherStats().playSoundTick;
				}
			});
		}
		Static.PROXY.climbUp();
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent ev) {
		if (! ev.getEntity().level.isClientSide) {
			PlayerEventHandler.onPlayerLogin(ev.getEntity());

			InvasionManager.addPlayer(ev.getEntity());

			PlayerEventHandler.unLockPAZs(ev.getEntity());

			//sync to client data pack.
			PVZDataPackManager.sendSyncPacketsTo(ev.getEntity());
		}
	}

	@SubscribeEvent
	public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent ev) {
		if (! ev.getEntity().level.isClientSide) {
			PlayerEventHandler.onPlayerLogout(ev.getEntity());

			InvasionManager.removePlayer(ev.getEntity());
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone ev) {
		PlayerEventHandler.clonePlayerData(ev.getOriginal(), ev.getEntity(), ev.isWasDeath());
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent ev) {
		if(! ev.getEntity().level.isClientSide) {
			PlayerUtil.getOptManager(ev.getEntity()).ifPresent(l -> l.syncToClient());
		}
	}

	@SubscribeEvent
	public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent ev) {
		if(! ev.getEntity().level.isClientSide) {
			PlayerUtil.getOptManager(ev.getEntity()).ifPresent(l -> l.syncToClient());
		}
	}

	@SubscribeEvent
	public static void onPlayerInteractSpec(PlayerInteractEvent.EntityInteractSpecific ev) {
		if(! ev.getLevel().isClientSide){
			if(ev.getHand() == InteractionHand.MAIN_HAND) {
				PlayerEventHandler.quickRemoveByPlayer(ev.getEntity(), ev.getTarget(), ev.getEntity().getMainHandItem());
				PlayerEventHandler.makeSuperMode(ev.getEntity(), ev.getTarget(), ev.getEntity().getMainHandItem());
			}
		}
		BowlingGloveItem.onPickUp(ev);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void banBucket(PlayerInteractEvent.EntityInteractSpecific ev) {
		if(! CompatUtil.canBucketEntity(ev.getEntity().level, ev.getTarget(), ev.getItemStack())){
			ev.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onPlayerTreeLevelUp(PlayerXpEvent.LevelChange ev) {
		if (!ev.getEntity().level.isClientSide && ev.isLevelUp()) {
			PlayerEventHandler.unLockPAZs(ev.getEntity());
			PlayerUtil.playClientSound(ev.getEntity(), SoundRegister.PLANT_GROW.get());
			PlayerUtil.addResource(ev.getEntity(), Resources.LOTTERY_CHANCE, 3);
		}
	}

	@SubscribeEvent
	public static void onSummonCardUse(SummonCardUseEvent ev) {
//		Player player = ev.getEntity();
//		if(! player.level.isClientSide) { //unlock almanac
//			SearchOption a = null;
//			if(ev.getHeldStack().getItem() instanceof PlantCardItem) {// unlock plant card
//			    IPlantType plant = ((PlantCardItem) ev.getHeldStack().getItem()).plantType;
//			    a = SearchOption.get(plant);
//			}
//			PlayerUtil.unLockAlmanac(player, a);
//		}
	}

}
