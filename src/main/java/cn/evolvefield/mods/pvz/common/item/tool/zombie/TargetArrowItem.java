package cn.evolvefield.mods.pvz.common.item.tool.zombie;

import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import com.hungteen.pvz.common.entity.bullet.TargetArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TargetArrowItem extends ArrowItem {

	public TargetArrowItem() {
		super(new Properties().tab(PVZItemGroups.PVZ_USEFUL));
	}

	@Override
	public AbstractArrow createArrow(Level worldIn, ItemStack stack, LivingEntity shooter) {
		TargetArrowEntity arrowentity = new TargetArrowEntity(worldIn, shooter);
		return arrowentity;
	}

	@Override
	public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
		return false;
	}

}
