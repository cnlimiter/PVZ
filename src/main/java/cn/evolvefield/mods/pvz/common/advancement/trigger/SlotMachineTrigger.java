package cn.evolvefield.mods.pvz.common.advancement.trigger;

import cn.evolvefield.mods.pvz.common.advancement.predicate.AmountPredicate;
import cn.evolvefield.mods.pvz.common.advancement.predicate.StringPredicate;
import cn.evolvefield.mods.pvz.utils.StringUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class SlotMachineTrigger extends SimpleCriterionTrigger<SlotMachineTrigger.Instance> {

	private static final ResourceLocation ID = StringUtil.prefix("slot_machine");
	public static final SlotMachineTrigger INSTANCE = new SlotMachineTrigger();

	public ResourceLocation getId() {
		return ID;
	}

	/**
	 * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
	 */
	@Override
	protected Instance createInstance(JsonObject json, EntityPredicate.Composite player,
									  DeserializationContext p_230241_3_) {
		AmountPredicate amount = AmountPredicate.deserialize(json.get("amount"));
		StringPredicate type = StringPredicate.deserialize(json.get("type"));
		return new Instance(player, amount, type);
	}

	public void trigger(ServerPlayer player, int amount, String slotType) {
		this.trigger(player, (instance) -> {
			return instance.test(player, amount, slotType);
		});
	}

	public static class Instance extends AbstractCriterionTriggerInstance {
		private final AmountPredicate amount;
		private final StringPredicate type;

		public Instance(EntityPredicate.Composite player, AmountPredicate amount, StringPredicate type) {
			super(ID, player);
			this.amount = amount;
			this.type = type;
		}

		public boolean test(ServerPlayer player, int amount, String type) {
			return this.amount.test(player, amount) && this.type.test(player, type);
		}

		public JsonElement func_200288_b() {
			JsonObject jsonobject = new JsonObject();
			jsonobject.add("amount", this.amount.serialize());
			jsonobject.addProperty("type", this.type.serialize());
			return jsonobject;
		}
	}

}
