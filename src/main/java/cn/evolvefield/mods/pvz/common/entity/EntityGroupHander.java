package cn.evolvefield.mods.pvz.common.entity;

import cn.evolvefield.mods.pvz.api.enums.PVZGroupType;
import cn.evolvefield.mods.pvz.api.enums.Resources;
import cn.evolvefield.mods.pvz.common.misc.tags.PVZEntityTypeTags;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;

public class EntityGroupHander {

    /**
     * get entity's group.
     * specially mention : multiparts' group the same as its owner.
     * others entity is group 0.
     */
    public static PVZGroupType getEntityGroupType(Entity entity){
        final EntityType<?> entityType = entity.getType();
        if(entityType.is(PVZEntityTypeTags.PVZ_PLANT_GROUP_ENTITIES)){
            return PVZGroupType.PLANTS;
        }
        if(entityType.is(PVZEntityTypeTags.PVZ_ZOMBIE_GROUP_ENTITIES)){
            return PVZGroupType.ZOMBIES;
        }
        //this type is a monster or in monster tag (can not be banned).
        if(entityType.getCategory() == MobCategory.MONSTER || entityType.is(PVZEntityTypeTags.PVZ_OTHER_MONSTERS) && ! entityType.is(PVZEntityTypeTags.PVZ_NOT_MONSTERS)){
            return PVZGroupType.OTHER_MONSTERS;
        }
        //this type is a tamable entity or in guardian tag (can not be banned).
        if((entity instanceof TamableAnimal || entityType.is(PVZEntityTypeTags.PVZ_OTHER_GUARDIANS)) && ! entityType.is(PVZEntityTypeTags.PVZ_NOT_GUARDIANS)){
            return PVZGroupType.OTHER_GUARDIANS;
        }
        return PVZGroupType.NEUTRALS;
    }

    public static PVZGroupType getPlayerGroup(Player player){
        final int group = PlayerUtil.getResource(player, Resources.GROUP_TYPE);
        return getGroup(group);
    }

    public static PVZGroupType getGroup(int type){
        final int group = Mth.clamp(type, Resources.GROUP_TYPE.min, Resources.GROUP_TYPE.max);
        return PVZGroupType.values()[group + 2];
    }

    public static boolean isMonsterGroup(PVZGroupType groupType){
        return groupType.ordinal() - 2 < 0;
    }

    public static boolean checkCanAttack(PVZGroupType g1, PVZGroupType g2) {
		return g1 != g2;
	}

	public static boolean checkCanTarget(PVZGroupType g1, PVZGroupType g2) {
		return (g1.ordinal() - 2) * (g2.ordinal() - 2) < 0;
	}
}
