package cn.evolvefield.mods.pvz.common.world.spawn;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.utils.BiomeUtil;
import com.hungteen.pvz.PVZConfig;
import com.hungteen.pvz.PVZMod;
import com.hungteen.pvz.common.entity.EntityRegister;
import com.hungteen.pvz.common.entity.misc.drop.SunEntity;
import com.hungteen.pvz.common.world.biome.BiomeRegister;
import com.hungteen.pvz.utils.BiomeUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid= Static.MOD_ID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class EntitySpawnRegister {

	public static final PlacementModifierType IN_SKY = PlacementModifierType.create("pvz_in_sky", (world, pos, type) -> {
		return world.canSeeSky(pos) && world.canSeeSky(pos.offset(0, - 5, 0));
	});

	public static final PlacementType ON_SNOW = PlacementType.create("pvz_on_snow", (world, pos, type) -> {
		return world.getBlockState(pos).getBlock() == Blocks.SNOW || world.getBlockState(pos.below()).getBlock() == Blocks.SNOW_BLOCK;
	});

	@SubscribeEvent
	public static void registerEntitySpawns(RegisterEvent evt) {
		EntitySpawnPlacementRegistry.register(EntityRegister.SUN.get(), IN_SKY, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SunEntity::canSunSpawn);
//		EntitySpawnPlacementRegistry.register(EntityRegister.FOODIE_ZOMBIE.get(), PlacementType.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FoodieZombieEntity::canSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.CRAZY_DAVE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);

		EntitySpawnPlacementRegistry.register(EntityRegister.NORMAL_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.FLAG_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.CONEHEAD_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.POLE_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.BUCKETHEAD_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);

		EntitySpawnPlacementRegistry.register(EntityRegister.TOMB_STONE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.GIGA_TOMB_STONE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
	    EntitySpawnPlacementRegistry.register(EntityRegister.NEWSPAPER_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.SCREENDOOR_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.FOOTBALL_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.GIGA_FOOTBALL_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.DANCING_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.BACKUP_DANCER.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.OLD_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.SUNDAY_EDITION_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);

		EntitySpawnPlacementRegistry.register(EntityRegister.SNORKEL_ZOMBIE.get(), PlacementType.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.ZOMBONI.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.BOBSLE_TEAM.get(), ON_SNOW, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
//		EntitySpawnPlacementRegistry.register(EntityRegister.ZOMBIE_DOLPHIN.get(), PlacementType.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FoodieZombieEntity::canSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.DOLPHIN_RIDER.get(), PlacementType.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.LAVA_ZOMBIE.get(), PlacementType.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canLavaZombieSpawn);

		EntitySpawnPlacementRegistry.register(EntityRegister.PUMPKIN_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.TRICK_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);

		EntitySpawnPlacementRegistry.register(EntityRegister.JACK_IN_BOX_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.BALLOON_ZOMBIE.get(), IN_SKY, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.DIGGER_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.POGO_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.YETI_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canYetiSpawn);

		EntitySpawnPlacementRegistry.register(EntityRegister.BUNGEE_ZOMBIE.get(), IN_SKY, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.LADDER_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.CATAPULT_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.GARGANTUAR.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.GIGA_GARGANTUAR.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);

		EntitySpawnPlacementRegistry.register(EntityRegister.PEASHOOTER_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.WALLNUT_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.GATLINGPEA_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.TALLNUT_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.SQUASH_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
		EntitySpawnPlacementRegistry.register(EntityRegister.JALAPENO_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnChecker::canZombieSpawn);
	}

	/**
	 * {@link BiomeRegister#biomeModification(BiomeLoadingEvent)}
	 */
	public static void addEntitySpawnToBiome(BiomeLoadingEvent event, ResourceKey<Biome> biomeKey) {
		if(BiomeUtil.isOverworld(biomeKey)) {
			if(BiomeUtil.isLand(biomeKey)) {
				event.getSpawns().addSpawn(EntityClassification.AMBIENT, new Spawners(EntityRegister.SUN.get(), 2 * PVZConfig.COMMON_CONFIG.WorldSettings.SunSpawnWeight.get(), 1, 1));
//				event.getSpawns().addSpawn(EntityClassification.MONSTER, new Spawners(EntityRegister.GIGA_TOMB_STONE.get(), PVZConfig.COMMON_CONFIG.WorldSettings.GigaTombStoneSpawnWeight.get(), 1, 1));
			}
			if(BiomeUtil.isDesert(biomeKey)) {
			}
			if(BiomeUtil.isOcean(biomeKey)) {
//				event.getSpawns().addSpawn(EntityClassification.CREATURE, new Spawners(EntityRegister.FOODIE_ZOMBIE.get(), PVZConfig.COMMON_CONFIG.WorldSettings.EntitySpawnSettings.FoodieZombieSpawnWeight.get(), 1, 1));
			}
			if(BiomeUtil.isPlain(biomeKey)) {
			}
			if(BiomeUtil.isConiferous(biomeKey)) {
			}
		}
		if(BiomeUtil.isNetherWaste(biomeKey)) {
			event.getSpawns().addSpawn(EntityClassification.MONSTER, new Spawners(EntityRegister.LAVA_ZOMBIE.get(), PVZConfig.COMMON_CONFIG.WorldSettings.LavaZombieSpawnWeight.get(), 1, 1));
		}
		if(BiomeUtil.isTheEnd(biomeKey)) {
		}
	}

}
