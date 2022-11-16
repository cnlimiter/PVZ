package cn.evolvefield.mods.pvz.common.entity.bullet;

import cn.evolvefield.mods.pvz.common.entity.zombie.roof.BungeeZombieEntity;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

public class TargetArrowEntity extends AbstractArrow {

	public TargetArrowEntity(EntityType<? extends AbstractArrow> type, Level worldIn) {
		super(type, worldIn);
	}

	public TargetArrowEntity(Level worldIn, LivingEntity living) {
		super(EntityRegister.TARGET_ARROW.get(), living, worldIn);
	}

	@Override
	public void tick() {
		if(! level.isClientSide) {
			if(! EntityUtil.isEntityValid(this.getOwner())) { // shooter died
				this.remove(RemovalReason.KILLED);
				return ;
			} else {
				if(this.getOwner() instanceof BungeeZombieEntity) {
					BungeeZombieEntity bungee = (BungeeZombieEntity) this.getOwner();
					if(EntityUtil.isEntityValid(bungee.getStealTarget())) {
						this.shoot(bungee.getStealTarget());
					} else {
						this.remove(RemovalReason.KILLED);
						return ;
					}
				}
			}
		}
		super.tick();
	}



	@Override
	protected void onHitEntity(EntityHitResult result) {
		if(result.getEntity() instanceof LivingEntity && this.getOwner() instanceof Player) {// summon bungee
			if(! BungeeZombieEntity.canBungeeSteal(result.getEntity())) {
				super.onHitEntity(result);
				return ;
			}
			BungeeZombieEntity zombie = EntityRegister.BUNGEE_ZOMBIE.get().create(level);
			zombie.setBungeeType(BungeeZombieEntity.BungeeTypes.HELP);
			zombie.setCharmed(true);
			zombie.setStealTarget((LivingEntity) result.getEntity());
			EntityUtil.onEntitySpawn(level, zombie, blockPosition().above(20));
			super.onHitEntity(result);
			this.remove(RemovalReason.KILLED);
		}
	}

	public void shoot(LivingEntity target) {
		var speed = target.position().subtract(this.position()).normalize();
		double multi = 1.3D;
		this.setDeltaMovement(speed.multiply(multi, multi, multi));
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.5F, 0.5F);
	}

	@Override
	protected ItemStack getPickupItem() {
		return new ItemStack(ItemRegister.TARGET_ARROW.get());
	}

	@Override
	public byte getPierceLevel() {
		return 0;
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
