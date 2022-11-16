package cn.evolvefield.mods.pvz.common.container.shop;

import cn.evolvefield.mods.pvz.api.enums.Resources;
import cn.evolvefield.mods.pvz.common.container.ContainerRegister;
import cn.evolvefield.mods.pvz.common.entity.npc.AbstractDaveEntity;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import com.hungteen.pvz.common.container.ContainerRegister;
import com.hungteen.pvz.common.entity.npc.AbstractDaveEntity;
import com.hungteen.pvz.common.misc.sound.SoundRegister;
import com.hungteen.pvz.utils.PlayerUtil;
import com.hungteen.pvz.utils.enums.Resources;
import net.minecraft.entity.player.Player;
import net.minecraft.world.entity.player.Player;

public class PennyShopContainer extends AbstractDaveShopContainer {

	public PennyShopContainer(int id, Player player, int entityId) {
		super(ContainerRegister.PENNY_SHOP.get(), id, player, entityId);
	}

	@Override
	public void buyGood(AbstractDaveEntity.GoodType good) {
		super.buyGood(good);
		PlayerUtil.addResource(player, Resources.GEM_NUM, - good.getGoodPrice());
		PlayerUtil.playClientSound(player, SoundRegister.DAVE_HAPPY.get());
	}

}
