package cn.evolvefield.mods.pvz.common.entity.ai.goal;

import cn.evolvefield.mods.pvz.common.world.challenge.Challenge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * @program: pvzmod-1.16.5
 * @author: HungTeen
 * @create: 2022-02-03 13:55
 **/
public class ChallengeMoveGoal extends Goal {

    private final Mob owner;
    private final Challenge challenge;
    private final BlockPos center;
    private int tick = 0;

    public ChallengeMoveGoal(Mob owner, Challenge challenge){
        this.owner = owner;
        this.challenge = challenge;
        this.center = this.challenge.getCenter();
//        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        //TODO AI disappearance when entering the game again
    }

    @Override
    public boolean canUse() {
        if(this.challenge == null || this.challenge.isRemoving()){
            return false;
        }
        if(this.owner.getTarget() != null){
            return false;
        }
        if(this.owner.distanceToSqr(this.center.getX(), this.center.getY(), this.center.getZ()) < 100){
            return false;
        }
        if(++ tick < 60) {
        	return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    public void start() {
    }

    @Override
    public void tick() {
        this.owner.getNavigation().moveTo(this.center.getX(), this.center.getY() + 1, this.center.getZ(), 1);
    }

    public void stop() {
    	this.tick = 0;
        this.owner.getNavigation().stop();
        super.stop();
    }

}
