package cn.evolvefield.mods.pvz.common.block.plants;

import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PeaBlock extends CropBlock {

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
    		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
    		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
    		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
    		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
    		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
    		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
    		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
    		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    public PeaBlock(Properties builder) {
		super(builder);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter p_52298_, BlockPos p_52299_, CollisionContext p_52300_) {
		return SHAPE_BY_AGE[state.getValue(this.getAgeProperty())];
	}

	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand p_60507_, BlockHitResult p_60508_) {
		if(! worldIn.isClientSide) {
			if(this.isMaxAge(state)) {
				worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemRegister.PEA.get(), 1)));
				worldIn.setBlockAndUpdate(pos, this.getStateForAge(0));
				return InteractionResult.SUCCESS;
			}
		}
		return super.use(state, worldIn, pos, player, p_60507_, p_60508_);	}



	@Override
	protected ItemLike getBaseSeedId() {
		return ItemRegister.PEA.get();
	}
}
