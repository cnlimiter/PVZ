package cn.evolvefield.mods.pvz;

import cn.evolvefield.mods.pvz.common.advancement.AdvancementHandler;
import cn.evolvefield.mods.pvz.common.block.cubes.OriginBlock;
import cn.evolvefield.mods.pvz.common.datapack.PVZDataPackManager;
import cn.evolvefield.mods.pvz.common.world.biome.BiomeRegister;
import cn.evolvefield.mods.pvz.common.world.feature.GenStructures;
import cn.evolvefield.mods.pvz.init.config.PVZConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;


import static cn.evolvefield.mods.pvz.Static.PROXY;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Static.MOD_ID)
public class Pvz {


    public Pvz() {
        {
            final Pair<PVZConfig.Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(PVZConfig.Common::new);
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, specPair.getRight());
            PVZConfig.COMMON_CONFIG = specPair.getLeft();
        }
        {
            final Pair<PVZConfig.Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(PVZConfig.Client::new);
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, specPair.getRight());
            PVZConfig.CLIENT_CONFIG = specPair.getLeft();
        }

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        RegistryHandler.deferredRegister(modBus);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(EventPriority.NORMAL, GenStructures::addDimensionalSpacing);
        forgeBus.addListener(EventPriority.HIGH, BiomeRegister::biomeModification);
        forgeBus.addListener(EventPriority.NORMAL, PVZDataPackManager::addReloadListenerEvent);

        AdvancementHandler.init();
        RegistryHandler.coreRegister();

        PROXY.init();
    }

    @SubscribeEvent
    public static void setUpComplete(FMLLoadCompleteEvent event) {
        PROXY.postInit();
    }

    @SubscribeEvent
    public static void setUp(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            PROXY.setUp();
            RegistryHandler.setUp(event);
            OriginBlock.updateRadiationMap();
        });
    }

    @SubscribeEvent
    public static void setUpClient(FMLClientSetupEvent event) {
        PROXY.setUpClient();
    }


}
