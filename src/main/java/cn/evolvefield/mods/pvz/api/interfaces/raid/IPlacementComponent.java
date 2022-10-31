package cn.evolvefield.mods.pvz.api.interfaces.raid;

import com.google.gson.JsonElement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IPlacementComponent {

    BlockPos getPlacePosition(Level world, BlockPos origin);

    /**
     * make sure constructer has no argument,
     * and use this method to initiate instance.
     */
    void readJson(JsonElement json);
}
