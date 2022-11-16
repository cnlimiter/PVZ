package cn.evolvefield.mods.pvz.api.enums;

import cn.evolvefield.mods.pvz.utils.ConfigUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/31 13:28
 * Description:
 */
public enum Resources {
    /* player tree maxLevel */
    TREE_LVL(1, 200),
    /* xp of player tree maxLevel */
    TREE_XP(0, 0),
    /* money, common currency */
    MONEY(0, 9999999),
    /* jewel, special currency */
    GEM_NUM(0, 9999999),
    /* sun amount, maximum is limited by tree maxLevel*/
    SUN_NUM(0, 0),
    /* max plant food amount */
    MAX_ENERGY_NUM(1, 10),
    /* plant food amount */
    ENERGY_NUM(0, 0),
    /* the tick to control fog display */
//	NO_FOG_TICK(- 9999999, 9999999),
    /* kill zombie count */
//	KILL_COUNT(0, 9999999),
    /* the chance to use slot machine */
    LOTTERY_CHANCE(0, 9999999),
    /* the group of player */
    GROUP_TYPE(- 2, 2),
    /* card slot */
    SLOT_NUM(1, 9),
    /* mission use */
    MISSION_FINISH_TIME(0, 9999999),
    MISSION_TYPE(0, 3),
    MISSION_STAGE(0, 4),
    MISSION_VALUE(0, 9999999)
    ;

    /**
     * {@link PlayerDataManager#PlayerDataManager(net.minecraft.entity.player.Player)}
     */
    public static int getInitialValue(Resources res) {
        return switch (res) {
            case SUN_NUM -> 50;
            case LOTTERY_CHANCE -> 10;
            case GROUP_TYPE -> ConfigUtil.getPlayerInitialGroup();
//		case NO_FOG_TICK: return 0;
            default -> res.min;
        };
    }

    public static final int INF = 9999999;
    public final int min;
    public final int max;

    private Resources(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public MutableComponent getText() {
        return Component.translatable("resource.pvz." + this.toString().toLowerCase());
    }
}
