package cn.evolvefield.mods.pvz.common.advancement.trigger;

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

public class PlayerPlacePAZTrigger extends SimpleCriterionTrigger<PlayerPlacePAZTrigger.Instance> {

	private static final ResourceLocation ID = StringUtil.prefix("player_place_paz");
	public static final PlayerPlacePAZTrigger INSTANCE = new PlayerPlacePAZTrigger();

	public ResourceLocation getId() {
		return ID;
	}

	/**
	 * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
	 */
	@Override
	protected Instance createInstance(JsonObject json, EntityPredicate.Composite player,
									  DeserializationContext p_230241_3_) {
		return new Instance(player, StringPredicate.deserialize(json.get("place_type")), StringPredicate.deserialize(json.get("paz_type")));
	}

	public void trigger(ServerPlayer player, String placeType, String pazType) {
		this.trigger(player, (instance) -> {
			return instance.test(player, placeType, pazType);
		});
	}

	public static class Instance extends AbstractCriterionTriggerInstance {

		private final StringPredicate placeType;
		private final StringPredicate pazType;

		public Instance(EntityPredicate.Composite player, StringPredicate placeType, StringPredicate pazType) {
			super(ID, player);
			this.placeType = placeType;
			this.pazType = pazType;
		}

		public boolean test(ServerPlayer player, String placeType, String pazType) {
			return this.placeType.test(player, placeType) && this.pazType.test(player, pazType);
		}

		public JsonElement func_200288_b() {
			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("place_type", this.placeType.serialize());
			jsonobject.addProperty("paz_type", this.pazType.serialize());
			return jsonobject;
		}
	}

	public enum PlaceTypes{
		PLANT,
		UPGRADE,
		ZOMBIE,
	}

}
