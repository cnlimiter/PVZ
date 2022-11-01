package cn.evolvefield.mods.pvz.common.block.special;

import cn.evolvefield.mods.pvz.common.block.AbstractFacingBlock;
import com.hungteen.pvz.common.tileentity.CardFusionTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class CardFusionBlock extends AbstractFacingBlock {

	public CardFusionBlock() {
		super(Properties.copy(Blocks.IRON_BLOCK));
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player,
								 InteractionHand handIn, BlockHitResult hit) {
		if (! worldIn.isClientSide && handIn == InteractionHand.MAIN_HAND) {
			CardFusionTileEntity te = (CardFusionTileEntity) worldIn.getBlockEntity(pos);
		    NetworkHooks.openScreen((ServerPlayer) player, te, pos);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter iBlockReader, List<Component> textComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, iBlockReader, textComponents, tooltipFlag);
		textComponents.add(Component.translatable("tooltip.pvz.card_fusion_table").withStyle(ChatFormatting.GREEN));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}




	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return new CardFusionTileEntity();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			var tileentity = worldIn.getBlockEntity(pos);
			if (tileentity instanceof CardFusionTileEntity) {
				CardFusionTileEntity te = (CardFusionTileEntity) worldIn.getBlockEntity(pos);
				for (int i = 0; i < te.handler.getSlots(); ++ i) {
					InventoryHelper.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(),
							te.handler.getStackInSlot(i));
				}
			}
			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}

}
