package cn.evolvefield.mods.pvz.common.impl;

import cn.evolvefield.mods.pvz.api.PVZAPI;
import cn.evolvefield.mods.pvz.api.interfaces.raid.*;
import cn.evolvefield.mods.pvz.api.interfaces.types.*;
import cn.evolvefield.mods.pvz.common.impl.plant.PlantType;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.common.world.challenge.Challenge;
import cn.evolvefield.mods.pvz.common.world.challenge.ChallengeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.*;
import java.util.function.Supplier;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 18:32
 * Description:
 */
public class PVZAPIImpl implements PVZAPI.IPVZAPI {

    @Override
    public void registerPlantType(IPlantType type) {
        PlantType.registerPlant(type);
    }

    @Override
    public void registerPlantTypes(Collection<IPlantType> types) {
        PlantType.registerPlants(types);
    }

    @Override
    public void registerZombieType(IZombieType type) {
        ZombieType.registerZombie(type);
    }

    @Override
    public void registerZombieTypes(Collection<IZombieType> types) {
        ZombieType.registerZombies(types);
    }

    @Override
    public void registerEssenceType(IEssenceType type) {
        EssenceTypes.registerEssence(type);
    }

    @Override
    public void registerEssenceTypes(Collection<IEssenceType> types) {
        types.forEach(type -> EssenceTypes.registerEssence(type));
    }

    @Override
    public void registerRankType(IRankType type) {
        RankTypes.registerRankType(type);
    }

    @Override
    public void registerSkillType(ISkillType type) {
        SkillTypes.registerSkillType(type);
    }

    @Override
    public void registerSkillTypes(Collection<ISkillType> types) {
        types.forEach(type -> registerSkillType(type));
    }

    @Override
    public void registerCD(ICoolDown type) {
        CoolDowns.registerCD(type);
    }

    @Override
    public void registerCDs(Collection<ICoolDown> types) {
        CoolDowns.registerCDs(types);
    }

    @Override
    public void registerSpawnAmount(String name, Class<? extends IAmountComponent> c) {
        ChallengeManager.registerAmountComponent(name, c);
    }

    @Override
    public void registerSpawnPlacement(String name, Class<? extends IPlacementComponent> c) {
        ChallengeManager.registerPlacementComponent(name, c);
    }

    @Override
    public void registerReward(String name, Class<? extends IRewardComponent> c) {
        ChallengeManager.registerRewardComponent(name, c);
    }

    @Override
    public void registerRaidType(String name, Class<? extends IChallengeComponent> c) {
        ChallengeManager.registerChallengeComponent(name, c);
    }

    @Override
    public void registerWaveType(String name, Class<? extends IWaveComponent> c) {
        ChallengeManager.registerWaveComponent(name, c);
    }

    @Override
    public void registerSpawnType(String name, Class<? extends ISpawnComponent> c) {
        ChallengeManager.registerSpawnComponent(name, c);
    }

    @Override
    public boolean createRaid(ServerLevel world, ResourceLocation res, BlockPos pos) {
        if(! ChallengeManager.hasChallengeNearby(world, pos)) {
            return ChallengeManager.createChallenge(world, res, pos);
        }
        return false;
    }

    @Override
    public boolean isRaider(ServerLevel world, Entity entity) {
        return ChallengeManager.isRaider(world, entity);
    }

    @Override
    public Optional<Challenge> getNearByRaid(ServerLevel world, BlockPos pos) {
        return ChallengeManager.getChallengeNearBy(world, pos);
    }

    @Override
    public Map<ResourceLocation, IChallengeComponent> getRaidTypes() {
        return ChallengeManager.getChallengeTypes();
    }

    @Override
    public List<IPlantType> getPlants() {
        return PlantType.getPlants();
    }

    @Override
    public List<IZombieType> getZombies() {
        return ZombieType.getZombies();
    }

    @Override
    public List<IPAZType> getPAZs() {
        final List<IPAZType> list = new ArrayList<>();
        list.addAll(getPlants());
        list.addAll(getZombies());
        return list;
    }

    @Override
    public List<IEssenceType> getEssences() {
        return EssenceTypes.getEssences();
    }

    @Override
    public Optional<IPAZType> getTypeByID(String id) {
        final Optional<IPlantType> opt1 = getPlantTypeByID(id);
        final Optional<IZombieType> opt2 = getZombieTypeByID(id);
        return opt1.isPresent() ? Optional.ofNullable(opt1.get()) : Optional.ofNullable(opt2.get());
    }

    @Override
    public Optional<IPlantType> getPlantTypeByID(String id) {
        return PlantType.getPlantByName(id);
    }

    @Override
    public Optional<IZombieType> getZombieTypeByID(String id) {
        return ZombieType.getZombieByName(id);
    }

    @Override
    public void registerPeaGunMode(IPlantType type) {
        PeaGunItem.registerPeaGunShootMode(type);
    }

    @Override
    public void registerBowlingMode(IPlantType type, Supplier<EntityType<? extends Entity>> supplier, float size) {
        BowlingGloveItem.registerBowling(type, supplier, size);
    }
}
