package cn.evolvefield.mods.pvz.common.container.inventory;

import net.minecraft.entity.player.Player;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;

public class ItemInventory extends SimpleContainer {

	private static final String NAME = "BackPack";
	private final ItemStack stack;

	public ItemInventory(ItemStack stack, int size) {
		super(size);
		this.stack = stack;
		ListTag list = new ListTag();
		if(!stack.isEmpty() && stack.getOrCreateTag().contains(NAME)) {
			list = stack.getOrCreateTag().getList(NAME, Tag.TAG_COMPOUND);
		}
		for (int i = 0; i < size && i < list.size(); i++) {
			setItem(i, ItemStack.of(list.getCompound(i)));
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return !stack.isEmpty();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		var list = new ListTag();
		for (int i = 0; i < getContainerSize(); i++) {
			list.add(getItem(i).save(new CompoundTag()));
		}
		this.stack.getOrCreateTag().put(NAME, list);
	}
}
