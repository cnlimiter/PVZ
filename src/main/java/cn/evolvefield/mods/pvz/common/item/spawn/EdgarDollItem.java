package cn.evolvefield.mods.pvz.common.item.spawn;

import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class EdgarDollItem extends Item {

	public EdgarDollItem() {
		super(new Properties().tab(PVZItemGroups.PVZ_USEFUL));
	}

//	@Override
//	public ActionResultType useOn(ItemUseContext context) {
//		final PlayerEntity player = context.getPlayer();
//		final World world = context.getLevel();
//		final BlockPos pos = context.getClickedPos();
//		if(! world.isClientSide && ! player.getCooldowns().isOnCooldown(this) && context.getClickedFace() == Direction.UP) {
//			if(this.canSpawnHere(world, pos)) {
//				Edgar090505Entity zomboss = EntityRegister.EDGAR_090505.get().create(world);
//				EntityUtil.onEntitySpawn(world, zomboss, pos.above());
//				context.getItemInHand().shrink(1);
//			}
//		}
//		return super.useOn(context);
//	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(Component.translatable("tooltip.pvz.wait_for_update").withStyle(ChatFormatting.RED));
	}

//	private boolean canSpawnHere(World world, BlockPos pos) {
//		for(int i = - 5; i <= 5; ++ i) {
//			for(int j = - 5; j <= 5; ++ j) {
//				for(int k = 1; k <= 12; ++ k) {
//					BlockPos tmp = pos.offset(i, k, j);
//					if(! world.getBlockState(tmp).isAir(world, pos)) {
//						return false;
//					}
//				}
//			}
//		}
//		return world.getBlockState(pos).canOcclude();
//	}

}
