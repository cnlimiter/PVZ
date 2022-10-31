package cn.evolvefield.mods.pvz.common.impl.challenge.reward;

import cn.evolvefield.mods.pvz.api.interfaces.base.IChallenge;
import cn.evolvefield.mods.pvz.api.interfaces.raid.IAmountComponent;
import cn.evolvefield.mods.pvz.api.interfaces.raid.IRewardComponent;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import com.google.gson.*;
import com.hungteen.pvz.common.entity.misc.GiftBoxEntity;
import com.hungteen.pvz.common.world.challenge.ChallengeManager;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemRewardComponent implements IRewardComponent {

    public static final String NAME = "items";
    private final List<Pair<ItemStack, IAmountComponent>> list = new ArrayList<>();

    @Override
    public void reward(ServerPlayer player) {
    }

    @Override
    public void rewardGlobally(IChallenge challenge) {
        GiftBoxEntity rewardChestEntity = EntityRegister.GIFT_BOX.get().create(challenge.getWorld());
        NonNullList<ItemStack> stacks = NonNullList.create();
        list.forEach(pair -> {
            final int count = pair.getSecond().getSpawnAmount();
            final ItemStack stack = pair.getFirst().copy();
            stack.setCount(count);
            stacks.add(stack);
        });

        rewardChestEntity.setDrops(stacks);
        rewardChestEntity.setPos(challenge.getCenter().getX() + 0.5, challenge.getCenter().getY(), challenge.getCenter().getZ() + 0.5);

        challenge.getWorld().addFreshEntity(rewardChestEntity);
    }

    @Override
    public void readJson(JsonElement json) {
        if (json.isJsonArray()) {
            final JsonArray array = json.getAsJsonArray();
            array.forEach(e -> {
                if (e.isJsonObject()) {
                    final JsonObject obj = e.getAsJsonObject();

                    ItemStack stack = new ItemStack(GsonHelper.getAsItem(obj, "item"));
                    if (obj.has("data")) {
                        throw new JsonParseException("Disallowed data tag found");
                    }

                    if (obj.has("nbt")) {
                        try {
                            CompoundTag compoundnbt = JsonToNBT.parseTag(GsonHelper.convertToString(obj.get("nbt"), "nbt"));
                            stack.setTag(compoundnbt);
                        } catch (CommandSyntaxException commandsyntaxexception) {
                            throw new JsonSyntaxException("Invalid nbt tag: " + commandsyntaxexception.getMessage());
                        }
                    }

                    IAmountComponent component = ChallengeManager.readAmount(obj, "amount");

                    list.add(Pair.of(stack, component));
                }
            });
        }
    }

}
