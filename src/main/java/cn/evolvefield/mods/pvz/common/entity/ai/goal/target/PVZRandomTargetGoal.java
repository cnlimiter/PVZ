package cn.evolvefield.mods.pvz.common.entity.ai.goal.target;

import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class PVZRandomTargetGoal extends PVZTargetGoal {

	public PVZRandomTargetGoal(Mob mobIn, boolean mustSee, boolean mustReach, float w, float h) {
		super(mobIn, mustSee, mustReach, w, h);
	}

	public PVZRandomTargetGoal(Mob mobIn, boolean mustSee, boolean mustReach, float w, float h1, float h2) {
		super(mobIn, mustSee, mustReach, w, h1, h2);
	}

	@Override
	public boolean canUse() {
		if (this.targetChance > 0 && this.mob.getRandom().nextInt(this.targetChance) != 0) {
			return false;
		}
		List<LivingEntity> list1 = EntityUtil.getTargetableLivings(mob, getAABB()).stream().filter(target -> (!this.mustSee || this.checkSenses(target)) && this.checkOther(target)).toList();
		if (list1.isEmpty()) {
			return false;
		}
		int pos = this.mob.getRandom().nextInt(list1.size());
		this.targetMob = list1.get(pos);
		return true;
	}

}
