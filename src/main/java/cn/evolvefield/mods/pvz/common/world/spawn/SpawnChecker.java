package cn.evolvefield.mods.pvz.common.world.spawn;

import com.hungteen.pvz.common.entity.zombie.PVZZombieEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ILevel;
import net.minecraft.world.LightType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.server.ServerLevel;

import java.util.Random;

public class SpawnChecker {

	/**
	 * no need to check invasion spawn list.
	 * {@link EntitySpawnRegister#registerEntitySpawns(net.minecraftforge.event.RegistryEvent.Register)}
	 */
	public static boolean canZombieSpawn(EntityType<? extends PVZZombieEntity> zombieType, LevelAccessor worldIn,
										 MobSpawnType reason, BlockPos pos, Random rand) {
		return checkSpawn(zombieType, worldIn, reason, pos, rand);
	}

	public static boolean canLavaZombieSpawn(EntityType<? extends PVZZombieEntity> zombieType, LevelAccessor worldIn,
											 MobSpawnType reason, BlockPos pos, Random rand) {
		return worldIn.getDifficulty() == Difficulty.HARD && checkSpawn(zombieType, worldIn, reason, pos, rand);
	}

//	/**
//	 * is not natural spawn or in invasion spawn list.
//	 * {@link EntitySpawnRegister#registerEntitySpawns(net.minecraftforge.event.RegistryEvent.Register)}
//	 */
//	public static boolean canGroundInvasionZombieSpawn(EntityType<? extends PVZZombieEntity> zombieType, ILevel worldIn,
//			SpawnReason reason, BlockPos pos, Random rand) {
//		return checkSpawn(zombieType, worldIn, reason, pos, rand);
//	}
//
//	/**
//	 * no need to check invasion spawn list.
//	 * {@link EntitySpawnRegister#registerEntitySpawns(net.minecraftforge.event.RegistryEvent.Register)}
//	 */
//	public static boolean canNightZombieSpawn(EntityType<? extends PVZZombieEntity> zombieType, ILevel worldIn,
//			SpawnReason reason, BlockPos pos, Random rand) {
//		return (worldIn instanceof ServerLevel && ! ((ServerLevel) worldIn).isDay()) && checkSpawn(zombieType, worldIn, reason, pos, rand);
//	}

//	/**
//	 * is not natural spawn or in invasion spawn list. <br>
//	 * can spawn in water face. <br>
//	 * {@link EntitySpawnRegister#registerEntitySpawns(net.minecraftforge.event.RegistryEvent.Register)}
//	 */
//	public static boolean canWaterInvasionZombieSpawn(EntityType<? extends PVZZombieEntity> zombieType, ILevel worldIn,
//			SpawnReason reason, BlockPos pos, Random rand) {
//		return checkLightAndDifficulty(worldIn, pos)
//				&& (reason == SpawnReason.SPAWNER || isInWater(worldIn, pos));
//	}

//	/**
//	 * is not natural spawn or in invasion spawn list. <br>
//	 * can spawn in not so high Sky. <br>
//	 * {@link EntitySpawnRegister#registerEntitySpawns(net.minecraftforge.event.RegistryEvent.Register)}
//	 */
//	public static boolean canSkyInvasionZombieSpawn(EntityType<? extends PVZZombieEntity> zombieType, ILevel worldIn,
//			SpawnReason reason, BlockPos pos, Random rand) {
//		return checkLightAndDifficulty(worldIn, pos) && (reason == SpawnReason.SPAWNER || canSeeSky(worldIn, pos));
//	}

	/**
	 * is not natural spawn or in invasion spawn list or is in thundering. <br>
	 * can spawn in not so high Sky. <br>
	 * {@link EntitySpawnRegister#registerEntitySpawns(net.minecraftforge.event.RegistryEvent.Register)}
	 */
	public static boolean canYetiSpawn(EntityType<? extends PVZZombieEntity> zombieType, LevelAccessor worldIn,
									   MobSpawnType reason, BlockPos pos, Random rand) {
		if(worldIn instanceof ServerLevel && canZombieSpawn(zombieType, worldIn, reason, pos, rand)) {
			return ((ServerLevel) worldIn).isThundering() && ! ((ServerLevel) worldIn).isDay() && rand.nextInt(3) == 0;
		}
		return false;
	}

	private static boolean checkSpawn(EntityType<? extends PathfinderMob> zombieType, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, Random rand) {
		return isDarkEnough(worldIn, pos) && worldIn.getDifficulty() != Difficulty.PEACEFUL && Mob.checkMobSpawnRules(zombieType, worldIn, reason, pos, rand);
	}
//
//	private static boolean isInWater(ILevel world, BlockPos pos) {
//		return world.getFluidState(pos.below()).is(FluidTags.WATER);
//	}
//
//	private static boolean canSeeSky(ILevel world, BlockPos pos) {
//	      return world.canSeeSky(pos);
//	}
//
//	private static boolean checkLightAndDifficulty(ILevel worldIn, BlockPos pos) {
//		return worldIn.getDifficulty() != Difficulty.PEACEFUL && isDarkEnough(worldIn, pos);
//	}

	private static boolean isDarkEnough(LevelAccessor worldIn, BlockPos pos) {
		return worldIn.getBrightness(LightLayer.BLOCK, pos) < 7;
	}

}
