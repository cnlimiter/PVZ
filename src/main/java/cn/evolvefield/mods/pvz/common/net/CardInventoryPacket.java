package cn.evolvefield.mods.pvz.common.net;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.enums.Resources;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CardInventoryPacket{

	public static final String FLAG = "empty_slot";
	private final int pos;
	private final CompoundTag data;

	public CardInventoryPacket(int pos, CompoundTag data) {
		this.pos = pos;
		this.data = data;
	}

	public CardInventoryPacket(FriendlyByteBuf buffer) {
		this.pos = buffer.readInt();
		this.data = buffer.readNbt();
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(this.pos);
		buffer.writeNbt(this.data);
	}

	public static class Handler {
		public static void onMessage(CardInventoryPacket message, Supplier<NetworkEvent.Context> ctx) {
		    ctx.get().enqueueWork(() -> {
				if(ctx.get().getDirection().getReceptionSide().isClient()){
//					System.out.println("Server to Client");
					if(message.pos >= 0 && message.pos <= Resources.SLOT_NUM.max) {
						PlayerUtil.getOptManager(Static.PROXY.getPlayer()).ifPresent(l -> l.setItemAt(ItemStack.of(message.data), message.pos, false));
					} else {
						if(message.data.contains(FLAG)) {
							PlayerUtil.getOptManager(Static.PROXY.getPlayer()).ifPresent(l -> l.setCurrentPos(message.data.getInt(FLAG), false));
						} else {
							Static.LOGGER.error("Card Inventory Packet : receive wrong data !");
						}
					}
				} else{
//					System.out.println("Client to Server");
					if(message.pos >= 0 && message.pos <= Resources.SLOT_NUM.max) {
						PlayerUtil.getOptManager(ctx.get().getSender()).ifPresent(l -> l.setItemAt(ItemStack.of(message.data), message.pos, false));
					} else {
						if(message.data.contains(FLAG)) {
							PlayerUtil.getOptManager(ctx.get().getSender()).ifPresent(l -> l.setCurrentPos(message.data.getInt(FLAG), false));
						} else {
							Static.LOGGER.error("Card Inventory Packet : receive wrong data !");
						}
					}
				}
		    });
		    ctx.get().setPacketHandled(true);
	    }
	}
}
