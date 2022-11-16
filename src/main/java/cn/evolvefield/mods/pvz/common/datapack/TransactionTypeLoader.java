package cn.evolvefield.mods.pvz.common.datapack;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.interfaces.raid.IAmountComponent;
import cn.evolvefield.mods.pvz.common.entity.npc.AbstractDaveEntity;
import cn.evolvefield.mods.pvz.common.world.challenge.ChallengeManager;
import com.google.gson.*;
import com.hungteen.pvz.PVZMod;
import com.hungteen.pvz.api.raid.IAmountComponent;
import com.hungteen.pvz.common.entity.npc.AbstractDaveEntity;
import com.hungteen.pvz.common.world.challenge.ChallengeManager;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: pvzmod-1.16.5
 * @author: HungTeen
 * @create: 2022-02-07 11:19
 **/
public class TransactionTypeLoader extends SimpleJsonResourceReloadListener {

    public static final Map<ResourceLocation, AbstractDaveEntity.TransactionType> TRANSACTIONS = new HashMap<>();
    public static final Map<ResourceLocation, JsonElement> JSONS = new HashMap<>();
    private static final Gson GSON = (new GsonBuilder()).create();
    public static final String NAME = "transaction";


    public TransactionTypeLoader() {
        super(GSON, NAME + "s");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
        TRANSACTIONS.clear();

        map.forEach((res, jsonElement) -> {
            updateResource(res, jsonElement);

            JSONS.put(res, jsonElement);
        });

        Static.LOGGER.info("Loaded {} custom transaction type", TRANSACTIONS.size());

    }

    public static void updateResource(ResourceLocation res, JsonElement jsonElement) {
        try {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, NAME);

            final AbstractDaveEntity.TransactionType transactionType = new AbstractDaveEntity.TransactionType(res);

            /* amount */
            {
                JsonObject obj = GsonHelper.getAsJsonObject(jsonObject, "good_count");
                if (obj != null && !obj.entrySet().isEmpty()) {
                    for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                        final IAmountComponent tmp = ChallengeManager.getAmountComponent(entry.getKey());
                        if (tmp != null) {
                            tmp.readJson(entry.getValue());
                            transactionType.setGoodCount(tmp);
                        } else {
                            Static.LOGGER.warn("Amount Component : Read Spawn Amount Wrongly");
                        }
                        break;
                    }
                }
            }

            transactionType.setEnvelope(GsonHelper.getAsBoolean(jsonObject, "has_envelope", false));
            transactionType.setSlotMachine(GsonHelper.getAsBoolean(jsonObject, "has_slot_machine", false));
            transactionType.setEnjoyCard(GsonHelper.getAsBoolean(jsonObject, "has_enjoy_card", false));


            JsonArray array = GsonHelper.getAsJsonArray(jsonObject, "goods", new JsonArray());
            array.forEach(e -> {
                if(e.isJsonObject()) {
                    final JsonObject obj  = e.getAsJsonObject();

                    final String string = GsonHelper.getAsString(obj, "type", "item");
                    final AbstractDaveEntity.GoodTypes type = AbstractDaveEntity.GoodTypes.valueOf(string.toUpperCase());

                    ItemStack stack = ItemStack.EMPTY;
                    if(type == AbstractDaveEntity.GoodTypes.ITEM){
                        Item item = GsonHelper.getAsItem(obj, "item");
                        if(obj.has("data")) {
                            throw new JsonParseException("Disallowed data tag found");
                        } else {
                            stack = new ItemStack(item);
                            if(obj.has("nbt")) {
                                try {
                                    var compoundnbt = TagParser.parseTag(GsonHelper.convertToString(obj.get("nbt"), "nbt"));
                                    stack.setTag(compoundnbt);
                                } catch (CommandSyntaxException commandsyntaxexception) {
                                    throw new JsonSyntaxException("Invalid nbt tag: " + commandsyntaxexception.getMessage());
                                }
                            }
                        }
                    }

                    final int price = GsonHelper.getAsInt(obj, "price", 1000);

                    final int weight = GsonHelper.getAsInt(obj, "weight", 100);

                    final int limit = GsonHelper.getAsInt(obj, "limit", 10);

                    final boolean must = GsonHelper.getAsBoolean(obj, "must", false);

                    AbstractDaveEntity.GoodType goodType = new AbstractDaveEntity.GoodType(type, stack, price, weight, limit, must);

                    transactionType.addGood(goodType);
                }
            });

            TRANSACTIONS.put(res, transactionType);

        } catch (IllegalArgumentException | JsonParseException e) {
            Static.LOGGER.error("Parsing error loading transaction type {}: {}", res, e.getMessage());
        }
    }

    @Nullable
    public static AbstractDaveEntity.TransactionType getTransactionByRes(ResourceLocation res){
        return TRANSACTIONS.get(res);
    }

}
