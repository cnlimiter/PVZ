package cn.evolvefield.mods.pvz.common.impl.zombie;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.interfaces.types.IZombieType;
import cn.evolvefield.mods.pvz.common.impl.RankTypes;
import cn.evolvefield.mods.pvz.init.misc.PVZLoot;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import com.hungteen.pvz.client.model.entity.zombie.other.RaZombieModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class OtherZombies extends ZombieType {

	private static final List<IZombieType> LIST = new ArrayList<>();

	/*
	 * egypt.
	 */
	public static final ZombieType RA_ZOMBIE = new OtherZombies("ra_zombie", new ZombieFeatures()
		    .rank(RankTypes.GREEN).xp(10)
			.entityType(() -> EntityRegister.RA_ZOMBIE.get())
			.zombieModel(() -> RaZombieModel::new).scale(0.5F)
			.loot(PVZLoot.RA_ZOMBIE)
			.eatCommonSkill(Arrays.asList())
	);

	public static void register() {
		registerZombies(LIST);
	}

	private OtherZombies(String name, ZombieFeatures features) {
		super(name, features);
		LIST.add(this);
	}

	@Override
	public int getSortPriority() {
		return 70;
	}

	@Override
	public String getCategoryName() {
		return "other";
	}

	@Override
	public String getModID() {
		return Static.MOD_ID;
	}

}
