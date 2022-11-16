package cn.evolvefield.mods.pvz.common.container;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.common.item.spawn.card.ImitaterCardItem;
import cn.evolvefield.mods.pvz.init.registry.ContainerRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ImitaterContainer extends AbstractContainerMenu {

	private Container backpack;
	private final Player player;
	private final ItemStack stack;

	public ImitaterContainer(int id, Player player) {
		super(ContainerRegister.IMITATER.get(), id);
		this.player = player;
		this.stack = this.player.getOffhandItem();
		if(stack.getItem() != ItemRegister.IMITATER_CARD.get()) {
			Static.LOGGER.debug("ERROR OFFHAND ITEM !");
			return ;
		}
		this.backpack = ImitaterCardItem.getInventory(this.stack);
		this.addSlot(new Slot(backpack, 0, 80, 20) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return ImitaterCardItem.isValidImitateSlot(stack);
			}
		});//special slots
		for(int i = 0; i < 3; ++ i) {
			for(int j = 0; j < 9; ++ j) {
				this.addSlot(new Slot(player.getInventory(), j + i * 9 + 9, 8 + 18 * j, 51 + 18 * i));
			}
		}
		for(int i = 0; i < 9; ++ i) {
			this.addSlot(new Slot(player.getInventory(), i, 8 + 18 * i, 109));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index == 0) {
				if (! this.moveItemStackTo(itemstack1, 1, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (index < 28) {
				if(! moveItemStackTo(itemstack1, 0, 1, false)
						&& ! moveItemStackTo(itemstack1, 28, this.slots.size(), false)) {
					return ItemStack.EMPTY;
				}
			} else if(index < this.slots.size()){
				if (! this.moveItemStackTo(itemstack1, 0, 28, false)) {
					return ItemStack.EMPTY;
				}
			}
			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		return itemstack;
	}

	@Override
	public boolean stillValid(Player playerIn) {
		if(playerIn.getOffhandItem().getItem() != ItemRegister.IMITATER_CARD.get()) {
			return false;
		}
		return true;
	}

}
