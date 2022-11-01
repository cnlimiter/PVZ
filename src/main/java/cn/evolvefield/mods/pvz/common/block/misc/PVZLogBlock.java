package cn.evolvefield.mods.pvz.common.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @program: pvzmod-1.16.5
 * @author: HungTeen
 * @create: 2022-03-02 14:36
 **/
public class PVZLogBlock extends RotatedPillarBlock {

    private final int fireSpeed;
    private final int burnSpeed;

    public PVZLogBlock(Properties properties, int fireSpeed, int burnSpeed) {
        super(properties);
        this.fireSpeed = fireSpeed;
        this.burnSpeed = burnSpeed;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return this.fireSpeed;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return this.burnSpeed;
    }
}
