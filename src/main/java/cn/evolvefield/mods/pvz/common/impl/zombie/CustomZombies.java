package cn.evolvefield.mods.pvz.common.impl.zombie;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.interfaces.types.IZombieType;
import cn.evolvefield.mods.pvz.common.impl.RankTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CustomZombies extends ZombieType {

	private static final List<IZombieType> LIST = new ArrayList<>();

	/*
	 * zombotany.
	 */

	public static final ZombieType LAVA_ZOMBIE = new CustomZombies("lava_zombie", new ZombieFeatures()
		    .rank(RankTypes.PURPLE).xp(36)
			.entityType(() -> EntityRegister.LAVA_ZOMBIE.get())
			.zombieModel(() -> LavaZombieModel::new).scale(0.5F)
			.loot(PVZLoot.LAVA_ZOMBIE)
			.eatCommonSkill(Arrays.asList())
	);

	public static final ZombieType MOURNER_ZOMBIE = new CustomZombies("mourner_zombie", new ZombieFeatures()
		    .rank(RankTypes.GREEN).xp(10)
			.entityType(() -> EntityRegister.MOURNER_ZOMBIE.get())
			.zombieModel(() -> MournerZombieModel::new).scale(0.5F)
			.loot(PVZLoot.LAVA_ZOMBIE)
			.commonSkill(Arrays.asList())
	);

	public static final ZombieType COFFIN = new CustomZombies("coffin", new ZombieFeatures()
		    .rank(RankTypes.BLACK).xp(100)
			.entityType(() -> EntityRegister.COFFIN.get())
			.scale(0.5F)
			.loot(PVZLoot.COFFIN)
			.commonSkill(Arrays.asList())
	);

	public static final ZombieType NOBLE_ZOMBIE = new CustomZombies("noble_zombie", new ZombieFeatures()
		    .rank(RankTypes.MEGA).xp(250)
			.entityType(() -> EntityRegister.NOBLE_ZOMBIE.get())
			.zombieModel(() -> NobleZombieModel::new).scale(0.5F)
			.loot(PVZLoot.NOBLE_ZOMBIE)
			.commonSkill(Arrays.asList())
	);

	public static final ZombieType TRICK_ZOMBIE = new CustomZombies("trick_zombie", new ZombieFeatures()
		    .rank(RankTypes.GRAY).xp(3)
			.entityType(() -> EntityRegister.TRICK_ZOMBIE.get())
			.zombieModel(() -> TrickZombieModel::new).scale(0.5F)
			.loot(PVZLoot.TRICK_ZOMBIE)
			.commonSkill(Arrays.asList())
	);

	public static final ZombieType GIGA_TOMBSTONE = new CustomZombies("giga_tomb_stone", new ZombieFeatures()
		    .rank(RankTypes.WHITE).xp(1)
			.entityType(() -> EntityRegister.GIGA_TOMB_STONE.get())
			.scale(0.5F)
	);

	public static void register() {
		registerZombies(LIST);
	}

	private CustomZombies(String name, ZombieFeatures features) {
		super(name, features);
		LIST.add(this);
	}

	@Override
	public int getSortPriority() {
		return 60;
	}

	@Override
	public String getCategoryName() {
		return "custom";
	}

	@Override
	public String getModID() {
		return Static.MOD_ID;
	}

}
