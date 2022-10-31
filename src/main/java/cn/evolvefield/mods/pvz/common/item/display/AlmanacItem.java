package cn.evolvefield.mods.pvz.common.item.display;

import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class AlmanacItem extends Item {

	public AlmanacItem() {
		super(new Properties().tab(PVZItemGroups.PVZ_USEFUL).stacksTo(1));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		if(!worldIn.isClientSide && playerIn instanceof ServerPlayer) {
			NetworkHooks.openScreen((ServerPlayer) playerIn, new SimpleMenuProvider() {

				@Override
				public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
					return new AlmanacContainer(id, player);
				}

				@Override
				public Component getDisplayName() {
					return Component.translatable("gui.pvz.almanac.show");
				}
			});
		}
		return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
	}

}
