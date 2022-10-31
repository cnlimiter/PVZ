package cn.evolvefield.mods.pvz.common.item.blockitem;

import cn.evolvefield.mods.pvz.init.registry.BlockRegister;
import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class LilyPadItem extends BlockItem {

	public LilyPadItem() {
		super(BlockRegister.LILY_PAD.get(),new Properties().tab(PVZItemGroups.PVZ_MISC));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return InteractionResult.PASS;
	}



	@Override
	public InteractionResultHolder<ItemStack> use(Level p_77659_1_, Player p_77659_2_, InteractionHand p_77659_3_) {
	      BlockHitResult blockraytraceresult = getPlayerPOVHitResult(p_77659_1_, p_77659_2_, ClipContext.Fluid.SOURCE_ONLY);
			BlockHitResult blockraytraceresult1 = blockraytraceresult.withPosition(blockraytraceresult.getBlockPos().above());
	      InteractionResult actionresulttype = super.useOn(new UseOnContext(p_77659_2_, p_77659_3_, blockraytraceresult1));
	      return new InteractionResultHolder<>(actionresulttype, p_77659_2_.getItemInHand(p_77659_3_));
	   }

}
