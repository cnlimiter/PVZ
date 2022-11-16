package cn.evolvefield.mods.pvz.common.advancement.trigger;

import cn.evolvefield.mods.pvz.common.advancement.predicate.AmountPredicate;
import cn.evolvefield.mods.pvz.utils.StringUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class EntityEffectAmountTrigger extends CriterionTrigger<EntityEffectAmountTrigger.Instance> {

	private static final ResourceLocation ID = StringUtil.prefix("entity_effect_amount");
	public static final EntityEffectAmountTrigger INSTANCE = new EntityEffectAmountTrigger();

	public ResourceLocation getId() {
		return ID;
	}

	/**
	 * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
	 */
	@Override
	protected Instance createInstance(JsonObject json, EntityPredicate.Composite player,
									  DeserializationContext p_230241_3_) {
		return new Instance(player, EntityPredicate.fromJson(json.get("entity")), AmountPredicate.deserialize(json.get("amount")));
	}

	public void trigger(ServerPlayer player, Entity entity, int amount) {
		this.trigger(player, (instance) -> {
			return instance.test(player, entity, amount);
		});
	}

	public static class Instance extends AbstractCriterionTriggerInstance {

		private final EntityPredicate entity;
		private final AmountPredicate amount;

		public Instance(EntityPredicate.Composite player, EntityPredicate entity, AmountPredicate amount) {
			super(ID, player);
			this.entity = entity;
			this.amount = amount;
		}

		public boolean test(ServerPlayer player, Entity entity, int amount) {
			return this.entity.matches(player, entity) && this.amount.test(player, amount);
		}

		public JsonElement func_200288_b() {
			JsonObject jsonobject = new JsonObject();
			jsonobject.add("entity", this.entity.serializeToJson());
			jsonobject.add("amount", this.amount.serialize());
			return jsonobject;
		}
	}

}
