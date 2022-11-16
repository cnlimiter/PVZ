package cn.evolvefield.mods.pvz.common.entity.zombie.roof;

import cn.evolvefield.mods.pvz.common.entity.misc.drop.JewelEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.RoofZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;

public class Edgar090517Entity extends Edgar090505Entity {

    public Edgar090517Entity(EntityType<? extends PathfinderMob> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void spawnSpecialDrops() {
        final int playerCnt = this.bossInfo.getPlayers().size();
        for (int i = 0; i < 4 + 3 * playerCnt; ++i) {
            JewelEntity jewel = EntityRegister.JEWEL.get().create(level);
            EntityUtil.onEntityRandomPosSpawn(level, jewel, blockPosition().above(5), 4);
        }
    }

    @Override
    public int getBossStage() {
        final float percent = this.bossInfo.getProgress();
        return percent > 4F / 5 ? 1 :
                percent > 3F / 5 ? 2 :
                        percent > 2F / 5 ? 3 :
                                percent > 1F / 5 ? 4 : 5;
    }

    public EntityDimensions getDimensions(Pose poseIn) {
        return EntityDimensions.scalable(2F, 9F);
    }

    @Override
    public boolean shootAutoBall() {
        return true;
    }

    @Override
    public int getShootBallCD() {
        return this.getBossStage() < 2 ? 700 : this.getBossStage() < 4 ? 500 : 300;
    }

    @Override
    public int getStealPlantCD() {
        return this.getBossStage() < 3 ? 600 : 400;
    }

    @Override
    public float getElementBallSpeed() {
        return this.getBossStage() < 3 ? 0.18F : this.getBossStage() < 5 ? 0.21F : 0.23F;
    }

    @Override
    public int getSpawnCount() {
        return this.bossInfo.getPlayers().size() + 1;
    }

    @Override
    public float getWalkSpeed() {
        return 0;
    }

    @Override
    public float getEatDamage() {
        return ZombieUtil.LITTLE_HIGH;
    }

    @Override
    public float getLife() {
        return 1000;
    }

    @Override
    public float getInnerLife() {
        return 11000;
    }

    @Override
    public ZombieType getZombieType() {
        return RoofZombies.EDGAR_090517;
    }

}
