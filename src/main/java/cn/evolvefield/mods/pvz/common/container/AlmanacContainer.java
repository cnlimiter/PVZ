package cn.evolvefield.mods.pvz.common.container;

import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import com.hungteen.pvz.common.item.ItemRegister;
import net.minecraft.entity.player.Player;
import net.minecraft.inventory.container.Slot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AlmanacContainer extends AbstractOptionContainer {

	@SuppressWarnings("unused")
	private final Player player;

	public AlmanacContainer(int id, Player player) {
		super(ContainerRegister.ALMANAC.get(), id);
		this.player = player;
	}

	@Override
	public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
		return null;
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return playerIn.getMainHandItem().getItem()== ItemRegister.ALMANAC.get()
				|| playerIn.getOffhandItem().getItem()==ItemRegister.ALMANAC.get();
	}

	@Override
	public boolean isCraftSlot(Slot slot) {
		return false;
	}

}
