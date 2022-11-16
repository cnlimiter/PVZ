package cn.evolvefield.mods.pvz.common.entity.creature;

import cn.evolvefield.mods.pvz.api.enums.PVZGroupType;
import cn.evolvefield.mods.pvz.api.interfaces.base.IHasGroup;
import cn.evolvefield.mods.pvz.api.interfaces.base.IHasOwner;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

/**
 * @program: pvzmod-1.16.5
 * @author: HungTeen
 * @create: 2022-02-11 16:00
 **/
public class GardenPlantEntity extends PathfinderMob implements IHasOwner, IHasGroup {

    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(GardenPlantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(GardenPlantEntity.class, EntityDataSerializers.INT);

    public GardenPlantEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AGE, 0);
        this.entityData.define(STATE, 0);
    }

    @Override
    public PVZGroupType getEntityGroupType() {
        return PVZGroupType.PLANTS;
    }

    @Override
    public Optional<UUID> getOwnerUUID() {
        return Optional.empty();
    }

    public enum GardenStates{
        NORMAL,
        INSECT,
        MUSIC,
        FERTILIZER,
        WATER,
        BEE
    }
}
