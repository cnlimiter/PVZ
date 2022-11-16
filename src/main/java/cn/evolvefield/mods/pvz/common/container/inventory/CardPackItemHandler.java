package cn.evolvefield.mods.pvz.common.container.inventory;

import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import com.hungteen.pvz.utils.PlayerUtil;
import net.minecraft.entity.player.Player;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.ItemStackHandler;

import java.util.HashMap;

public class CardPackItemHandler extends ItemStackHandler {

	private HashMap<Integer, Integer> map = new HashMap<>();
	private final Player player;
	private final int slotNum;

	public CardPackItemHandler(Player player, int size) {
		super(size);
		this.player = player;
		this.slotNum = size;

		if(! player.level.isClientSide) {
			int pos = 0;
		    for (int i = 0; i <= this.slotNum; ++ i) {
			    if(i != PlayerUtil.getEmptyPos(this.player)) {
			    	this.map.put(pos, i);
				    this.setStackInSlot(pos, PlayerUtil.getItemStack(this.player, i));
				    ++ pos;
			    }
		     }
//		} else {
//			int pos = 0;
//		    for (int i = 0; i <= this.slotNum; ++ i) {
//			    if(i != ClientPlayerResources.emptySlot) {
//				    setItem(pos, ClientPlayerResources.SUMMON_CARDS.get(i));
//				    ++ pos;
//			    }
//		     }
		}
	}

	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);
//		for(int i = 0; i < this.getContainerSize(); ++ i) {
//			System.out.println(this.getItem(i));
//		}
		if(! player.level.isClientSide) {
			PlayerUtil.setItemStack(this.player, this.getStackInSlot(slot), this.map.get(slot));
		}
	}

}
