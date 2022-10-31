package cn.evolvefield.mods.pvz.common.world.invasion;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 13:45
 * Description:
 */
public class SpawnType {
    private final EntityType<? extends Mob> spawnType;
    private CompoundTag nbt = new CompoundTag();
    private int invasionLevel;
    private int spawnWeight;
    private PlaceType placeType;

    public SpawnType(EntityType<? extends Mob> spawnType){
        this.spawnType = spawnType;
    }

    public void setInvasionLevel(int invasionLevel) {
        this.invasionLevel = invasionLevel;
    }

    public void setSpawnWeight(int spawnWeight) {
        this.spawnWeight = spawnWeight;
    }

    public EntityType<? extends Mob> getSpawnType() {
        return spawnType;
    }

    public int getInvasionLevel() {
        return invasionLevel;
    }

    public int getSpawnWeight() {
        return spawnWeight;
    }

    public void setNbt(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public CompoundTag getNbt() {
        return nbt;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public boolean checkPos(Level world, BlockPos pos){
        if(getPlaceType() == PlaceType.LAND){
            return world.getBlockState(pos.below()).isValidSpawn(world, pos.below(), this.spawnType) && world.getBlockState(pos.below()).getFluidState().isEmpty();
        } else if(getPlaceType() == PlaceType.WATER){
            return ! world.getBlockState(pos.below()).getFluidState().isEmpty();
        } else if(getPlaceType() == PlaceType.SNOW){
            return world.getBlockState(pos).getBlock() == Blocks.SNOW || world.getBlockState(pos.below()).getBlock() == Blocks.SNOW_BLOCK;
        } else if(getPlaceType() == PlaceType.SKY){
            return true;
        }
        return false;
    }

    public enum PlaceType{

        LAND,
        WATER,
        SNOW,
        SKY
    }
}
