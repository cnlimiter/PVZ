package cn.evolvefield.mods.pvz.common.block.plants;

import net.minecraft.block.*;
import net.minecraft.block.trees.Tree;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;

import java.util.Random;
import java.util.function.Supplier;

public class PVZSaplingBlock extends BushBlock implements IPlantable {

	public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
	private final Supplier<Tree> treeGrower;

	public PVZSaplingBlock(Supplier<Tree> grower) {
		super(Properties.copy(Blocks.OAK_SAPLING));
		this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, Integer.valueOf(0)));
		this.treeGrower = grower;
	}

	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
		if (!worldIn.isAreaLoaded(pos, 1)) {
			return; // Forge: prevent loading unloaded chunks when checking neighbor's light
		}
		if (worldIn.getMaxLocalRawBrightness(pos.above()) >= 9 && rand.nextInt(7) == 0) {
			this.genTree(worldIn, pos, state, rand);
		}
	}

	public void genTree(ServerLevel p_226942_1_, BlockPos p_226942_2_, BlockState p_226942_3_, RandomSource p_226942_4_) {
		if (p_226942_3_.getValue(STAGE) == 0) {
			p_226942_1_.setBlock(p_226942_2_, p_226942_3_.cycle(STAGE), 4);
		} else {
			if (!net.minecraftforge.event.ForgeEventFactory.saplingGrowTree(p_226942_1_, p_226942_4_, p_226942_2_)) return;
			this.treeGrower.get().growTree(p_226942_1_, p_226942_1_.getChunkSource().getGenerator(), p_226942_2_, p_226942_3_, p_226942_4_);
		}
	}

	/**
	 * Whether this IGrowable can grow
	 */
	public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	public boolean isBonemealSuccess(Level worldIn, RandomSource rand, BlockPos pos, BlockState state) {
		return (double) worldIn.random.nextFloat() < 0.45D;
	}

	public void performBonemeal(ServerLevel worldIn, RandomSource rand, BlockPos pos, BlockState state) {
		this.genTree(worldIn, pos, state, rand);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(STAGE);
	}

}
