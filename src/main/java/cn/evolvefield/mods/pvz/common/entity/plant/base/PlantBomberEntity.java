package cn.evolvefield.mods.pvz.common.entity.plant.base;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

/**
 * Bomber Plants will be remove after several seconds and perform explosion when dying.
 * use ATTACK_TIME to perform scale animation.
 */
public abstract class PlantBomberEntity extends PVZPlantEntity {

	protected boolean hasBombAlamancs = true;

	public PlantBomberEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		final int time = this.getAttackTime();
		if(this.level.isClientSide) {
			if(time == this.getReadyTime() - 1) {
		    	this.startBomb(false);
			}
		} else {
		    if(time > this.getReadyTime()) {
			    this.startBomb(true);
			    this.remove(RemovalReason.KILLED);
		    }
		    this.setAttackTime(time + 1);
		}
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		if(hasBombAlamancs){
			list.addAll(Arrays.asList(
					Pair.of(PAZAlmanacs.EXPLODE_DAMAGE, this.getExplodeDamage()),
					Pair.of(PAZAlmanacs.EXPLODE_RANGE, this.getExplodeRange())
			));
		}
	}

	public float getExplodeDamage(){return 0;}

	public float getExplodeRange(){return 0;}

	/**
	 * start explosion.
	 */
    protected abstract void startBomb(boolean server);

    /**
     * explosion pre time
     */
	public abstract int getReadyTime();

	@Override
	public boolean isPlantImmuneTo(DamageSource source) {
		return super.isPlantImmuneTo(source) || this.canNormalUpdate();
	}

	@Override
	public int getSuperTimeLength() {
		return 0;
	}

}
