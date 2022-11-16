package cn.evolvefield.mods.pvz.common.advancement.trigger;

import cn.evolvefield.mods.pvz.common.advancement.predicate.AmountPredicate;
import cn.evolvefield.mods.pvz.utils.StringUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PlantLevelTrigger extends SimpleCriterionTrigger<PlantLevelTrigger.Instance> {

	private static final ResourceLocation ID = StringUtil.prefix("plant_level");
	public static final PlantLevelTrigger INSTANCE = new PlantLevelTrigger();

	public ResourceLocation getId() {
		return ID;
	}

	/**
	 * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
	 */
	protected Instance createInstance(JsonObject json, EntityPredicate.Composite player,
									  DeserializationContext p_230241_3_) {
		AmountPredicate amount = AmountPredicate.deserialize(json.get("amount"));
		return new Instance(player, amount);
	}

	public void trigger(ServerPlayer player, int amount) {
		this.trigger(player, (instance) -> {
			return instance.test(player, amount);
		});
	}

	public static class Instance extends AbstractCriterionTriggerInstance {
		private final AmountPredicate amount;

		public Instance(EntityPredicate.Composite player, AmountPredicate amount) {
			super(ID, player);
			this.amount = amount;
		}

		public boolean test(ServerPlayer player, int amount) {
			return this.amount.test(player, amount);
		}

		public JsonElement func_200288_b() {
			JsonObject jsonobject = new JsonObject();
			jsonobject.add("amount", this.amount.serialize());
			return jsonobject;
		}
	}

}
