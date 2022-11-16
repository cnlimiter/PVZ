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

public class InvasionTrigger extends SimpleCriterionTrigger<InvasionTrigger.Instance> {

    private static final ResourceLocation ID = StringUtil.prefix("invasion");
    public static final InvasionTrigger INSTANCE = new InvasionTrigger();

    public ResourceLocation getId() {
        return ID;
    }

    /**
     * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
     */
    @Override
    protected Instance createInstance(JsonObject json, EntityPredicate.Composite player, DeserializationContext p_230241_3_) {
        return new Instance(player);
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, (instance) -> {
            return instance.test(player);
        });
    }

    public static class Instance extends AbstractCriterionTriggerInstance {

        public Instance(EntityPredicate.Composite player) {
            super(ID, player);
        }

        public boolean test(ServerPlayer player) {
            return true;
        }

        public JsonElement func_200288_b() {
            JsonObject jsonobject = new JsonObject();
            return jsonobject;
        }
    }

}
