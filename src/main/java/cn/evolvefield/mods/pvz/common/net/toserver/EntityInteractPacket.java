package cn.evolvefield.mods.pvz.common.net.toserver;

import com.hungteen.pvz.common.entity.plant.explosion.CobCannonEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EntityInteractPacket {
	private int type;
	private int op;
	private int num;

	public EntityInteractPacket(int type, int op, int num) {
		this.type = type;
		this.op = op;
		this.num = num;
	}

	public EntityInteractPacket(FriendlyByteBuf buffer) {
		this.type = buffer.readInt();
		this.op = buffer.readInt();
		this.num = buffer.readInt();
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(this.type);
		buffer.writeInt(this.op);
		buffer.writeInt(this.num);
	}

	public static class Handler {
		public static void onMessage(EntityInteractPacket message, Supplier<NetworkEvent.Context> ctx) {
			final ServerPlayer player = ctx.get().getSender();
			ctx.get().enqueueWork(()->{
		    	Entity entity = player.level.getEntity(message.type);
		    	if(entity instanceof CobCannonEntity) {
		    		CobCannonEntity cob = (CobCannonEntity) entity;
		    		cob.checkAndAttack();
		    	}
		    });
		    ctx.get().setPacketHandled(true);
	    }
	}

}
