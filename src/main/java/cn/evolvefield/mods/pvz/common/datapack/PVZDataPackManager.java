package cn.evolvefield.mods.pvz.common.datapack;

import cn.evolvefield.mods.pvz.common.impl.plant.PlantType;
import cn.evolvefield.mods.pvz.common.net.PVZPacketHandler;
import cn.evolvefield.mods.pvz.common.net.toclient.DatapackPacket;
import com.hungteen.pvz.PVZMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.network.PacketDistributor;

public class PVZDataPackManager {

    /**
     * {@link PVZMod#PVZMod()}
     */
    public static void addReloadListenerEvent(AddReloadListenerEvent event) {
        event.addListener(new PlantType.PlantTypeLoader());
        event.addListener(new LotteryTypeLoader());
        event.addListener(new InvasionTypeLoader());
        event.addListener(new ChallengeTypeLoader());
        event.addListener(new TransactionTypeLoader());
    }

    public static void sendSyncPacketsTo(Player player){
        if(player instanceof ServerPlayer) {
            LotteryTypeLoader.JSONS.entrySet().forEach(entry -> {
                PVZPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new DatapackPacket(LotteryTypeLoader.NAME, entry.getKey().toString(), entry.getValue().toString()));
            });

            TransactionTypeLoader.JSONS.entrySet().forEach(entry -> {
                PVZPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new DatapackPacket(TransactionTypeLoader.NAME, entry.getKey().toString(), entry.getValue().toString()));
            });

            ChallengeTypeLoader.JSONS.entrySet().forEach(entry -> {
                PVZPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new DatapackPacket(ChallengeTypeLoader.NAME, entry.getKey().toString(), entry.getValue().toString()));
            });
        }
    }

}
