package cn.evolvefield.mods.pvz.common.impl.challenge.amount;

import cn.evolvefield.mods.pvz.api.interfaces.raid.IAmountComponent;
import com.google.gson.JsonElement;
import net.minecraft.util.GsonHelper;

public class ConstantAmount implements IAmountComponent {

	public static final String NAME = "count";
	private int cnt = 1;

	public ConstantAmount() {
	}

	@Override
	public int getSpawnAmount() {
		return this.cnt;
	}

	@Override
	public void readJson(JsonElement json) {
		this.cnt = GsonHelper.convertToInt(json, NAME);
	}

}
