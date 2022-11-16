package cn.evolvefield.mods.pvz.common.misc.tags;

import cn.evolvefield.mods.pvz.utils.StringUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/11/14 0:15
 * Description:
 */
public class PVZEntityTypeTags {
    public static final TagKey<EntityType<?>> PVZ_PLANT_GROUP_ENTITIES = forgeTag("pvz_plant_group_entities");
    public static final TagKey<EntityType<?>> PVZ_ZOMBIE_GROUP_ENTITIES = forgeTag("pvz_zombie_group_entities");
    public static final TagKey<EntityType<?>> PVZ_OTHER_MONSTERS = forgeTag("pvz_other_monsters");
    public static final TagKey<EntityType<?>> PVZ_OTHER_GUARDIANS = forgeTag("pvz_other_guardians");
    public static final TagKey<EntityType<?>> PVZ_NOT_MONSTERS = forgeTag("pvz_not_monsters");
    public static final TagKey<EntityType<?>> PVZ_NOT_GUARDIANS = forgeTag("pvz_not_guardians");

    //pvz
    public static final TagKey<EntityType<?>> PVZ_PLANTS = pvzTag("pvz_plants");
    public static final TagKey<EntityType<?>> PVZ_ZOMBIES = pvzTag("pvz_zombies");
    public static final TagKey<EntityType<?>> BUNGEE_SPAWNS = pvzTag("bungee_spawns");

    private static TagKey<EntityType<?>> pvzTag(String name){
        return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(name));
    }

    private static TagKey<EntityType<?>> forgeTag(String name){
        return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("forge", name));
    }
}
