package cn.evolvefield.mods.pvz.common.impl.challenge.placement;

import cn.evolvefield.mods.pvz.api.interfaces.raid.IPlacementComponent;
import cn.evolvefield.mods.pvz.utils.MathUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class CenterPlacement implements IPlacementComponent {

	public static final String NAME = "center";
	private boolean onSurface;
	private int radius;

	@Override
	public BlockPos getPlacePosition(Level world, BlockPos origin) {
		final int dx = MathUtil.getRandomInRange(world.getRandom(), this.radius);
		final int dz = MathUtil.getRandomInRange(world.getRandom(), this.radius);
		final int height = this.onSurface ? world.getHeight(Heightmap.Types.WORLD_SURFACE, origin.getX() + dx, origin.getZ() + dz) : origin.getY();
		return new BlockPos(origin.getX() + dx, height, origin.getZ() + dz);
	}

	@Override
	public void readJson(JsonElement json) {
		JsonObject obj = json.getAsJsonObject();
		if(obj != null) {
			this.radius = GsonHelper.getAsInt(obj, "radius", 1);
			this.onSurface = GsonHelper.getAsBoolean(obj, "ground", true);
		}
	}

}
