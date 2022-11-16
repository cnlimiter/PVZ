package cn.evolvefield.mods.pvz.api.interfaces.util;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/11/14 13:07
 * Description:
 */
public interface IPult {
    /**
     * check attacker's pult condition.
     */
    boolean shouldPult();

    /**
     * attack interval.
     */
    int getPultCD();

    /**
     * when tick reach the CD, start attack.
     */
    void startPultAttack();

    /**
     * pulter attack range.
     */
    float getPultRange();

    /**
     * shoot entity out.
     */
    void pultBullet();
}
