package cn.evolvefield.mods.pvz.common.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.Player;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @program: pvzmod-1.16.5
 * @author: HungTeen
 * @create: 2022-01-20 16:32
 **/
public abstract class PVZTileEntity extends BlockEntity {

    public PVZTileEntity(BlockEntityType<?> type, BlockPos pPos, BlockState pBlockState) {
        super(type);
    }

    /**
     * used by container.
     */
    public boolean isUsableByPlayer(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr((double) this.worldPosition.getX() + 0.5D,
                (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

}
