package cn.evolvefield.mods.pvz.common.net;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.common.net.toclient.*;
import cn.evolvefield.mods.pvz.common.net.toserver.ClickButtonPacket;
import cn.evolvefield.mods.pvz.common.net.toserver.EntityInteractPacket;
import cn.evolvefield.mods.pvz.common.net.toserver.PVZMouseScrollPacket;
import cn.evolvefield.mods.pvz.common.net.toserver.UpdateMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PVZPacketHandler {

	private static final ResourceLocation CHANNEL_NAME = new ResourceLocation(Static.MOD_ID + ":networking");
	private static final String PROTOCOL_VERSION = "1.0";

	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			CHANNEL_NAME,
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	public static void init() {
		int id = 0;
		CHANNEL.registerMessage(id ++, PlayerStatsPacket.class, PlayerStatsPacket::encode, PlayerStatsPacket::new, PlayerStatsPacket.Handler::onMessage);
		CHANNEL.registerMessage(id ++, OpenGuiPacket.class, OpenGuiPacket::encode, OpenGuiPacket::new, OpenGuiPacket.Handler::onMessage);
		CHANNEL.registerMessage(id ++, ClickButtonPacket.class, ClickButtonPacket::encode, ClickButtonPacket::new, ClickButtonPacket.Handler::onMessage);
		CHANNEL.registerMessage(id ++, PlaySoundPacket.class, PlaySoundPacket::encode, PlaySoundPacket::new, PlaySoundPacket.Handler::onMessage);
		CHANNEL.registerMessage(id ++, SpawnParticlePacket.class, SpawnParticlePacket::encode, SpawnParticlePacket::new, SpawnParticlePacket.Handler::onMessage);
		CHANNEL.registerMessage(id ++, OtherStatsPacket.class, OtherStatsPacket::encode, OtherStatsPacket::new, OtherStatsPacket.Handler::onMessage);
		CHANNEL.registerMessage(id ++, UpdateMotionPacket.class, UpdateMotionPacket::encode, UpdateMotionPacket::new, UpdateMotionPacket.Handler::onMessage);
		CHANNEL.registerMessage(id ++, EntityInteractPacket.class, EntityInteractPacket::encode, EntityInteractPacket::new, EntityInteractPacket.Handler::onMessage);
		CHANNEL.registerMessage(id ++, PVZMouseScrollPacket.class, PVZMouseScrollPacket::encode, PVZMouseScrollPacket::new, PVZMouseScrollPacket.Handler::onMessage);
		CHANNEL.registerMessage(id ++, CardInventoryPacket.class, CardInventoryPacket::encode, CardInventoryPacket::new, CardInventoryPacket.Handler::onMessage);
		CHANNEL.registerMessage(id ++, PAZStatsPacket.class, PAZStatsPacket::encode, PAZStatsPacket::new, PAZStatsPacket.Handler::onMessage);
		CHANNEL.registerMessage(id ++, DatapackPacket.class, DatapackPacket::encode, DatapackPacket::new, DatapackPacket.Handler::onMessage);

	}

}
