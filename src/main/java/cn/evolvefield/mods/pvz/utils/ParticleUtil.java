package cn.evolvefield.mods.pvz.utils;

import cn.evolvefield.mods.pvz.init.registry.ParticleRegister;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/11/14 0:30
 * Description:
 */
public class ParticleUtil {
    public static void spawnSplash(Level world, Vec3 vec, int cnt) {
        for(int i = 0; i < cnt; ++i) {
            var rand = world.random;
            world.addParticle(ParticleRegister.DIRT_BURST_OUT.get(), vec.x + 0.5d, vec.y, vec.z + 0.5d, (rand.nextFloat() - 0.5) / 10, 0.05d, (rand.nextFloat() - 0.5) / 10);
            world.addParticle(ParticleRegister.DIRT_BURST_OUT.get(), vec.x + 0.5d, vec.y, vec.z - 0.5d, (rand.nextFloat() - 0.5) / 10, 0.05d, (rand.nextFloat() - 0.5) / 10);
            world.addParticle(ParticleRegister.DIRT_BURST_OUT.get(), vec.x - 0.5d, vec.y, vec.z + 0.5d, (rand.nextFloat() - 0.5) / 10, 0.05d, (rand.nextFloat() - 0.5) / 10);
            world.addParticle(ParticleRegister.DIRT_BURST_OUT.get(), vec.x - 0.5d, vec.y, vec.z - 0.5d, (rand.nextFloat() - 0.5) / 10, 0.05d, (rand.nextFloat() - 0.5) / 10);
        }
    }
}
