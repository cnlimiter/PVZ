package cn.evolvefield.mods.pvz.common.advancement.trigger;

import cn.evolvefield.mods.pvz.utils.StringUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class CharmZombieTrigger extends SimpleCriterionTrigger<CharmZombieTrigger.Instance> {

	private static final ResourceLocation ID = StringUtil.prefix("charm_zombie");
	public static final CharmZombieTrigger INSTANCE = new CharmZombieTrigger();

	public ResourceLocation getId() {
		return ID;
	}

	/**
	 * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
	 */
	@Override
	protected Instance createInstance(JsonObject json, EntityPredicate.Composite player,
									  DeserializationContext p_230241_3_) {
		return new Instance(player, EntityPredicate.fromJson(json.get("entity")));
	}

	public void trigger(ServerPlayer player, Entity entity) {
		this.trigger(player, (instance) -> {
			return instance.test(player, entity);
		});
	}

	public static class Instance extends AbstractCriterionTriggerInstance {

		private final EntityPredicate entity;

		public Instance(EntityPredicate.Composite player, EntityPredicate entity) {
			super(ID, player);
			this.entity = entity;
		}

		public boolean test(ServerPlayer player, Entity entity) {
			return this.entity.matches(player, entity);
		}

		public JsonElement func_200288_b() {
			JsonObject jsonobject = new JsonObject();
			jsonobject.add("entity", this.entity.serializeToJson());
			return jsonobject;
		}
	}

}
