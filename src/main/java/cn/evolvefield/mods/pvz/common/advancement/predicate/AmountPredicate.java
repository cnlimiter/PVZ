package cn.evolvefield.mods.pvz.common.advancement.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;

public class AmountPredicate {

	public static final AmountPredicate ANY = Builder.create().build();
	private final MinMaxBounds.Ints amount;

	public AmountPredicate() {
		amount = MinMaxBounds.Ints.ANY;
	}

	public AmountPredicate(MinMaxBounds.Ints bound) {
		amount = bound;
	}

	public boolean test(ServerPlayer player, int amount) {
		if(this == ANY) return true;
		return this.amount.matches(amount);
	}

	public static AmountPredicate deserialize(@Nullable JsonElement element) {
		if (element != null && !element.isJsonNull()) {
			JsonObject jsonobject = GsonHelper.convertToJsonObject(element, "amount");
			MinMaxBounds.Ints bound = MinMaxBounds.Ints.fromJson(jsonobject.get("amount"));
			return new AmountPredicate(bound);
		} else {
			return ANY;
		}
	}

	public JsonElement serialize() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonobject = new JsonObject();
			jsonobject.add("dealt", this.amount.serializeToJson());
			return jsonobject;
		}
	}

	public static class Builder {
		private MinMaxBounds.Ints dealt = MinMaxBounds.Ints.ANY;

		public static Builder create() {
			return new Builder();
		}

		public AmountPredicate build() {
			return new AmountPredicate(this.dealt);
		}
	}

}
