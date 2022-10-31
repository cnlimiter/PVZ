package cn.evolvefield.mods.pvz.common.impl.challenge.placement;

import cn.evolvefield.mods.pvz.api.interfaces.raid.IPlacementComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;

public class OffsetPlacement implements IPlacementComponent {

	public static final String NAME = "offset";
	private BlockPos center = null;
	private BlockPos offset = BlockPos.ZERO;

	@Override
	public BlockPos getPlacePosition(Level world, BlockPos origin) {
		final BlockPos now = this.center == null ? origin : this.center;
		return now.offset(offset.getX(), offset.getY(), offset.getZ());
	}

	@Override
	public void readJson(JsonElement json) {
		JsonObject obj = json.getAsJsonObject();
		if(obj != null) {
			if(obj.has("x") && obj.has("y") && obj.has("z")) {
				this.center = new BlockPos(GsonHelper.getAsInt(obj, "x"), GsonHelper.getAsInt(obj, "y"), GsonHelper.getAsInt(obj, "z"));
			}
			if(obj.has("dx") && obj.has("dy") && obj.has("dz")) {
				this.offset = new BlockPos(GsonHelper.getAsInt(obj, "dx"), GsonHelper.getAsInt(obj, "dy"), GsonHelper.getAsInt(obj, "dz"));
			}
		}
	}

}
