package cn.evolvefield.mods.pvz.common.entity.bullet.itembullet;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.explosion.PotatoMineEntity;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.utils.PlantUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class PotatoEntity extends PVZItemBulletEntity{

	public PotatoEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
		this.setNoGravity(false);
	}

	public PotatoEntity(Level worldIn, LivingEntity thrower) {
		super(EntityRegister.POTATO.get(), worldIn, thrower);
		this.setNoGravity(false);
	}

	public void shoot(double x, double y, double z) {
		this.setDeltaMovement(x, y, z);
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(Items.POTATO);
	}

	@Override
	public void onImpact(HitResult result) {
		this.level.broadcastEntityEvent(this, (byte)3);
        if(! this.checkLive(result)) {
            if(! (this.getThrower() instanceof PVZPlantEntity)) {
            	Static.LOGGER.warn("Who is shooting potato, there are some matter !");
            	return ;
            }
            PotatoMineEntity mine = EntityRegister.POTATO_MINE.get().create(level);
            PlantUtil.copyPlantData(mine, (PVZPlantEntity) getThrower());
            mine.setPos(this.getX(), this.getY(), this.getZ());
            mine.setRisingFromDirt();
            this.level.addFreshEntity(mine);
            this.remove(RemovalReason.KILLED);
        }
	}

	@Override
	protected int getMaxLiveTick() {
		return 120;
	}

	@Override
	protected float getGravityVelocity() {
		return 0.06f;
	}

}
