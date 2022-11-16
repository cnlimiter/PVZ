package cn.evolvefield.mods.pvz.common.entity.ai.goal;

import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkStatus;

import javax.annotation.Nullable;

public class ZombieBreakPlantBlockGoal extends MoveToBlockGoal {

	protected final Block plantBlock;
	protected final PVZZombieEntity zombie;
	protected int breakingTime;

	public ZombieBreakPlantBlockGoal(Block blockIn, PVZZombieEntity zombie, double speed, int yMax) {
		super(zombie, speed, 24, yMax);
		this.plantBlock = blockIn;
		this.zombie = zombie;
	}

	@Override
	public boolean canUse() {
		if(! this.canZombieContinue()) {
			return false;
		}
		if (!net.minecraftforge.common.ForgeHooks.canEntityDestroy(zombie.level, this.blockPos, zombie)) {
			return false;
		} else if (this.nextStartTick > 0) {
			--this.nextStartTick;
			return false;
		} else if (this.tryFindBlock()) {
			this.nextStartTick = 20;
			return true;
		} else {
			this.nextStartTick = this.nextStartTick(this.mob);
			return false;
		}
	}

	@Override
	public void stop() {
		super.stop();
		zombie.fallDistance = 1.0F;
	}

	@Override
	public void start() {
		super.start();
		this.breakingTime = 0;
	}

	@Override
	public boolean canContinueToUse() {
		if(! this.canZombieContinue()) {
			return false;
		}
		return super.canContinueToUse();
	}

	@Override
	public void tick() {
		super.tick();
		var world = zombie.level;
		if (this.isEntityNearBy() && this.blockPos != null) {
			if (this.breakingTime % 2 == 0) {
				if (this.breakingTime % 6 == 0) {
					this.playBreakingSound(world, this.blockPos);
				}
			}
			if (this.breakingTime > this.getBreakTime(zombie)) {
				world.destroyBlock(this.blockPos, false);
				if (!world.isClientSide) {
					this.playBrokenSound(world, this.blockPos);
				}
			}
			zombie.level.destroyBlockProgress(zombie.getId(), this.blockPos, breakingTime / 8);
			++ this.breakingTime;
		}
	}



	/**
	 * only execute when zombie has no attack target.
	 * and charmed zombie do not break plant block.
	 */
	public boolean canZombieContinue() {
		return zombie.canBreakPlantBlock() && zombie.getTarget() == null;
	}

	private boolean tryFindBlock() {
		return this.blockPos != null && this.isValidTarget(this.mob.level, this.blockPos) ? true
				: this.findNearestBlock();
	}

	protected int getBreakTime(Mob entity) {
		return 60;
	}

	public void playBreakingSound(LevelAccessor worldIn, BlockPos pos) {
	}

	public void playBrokenSound(Level worldIn, BlockPos pos) {
	}

	protected boolean isEntityNearBy() {
		if(Math.abs(this.blockPos.getY() - zombie.getY()) > 3) {
			return false;
		}
		double x = this.blockPos.getX(), z = this.blockPos.getZ();
		double xx = zombie.getX(), zz = zombie.getZ();
		return (x - xx) * (x - xx) + (z - zz) * (z - zz) <= 4;
	}

	@Nullable
	private BlockPos findTarget(BlockPos pos, LevelAccessor worldIn) {
		if (worldIn.getBlockState(pos).getBlock() == this.plantBlock) {
			return pos;
		} else {
			BlockPos[] ablockpos = new BlockPos[] { pos.below(), pos.west(), pos.east(), pos.north(), pos.south(),
					pos.below().below() };

			for (BlockPos blockpos : ablockpos) {
				if (worldIn.getBlockState(blockpos).getBlock() == this.plantBlock) {
					return blockpos;
				}
			}

			return null;
		}
	}
	/**
	 * Return true to set given position as destination
	 */
	@SuppressWarnings("deprecation")
	protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
		var ichunk = worldIn.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
		if (ichunk == null) {
			return false;
		} else {
			return ichunk.getBlockState(pos).getBlock() == this.plantBlock && ichunk.getBlockState(pos.above()).isAir()
					&& ichunk.getBlockState(pos.above(2)).isAir();
		}
	}
}
