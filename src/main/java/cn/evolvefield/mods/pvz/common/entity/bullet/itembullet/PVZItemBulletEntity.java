package cn.evolvefield.mods.pvz.common.entity.bullet.itembullet;

import cn.evolvefield.mods.pvz.common.entity.bullet.AbstractBulletEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public abstract class PVZItemBulletEntity extends AbstractBulletEntity implements ItemSupplier {

	public PVZItemBulletEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public PVZItemBulletEntity(EntityType<? extends Projectile> type, Level worldIn, LivingEntity shooter) {
		super(type, worldIn, shooter);
	}

}
