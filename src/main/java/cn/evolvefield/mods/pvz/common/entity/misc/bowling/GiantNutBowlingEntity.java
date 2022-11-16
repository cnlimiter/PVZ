package cn.evolvefield.mods.pvz.common.entity.misc.bowling;

import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class GiantNutBowlingEntity extends AbstractBowlingEntity {

	public GiantNutBowlingEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public GiantNutBowlingEntity(EntityType<? extends Projectile> type, Level worldIn, Player entity) {
		super(type, worldIn, entity);
	}

	@Override
	protected void dealDamageTo(Entity entity) {
		if(this.hitEntities == null) {
			this.hitEntities = new IntOpenHashSet();
		}
		if(this.hitEntities != null && ! this.hitEntities.contains(entity.getId())) {
			entity.hurt(PVZEntityDamageSource.normal(this, this.getOwner()), 200);
			this.hitEntities.add(entity.getId());
		    EntityUtil.playSound(this, SoundRegister.BOWLING_HIT.get());
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(2.5F, 2.5F);
	}

	@Override
	protected void changeDiretion() {
	}

}
