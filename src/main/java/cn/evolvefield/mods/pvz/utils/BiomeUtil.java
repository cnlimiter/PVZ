package cn.evolvefield.mods.pvz.utils;

import cn.evolvefield.mods.pvz.common.world.biome.BiomeRegister;
import com.hungteen.pvz.common.world.biome.BiomeRegister;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ResourceKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BiomeUtil {

	public static final Set<Biome> OVERWORLD_LAND = new HashSet<>();
	public static final Set<Biome> OVERWORLD_DESERT = new HashSet<>();
	public static final Set<Biome> OVERWORLD_OCEAN = new HashSet<>();
	public static final Set<Biome> OVERWORLD_PLAIN = new HashSet<>();
	public static final Set<Biome> OVERWORLD_CONIFEROUS = new HashSet<>();//taiga
	public static final Set<Biome> OVERWORLD_SNOW_LAND = new HashSet<>();
	public static final Set<Biome> OVERWORLD_FOREST = new HashSet<>();
	public static final Set<Biome> NETHER = new HashSet<>();
	public static final Set<Biome> THE_END = new HashSet<>();

	public static void initBiomeSet() {
		for(Biome biome : ForgeRegistries.BIOMES) {
			if(biome == BiomeRegister.ZEN_GARDEN.get()) continue;//zen garden will not be add
			ResourceKey<Biome> biomeKey = getKey(biome);
			if(isOverworld(biomeKey)) {
				if(isLand(biomeKey)) {
					OVERWORLD_LAND.add(biome);
					if(isSnowy(biomeKey)) {
						OVERWORLD_SNOW_LAND.add(biome);
					}
				}
				if(isDesert(biomeKey)) {
					OVERWORLD_DESERT.add(biome);
				}
				if(isOcean(biomeKey)) {
					OVERWORLD_OCEAN.add(biome);
				}
				if(isPlain(biomeKey)) {
					OVERWORLD_PLAIN.add(biome);
				}
				if(isConiferous(biomeKey)) {
					OVERWORLD_CONIFEROUS.add(biome);
				}
				if(isForest(biomeKey)) {
					OVERWORLD_FOREST.add(biome);
				}
			}
			if(isNether(biomeKey)) {
				NETHER.add(biome);
			}
			if(isTheEnd(biomeKey)) {
				THE_END.add(biome);
			}
		}
	}

	public static boolean isLand(ResourceKey<Biome> biomeKey) {
		return biomeKey != Biomes.WARM_OCEAN && biomeKey != Biomes.RIVER;
	}

	public static boolean isOcean(ResourceKey<Biome> biomeKey) {
		return biomeKey == Biomes.OCEAN;
	}

	public static boolean isDesert(ResourceKey<Biome> biomeKey) {
		return BiomeDictionary.hasType(biomeKey, Type.SANDY);
	}

	public static boolean isPlain(ResourceKey<Biome> biomeKey) {
		return BiomeDictionary.hasType(biomeKey, Type.PLAINS);
	}

	public static boolean isConiferous(ResourceKey<Biome> biomeKey) {
		return BiomeDictionary.hasType(biomeKey, Type.CONIFEROUS);
	}

	public static boolean isSnowy(ResourceKey<Biome> biomeKey) {
		return BiomeDictionary.hasType(biomeKey, Type.SNOWY);
	}

	public static boolean isForest(ResourceKey<Biome> biomeKey) {
		return BiomeDictionary.hasType(biomeKey, Type.FOREST);
	}

	public static boolean isOverworld(ResourceKey<Biome> biomeKey) {
		return BiomeDictionary.hasType(biomeKey, Type.OVERWORLD);
	}

	public static boolean isNether(ResourceKey<Biome> biomeKey) {
		return BiomeDictionary.hasType(biomeKey, Type.NETHER);
	}

	public static boolean isNetherWaste(ResourceKey<Biome> biomeKey) {
		return biomeKey.equals(Biomes.NETHER_WASTES);
	}

	public static boolean isTheEnd(ResourceKey<Biome> biomeKey) {
		return BiomeDictionary.hasType(biomeKey, Type.END);
	}

	public static final Method GET_SKY_COLOR_WITH_TEMPERATURE_MODIFIER = ObfuscationReflectionHelper.findMethod(OverworldBiomes.class, /* getSkyColorWithTemperatureModifier */ "func_244206_a", float.class);

	public static int getSkyColor(float temp) {
		final int skyColour;
		try {
			skyColour = (int) GET_SKY_COLOR_WITH_TEMPERATURE_MODIFIER.invoke(null, temp);
		} catch (final IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("Error: Unable to get sky colour", e);
		}
		return skyColour;
	}

	public static ResourceKey<Biome> getKey(final Biome biome) {
		return ResourceKey.create(ForgeRegistries.Keys.BIOMES, Objects.requireNonNull(ForgeRegistries.BIOMES.getKey(biome), "Biome registry name was null"));
	}

}
