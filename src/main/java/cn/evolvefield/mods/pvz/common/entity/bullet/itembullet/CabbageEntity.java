package cn.evolvefield.mods.pvz.common.entity.bullet.itembullet;

import cn.evolvefield.mods.pvz.common.entity.bullet.PultBulletEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class CabbageEntity extends PultBulletEntity implements ItemSupplier {

	public CabbageEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public CabbageEntity(Level worldIn, LivingEntity shooter) {
		super(EntityRegister.CABBAGE.get(), worldIn, shooter);
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(ItemRegister.CABBAGE.get());
	}

	protected void dealDamage(Entity target) {
		target.hurt(PVZEntityDamageSource.cabbage(this, this.getThrower()), this.getAttackDamage());
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.5F, 0.5F);
	}

}
