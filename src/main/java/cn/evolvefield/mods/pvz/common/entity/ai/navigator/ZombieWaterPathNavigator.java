package cn.evolvefield.mods.pvz.common.entity.ai.navigator;

import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class ZombieWaterPathNavigator extends WaterBoundPathNavigation {

	public ZombieWaterPathNavigator(Mob p_i45873_1_, Level p_i45873_2_) {
		super(p_i45873_1_, p_i45873_2_);
	}

	@Override
	protected PathFinder createPathFinder(int p_179679_1_) {
		this.nodeEvaluator = new SwimNodeEvaluator(true);
		return new PathFinder(this.nodeEvaluator, p_179679_1_);
	}

	@Override
	public void tick() {
		++this.tick;
		if (tick % 20 == 0) {
			this.recomputePath();
		}
		if (!this.isDone()) {
			if (this.canUpdatePath()) {
				this.followThePath();
			} else if (this.path != null && !this.path.isDone()) {
				var vector3d = this.path.getNextEntityPos(this.mob);
				if (Mth.floor(this.mob.getX()) == Mth.floor(vector3d.x)
						&& Mth.floor(this.mob.getY()) == Mth.floor(vector3d.y)
						&& Mth.floor(this.mob.getZ()) == Mth.floor(vector3d.z)) {
					this.path.advance();
				}
			}

			DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
			if (!this.isDone()) {
				var vector3d1 = this.path.getNextEntityPos(this.mob);
				this.mob.getMoveControl().setWantedPosition(vector3d1.x, vector3d1.y, vector3d1.z, this.speedModifier);
			}
		}
	}

	@Override
	protected void followThePath() {
		if (this.path != null) {

			var vector3d = this.getTempMobPos();
			float f = this.mob.getBbWidth();
			float f1 = f > 0.75F ? f / 2.0F : 0.75F - f / 2.0F;
			var vector3d1 = this.mob.getDeltaMovement();
			if (Math.abs(vector3d1.x) > 0.2D || Math.abs(vector3d1.z) > 0.2D) {
				f1 = (float) ((double) f1 * vector3d1.length() * 6.0D);
			}

			var vector3d2 = Vec3.atBottomCenterOf(this.path.getNextNodePos());
			if (Math.abs(this.mob.getX() - vector3d2.x) < (double) f1
					&& Math.abs(this.mob.getZ() - vector3d2.z) < (double) f1
//					&& Math.abs(this.mob.getY() - vector3d2.y) < (double) (f1 * 2.0F)
			) {

				this.path.advance();
			}

			for (int j = Math.min(this.path.getNextNodeIndex() + 6, this.path.getNodeCount() - 1); j > this.path
					.getNextNodeIndex(); --j) {
				vector3d2 = this.path.getEntityPosAtNode(this.mob, j);
				if (!(vector3d2.distanceToSqr(vector3d) > 36.0D)
						&& this.canMoveDirectly(vector3d, vector3d2)) {
					this.path.setNextNodeIndex(j);
					break;
				}
			}

			this.doStuckDetection(vector3d);
		}
	}

}
