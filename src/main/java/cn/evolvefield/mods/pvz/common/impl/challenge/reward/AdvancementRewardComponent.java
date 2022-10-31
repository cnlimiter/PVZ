package cn.evolvefield.mods.pvz.common.impl.challenge.reward;

import cn.evolvefield.mods.pvz.api.interfaces.base.IChallenge;
import cn.evolvefield.mods.pvz.api.interfaces.raid.IRewardComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementRewardComponent implements IRewardComponent {

	public static final String NAME = "advancements";
	private AdvancementRewards reward = AdvancementRewards.EMPTY;

	@Override
	public void reward(ServerPlayer player) {
		this.reward.grant(player);
	}

	@Override
	public void rewardGlobally(IChallenge challenge) {
	}

	@Override
	public void readJson(JsonElement json) {
		final JsonObject obj = json.getAsJsonObject();
		if(obj != null) {
			this.reward = AdvancementRewards.deserialize(obj);
		}
	}

}
