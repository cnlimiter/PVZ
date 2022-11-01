package cn.evolvefield.mods.pvz.init.registry;

import cn.evolvefield.mods.pvz.Static;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 18:59
 * Description:
 */
public class ParticleRegister {
    //don't forget register particle factory in client register
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =  DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Static.MOD_ID);

    public static final RegistryObject<SimpleParticleType> RED_BOMB = PARTICLE_TYPES.register("red_bomb", ()->{return new SimpleParticleType(false);});
    public static final RegistryObject<SimpleParticleType> YELLOW_BOMB = PARTICLE_TYPES.register("yellow_bomb", ()->{return new SimpleParticleType(false);});
    public static final RegistryObject<SimpleParticleType> DIRT_BURST_OUT = PARTICLE_TYPES.register("dirt_burst_out", ()->{return new SimpleParticleType(false);});
    public static final RegistryObject<SimpleParticleType> YELLOW_FLAME = PARTICLE_TYPES.register("yellow_flame", ()->{return new SimpleParticleType(false);});
    public static final RegistryObject<SimpleParticleType> BLUE_FLAME = PARTICLE_TYPES.register("blue_flame", ()->{return new SimpleParticleType(false);});
    public static final RegistryObject<SimpleParticleType> SLEEP = PARTICLE_TYPES.register("sleep", ()->{return new SimpleParticleType(false);});
    public static final RegistryObject<SimpleParticleType> SPORE = PARTICLE_TYPES.register("spore", ()->{return new SimpleParticleType(false);});
    public static final RegistryObject<SimpleParticleType> FUME = PARTICLE_TYPES.register("fume", ()->{return new SimpleParticleType(false);});
    public static final RegistryObject<SimpleParticleType> SNOW_FLOWER = PARTICLE_TYPES.register("snow_flower", ()->{return new SimpleParticleType(false);});
    public static final RegistryObject<SimpleParticleType> DOOM = PARTICLE_TYPES.register("doom", ()->{return new SimpleParticleType(false);});
    public static final RegistryObject<SimpleParticleType> MELON_SLICE = PARTICLE_TYPES.register("melon_slice", ()->{return new SimpleParticleType(false);});
    public static final RegistryObject<SimpleParticleType> FROZEN_MELON_SLICE = PARTICLE_TYPES.register("frozen_melon_slice", ()->{return new SimpleParticleType(false);});
    public static final RegistryObject<SimpleParticleType> GREEN_SWEEP = PARTICLE_TYPES.register("green_sweep", ()->{return new SimpleParticleType(false);});
    public static final RegistryObject<SimpleParticleType> POP_CORN = PARTICLE_TYPES.register("pop_corn", ()->{return new SimpleParticleType(false);});

}
