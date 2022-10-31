package cn.evolvefield.mods.pvz.init.registry;

import cn.evolvefield.mods.pvz.Static;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 18:42
 * Description:
 */
public class PVZAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Static.MOD_ID);

    public static final RegistryObject<Attribute> INNER_DEFENCE_HP = ATTRIBUTES.register("inner_defence_hp", () -> new RangedAttribute("attribute.name.pvz.inner_defence_hp", 0D, 0D, 1000000D).setSyncable(true));
    public static final RegistryObject<Attribute> OUTER_DEFENCE_HP = ATTRIBUTES.register("outer_defence_hp", () -> new RangedAttribute("attribute.name.pvz.outer_defence_hp", 0D, 0D, 1000000D).setSyncable(true));

}
