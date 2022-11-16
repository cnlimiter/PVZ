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

public class ChallengeTrigger extends SimpleCriterionTrigger<ChallengeTrigger.Instance> {

    private static final ResourceLocation ID = StringUtil.prefix("challenge");
    public static final ChallengeTrigger INSTANCE = new ChallengeTrigger();

    public ResourceLocation getId() {
        return ID;
    }


    /**
     * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
     */
    @Override
    protected Instance createInstance(JsonObject json, EntityPredicate.Composite player, DeserializationContext p_230241_3_) {
        StringPredicate type = StringPredicate.deserialize(json.get("type"));
        return new Instance(player, type);
    }

    public void trigger(ServerPlayer player, String s) {
        this.trigger(player, (instance) -> {
            return instance.test(player, s);
        });
    }



    public static class Instance extends AbstractCriterionTriggerInstance {
        private final StringPredicate type;

        public Instance(EntityPredicate.Composite player, StringPredicate res) {
            super(ID, player);
            this.type = res;
        }

        public boolean test(ServerPlayer player, String s) {
            return this.type.test(player, s);
        }

        public JsonElement func_200288_b() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("type", this.type.serialize());
            return jsonobject;
        }
    }

}
