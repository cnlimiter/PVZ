package cn.evolvefield.mods.pvz.common.container.shop;

import cn.evolvefield.mods.pvz.api.enums.Resources;
import cn.evolvefield.mods.pvz.common.container.ContainerRegister;
import cn.evolvefield.mods.pvz.common.entity.npc.AbstractDaveEntity;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import net.minecraft.world.entity.player.Player;

public class SunShopContainer extends AbstractDaveShopContainer {

	public SunShopContainer(int id, Player player, int entityId) {
		super(ContainerRegister.SUN_SHOP.get(), id, player, entityId);
	}

	@Override
	public void buyGood(AbstractDaveEntity.GoodType good) {
		super.buyGood(good);
		PlayerUtil.addResource(player, Resources.SUN_NUM, - good.getGoodPrice());
		PlayerUtil.playClientSound(player, SoundRegister.DAVE_HAPPY.get());
	}

}
