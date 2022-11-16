package cn.evolvefield.mods.pvz.common.net.toclient;

import cn.evolvefield.mods.pvz.common.datapack.ChallengeTypeLoader;
import cn.evolvefield.mods.pvz.common.datapack.LotteryTypeLoader;
import cn.evolvefield.mods.pvz.common.datapack.TransactionTypeLoader;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DatapackPacket {

	private String type;
	private String res;
	private String data;

	public DatapackPacket(String type, String res, String data) {
		this.type = type;
		this.res = res;
		this.data = data;
	}

	public DatapackPacket(FriendlyByteBuf buffer) {
		this.type = buffer.readUtf();
		this.res = buffer.readUtf();
		this.data = buffer.readUtf();
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(this.type);
		buffer.writeUtf(this.res);
		buffer.writeUtf(this.data);
	}

	public static class Handler {
		public static void onMessage(DatapackPacket message, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				final ResourceLocation resourceLocation = new ResourceLocation(message.res);
				final JsonElement jsonElement = new JsonParser().parse(message.data);

				if(message.type.equals(LotteryTypeLoader.NAME)){
					LotteryTypeLoader.updateResource(resourceLocation, jsonElement);
				} else if (message.type.equals(TransactionTypeLoader.NAME)){
					TransactionTypeLoader.updateResource(resourceLocation, jsonElement);
				} else if (message.type.equals(ChallengeTypeLoader.NAME)){
					ChallengeTypeLoader.updateResource(resourceLocation, jsonElement);
				}
			});
		    ctx.get().setPacketHandled(true);
	    }
	}
}
