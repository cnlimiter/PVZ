package cn.evolvefield.mods.pvz.common.net.toclient;

import com.hungteen.pvz.client.particle.ParticleRegister;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpawnParticlePacket {

	private int type;
	private double x;
	private double y;
	private double z;

	public SpawnParticlePacket(int type, double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
	}

	public SpawnParticlePacket(FriendlyByteBuf buffer) {
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
		@SuppressWarnings("resource")
		public static void onMessage(SpawnParticlePacket message, Supplier<NetworkEvent.Context> ctx) {
		    ctx.get().enqueueWork(()->{
		    	int id = message.type;
		    	ParticleOptions particle = null;
		    	if(id == 0) {
		    		particle = ParticleRegister.RED_BOMB.get();
		    	} else if(id == 1) {
		    		particle = ParticleRegister.MELON_SLICE.get();
		    	} else if(id == 2) {
		    		particle = ParticleRegister.FROZEN_MELON_SLICE.get();
		    	} else if(id == 3) {
		    		particle = ParticleRegister.DIRT_BURST_OUT.get();
		    	} else if(id == 4) {
		    		particle = ParticleRegister.YELLOW_BOMB.get();
		    	} else if(id == 5) {
		    		particle = ParticleTypes.EXPLOSION;
		    	} else if(id == 6) {
		    		particle = ParticleRegister.DIRT_BURST_OUT.get();
		    	} else if(id == 7) {
		    		particle = ParticleRegister.GREEN_SWEEP.get();
		    	} else if(id == 8) {
		    		particle = ParticleRegister.YELLOW_BOMB.get();
		    	} else if(id == 9) {
		    		particle = ParticleRegister.POP_CORN.get();
		    	}
		    	if(particle != null && Static.PROXY.getPlayer() != null) {
		    		Static.PROXY.getPlayer().level.addParticle(particle, message.x, message.y, message.z, 0, 0, 0);
		    	}
		    });
		    ctx.get().setPacketHandled(true);
	    }
	}

}
