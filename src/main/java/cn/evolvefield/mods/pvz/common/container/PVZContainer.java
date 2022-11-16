package cn.evolvefield.mods.pvz.common.container;

import net.minecraft.entity.player.Player;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.MenuType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public abstract class PVZContainer extends AbstractContainerMenu {

	protected PVZContainer(MenuType<?> type, int id) {
		super(type, id);
	}

	/**
	 * default offset.
	 */
	public void addInventoryAndHotBar(Player player, int leftX, int leftY) {
		this.addPlayerInventory(player, leftX, leftY);
		this.addPlayerHotBar(player, leftX, leftY + 58);
	}

	public void addPlayerInventory(Player player, int leftX, int leftY) {
		for(int i = 0; i < 3; ++ i) {
			for(int j = 0; j < 9; ++ j) {
				this.addSlot(new Slot(player.getInventory(), j + i * 9 + 9, leftX + 18 * j, leftY + 18 * i));
			}
		}
	}

	public void addPlayerHotBar(Player player, int leftX, int leftY) {
		for(int i = 0; i < 9; ++ i) {
			this.addSlot(new Slot(player.getInventory(), i, leftX + 18 * i, leftY));
		}
	}

}
