package cn.evolvefield.mods.pvz.common.entity.zombie.custom;

import cn.evolvefield.mods.pvz.common.entity.ai.navigator.LavaZombiePathNavigator;
import cn.evolvefield.mods.pvz.common.entity.zombie.base.SwimmerZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.CustomZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.init.misc.PVZLoot;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.shapes.CollisionContext;

public class LavaZombieEntity extends SwimmerZombieEntity {

	public LavaZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ZombieUtil.WALK_FAST);
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ZombieUtil.LITTLE_LOW);
	}

	@Override
	public void zombieTick() {
		super.zombieTick();
		this.floatInLava();
	}

	private void floatInLava() {
		if (this.isInLava()) {
			CollisionContext iselectioncontext = CollisionContext.of(this);
			if (iselectioncontext.isAbove(LiquidBlock.STABLE_SHAPE, this.blockPosition(), true)
					&& !this.level.getFluidState(this.blockPosition().above()).is(FluidTags.LAVA)) {
				this.onGround = true;
			} else {
				this.setDeltaMovement(this.getDeltaMovement().scale(0.5D).add(0.0D, 0.06D, 0.0D));
			}
		}
	}

	@Override
	public boolean doHurtTarget(Entity entityIn) {
		entityIn.setSecondsOnFire(5);
		return super.doHurtTarget(entityIn);
	}

	@Override
	public float getLife() {
		return 360;
	}

	@Override
	protected float getWaterSlowDown() {
		return 0.935f;
	}


	@Override
	public float getWalkTargetValue(BlockPos p_205022_1_, LevelReader p_205022_2_) {
		if (p_205022_2_.getBlockState(p_205022_1_).getFluidState().is(FluidTags.LAVA)) {
			return 10.0F;
		} else {
			return this.isInLava() ? Float.NEGATIVE_INFINITY : 0.0F;
		}
	}


	@Override
	public boolean canStandOnFluid(FluidState p_230285_1_) {
		return p_230285_1_.is(FluidTags.LAVA);
	}

	@Override
	protected PathNavigation createNavigation(Level world) {
		return new LavaZombiePathNavigator(this, world);
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return PVZLoot.LAVA_ZOMBIE;
	}

	@Override
	public ZombieType getZombieType() {
		return CustomZombies.LAVA_ZOMBIE;
	}

}
