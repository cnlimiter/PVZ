package cn.evolvefield.mods.pvz;

import cn.evolvefield.mods.pvz.common.cap.CapabilityHandler;
import cn.evolvefield.mods.pvz.common.effect.PotionRecipeHandler;
import cn.evolvefield.mods.pvz.common.effect.PotionRegister;
import cn.evolvefield.mods.pvz.common.impl.EssenceTypes;
import cn.evolvefield.mods.pvz.common.impl.RankTypes;
import cn.evolvefield.mods.pvz.common.impl.*;
import cn.evolvefield.mods.pvz.common.impl.plant.CustomPlants;
import cn.evolvefield.mods.pvz.common.impl.plant.MemePlants;
import cn.evolvefield.mods.pvz.common.impl.plant.OtherPlants;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.impl.zombie.*;
import cn.evolvefield.mods.pvz.common.item.misc.PVZSpawnEggItem;
import cn.evolvefield.mods.pvz.common.net.PVZPacketHandler;
import cn.evolvefield.mods.pvz.common.tileentity.TileEntityRegister;
import cn.evolvefield.mods.pvz.common.world.FeatureRegister;
import cn.evolvefield.mods.pvz.common.world.biome.BiomeRegister;
import cn.evolvefield.mods.pvz.common.world.challenge.ChallengeManager;
import cn.evolvefield.mods.pvz.init.registry.*;

import cn.evolvefield.mods.pvz.utils.BiomeUtil;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@EventBusSubscriber(modid = Static.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class RegistryHandler {

	/**
	 * put all deferred register together.
	 * {@link Pvz#Pvz()}
	 */
	public static void deferredRegister(IEventBus bus) {
		SoundRegister.SOUNDS.register(bus);
		ItemRegister.ITEMS.register(bus);
		BlockRegister.BLOCKS.register(bus);
		EntityRegister.ENTITY_TYPES.register(bus);
		ParticleRegister.PARTICLE_TYPES.register(bus);
		EffectRegister.EFFECTS.register(bus);
		BiomeRegister.BIOMES.register(bus);
		FeatureRegister.FEATURES.register(bus);
		FeatureRegister.STRUCTURE_FEATURES.register(bus);
		TileEntityRegister.TILE_ENTITY_TYPES.register(bus);
		EnchantmentRegister.ENCHANTMENTS.register(bus);
		ContainerRegister.CONTAINER_TYPES.register(bus);
		PotionRegister.POTIONS.register(bus);
		RecipeRegister.RECIPE_SERIALIZERS.register(bus);
		PVZAttributes.ATTRIBUTES.register(bus);
	}

	/**
	 * register paz stuff.
	 */
	public static void coreRegister() {
		//register essences.
		EssenceTypes.EssenceType.register();
		//register ranks.
		RankTypes.RankType.register();
		//register skills.
		SkillTypes.SkillType.register();
		//register plants.
		PVZPlants.register();
		CustomPlants.register();
		MemePlants.register();
		OtherPlants.register();
		//register zombies.
		GrassZombies.register();
		PoolZombies.register();
		RoofZombies.register();
		CustomZombies.register();
		Zombotanies.register();
		OtherZombies.register();
		//register challenge.
		ChallengeManager.registerChallengeStuff();
	}

	/**
	 * {@link Pvz#setUp(FMLCommonSetupEvent)}
	 */
    public static void setUp(FMLCommonSetupEvent ev){
    	CapabilityHandler.registerCapabilities();
    	PVZPacketHandler.init();
    	BiomeRegister.registerBiomes(ev);
    	PotionRecipeHandler.registerPotionRecipes();
    	CommonRegister.registerCompostable();
    	BiomeUtil.initBiomeSet();
    }

	/**
	 * Exists to work around a limitation with Spawn Eggs:
	 * Spawn Eggs require an EntityType, but EntityTypes are created AFTER Items.
	 * Therefore it is "impossible" for modded spawn eggs to exist.
	 * To get around this we have our own custom SpawnEggItem, but it needs
	 * some extra setup after Item and EntityType registration completes.
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPostRegisterEntities(final RegisterEvent  event) {
		if (event.getRegistryKey() == Registry.ENTITY_TYPE_REGISTRY)
			PVZSpawnEggItem.initUnaddedEggs();
	}

}
