package cn.evolvefield.mods.pvz.init.event;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.common.world.invasion.InvasionManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Static.MOD_ID)
public class PVZServerEvents {

	@SubscribeEvent
    public static void serverInit(ServerStartingEvent ev) {
    	var world = ev.getServer().getLevel(Level.OVERWORLD);
    	InvasionManager.syncStartInvasionCache(world);
    }

    @SubscribeEvent
    public static void serverShutDown(ServerStoppingEvent ev) {
    	var world = ev.getServer().getLevel(Level.OVERWORLD);
    	InvasionManager.syncEndInvasionCache(world);
    }

}
