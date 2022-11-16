package cn.evolvefield.mods.pvz.common.entity.plant.base;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.attract.ICanAttract;
import cn.evolvefield.mods.pvz.api.interfaces.attract.ICanBeAttracted;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.common.entity.ai.goal.misc.PlantAttractGoal;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public abstract class PlantDefenderEntity extends PVZPlantEntity implements ICanAttract {

	public PlantDefenderEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new PlantAttractGoal(this, this, 20));
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.addAll(Arrays.asList(
				Pair.of(PAZAlmanacs.ARMOR, this.getArmor()),
				Pair.of(PAZAlmanacs.ARMOR_TOUGHNESS, this.getArmorToughness())
		));
	}

	@Override
	public void startSuperMode(boolean first) {
		super.startSuperMode(first);
		this.setInnerDefenceLife(this.getSuperLife());
	}

	@Override
	public boolean canAttract(LivingEntity entity) {
		if(entity instanceof ICanBeAttracted && ! ((ICanBeAttracted) entity).canBeAttractedBy(this)) {
			return false;
		}
		if(! this.getSensing().hasLineOfSight(entity)) {
			return false;
		}
		return true;
	}

	@Override
	public void attract(LivingEntity target) {
		if(target instanceof Mob mob && (! (mob.getTarget() instanceof ICanAttract))) {
			mob.setTarget(this);
		}
		if(target instanceof ICanBeAttracted attracted) {
			attracted.attractBy(this);
		}
	}

	@Override
	public float getAttractRange() {
		return 2.5F;
	}

	@Override
	public int getSuperTimeLength() {
		return 20;
	}

	/**
	 * for extra life.
	 */
	public abstract float getSuperLife();

}
