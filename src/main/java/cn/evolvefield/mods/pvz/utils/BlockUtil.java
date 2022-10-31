package cn.evolvefield.mods.pvz.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.IItemHandler;

public class BlockUtil {
	/**
	 * Calculate the redstone current from a item stack handler
	 *
	 * @param handler The handler
	 * @return The redstone power
	 */
	public static int calculateRedstone(IItemHandler handler) {
		int i = 0;
		float f = 0.0F;
		for (int j = 0; j < handler.getSlots(); j++) {
			ItemStack stack = handler.getStackInSlot(j);
			if (!stack.isEmpty()) {
				f += (float) stack.getCount() / (float) Math.min(handler.getSlotLimit(j), stack.getMaxStackSize());
				i++;
			}
		}
		f = f / (float) handler.getSlots();
		return Mth.floor(f * 14.0F) + (i > 0 ? 1 : 0);
	}

	public static AABB getAABB(BlockPos pos, double w, double h) {
		return new AABB(pos.getX() - w, pos.getY() - h, pos.getZ() - w, pos.getX() + w, pos.getY() + h, pos.getZ() + w);
	}

	public static double getBlockPosOffset(LevelAccessor worldReader, BlockPos pos, AABB aabb) {
		AABB axisalignedbb = new AABB(pos);
		Iterable<VoxelShape> stream = worldReader.getCollisions((Entity) null, axisalignedbb);
		return 1.0D + Shapes.collide(Direction.Axis.Y, aabb, stream, -1.0D);
	}

}
