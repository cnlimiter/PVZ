package cn.evolvefield.mods.pvz.init.event;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.init.config.PVZConfig;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= Static.MOD_ID)
public class PVZEntityEvents {

	@SubscribeEvent
	public static void onEntityJoinOverWorld(EntityJoinLevelEvent ev) {
		if(! PVZConfig.COMMON_CONFIG.RuleSettings.CanSpawnDefaultMonster.get()) {
			if(! ev.getEntity().level.isClientSide && ev.getLevel().dimension() == Level.OVERWORLD) {
			    if(! (ev.getEntity() instanceof PVZZombieEntity) && ev.getEntity() instanceof Monster) {
				    ev.setCanceled(true);
			    }
			}
		}
	}
}
