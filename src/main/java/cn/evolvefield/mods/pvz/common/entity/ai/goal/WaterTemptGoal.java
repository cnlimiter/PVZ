package cn.evolvefield.mods.pvz.common.entity.ai.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumSet;

public class WaterTemptGoal extends Goal {
	private static final TargetingConditions ENTITY_PREDICATE = TargetingConditions.forNonCombat().range(10.0D)
			.ignoreLineOfSight();
	protected final PathfinderMob creature;
	private final double speed;
	private double targetX;
	private double targetY;
	private double targetZ;
	private double pitch;
	private double yaw;
	protected Player closestPlayer;
	private int delayTemptCounter;
	private boolean isRunning;
	private final Ingredient temptItem;
	private final boolean scaredByPlayerMovement;

	public WaterTemptGoal(PathfinderMob creatureIn, double speedIn, Ingredient temptItemsIn,
			boolean scaredByPlayerMovementIn) {
		this(creatureIn, speedIn, scaredByPlayerMovementIn, temptItemsIn);
	}

	public WaterTemptGoal(PathfinderMob creatureIn, double speedIn, boolean scaredByPlayerMovementIn,
			Ingredient temptItemsIn) {
		this.creature = creatureIn;
		this.speed = speedIn;
		this.temptItem = temptItemsIn;
		this.scaredByPlayerMovement = scaredByPlayerMovementIn;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		if (!(creatureIn.getNavigation() instanceof WaterBoundPathNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
		}
	}

	/**
	 * Returns whether execution should begin. You can also read and cache any state
	 * necessary for execution in this method as well.
	 */
	public boolean canUse() {
		if (this.delayTemptCounter > 0) {
			--this.delayTemptCounter;
			return false;
		} else {
			this.closestPlayer = this.creature.level.getNearestPlayer(ENTITY_PREDICATE, this.creature);
			if (this.closestPlayer == null) {
				return false;
			} else {
				return this.isTempting(this.closestPlayer.getMainHandItem())
						|| this.isTempting(this.closestPlayer.getOffhandItem());
			}
		}
	}

	protected boolean isTempting(ItemStack stack) {
		return this.temptItem.test(stack);
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean canContinueToUse() {
		if (this.isScaredByPlayerMovement()) {
			if (this.creature.distanceToSqr(this.closestPlayer) < 36.0D) {
				if (this.closestPlayer.distanceToSqr(this.targetX, this.targetY,
						this.targetZ) > 0.010000000000000002D) {
					return false;
				}

				if (Math.abs((double) this.closestPlayer.getXRot() - this.pitch) > 5.0D
						|| Math.abs((double) this.closestPlayer.getYRot() - this.yaw) > 5.0D) {
					return false;
				}
			} else {
				this.targetX = this.closestPlayer.getX();
				this.targetY = this.closestPlayer.getY();
				this.targetZ = this.closestPlayer.getZ();
			}

			this.pitch = (double) this.closestPlayer.getXRot();
			this.yaw = (double) this.closestPlayer.getYRot();
		}

		return this.canUse();
	}

	protected boolean isScaredByPlayerMovement() {
		return this.scaredByPlayerMovement;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void start() {
		this.targetX = this.closestPlayer.getX();
		this.targetY = this.closestPlayer.getY();
		this.targetZ = this.closestPlayer.getZ();
		this.isRunning = true;
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by
	 * another one
	 */
	public void stop() {
		this.closestPlayer = null;
		this.creature.getNavigation().stop();
		this.delayTemptCounter = 100;
		this.isRunning = false;
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void tick() {
		this.creature.getLookControl().setLookAt(this.closestPlayer,
				(float) (this.creature.getMaxHeadYRot() + 20), (float) this.creature.getMaxHeadXRot());
		if (this.creature.distanceToSqr(this.closestPlayer) < 6.25D) {
			this.creature.getNavigation().stop();
		} else {
			this.creature.getNavigation().moveTo(this.closestPlayer, this.speed);
		}

	}

	/**
	 * @see #isRunning
	 */
	public boolean isRunning() {
		return this.isRunning;
	}
}
