package cn.evolvefield.mods.pvz.common.entity.creature;

import cn.evolvefield.mods.pvz.utils.AlgorithmUtil;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SnailEntity extends Animal {

	protected Optional<Entity> targetEntity = Optional.empty();

	protected SnailEntity(EntityType<? extends Animal> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(3, new SnailNearestTargetGoal(this, 15));
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
		return null;
	}

	public final class SnailNearestTargetGoal extends Goal {

		private final SnailEntity owner;
		private final Level world;
		private final float range;
		private final int targetChance = 10;
		private final AlgorithmUtil.EntitySorter sorter;

		public SnailNearestTargetGoal(SnailEntity snail, float range) {
			this.owner = snail;
			this.range = range;
			this.world = this.owner.level;
			this.sorter = new AlgorithmUtil.EntitySorter(this.owner);
		}

		@Override
		public boolean canUse() {
			if(this.owner.getRandom().nextInt(this.targetChance) != 0) return false;
			List<Entity> list = this.world.getEntitiesOfClass(Entity.class, EntityUtil.getEntityAABB(this.owner, this.range, this.range), (entity) -> {
				return true;
			});
			if(list.isEmpty()) return false;
			Collections.sort(list, this.sorter);
			this.owner.targetEntity = Optional.of(list.get(0));
			return true;
		}

	}

}
