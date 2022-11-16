package cn.evolvefield.mods.pvz.api.interfaces.util;

import net.minecraft.world.entity.Entity;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/11/15 13:26
 * Description:
 */
public interface IShooter {
    /**
     * shoot bullet to attack
     */
    void shootBullet();

    /**
     * get current shoot CD
     */
    int getShootCD();

    /**
     * bullet initial move speed
     */
    float getBulletSpeed();

    /**
     * perform shoot attack
     */
    void startShootAttack();

    /**
     * is suitable angle
     */
    boolean checkY(Entity target);
}
