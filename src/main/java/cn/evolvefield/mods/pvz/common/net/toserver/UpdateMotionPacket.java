package cn.evolvefield.mods.pvz.common.net.toserver;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateMotionPacket {

	private int type;
	private double x;
	private double y;
	private double z;

	public UpdateMotionPacket(int type, double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
	}

	public UpdateMotionPacket(FriendlyByteBuf buffer) {
		this.type = buffer.readInt();
		this.x = buffer.readDouble();
		this.y = buffer.readDouble();
		this.z = buffer.readDouble();
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(this.type);
		buffer.writeDouble(x);
		buffer.writeDouble(y);
		buffer.writeDouble(z);
	}

	public static class Handler {
		public static void onMessage(UpdateMotionPacket message, Supplier<NetworkEvent.Context> ctx) {
			final ServerPlayer player = ctx.get().getSender();
			ctx.get().enqueueWork(()->{
		    	Entity entity = player.level.getEntity(message.type);
		    	if(entity != null) {
		    		entity.setDeltaMovement(message.x, message.y, message.z);
		    	}
		    });
		    ctx.get().setPacketHandled(true);
	    }
	}

}
