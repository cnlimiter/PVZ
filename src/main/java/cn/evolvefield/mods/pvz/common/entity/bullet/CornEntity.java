package cn.evolvefield.mods.pvz.common.entity.bullet;

import cn.evolvefield.mods.pvz.common.advancement.trigger.EntityEffectAmountTrigger;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.roof.GargantuarEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CornEntity extends PultBulletEntity {

	public int cornCnt;

	public CornEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
		this.height = 20;
	}

	public CornEntity(Level worldIn, LivingEntity living) {
		super(EntityRegister.CORN.get(), worldIn, living);
		this.height = 20;
	}

	@Override
	public void tick() {
		this.noPhysics = true;
		super.tick();
	}

	@Override
	protected void dealDamage(Entity target) {
		this.dealExplosionDamage();
	}

	@Override
	protected void onHitBlock() {
		this.dealExplosionDamage();
	}

	private void dealExplosionDamage() {
		final float range = 4F;
		int killCnt = 0;
		for(Entity entity : EntityUtil.getTargetableEntities(this.getOwnerOrSelf(), EntityUtil.getEntityAABB(this, range, range))) {
			entity.hurt(PVZEntityDamageSource.corn(this, this.getThrower()), this.attackDamage);
			if(! EntityUtil.isEntityValid(entity) && entity instanceof GargantuarEntity) {
				++ killCnt;
			}
		};
		if(this.getThrower() instanceof PVZPlantEntity) {
			var player = EntityUtil.getEntityOwner(level, getThrower());
			if(player != null && player instanceof ServerPlayer) {
				EntityEffectAmountTrigger.INSTANCE.trigger((ServerPlayer) player, getThrower(), killCnt);
			}
		}
		ItemEntity item = new ItemEntity(level, getX(), getY() + 1, getZ(), new ItemStack(ItemRegister.POP_CORN.get(), this.cornCnt));
		item.setDefaultPickUpDelay();
		level.addFreshEntity(item);
		this.cornCnt = 0;
		for(int i = 0; i < 3; ++ i) {
			EntityUtil.spawnParticle(this, 8);
		}
		for(int i = 0; i < 20; ++ i) {
			EntityUtil.spawnParticle(this, 9);
		}
		EntityUtil.playSound(this, SoundRegister.CHERRY_BOMB.get());
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(1F, 1F);
	}

	@Override
	protected int getMaxLiveTick() {
		return 300;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("cannon_pop_corn_cnt")) {
			this.cornCnt = compound.getInt("cannon_pop_corn_cnt");
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("cannon_pop_corn_cnt", this.cornCnt);
	}

}
