package cn.evolvefield.mods.pvz.common.impl.challenge.reward;

import cn.evolvefield.mods.pvz.api.PVZAPI;
import cn.evolvefield.mods.pvz.api.interfaces.base.IChallenge;
import cn.evolvefield.mods.pvz.api.interfaces.raid.IRewardComponent;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPAZType;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class UnLockRewardComponent implements IRewardComponent {

    public static final String NAME = "unlocks";
    private final List<IPAZType> list = new ArrayList<>();

    @Override
    public void reward(ServerPlayer player) {
        this.list.forEach(type -> {
            PlayerUtil.setPAZLock(player, type, false);
            PlayerUtil.sendMsgTo(player, Component.translatable("challenge.pvz.unlock", type.getText().getString()).withStyle(ChatFormatting.GREEN));
        });
    }

    @Override
    public void rewardGlobally(IChallenge challenge) {
    }

    @Override
    public void readJson(JsonElement json) {
        if (json.isJsonArray()) {
            final JsonArray array = json.getAsJsonArray();
            array.forEach(e -> {
                if(e.isJsonPrimitive()){
                    final String string = e.getAsString();
                    PVZAPI.get().getTypeByID(string).ifPresent(type -> {
                        list.add(type);
                    });
                }
            });
        }
    }

}
