package cn.evolvefield.mods.pvz.common.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenGuiPacket {

	private int type;

	public OpenGuiPacket(int type) {
		this.type = type;
	}

	public OpenGuiPacket(FriendlyByteBuf buffer) {
		this.type = buffer.readInt();
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(this.type);
	}

	public static class Handler {
		public static void onMessage(OpenGuiPacket message, Supplier<NetworkEvent.Context> ctx) {
//			final ServerPlayerEntity player = ctx.get().getSender();
//			ctx.get().enqueueWork(() -> {
//				switch (Guis.values()[message.type]) {
//				case PLAYER_INVENTORY: {
//					NetworkHooks.openGui(player, new INamedContainerProvider() {
//
//						@Override
//						public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_,
//								PlayerEntity p_createMenu_3_) {
//							return new PlayerInventoryContainer(p_createMenu_1_, p_createMenu_3_);
//						}
//
//						@Override
//						public ITextComponent getDisplayName() {
//							return new TranslationTextComponent("gui.pvz.player_inventory.show");
//						}
//					});
//					return;
//				}
//				default: {
//					PVZMod.LOGGER.debug("GUI ID ERROR!");
//				}
//				}
//			});
			ctx.get().setPacketHandled(true);
		}
	}
}
