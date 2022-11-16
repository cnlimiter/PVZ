package cn.evolvefield.mods.pvz.init.event;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.common.world.challenge.ChallengeManager;
import cn.evolvefield.mods.pvz.common.world.invasion.InvasionManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= Static.MOD_ID)
public class PVZWorldEvents {

	@SubscribeEvent
	public static void onWorldTick(TickEvent.LevelTickEvent ev) {
		if (ev.phase != TickEvent.Phase.END || ev.level.isClientSide) {
			return;
		}
		ChallengeManager.tickChallenges(ev.level);
		if(ev.level.dimension() == Level.OVERWORLD) {
			InvasionManager.tick(ev);
		}
	}

}
