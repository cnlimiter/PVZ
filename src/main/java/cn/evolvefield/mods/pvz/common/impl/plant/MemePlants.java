package cn.evolvefield.mods.pvz.common.impl.plant;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.PVZAPI;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.impl.*;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import com.hungteen.pvz.client.model.entity.plant.magic.StrangeCatModel;
import com.hungteen.pvz.common.impl.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MemePlants extends PlantType {

	private static final List<IPlantType> LIST = new ArrayList<>();

	public static final IPlantType STRANGE_CAT = new MemePlants("strange_cat", new PlantFeatures()
			.cost(300).requiredLevel(60)
			.cd(CoolDowns.HUGE_SLOW).rank(RankTypes.PURPLE).essence(EssenceTypes.MAGIC)
			.entityType(() -> EntityRegister.STRANGE_CAT.get())
			.summonCard(() -> ItemRegister.STRANGE_CAT_CARD.get())
			.enjoyCard(() -> ItemRegister.STRANGE_CAT_ENJOY_CARD.get())
			.plantModel(() -> StrangeCatModel::new).scale(0.18F)
			.placement(Placements.ANY)
			.cdSkill(Arrays.asList(SkillTypes.LESS_SUN))
	);

	public static void register() {
		PVZAPI.get().registerPlantTypes(LIST);
	}

	private MemePlants(String name, PlantFeatures features) {
		super(name, features);
		LIST.add(this);
	}

    @Override
	public int getSortPriority() {
		return 80;
	}

	@Override
	public String getCategoryName() {
		return "meme";
	}

	@Override
	public String getModID() {
		return Static.MOD_ID;
	}
}
