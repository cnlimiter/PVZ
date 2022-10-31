package cn.evolvefield.mods.pvz.api.interfaces.paz;

import cn.evolvefield.mods.pvz.api.interfaces.attract.ICanBeAttracted;
import cn.evolvefield.mods.pvz.api.interfaces.base.*;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPAZType;
import com.mojang.datafixers.util.Pair;

import java.util.List;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/30 20:15
 * Description:
 */
public interface IPAZEntity extends IHasOwner, IHasGroup, ICanBeCharmed, ICanBeAttracted, IHasMetal {
    IPAZType getPAZType();

    /**
     * whether it can be slow down because of cold effect.
     */
    boolean canBeCold();

    /**
     * whether it can be too cold to move caused by frozen effect.
     */
    boolean canBeFrozen();

    /**
     * whether it can be stunned by butter bullet or not.
     */
    boolean canBeButtered();

    /**
     * whether it can change its group or not. {@link cn.evolvefield.mods.pvz.api.enums.PVZGroupType}
     */
    boolean canBeCharmed();

    /**
     * it will be much smaller than before when mini invasion is coming.
     */
    boolean canBeMini();

    /**
     * players can not see it when invisible invasion is coming.
     */
    boolean canBeInvisible();

    boolean canBeStealByBungee();

    /**
     * display in almanac. {@link cn.evolvefield.mods.pvz.common.item.display.AlmanacItem}
     */
    void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list);

    void setOuterDefenceLife(double life);

    void setInnerDefenceLife(double life);

    double getOuterDefenceLife();

    double getInnerDefenceLife();
}
