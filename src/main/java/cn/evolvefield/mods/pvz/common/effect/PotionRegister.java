package cn.evolvefield.mods.pvz.common.effect;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.init.registry.EffectRegister;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PotionRegister {

	public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, Static.MOD_ID);

	public static final RegistryObject<Potion> EXCITE_POTION_1 = POTIONS.register("excite_potion_1", () -> {return new Potion(new MobEffectInstance(EffectRegister.EXCITE_EFFECT.get(), 600, 0));});
	public static final RegistryObject<Potion> EXCITE_POTION_2 = POTIONS.register("excite_potion_2", () -> {return new Potion(new MobEffectInstance(EffectRegister.EXCITE_EFFECT.get(), 1800, 1));});
	public static final RegistryObject<Potion> EXCITE_POTION_3 = POTIONS.register("excite_potion_3", () -> {return new Potion(new MobEffectInstance(EffectRegister.EXCITE_EFFECT.get(), 3600, 0));});
	public static final RegistryObject<Potion> LIGHT_EYE_POTION_1 = POTIONS.register("light_eye_potion_1", () -> {return new Potion(new MobEffectInstance(EffectRegister.LIGHT_EYE_EFFECT.get(), 1200, 0));});
	public static final RegistryObject<Potion> LIGHT_EYE_POTION_2 = POTIONS.register("light_eye_potion_2", () -> {return new Potion(new MobEffectInstance(EffectRegister.LIGHT_EYE_EFFECT.get(), 9600, 0));});

}
