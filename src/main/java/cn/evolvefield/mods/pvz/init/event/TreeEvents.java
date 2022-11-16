package cn.evolvefield.mods.pvz.init.event;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.init.config.PVZConfig;
import cn.evolvefield.mods.pvz.init.registry.BlockRegister;
import cn.evolvefield.mods.pvz.utils.MathUtil;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= Static.MOD_ID)
public class TreeEvents {

	@SubscribeEvent
	public static void onTreeGrow(SaplingGrowTreeEvent ev) {
		if(! ev.getLevel().isClientSide()) {
			final boolean isBlocked = ! ev.getLevel().isEmptyBlock(ev.getPos().above());
			if(PVZConfig.COMMON_CONFIG.RuleSettings.AllowNaturalTurnOrigin.get() || isBlocked) {
				final double chance = PVZConfig.COMMON_CONFIG.BlockSettings.SaplingTurnChance.get();
				if(MathUtil.randDouble(ev.getRandomSource(), isBlocked ? chance * 1.2 : chance) && ev.getPos().getY() > 2) {
					ev.getLevel().setBlock(ev.getPos().below(), BlockRegister.ORIGIN_ORE.get().defaultBlockState(), 3);
					ev.setResult(Result.DENY);
				}
			}
		}
	}

}
