package cn.evolvefield.mods.pvz.common.net.toserver;

import cn.evolvefield.mods.pvz.common.cap.CapabilityHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PVZMouseScrollPacket {

	private double data;

	public PVZMouseScrollPacket(double data) {
		this.data = data;
	}

	public PVZMouseScrollPacket(FriendlyByteBuf buffer) {
		this.data = buffer.readDouble();
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeDouble(this.data);
	}

	public static class Handler {
		public static void onMessage(PVZMouseScrollPacket message, Supplier<NetworkEvent.Context> ctx) {
		    ctx.get().enqueueWork(() -> {
		    	final ServerPlayer player = ctx.get().getSender();
		    	player.getCapability(CapabilityHandler.PLAYER_DATA_CAPABILITY).ifPresent(l -> {
		    		if(message.data == 0) {
		    			l.getPlayerData().onSwitchCard();
		    		} else {
		    			l.getPlayerData().onScrollInventory(message.data);
		    		}
		    	});
		    });
		    ctx.get().setPacketHandled(true);
	    }
	}

}
