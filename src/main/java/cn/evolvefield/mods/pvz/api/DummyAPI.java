package cn.evolvefield.mods.pvz.api;

import cn.evolvefield.mods.pvz.api.interfaces.raid.*;
import cn.evolvefield.mods.pvz.api.interfaces.types.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.*;
import java.util.function.Supplier;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/31 1:20
 * Description:
 */
public class DummyAPI implements PVZAPI.IPVZAPI {

    public static final PVZAPI.IPVZAPI INSTANCE = new DummyAPI();

    @Override
    public void registerPlantType(IPlantType type) {
    }

    @Override
    public void registerPlantTypes(Collection<IPlantType> types) {
    }

    @Override
    public void registerZombieType(IZombieType type) {
    }

    @Override
    public void registerZombieTypes(Collection<IZombieType> types) {
    }

    @Override
    public void registerEssenceType(IEssenceType type) {

    }

    @Override
    public void registerEssenceTypes(Collection<IEssenceType> types) {
    }

    @Override
    public void registerRankType(IRankType type) {

    }

    @Override
    public void registerSkillType(ISkillType type) {

    }

    @Override
    public void registerSkillTypes(Collection<ISkillType> types) {

    }

    @Override
    public void registerCD(ICoolDown type) {
    }

    @Override
    public void registerCDs(Collection<ICoolDown> types) {

    }

    @Override
    public List<IPlantType> getPlants() {
        return new ArrayList<>();
    }

    @Override
    public List<IZombieType> getZombies() {
        return new ArrayList<>();
    }

    @Override
    public List<IPAZType> getPAZs() {
        return new ArrayList<>();
    }

    @Override
    public List<IEssenceType> getEssences() {
        return new ArrayList<>();
    }

    @Override
    public Optional<IPAZType> getTypeByID(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<IPlantType> getPlantTypeByID(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<IZombieType> getZombieTypeByID(String id) {
        return Optional.empty();
    }

    @Override
    public void registerPeaGunMode(IPlantType type) {
    }

    @Override
    public void registerBowlingMode(IPlantType type, Supplier<EntityType<? extends Entity>> supplier, float size) {
    }

    @Override
    public void registerSpawnAmount(String name, Class<? extends IAmountComponent> c) {
    }

    @Override
    public void registerSpawnPlacement(String name, Class<? extends IPlacementComponent> c) {
    }

    @Override
    public void registerRaidType(String name, Class<? extends IChallengeComponent> c) {
    }

    @Override
    public void registerWaveType(String name, Class<? extends IWaveComponent> c) {
    }

    @Override
    public void registerSpawnType(String name, Class<? extends ISpawnComponent> c) {
    }

    @Override
    public void registerReward(String name, Class<? extends IRewardComponent> c) {
    }

    @Override
    public boolean createRaid(ServerLevel world, ResourceLocation res, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isRaider(ServerLevel world, Entity entity) {
        return false;
    }

    @Override
    public Optional<Challenge> getNearByRaid(ServerLevel world, BlockPos pos) {
        return Optional.empty();
    }

    @Override
    public Map<ResourceLocation, IChallengeComponent> getRaidTypes() {
        return new HashMap<>();
    }
}
