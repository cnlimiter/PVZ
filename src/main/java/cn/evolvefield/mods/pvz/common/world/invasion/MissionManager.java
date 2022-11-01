package cn.evolvefield.mods.pvz.common.world.invasion;

import cn.evolvefield.mods.pvz.api.enums.Resources;
import cn.evolvefield.mods.pvz.common.cap.player.PlayerDataManager;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import cn.evolvefield.mods.pvz.utils.misc.WeightList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 14:09
 * Description:
 */
public class MissionManager {
    public static final int MAX_MISSION_STAGE = 5;
    public static final int KILL_IN_SECOND = 10;
    private static final int[] KILL_MISSIONS = new int[]{50, 100, 200, 300, 500};
    private static final int[] INSTANT_KILL_MISSIONS = new int[]{10, 20, 40, 60, 80};
    private static final int[] COLLECT_SUN_MISSIONS = new int[]{5000, 10000, 15000, 20000, 25000};

    /**
     * tick each second.
     */
    public static void tickMission(Invasion invasion){
        final Player player = invasion.getPlayer();
        if(player.tickCount % 20 == 5) {
            final MissionType type = getPlayerMission(player);
            if (type != MissionType.EMPTY) {
                final int now = PlayerUtil.getResource(player, Resources.MISSION_VALUE);
                int stage = PlayerUtil.getResource(player, Resources.MISSION_STAGE);
                int require = getRequireMissionValue(type, stage);
                while (stage < MAX_MISSION_STAGE && now >= require) {
                    rewardPlayer(player, type, stage);
                    ++stage;
                    require = getRequireMissionValue(type, stage);
                }
                if (type == MissionType.INSTANT_KILL) {
                    invasion.updateKillQueue();
                }
                //finish all mission.
                if (stage >= MAX_MISSION_STAGE) {
                    PlayerUtil.addResource(player, Resources.MISSION_FINISH_TIME, 1);
                    removeMission(player);
                } else {
                    PlayerUtil.setResource(player, Resources.MISSION_STAGE, stage);
                }
            }
        }
    }

    public static void removeMission(Player player){
        PlayerUtil.getOptManager(player).ifPresent(l -> l.getInvasion().clearMission());
    }

    public static void rewardPlayer(Player player, MissionType type, int stage){
        PlayerUtil.sendMsgTo(player, Component.translatable("invasion.pvz.mission.finish", stage));
        if(stage == 0 || stage == 2){
            rewardMoney(player, stage == 0 ? 250 : 500);
        } else if(stage == 1 || stage == 3){
            rewardLottery(player, 5);
        } else{
            rewardJewel(player, player.getRandom().nextInt(1) + 1);
        }
    }

    private static void rewardMoney(Player player, int amount) {
        PlayerUtil.addResource(player, Resources.MONEY, amount);
        PlayerUtil.playClientSound(player, SoundRegister.SUN_PICK.get());
    }

    private static void rewardJewel(Player player, int amount){
        PlayerUtil.addResource(player, Resources.GEM_NUM, amount);
        PlayerUtil.playClientSound(player, SoundRegister.JEWEL_PICK.get());
    }

//    private static void rewardCard(Player player, int count){
//        for(int i = 0; i < count; ++ i){
//            Optional.ofNullable(InvasionManager.getSpawnInvasion()).ifPresent(type -> {
//                LotteryTypeLoader.getLotteryType(type.getBonusResource()).ifPresent(lotteryType -> {
//                    final SlotMachineTileEntity.SlotType slotType = lotteryType.getSlotType(player.getRandom());
//                    if(slotType.getStack().isPresent()){
//                        player.addItem(slotType.getStack().get());
//                    } else{
//                        rewardJewel(player, 1);
//                    }
//                });
//            });
//        }
//    }

    private static void rewardLottery(Player player, int amount){
        PlayerUtil.addResource(player, Resources.LOTTERY_CHANCE, amount);
        PlayerUtil.playClientSound(player, SoundRegister.SLOT_MACHINE.get());
    }

    public static MissionType getMission(RandomSource rand){
        WeightList<MissionType> list = new WeightList<>();
        for(MissionType type : MissionType.values()){
            if(type.weight != 0){
                list.addItem(type, type.weight);
            }
        }
        return list.getRandomItem(rand).get();
    }

    public static MissionType getPlayerMission(Player player){
        final PlayerDataManager manager = PlayerUtil.getManager(player);
        if(manager != null){
            return MissionType.values()[manager.getResource(Resources.MISSION_TYPE)];
        }
        return MissionType.EMPTY;
    }

    public static int getRequireMissionValue(MissionType type, int stage){
        if(stage >= 0 && stage < MAX_MISSION_STAGE){
            switch (type){
                case KILL: return KILL_MISSIONS[stage];
                case INSTANT_KILL: return INSTANT_KILL_MISSIONS[stage];
                case COLLECT_SUN: return COLLECT_SUN_MISSIONS[stage];
            }
        }
        return 9999999;
    }

    public enum MissionType{
        EMPTY(3),
        KILL(10),
        INSTANT_KILL(8),
        COLLECT_SUN(5);

        public final int weight;

        MissionType(int weight){
            this.weight = weight;
        }
    }
}
