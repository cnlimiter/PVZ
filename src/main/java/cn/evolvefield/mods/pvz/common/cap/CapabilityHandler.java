package cn.evolvefield.mods.pvz.common.cap;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.common.cap.player.IPlayerDataCapability;
import cn.evolvefield.mods.pvz.common.cap.player.PlayerDataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 14:10
 * Description:
 */
public class CapabilityHandler {
    public static final Capability<IPlayerDataCapability> PLAYER_DATA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void registerCapabilities(){
        MinecraftForge.EVENT_BUS.register(CapabilityHandler.class);
    }

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event){
        Entity entity = event.getObject();
        if (entity instanceof Player player){
            event.addCapability(new ResourceLocation(Static.MOD_ID, "player_data"), new PlayerDataProvider(player));
        }
    }
}
