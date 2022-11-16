package cn.evolvefield.mods.pvz.init.registry;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.common.container.*;
import cn.evolvefield.mods.pvz.common.container.shop.DaveShopContainer;
import cn.evolvefield.mods.pvz.common.container.shop.PennyShopContainer;
import cn.evolvefield.mods.pvz.common.container.shop.SunShopContainer;
import com.hungteen.pvz.PVZMod;
import com.hungteen.pvz.client.gui.screen.*;
import com.hungteen.pvz.client.gui.screen.shop.DaveShopScreen;
import com.hungteen.pvz.client.gui.screen.shop.PennyShopScreen;
import com.hungteen.pvz.client.gui.screen.shop.SunShopScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = Static.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ContainerRegister {

	public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Static.MOD_ID);

	public static final RegistryObject<MenuType<AlmanacContainer>> ALMANAC = CONTAINER_TYPES.register("almanac", () -> {
		return IForgeMenuType.create((windowId, inv, data) -> {
            return new AlmanacContainer(windowId, inv.player);
        });
	});

	public static final RegistryObject<MenuType<PeaGunContainer>> PEA_GUN = CONTAINER_TYPES.register("pea_gun", () -> {
		return IForgeMenuType.create((windowId, inv, data) -> {
            return new PeaGunContainer(windowId, inv.player);
        });
	});

	public static final RegistryObject<MenuType<DaveShopContainer>> DAVE_SHOP = CONTAINER_TYPES.register("dave_shop", () -> {
		return IForgeMenuType.create((windowId, inv, data) -> {
            return new DaveShopContainer(windowId, inv.player, data.readInt());
        });
	});

	public static final RegistryObject<MenuType<SunShopContainer>> SUN_SHOP = CONTAINER_TYPES.register("sun_shop", () -> {
		return IForgeMenuType.create((windowId, inv, data) -> {
            return new SunShopContainer(windowId, inv.player, data.readInt());
        });
	});

	public static final RegistryObject<MenuType<SunConverterContainer>> SUN_CONVERTER = CONTAINER_TYPES.register("sun_converter", () -> {
		return IForgeMenuType.create((windowId, inv, data) -> {
            return new SunConverterContainer(windowId, inv.player, data.readBlockPos());
        });
	});

	public static final RegistryObject<MenuType<FragmentSpliceContainer>> FRAGMENT_SPLICE = CONTAINER_TYPES.register("fragment_splice", () -> {
		return IForgeMenuType.create((windowId, inv, data) -> {
            return new FragmentSpliceContainer(windowId, inv.player, data.readBlockPos());
        });
	});

	public static final RegistryObject<MenuType<SlotMachineContainer>> SLOT_MACHINE = CONTAINER_TYPES.register("slot_machine", () -> {
		return IForgeMenuType.create((windowId, inv, data) -> {
            return new SlotMachineContainer(windowId, inv.player, data.readBlockPos());
        });
	});

	public static final RegistryObject<MenuType<PennyShopContainer>> PENNY_SHOP = CONTAINER_TYPES.register("penny_shop", () -> {
		return IForgeMenuType.create((windowId, inv, data) -> {
            return new PennyShopContainer(windowId, inv.player, data.readInt());
        });
	});

	public static final RegistryObject<MenuType<EssenceAltarContainer>> ESSENCE_ALTAR = CONTAINER_TYPES.register("essence_altar", () -> {
		return IForgeMenuType.create((windowId, inv, data) -> {
            return new EssenceAltarContainer(windowId, inv.player, data.readBlockPos());
        });
	});

	public static final RegistryObject<MenuType<CardFusionContainer>> CARD_FUSION = CONTAINER_TYPES.register("card_fusion", () -> {
		return IForgeMenuType.create((windowId, inv, data) -> {
            return new CardFusionContainer(windowId, inv.player, data.readBlockPos());
        });
	});

	public static final RegistryObject<MenuType<ImitaterContainer>> IMITATER = CONTAINER_TYPES.register("imitater", () -> {
		return IForgeMenuType.create((windowId, inv, data) -> {
            return new ImitaterContainer(windowId, inv.player);
        });
	});

	public static final RegistryObject<MenuType<CardPackContainer>> CARD_PACK = CONTAINER_TYPES.register("card_pack", () -> {
		return IForgeMenuType.create((windowId, inv, data) -> {
            return new CardPackContainer(windowId, inv.player);
        });
	});

	@SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        MenuScreens.register(ALMANAC.get(), AlmanacScreen::new);
        MenuScreens.register(PEA_GUN.get(), PeaGunScreen::new);
        MenuScreens.register(DAVE_SHOP.get(), DaveShopScreen::new);
        MenuScreens.register(SUN_SHOP.get(), SunShopScreen::new);
        MenuScreens.register(SUN_CONVERTER.get(), SunConverterScreen::new);
        MenuScreens.register(FRAGMENT_SPLICE.get(), FragmentSpliceScreen::new);
        MenuScreens.register(SLOT_MACHINE.get(), SlotMachineScreen::new);
        MenuScreens.register(PENNY_SHOP.get(), PennyShopScreen::new);
        MenuScreens.register(ESSENCE_ALTAR.get(), EssenceAltarScreen::new);
        MenuScreens.register(CARD_FUSION.get(), CardFusionScreen::new);
        MenuScreens.register(IMITATER.get(), ImitaterScreen::new);
        MenuScreens.register(CARD_PACK.get(), CardPackScreen::new);
    }

}
