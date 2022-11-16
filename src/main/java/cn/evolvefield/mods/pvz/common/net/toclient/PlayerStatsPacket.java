package cn.evolvefield.mods.pvz.common.net.toclient;

import cn.evolvefield.mods.pvz.api.enums.Resources;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerStatsPacket{

	private int type;
	private int data;

	public PlayerStatsPacket(int x, int y) {
		this.type = x;
		this.data = y;
	}

	public PlayerStatsPacket(FriendlyByteBuf buffer) {
		this.type = buffer.readInt();
		this.data = buffer.readInt();
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(this.type);
		buffer.writeInt(this.data);
	}

	public static class Handler {
		public static void onMessage(PlayerStatsPacket message, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				PlayerUtil.setResource(Static.PROXY.getPlayer(), Resources.values()[message.type], message.data);
			});
		    ctx.get().setPacketHandled(true);
	    }
	}
}
