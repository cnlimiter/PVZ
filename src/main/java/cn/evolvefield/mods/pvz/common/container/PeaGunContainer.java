package cn.evolvefield.mods.pvz.common.container;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.common.item.tool.plant.PeaGunItem;
import cn.evolvefield.mods.pvz.init.misc.PVZItemTags;
import cn.evolvefield.mods.pvz.init.registry.ContainerRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PeaGunContainer extends AbstractContainerMenu {

	private Container backpack;
	private final Player player;
	private final ItemStack stack;

	public PeaGunContainer(int id, Player player) {
		super(ContainerRegister.PEA_GUN.get(), id);
		this.player = player;
		this.stack = this.player.getOffhandItem();
		if(stack.getItem() != ItemRegister.PEA_GUN.get()) {
			Static.LOGGER.debug("ERROR OFFHAND ITEM !");
			return ;
		}
		backpack = PeaGunItem.getInventory(stack);

		//special slots
		this.addSlot(new Slot(backpack, 0, 80, 21) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return PeaGunItem.isValidMode(stack);
			}
		});

		for(int i = 0; i < 3; ++ i) {
			for(int j = 0; j < 9; ++ j) {
				this.addSlot(new Slot(backpack, j + i * 9 + 1, 8 + 18 * j, 45 + 18 * i) {
					@Override
					public boolean mayPlace(ItemStack stack) {
						return stack.is(PVZItemTags.PEA_GUN_BULLETS);
					}
				});
			}
		}

		for(int i = 0;i < 3; ++ i) {
			for(int j = 0; j < 9; ++ j) {
				this.addSlot(new Slot(player.getInventory(), j + i * 9 + 9, 8 + 18 * j, 105 + 18 * i));
			}
		}

		for(int i = 0; i < 9; ++ i) {
			this.addSlot(new Slot(player.getInventory(), i, 8 + 18 * i, 163));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if(index == 0) {
				if (! this.moveItemStackTo(itemstack1, 1, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (index <= PeaGunItem.PEA_GUN_SLOT_NUM) {
				if (! this.moveItemStackTo(itemstack1, PeaGunItem.PEA_GUN_SLOT_NUM + 1, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (index <= PeaGunItem.PEA_GUN_SLOT_NUM + 27) {
				if(! moveItemStackTo(itemstack1, 0, PeaGunItem.PEA_GUN_SLOT_NUM + 1, false)
						&& ! moveItemStackTo(itemstack1, PeaGunItem.PEA_GUN_SLOT_NUM + 27 + 1, this.slots.size(), false)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (! this.moveItemStackTo(itemstack1, 0, PeaGunItem.PEA_GUN_SLOT_NUM + 27 + 1, false)) {
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
		if(playerIn.getOffhandItem().getItem() != ItemRegister.PEA_GUN.get()) {
			return false;
		}
		return true;
	}

}
