package cn.evolvefield.mods.pvz.common.block.special;

import cn.evolvefield.mods.pvz.common.enchantment.misc.SunMendingEnchantment;
import com.hungteen.pvz.common.item.tool.plant.SunStorageSaplingItem;
import com.hungteen.pvz.common.tileentity.SunConverterTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class SunConverterBlock extends Block {

	private static final VoxelShape AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);

	public SunConverterBlock() {
		super(Properties.copy(Blocks.IRON_BLOCK));
	}

	@Override
	public TileEntity createTileEntity(BlockState state, BlockGetter world) {
		return new SunConverterTileEntity();
	}

	@Override
	public ActionResultType use(BlockState state, Level worldIn, BlockPos pos, Player player,
			Hand handIn, BlockRayTraceResult hit) {
		if (!worldIn.isClientSide && handIn == Hand.MAIN_HAND) {
			SunConverterTileEntity te = (SunConverterTileEntity) worldIn.getBlockEntity(pos);
			NetworkHooks.openGui((ServerPlayer) player, te, pos);
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockReader, List<Component> textComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, blockReader, textComponents, tooltipFlag);
		textComponents.add(Component.translatable("tooltip.pvz.sun_converter").withStyle(ChatFormatting.ITALIC));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			var tileentity = worldIn.getBlockEntity(pos);
			if (tileentity instanceof SunConverterTileEntity) {
				SunConverterTileEntity te = (SunConverterTileEntity) worldIn.getBlockEntity(pos);
				for (int i = 0; i < te.handler.getSlots(); ++i) {
					InventoryHelper.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(),
							te.handler.getStackInSlot(i));
				}
			}
			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}

	/**
	 * check can place in slots or not.
	 */
	public static boolean isValidItem(ItemStack stack) {
		return SunStorageSaplingItem.isNotOnceSapling(stack) || SunMendingEnchantment.getLevel(stack) > 0;
	}

	@Override
	public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
		return AABB;
	}


	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

}
