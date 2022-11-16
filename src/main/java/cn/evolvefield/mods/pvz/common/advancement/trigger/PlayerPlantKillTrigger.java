package cn.evolvefield.mods.pvz.common.advancement.trigger;

import cn.evolvefield.mods.pvz.utils.StringUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class PlayerPlantKillTrigger extends SimpleCriterionTrigger<PlayerPlantKillTrigger.Instance> {

	private static final ResourceLocation ID = StringUtil.prefix("player_plant_kill");
	public static final PlayerPlantKillTrigger INSTANCE = new PlayerPlantKillTrigger();

	public ResourceLocation getId() {
		return ID;
	}

	/**
	 * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
	 */
	@Override
	protected Instance createInstance(JsonObject json, EntityPredicate.Composite player,
									  DeserializationContext p_230241_3_) {
		return new Instance(player, EntityPredicate.fromJson(json.get("entity")), DamageSourcePredicate.fromJson(json.get("killing_blow")));
	}

	public void trigger(ServerPlayer player, Entity entity, DamageSource source) {
		this.trigger(player, (instance) -> {
			return instance.test(player, entity, source);
		});
	}

	public static class Instance extends AbstractCriterionTriggerInstance {

		private final EntityPredicate entity;
		private final DamageSourcePredicate killingBlow;

		public Instance(EntityPredicate.Composite player, EntityPredicate entity, DamageSourcePredicate killingBlow) {
			super(ID, player);
			this.entity = entity;
			this.killingBlow = killingBlow;
		}

		public boolean test(ServerPlayer player, Entity entity, DamageSource source) {
			return !this.killingBlow.matches(player, source) ? false : this.entity.matches(player, entity);
		}

		public JsonElement func_200288_b() {
			JsonObject jsonobject = new JsonObject();
			jsonobject.add("entity", this.entity.serializeToJson());
			jsonobject.add("killing_blow", this.killingBlow.serializeToJson());
			return jsonobject;
		}
	}

}
