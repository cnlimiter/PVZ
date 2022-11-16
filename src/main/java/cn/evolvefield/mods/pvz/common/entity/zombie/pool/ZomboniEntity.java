package cn.evolvefield.mods.pvz.common.entity.zombie.pool;

import cn.evolvefield.mods.pvz.api.interfaces.base.IHasWheel;
import cn.evolvefield.mods.pvz.api.interfaces.util.IHasMultiPart;
import cn.evolvefield.mods.pvz.common.entity.PVZMultiPartEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.base.CarZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.part.PVZZombiePartEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.PoolZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class ZomboniEntity extends CarZombieEntity implements IHasMultiPart, IHasWheel {

	private PVZZombiePartEntity part;

	public ZomboniEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.setImmuneAllEffects();
		this.resetParts();
	}

	@Override
	public void zombieTick() {
		super.zombieTick();
		if(! level.isClientSide) {
			FrostWalkerEnchantment.onEntityMoved(this, level, this.blockPosition(), 1);
			BlockPos blockpos = this.blockPosition();
			BlockState state = Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 1);
            if ((this.level.isEmptyBlock(blockpos) || level.getBlockState(blockpos).getBlock() == Blocks.SNOW) && state.canSurvive(this.level, blockpos)) {
               this.level.setBlockAndUpdate(blockpos, state);
            }
		}
	}

	@Override
	public void tick() {
		super.tick();
		updateParts();
	}

	@Override
	public void spikeWheelBy(LivingEntity entity) {
		this.hurt(PVZEntityDamageSource.thorns(entity), EntityUtil.getMaxHealthDamage(this, 2));
	}

	@Override
	public void resetParts() {
		removeParts();
		this.part = new PVZZombiePartEntity(this, 1.2f, 1.5f);
		this.part.setOwner(this);
	}

	@Override
	public void removeParts() {
		if(this.part != null) {
			this.part.remove(RemovalReason.KILLED);
			this.part = null;
		}
	}

	@Override
	public void updateParts() {
		if(this.part != null) {
			if(! this.part.isAddedToWorld()) {
				this.level.addFreshEntity(this.part);
			}
			float j = 2 * 3.14159f * this.getYRot() / 360;
			float dis = this.getPartOffset();
			var pos = this.position();
			this.part.yRotO = this.getYRot();
			this.part.xRotO = this.getXRot();
			this.part.moveTo(pos.x() - Math.sin(j) * dis, pos.y() + 0.2f, pos.z() + Math.cos(j) * dis, this.getYRot(), this.getXRot());
			this.part.setOwner(this);
		}
	}

	public PVZMultiPartEntity[] getMultiParts() {
		return new PVZMultiPartEntity[] {this.part};
	}


	@Override
	public void remove(RemovalReason pReason) {
		removeParts();
		super.remove(pReason);
	}

	public float getPartOffset() {
		return 1.2f;
	}

	@Override
	public float getWalkSpeed() {
		return ZombieUtil.WALK_SLOW;
	}

	@Override
	public float getLife() {
		return 130;
	}

	@Override
	public int getArmorToughness() {
		return 12;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundRegister.METAL_HIT.get();
	}

	@Override
	public Optional<SoundEvent> getSpawnSound() {
		return Optional.ofNullable(SoundRegister.CAR.get());
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.8f, 2.3f);
	}

	@Override
	public ZombieType getZombieType() {
		return PoolZombies.ZOMBONI;
	}

}
